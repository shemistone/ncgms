/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import ncgms.entities.Message;
import ncgms.entities.User;
import ncgms.daos.InvoicesFacade;
import ncgms.daos.MessagesFacade;
import ncgms.daos.UsersFacade;

/**
 *
 * @author root
 */
public class InvoicerTask implements Runnable {

    private boolean ran = false;
    private final String configPath = "/etc/ncgms/invoice.conf";

    @Override
    public void run() {
        try {
            // Check if the routine has already run today
            ran = CharacterStream.readLine(configPath).equals("1");
        } catch (IOException ex) {
            Logger.getLogger(InvoicerTask.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!ran) {
            try {
                InvoicesFacade invoicesFacade = new InvoicesFacade();
                int result = invoicesFacade.createMonthlyInvoicesForAllClients();
                if (result > 0) {
                    // Set invoice routine as ran
                    CharacterStream.write(configPath, "1");
                    // Inform the logistics manager
                    User user = new User("admin", null, 1);
                    UsersFacade usersFacade = new UsersFacade();
                    String messageContent = "Monthly invoices processed at "
                            + new Date().toString() + " for all active clients. NCGMS Inc.";
                    Message message = new Message(0, messageContent, new Date().getTime(), 0,
                            new User(0, null, null, usersFacade.loadUserID()));
                    MessagesFacade messagesFacade = new MessagesFacade(message);
                    messagesFacade.insertMessage();
                    // Send sms to client
                    SMSSender.sendSmsSynchronous("0721868821", messageContent);

                } else {
                    // Pass
                }
            } catch (SQLException ex) {
                Logger.getLogger(InvoicerTask.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException fnfe) {
                Path path = Paths.get(configPath);
                try {
                    Files.createFile(path);
                } catch (IOException ex) {
                    Logger.getLogger(InvoicerTask.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(InvoicerTask.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(InvoicerTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                // Sleep for the next 24 hours
                Thread.sleep(864000000l);
                // Set invoice routine as ran
                CharacterStream.write(configPath, "0");

            } catch (InterruptedException | IOException ex) {
                Logger.getLogger(InvoicerTask.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
