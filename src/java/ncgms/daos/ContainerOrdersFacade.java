/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import ncgms.entities.Client;
import ncgms.entities.ContainerOrder;
import ncgms.entities.OrderDetail;

/**
 *
 * @author root
 */
public class ContainerOrdersFacade extends AbstractFacade {

    private ContainerOrder containerOrder = new ContainerOrder();

    public ContainerOrdersFacade() {
    }

    public ContainerOrdersFacade(ContainerOrder containerOrder) {
        this.containerOrder = containerOrder;
    }

    public ArrayList<ContainerOrder> loadAllContainerOrders() throws SQLException {
        connect();
        ArrayList<ContainerOrder> containerOrderList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Users`.*, `Clients`.*, `ContainerOrders`.*, "
                + " `ContainerOrders`.`dateAdded`, `ContainerOrders`.`totalPrice`,"
                + " `ContainerOrders`.`status`, `ContainerOrders`.`clientID` FROM "
                + " `Users` INNER JOIN `Clients` ON `Users`.`userID` = `Clients`.`clientID`"
                + " INNER JOIN `ContainerOrders` ON `Clients`.`clientID` = "
                + "`ContainerOrders`.`clientID` WHERE `Users`.`isActive` = \"" + 1 + "\""
                + " ORDER BY `ContainerOrders`.`orderID` DESC";

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create new client
            Client client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("address"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("plotName"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getInt("wantsToCancel"));

            this.containerOrder = new ContainerOrder(resultSet.getInt("orderID"),
                    resultSet.getLong("dateAdded"), resultSet.getDouble("totalPrice"),
                    resultSet.getString("status"), client, null);
            containerOrderList.add(this.containerOrder);
        }

        // Get the OrderDetails for each ContainerOrder object
        for (ContainerOrder newContainerOrder : containerOrderList) {
            // OrderDetail to set
            OrderDetail orderDetail = new OrderDetail();
            // Set the orderID
            orderDetail.setContainerOrder(newContainerOrder);
            OrderDetailsFacade orderDetailsFacade = new OrderDetailsFacade(orderDetail);
            newContainerOrder.setOrderDetailList(orderDetailsFacade.loadContainerOrderOrderDetails());
        }
        disconnect();
        return containerOrderList;
    }

    public ArrayList<ContainerOrder> loadClientContainerOrders() throws SQLException {
        connect();
        ArrayList<ContainerOrder> clientContainerOrderList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Users`.*, `Clients`.*, `ContainerOrders`.`orderID`, "
                + "`ContainerOrders`.`dateAdded`, `ContainerOrders`.`totalPrice`,"
                + " `ContainerOrders`.`status`, `ContainerOrders`.`clientID` FROM "
                + " `Users` INNER JOIN `Clients` ON `Users`.`userID` = `Clients`.`clientID`"
                + " INNER JOIN `ContainerOrders` ON `Clients`.`clientID` = "
                + "`ContainerOrders`.`clientID` WHERE `Users`.`isActive` = \"" + 1
                + "\" AND `ContainerOrders`.`clientID` = \"" + containerOrder.getClient().
                getUserID() + "\" ORDER BY `ContainerOrders`.`orderID` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create new client
            Client client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("address"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("plotName"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getInt("wantsToCancel"));

            this.containerOrder = new ContainerOrder(resultSet.getInt("orderID"),
                    resultSet.getLong("dateAdded"), resultSet.getDouble("totalPrice"),
                    resultSet.getString("status"), client, null);
            clientContainerOrderList.add(this.containerOrder);
        }

        // Get the OrderDetails for each ContainerOrder object
        for (ContainerOrder newContainerOrder : clientContainerOrderList) {
            // OrderDetail to set
            OrderDetail orderDetail = new OrderDetail();
            // Set the orderID
            orderDetail.setContainerOrder(newContainerOrder);
            OrderDetailsFacade orderDetailsFacade = new OrderDetailsFacade(orderDetail);
            newContainerOrder.setOrderDetailList(orderDetailsFacade.loadContainerOrderOrderDetails());
        }
        disconnect();
        return clientContainerOrderList;
    }

    public int approveContainerOrder() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `ContainerOrders` SET `status` = \"Approved\""
                + " WHERE `ContainerOrders`.`orderID` = \""
                + containerOrder.getOrderID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int cancelContainerOrder() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `ContainerOrders` SET `status` = \"Cancelled\""
                + " WHERE `ContainerOrders`.`orderID` = \""
                + containerOrder.getOrderID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }
    
    public int clearContainerOrder() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `ContainerOrders` SET `status` = \"Delivered\""
                + " WHERE `ContainerOrders`.`orderID` = \""
                + containerOrder.getOrderID() + "\"";
        int result = statement.executeUpdate(query);System.out.println(query);
        disconnect();
        return result;
    }

    
    public int removeContainerOrder() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "DELETE FROM `ContainerOrders` WHERE `orderID` = \""
                + this.containerOrder.getOrderID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public synchronized int insertContainerOrder() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `ContainerOrders`(`dateAdded`, `totalPrice`, "
                + " `status`, `clientID`) VALUES(\"" + containerOrder.getDateAdded()
                + " \", \"" + containerOrder.getTotalPrice() + "\", \""
                + containerOrder.getStatus() + "\", \""
                + containerOrder.getClient().getUserID() + "\")";
        int orderResult = statement.executeUpdate(query);
        int orderDetailResult = 0;
        if (orderResult == 1) {

            /* Load and set the orderID for the last ContainerOrder to be inserted */
            query = "SELECT `orderID` FROM `ContainerOrders` ORDER BY `orderID`"
                    + " DESC LIMIT 1";
            ResultSet resultSet = statement.executeQuery(query);
            int orderID = 0; // To store orderID
            if (resultSet.next()) {
                orderID = resultSet.getInt("orderID");
                containerOrder.setOrderID(orderID);
            }
            /* Load and set the orderID for the last ContainerOrder to be inserted */

            // Insert the orderDetail objects
            for (OrderDetail orderDetail : containerOrder.getOrderDetailList()) {
                // Set the orderID
                orderDetail.setContainerOrder(containerOrder);
                // Add the corresponding OrderDetail objects
                OrderDetailsFacade orderDetailsFacade = new OrderDetailsFacade(orderDetail);
                orderDetailResult = orderDetailsFacade.insertOrderDetail();

            }

        }
        disconnect();
        return orderDetailResult;
    }

    public ArrayList<ContainerOrder> loadMonthlyUnapporovedClientContainerOrders(
            Client client, long pastDate) throws SQLException {
        connect();
        ArrayList<ContainerOrder> monthlyUnapprovedClientContainerOrderList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `ContainerOrders` WHERE `clientID` = \""
                + client.getUserID() + "\" AND"
                + " `dateAdded` > \"" + pastDate + "\" AND `status` = \"Delivered\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.containerOrder = new ContainerOrder(resultSet.getInt("orderID"),
                    resultSet.getLong("dateAdded"), resultSet.getDouble("totalPrice"),
                    resultSet.getString("status"), client, null);
            // Add the containerOrders to clientContainerOrderList
            monthlyUnapprovedClientContainerOrderList.add(this.containerOrder);
        }
        disconnect();
        return monthlyUnapprovedClientContainerOrderList;
    }

    public ContainerOrder getContainerOrder() {
        return containerOrder;
    }

    public void setContainerOrder(ContainerOrder containerOrder) {
        this.containerOrder = containerOrder;
    }

    public List<ContainerOrder> searchContainerOrderByOrderNumber(String searchTerm)
            throws SQLException {
        connect();
        ArrayList<ContainerOrder> containerOrderList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Users`.*, `Clients`.*, `ContainerOrders`.`orderID`,"
                + " `ContainerOrders`.`dateAdded`, `ContainerOrders`.`totalPrice`,"
                + " `ContainerOrders`.`status`, `ContainerOrders`.`clientID` FROM "
                + " `Users` INNER JOIN `Clients` ON `Users`.`userID` = `Clients`.`clientID`"
                + " INNER JOIN `ContainerOrders` ON `Clients`.`clientID` ="
                + " `ContainerOrders`.`clientID` WHERE `Users`.`isActive` = \""
                + 1 + "\" AND `ContainerOrders`.`orderID` = \"" + searchTerm + "\""
                + " ORDER BY `ContainerOrders`.`orderID` DESC";

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create new client
            Client client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("address"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("plotName"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getInt("wantsToCancel"));

            this.containerOrder = new ContainerOrder(resultSet.getInt("orderID"),
                    resultSet.getLong("dateAdded"), resultSet.getDouble("totalPrice"),
                    resultSet.getString("status"), client, null);
            containerOrderList.add(this.containerOrder);
        }

        // Get the OrderDetails for each ContainerOrder object
        for (ContainerOrder newContainerOrder : containerOrderList) {
            // OrderDetail to set
            OrderDetail orderDetail = new OrderDetail();
            // Set the orderID
            orderDetail.setContainerOrder(newContainerOrder);
            OrderDetailsFacade orderDetailsFacade = new OrderDetailsFacade(orderDetail);
            newContainerOrder.setOrderDetailList(orderDetailsFacade.loadContainerOrderOrderDetails());
        }
        disconnect();
        return containerOrderList;
    }

    public List<ContainerOrder> searchContainerOrderByFirstName(String searchTerm)
            throws SQLException {
        connect();
        ArrayList<ContainerOrder> containerOrderList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Users`.*, `Clients`.*, `ContainerOrders`.`orderID`, "
                + "`ContainerOrders`.`dateAdded`, `ContainerOrders`.`totalPrice`,"
                + " `ContainerOrders`.`status`, `ContainerOrders`.`clientID` FROM "
                + " `Users` INNER JOIN `Clients` ON `Users`.`userID` = `Clients`.`clientID`"
                + " INNER JOIN `ContainerOrders` ON `Clients`.`clientID` = "
                + "`ContainerOrders`.`clientID` WHERE `Users`.`isActive` = \""
                + 1 + "\" AND `Clients`.`firstName` = \"" + searchTerm + "\""
                + " ORDER BY `ContainerOrders`.`orderID` DESC";

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create new client
            Client client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("address"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("plotName"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getInt("wantsToCancel"));

            this.containerOrder = new ContainerOrder(resultSet.getInt("orderID"),
                    resultSet.getLong("dateAdded"), resultSet.getDouble("totalPrice"),
                    resultSet.getString("status"), client, null);
            containerOrderList.add(this.containerOrder);
        }

        // Get the OrderDetails for each ContainerOrder object
        for (ContainerOrder newContainerOrder : containerOrderList) {
            // OrderDetail to set
            OrderDetail orderDetail = new OrderDetail();
            // Set the orderID
            orderDetail.setContainerOrder(newContainerOrder);
            OrderDetailsFacade orderDetailsFacade = new OrderDetailsFacade(orderDetail);
            newContainerOrder.setOrderDetailList(orderDetailsFacade.loadContainerOrderOrderDetails());
        }
        disconnect();
        return containerOrderList;
    }

    public List<ContainerOrder> searchContainerOrderByLastName(String searchTerm)
            throws SQLException {
        connect();
        ArrayList<ContainerOrder> containerOrderList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Users`.*, `Clients`.*, `ContainerOrders`.`orderID`,"
                + " `ContainerOrders`.`dateAdded`, `ContainerOrders`.`totalPrice`,"
                + " `ContainerOrders`.`status`, `ContainerOrders`.`clientID` FROM "
                + " `Users` INNER JOIN `Clients` ON `Users`.`userID` = `Clients`.`clientID`"
                + " INNER JOIN `ContainerOrders` ON `Clients`.`clientID` = "
                + "`ContainerOrders`.`clientID` WHERE `Users`.`isActive` = \""
                + 1 + "\" AND `Clients`.`lastName` = \"" + searchTerm + "\""
                + " ORDER BY `ContainerOrders`.`orderID` DESC";

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create new client
            Client client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("address"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("plotName"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getInt("wantsToCancel"));

            this.containerOrder = new ContainerOrder(resultSet.getInt("orderID"),
                    resultSet.getLong("dateAdded"), resultSet.getDouble("totalPrice"),
                    resultSet.getString("status"), client, null);
            containerOrderList.add(this.containerOrder);
        }

        // Get the OrderDetails for each ContainerOrder object
        for (ContainerOrder newContainerOrder : containerOrderList) {
            // OrderDetail to set
            OrderDetail orderDetail = new OrderDetail();
            // Set the orderID
            orderDetail.setContainerOrder(newContainerOrder);
            OrderDetailsFacade orderDetailsFacade = new OrderDetailsFacade(orderDetail);
            newContainerOrder.setOrderDetailList(orderDetailsFacade.loadContainerOrderOrderDetails());
        }
        disconnect();
        return containerOrderList;
    }

    public List<ContainerOrder> searchContainerOrderByPlotName(String searchTerm)
            throws SQLException {
        connect();
        ArrayList<ContainerOrder> containerOrderList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Users`.*, `Clients`.*, `ContainerOrders`.`orderID`, "
                + "`ContainerOrders`.`dateAdded`, `ContainerOrders`.`totalPrice`,"
                + " `ContainerOrders`.`status`, `ContainerOrders`.`clientID` FROM "
                + " `Users` INNER JOIN `Clients` ON `Users`.`userID` = `Clients`.`clientID`"
                + " INNER JOIN `ContainerOrders` ON `Clients`.`clientID` = "
                + "`ContainerOrders`.`clientID` WHERE `Users`.`isActive` = \""
                + 1 + "\" AND `Clients`.`plotName` = \"" + searchTerm + "\""
                + " ORDER BY `ContainerOrders`.`orderID` DESC";

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create new client
            Client client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("address"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("plotName"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getInt("wantsToCancel"));

            this.containerOrder = new ContainerOrder(resultSet.getInt("orderID"),
                    resultSet.getLong("dateAdded"), resultSet.getDouble("totalPrice"),
                    resultSet.getString("status"), client, null);
            containerOrderList.add(containerOrder);
        }

        // Get the OrderDetails for each ContainerOrder object
        for (ContainerOrder newContainerOrder : containerOrderList) {
            // OrderDetail to set
            OrderDetail orderDetail = new OrderDetail();
            // Set the orderID
            orderDetail.setContainerOrder(newContainerOrder);
            OrderDetailsFacade orderDetailsFacade = new OrderDetailsFacade(orderDetail);
            newContainerOrder.setOrderDetailList(orderDetailsFacade.loadContainerOrderOrderDetails());
        }
        disconnect();
        return containerOrderList;
    }

    public List<ContainerOrder> searchContainerOrderByOrderNumber(
            String searchTerm, int clientID) throws SQLException {
        connect();
        ArrayList<ContainerOrder> containerOrderList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Users`.*, `Clients`.*, `ContainerOrders`.`orderID`,"
                + " `ContainerOrders`.`dateAdded`, `ContainerOrders`.`totalPrice`,"
                + " `ContainerOrders`.`status`, `ContainerOrders`.`clientID` FROM "
                + " `Users` INNER JOIN `Clients` ON `Users`.`userID` = `Clients`.`clientID`"
                + " INNER JOIN `ContainerOrders` ON `Clients`.`clientID` ="
                + " `ContainerOrders`.`clientID` WHERE `Users`.`isActive` = \""
                + 1 + "\" AND `ContainerOrders`.`orderID` = \"" + searchTerm + "\" "
                + " AND `Clients`.`clientID` = \"" + clientID + "\""
                + " ORDER BY `ContainerOrders`.`orderID` DESC";

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create new client
            Client client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("address"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("plotName"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getInt("wantsToCancel"));

            this.containerOrder = new ContainerOrder(resultSet.getInt("orderID"),
                    resultSet.getLong("dateAdded"), resultSet.getDouble("totalPrice"),
                    resultSet.getString("status"), client, null);
            containerOrderList.add(this.containerOrder);
        }

        // Get the OrderDetails for each ContainerOrder object
        for (ContainerOrder newContainerOrder : containerOrderList) {
            // OrderDetail to set
            OrderDetail orderDetail = new OrderDetail();
            // Set the orderID
            orderDetail.setContainerOrder(newContainerOrder);
            OrderDetailsFacade orderDetailsFacade = new OrderDetailsFacade(orderDetail);
            newContainerOrder.setOrderDetailList(orderDetailsFacade.loadContainerOrderOrderDetails());
        }
        disconnect();
        return containerOrderList;
    }

}
