/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.daos;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ncgms.entities.Client;
import ncgms.entities.ContainerOrder;
import ncgms.entities.Invoice;
import ncgms.entities.Message;
import ncgms.entities.User;
import ncgms.util.SMSSender;

/**
 * InvoicesFacade
 *
 * @author root
 */
public class InvoicesFacade extends AbstractFacade {

    private Invoice invoice = new Invoice();

    public InvoicesFacade() {
    }

    public InvoicesFacade(Invoice invoice) {
        this.invoice = invoice;
    }

    public ArrayList<Invoice> loadClientInvoices() throws SQLException {
        connect();
        // Invoice list to return
        ArrayList<Invoice> invoiceList = new ArrayList<>();

        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Invoices` WHERE `clientID` = \""
                + this.invoice.getClient().getUserID() + "\""
                + " ORDER BY `invoiceID` DESC";
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            this.invoice = new Invoice(resultSet.getInt("invoiceID"),
                    resultSet.getLong("dateAdded"), resultSet.getLong("dateDue"),
                    resultSet.getLong("datePaid"), resultSet.getDouble("amountDue"),
                    resultSet.getDouble("amountPaid"), resultSet.getDouble("balance"),
                    resultSet.getInt("isPaid"), null);
            invoiceList.add(this.invoice);
        }
        disconnect();
        return invoiceList;
    }

    public ArrayList<Invoice> loadAllInvoices() throws SQLException {
        connect();
        // Invoice list to return
        ArrayList<Invoice> invoiceList = new ArrayList<>();

        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Invoices` ORDER BY `invoiceID` DESC";
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            this.invoice = new Invoice(resultSet.getInt("invoiceID"),
                    resultSet.getLong("dateAdded"), resultSet.getLong("dateDue"),
                    resultSet.getLong("datePaid"), resultSet.getDouble("amountDue"),
                    resultSet.getDouble("amountPaid"), resultSet.getDouble("balance"),
                    resultSet.getInt("isPaid"), null);
            // Get this invoices client
            ClientsFacade clientsFacade = new ClientsFacade();
            this.invoice.setClient(clientsFacade.searchClientByClientID(
                    resultSet.getInt("clientID")));
            // Add invoice to invoiceList
            invoiceList.add(this.invoice);
        }
        disconnect();
        return invoiceList;
    }

    public int updateInvoice(Invoice invoice) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = null;
        int result = 0;
        query = "UPDATE `Invoices` SET `amountPaid` = \"" + invoice.getAmountPaid()
                + "\", `balance` = \"" + invoice.getBalance() + "\","
                + " `datePaid` = \"" + invoice.getDatePaid() + "\","
                + " `isPaid` = \"" + 1 + "\""
                + " WHERE `invoiceID` = \"" + invoice.getInvoiceID() + "\"";
        result += statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int createMonthlyInvoicesForAllClients() throws SQLException {
        connect();
        int result = 0;
        List<Client> clientList = new ArrayList<>();
        ClientsFacade clientsFacade = new ClientsFacade();
        clientList = clientsFacade.loadAllClients();

        // Create date for first of last month
        // Remember today is always first of the current month
        Calendar pastCalendar = new GregorianCalendar();
        int month = pastCalendar.get(Calendar.MONTH);
        // Move back one month
        if (month > 1) {
            month--;
            pastCalendar.set(Calendar.MONTH, month);
        } else {
            month = 12;
            pastCalendar.set(Calendar.MONTH, month);
        }
        long pastDate = pastCalendar.getTime().getTime();
        // Process invoices for each client
        for (Client client : clientList) {
            // Get all the unapproved orders for that client for last month
            ContainerOrdersFacade containerOrdersFacade = new ContainerOrdersFacade();
            List<ContainerOrder> monthlyUnapprovedClientContainerOrderList = new ArrayList<>();
            monthlyUnapprovedClientContainerOrderList = containerOrdersFacade.
                    loadMonthlyUnapporovedClientContainerOrders(client, pastDate);
            // Calculate the amount due fo all the orders of the prevoius month
            double amountDue = 0;
            for (ContainerOrder continerOrder : monthlyUnapprovedClientContainerOrderList) {
                amountDue += continerOrder.getTotalPrice();
            }

            // Get this clients' previous invoice balance
            double balance = loadClientsPreviousInvoiceBalance(client.getUserID());
            // Add balance to amountDue
            amountDue += balance;
            balance = amountDue;
            if (amountDue == 0) {// If amount due is zero
                continue;
            }

            // Create and insert the invoice
            this.invoice = new Invoice(0, new Date().getTime(), new Date().getTime() + 86400000,
                    0, amountDue, 0, balance, 0, client);
            result = this.insertInvoice();

            // Send a message to each client
            if (result > 0) {
                String messageContent = "Hello " + new ClientsFacade().searchClientByClientID(
                        client.getUserID()).getFirstName() + " You have received a new monthly bill. "
                        + " Amount due is KShs: " + invoice.getAmountDue()
                        + ". Please ensure that you pay before  " + this.invoice.getRealDateDue()
                        + " to avoid disruption of services. Thank you for your business support."
                        + " NCGMS Inc.";
                Message message = new Message(0, messageContent, new Date().getTime(),
                        0, new User(client.getUserID(), null, null, 0));
                MessagesFacade messagesFacade = new MessagesFacade(message);
                result = messagesFacade.insertMessage();
                try {
                    // Send an sms to the user
                    SMSSender.sendSmsSynchronous(
                            new ClientsFacade().searchClientByClientID(
                                    message.getUser().getUserID()
                            ).getPhone(), messageContent);
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(InvoicesFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                // No invoices for last month
            }
        }
        disconnect();
        return result;
    }

    public int insertInvoice() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Invoices`(`dateAdded`, `dateDue`, `amountDue`,"
                + " `balance`, `isPaid`, `clientID`) VALUES "
                + " (\"" + invoice.getDateAdded() + "\", \"" + invoice.getDateDue() + "\","
                + "\"" + invoice.getAmountDue() + "\", \"" + invoice.getBalance() + "\","
                + " \"" + invoice.getIsPaid() + "\", \"" + invoice.getClient().getUserID() + "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public List<Invoice> searchInvoiceByInvoiceNumber(String searchTerm) throws SQLException {
        connect();
        ArrayList<Invoice> invoiceList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Invoices`.*, `Clients`.* FROM `Invoices`"
                + " INNER JOIN `Clients` ON `Invoices`.`clientID` = `Clients`.`clientID`"
                + " WHERE `Invoices`.`invoiceID` = \"" + searchTerm + "\""
                + " ORDER BY `Invoices`.`invoiceID` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.invoice = new Invoice(resultSet.getInt("invoiceID"),
                    resultSet.getLong("dateAdded"), resultSet.getLong("dateDue"),
                    resultSet.getLong("datePaid"), resultSet.getDouble("amountDue"),
                    resultSet.getDouble("amountPaid"), resultSet.getDouble("balance"),
                    resultSet.getInt("isPaid"), null);
            // Get this invoices client
            ClientsFacade clientsFacade = new ClientsFacade();
            this.invoice.setClient(clientsFacade.searchClientByClientID(
                    resultSet.getInt("clientID")));
            // Add invoice to invoicelIST
            invoiceList.add(this.invoice);
        }
        disconnect();
        return invoiceList;
    }

    public List<Invoice> searchInvoiceByPlotName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Invoice> invoiceList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Invoices`.*, `Clients`.* FROM `Invoices`"
                + " INNER JOIN `Clients` ON `Invoices`.`clientID` = `Clients`.`clientID`"
                + " WHERE `Clients`.`plotName` = \"" + searchTerm + "\""
                + " ORDER BY `Invoices`.`invoiceID` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.invoice = new Invoice(resultSet.getInt("invoiceID"),
                    resultSet.getLong("dateAdded"), resultSet.getLong("dateDue"),
                    resultSet.getLong("datePaid"), resultSet.getDouble("amountDue"),
                    resultSet.getDouble("amountPaid"), resultSet.getDouble("balance"),
                    resultSet.getInt("isPaid"), null);
            // Get this invoices client
            ClientsFacade clientsFacade = new ClientsFacade();
            this.invoice.setClient(clientsFacade.searchClientByClientID(
                    resultSet.getInt("clientID")));
            // Add invoice to invoicelIST
            invoiceList.add(this.invoice);
        }
        disconnect();
        return invoiceList;
    }

    public List<Invoice> searchInvoiceByFirstName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Invoice> invoiceList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Invoices`.*, `Clients`.* FROM `Invoices`"
                + " INNER JOIN `Clients` ON `Invoices`.`clientID` = `Clients`.`clientID`"
                + " WHERE `Clients`.`firstName` = \"" + searchTerm + "\""
                + " ORDER BY `Invoices`.`invoiceID` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.invoice = new Invoice(resultSet.getInt("invoiceID"),
                    resultSet.getLong("dateAdded"), resultSet.getLong("dateDue"),
                    resultSet.getLong("datePaid"), resultSet.getDouble("amountDue"),
                    resultSet.getDouble("amountPaid"), resultSet.getDouble("balance"),
                    resultSet.getInt("isPaid"), null);
            // Get this invoices client
            ClientsFacade clientsFacade = new ClientsFacade();
            this.invoice.setClient(clientsFacade.searchClientByClientID(
                    resultSet.getInt("clientID")));
            // Add invoice to invoiceList
            invoiceList.add(this.invoice);
        }
        disconnect();
        return invoiceList;
    }

    public List<Invoice> searchInvoiceByLastName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Invoice> invoiceList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Invoices`.*, `Clients`.* FROM `Invoices`"
                + " INNER JOIN `Clients` ON `Invoices`.`clientID` = `Clients`.`clientID`"
                + " WHERE `Clients`.`lastName` = \"" + searchTerm + "\""
                + " ORDER BY `Invoices`.`invoiceID` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.invoice = new Invoice(resultSet.getInt("invoiceID"),
                    resultSet.getLong("dateAdded"), resultSet.getLong("dateDue"),
                    resultSet.getLong("datePaid"), resultSet.getDouble("amountDue"),
                    resultSet.getDouble("amountPaid"), resultSet.getDouble("balance"),
                    resultSet.getInt("isPaid"), null);
            // Get this invoices client
            ClientsFacade clientsFacade = new ClientsFacade();
            this.invoice.setClient(clientsFacade.searchClientByClientID(
                    resultSet.getInt("clientID")));
            // Add invoice to invoicelIST
            invoiceList.add(this.invoice);
        }
        disconnect();
        return invoiceList;
    }

    private double loadClientsPreviousInvoiceBalance(int clientID) throws SQLException {
        connect();
        double balance = 0;
        Statement statement = connection.createStatement();
        String query = "SELECT `Invoices`.`balance` FROM `Invoices` WHERE"
                + " `Invoices`.`clientID` = \"" + clientID + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            balance = resultSet.getDouble("balance");
        }
        disconnect();
        return balance;
    }

    public List<Invoice> searchInvoiceByInvoiceNumber(
            String searchTerm, int clientID) throws SQLException {
        connect();
        ArrayList<Invoice> invoiceList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Invoices`.*, `Clients`.* FROM `Invoices`"
                + " INNER JOIN `Clients` ON `Invoices`.`clientID` = `Clients`.`clientID`"
                + " WHERE `Invoices`.`invoiceID` = \"" + searchTerm + "\" AND"
                + " `Invoices`.`clientID` = \"" + clientID + "\""
                + " ORDER BY `Invoices`.`invoiceID` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.invoice = new Invoice(resultSet.getInt("invoiceID"),
                    resultSet.getLong("dateAdded"), resultSet.getLong("dateDue"),
                    resultSet.getLong("datePaid"), resultSet.getDouble("amountDue"),
                    resultSet.getDouble("amountPaid"), resultSet.getDouble("balance"),
                    resultSet.getInt("isPaid"), null);
            // Get this invoices client
            ClientsFacade clientsFacade = new ClientsFacade();
            this.invoice.setClient(clientsFacade.searchClientByClientID(
                    resultSet.getInt("clientID")));
            // Add invoice to invoicelIST
            invoiceList.add(this.invoice);
        }
        disconnect();
        return invoiceList;
    }
}
