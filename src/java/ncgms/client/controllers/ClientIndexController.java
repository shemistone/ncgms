/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.client.controllers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import ncgms.controllers.LogInLogOutController;
import ncgms.entities.Client;
import ncgms.entities.Invoice;
import ncgms.entities.Message;
import ncgms.entities.User;
import ncgms.daos.ClientsFacade;
import ncgms.daos.InvoicesFacade;
import ncgms.daos.MessagesFacade;
import ncgms.daos.UsersFacade;

/**
 *
 * @author root
 */
@ManagedBean
@RequestScoped
public class ClientIndexController implements Serializable {

    /**
     * Creates a new instance of IndexController
     */
    public ClientIndexController() {
    }

    public void cancelService() {
        try {
            UsersFacade usersFacade = new UsersFacade();

            User user = new LogInLogOutController().getUserFromSession();
            Client client = new Client();
            client.setUserID(user.getUserID());
            // Check if client has any invoices
            Invoice invoice = new Invoice();
            invoice.setClient((Client) user);
            InvoicesFacade invoicesFacade = new InvoicesFacade(invoice);
            if (!invoicesFacade.loadClientInvoices().isEmpty()) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Your have some uncleared invoices. Please clear them and try again.",
                        "Your have some uncleared invoices. Please clear them and try again");
                FacesContext.getCurrentInstance().addMessage("cancel_service_form", facesMessage);
                return;
            }

            ClientsFacade clientsFacade = new ClientsFacade(client);
            int clientResult = clientsFacade.cancelService();

            if (clientResult == 1) {
                // Inform the Admin
                // Get all client attributes
                client = clientsFacade.searchClientByClientID(user.getUserID());
                Message message = new Message(0, client.getPlotName()
                        + " wants to cancel garbage collection services",
                        new Date().getTime(), 0, new User(usersFacade.loadAdminUserID(), 
                                null, null, 0));
                MessagesFacade messagesFacade = new MessagesFacade(message);
                messagesFacade.insertMessage();
                // Log user out
                new LogInLogOutController().logOut();
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Your account has been deactivated.",
                        "Your account has been deactivated.");
                FacesContext.getCurrentInstance().addMessage("cancel_service_form", facesMessage);
            } else {
                // Pass
            }

        } catch (SQLException ex) {
            Logger.getLogger(ClientIndexController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String toHome() {
        return "/client/index?faces-redirect=true";
    }

    public String toComplaint() {
        return "/client/complaints?faces-redirect=true";
    }

    public String toContainer() {
        return "/client/containers?faces-redirect=true";
    }

    public String toInvoice() {
        return "/client/invoices?faces-redirect=true";
    }

    public String toMessage() {
        return "/client/messages?faces-redirect=true";
    }

    public String toContainerOrder() {
        return "/client/container_orders?faces-redirect=true";
    }

}
