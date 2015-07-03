/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.client.controllers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import ncgms.controllers.LogInLogOutController;
import ncgms.entities.Invoice;
import ncgms.entities.User;
import ncgms.daos.InvoicesFacade;
import ncgms.daos.UsersFacade;
import ncgms.entities.Client;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class ClientInvoiceController implements Serializable {

    private List<Invoice> invoiceList = new ArrayList<>();
    private List<Invoice> viewableInvoiceList = new ArrayList<>();
    private boolean noInvoicesRendered = false;

    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentInvoiceIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    /**
     * Creates a new instance of InvoiceController
     */
    public ClientInvoiceController() {
        initializeInvoiceList();
    }

    private void initializeInvoiceList() {
        try {
            // Initialize the variables for navigation
            currentInvoiceIndex = 0;
            currentPage = 1;
            // Create user object for current client            
            Client client = (Client) new LogInLogOutController().getClientFromSession();

            // Creata a new Invoice object
            Invoice invoice = new Invoice();
            // Set the clientID
            invoice.setClient(client);

            InvoicesFacade invoicesFacade = new InvoicesFacade(invoice);
            invoiceList = invoicesFacade.loadClientInvoices();

            // Set the number of pages
            this.noOfPages = this.invoiceList.size() / 10;
            // Set the last page
            if (this.invoiceList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableInvoiceList = new ArrayList<>();

            /* Initialize the invoicess */
            int index;
            if (this.invoiceList.size() >= 10) {// if there are more than 10 invoices

                // Set the first 10 invoices
                for (index = 0; index <= 9; index++) {
                    this.viewableInvoiceList.add(this.invoiceList.get(index));
                }
                currentInvoiceIndex = index;
            } else {
                this.viewableInvoiceList = this.invoiceList;
                currentInvoiceIndex = this.viewableInvoiceList.size();
            }
            /* Initialize the invoices */

        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not intialize invoice list.",
                    "Could not initialize invoice list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nextInvoicePage() {

        // Check if index is more than invoiceList size
        if (currentInvoiceIndex < this.invoiceList.size()) {// You are not at the last page
            int nextPage = currentPage + 1;
            // Check if next page is last page
            if (nextPage == this.noOfPages) {// Load the remaining invoices
                // Clear list first
                this.viewableInvoiceList.clear();
                for (; currentInvoiceIndex < this.invoiceList.size();) {
                    // Add the invoicess
                    this.viewableInvoiceList.add(this.invoiceList.get(currentInvoiceIndex++));
                }
                // Move to next page
                currentPage++;

            } else {// Load next 10 incvoices
                // Clear list first
                this.viewableInvoiceList.clear();
                for (int i = 0; i <= 9; i++) {
                    // Add the invoices
                    this.viewableInvoiceList.add(this.invoiceList.get(currentInvoiceIndex++));
                }

                // Move to next page            
                currentPage++;
            }
        } else {// You are at the last page
            // Set currentInvoiceIndex to the size of the invoiceList
            currentInvoiceIndex = this.invoiceList.size();
        }
    }

    public void previousInvoicePage() {

        if (currentInvoiceIndex < this.invoiceList.size()) {// You are not at the last page
            int previousPage = currentPage - 1;
            // Check if previous page is the first page
            if (previousPage < 1) {
                // Pass
            } else {
                //Move back 20 elements
                currentInvoiceIndex -= 20;
                // Clear list first
                this.viewableInvoiceList.clear();

                // Load previous ten invoices
                for (int i = 0; i <= 9; i++) {

                    // Add the invoices
                    this.viewableInvoiceList.add(this.invoiceList.get(currentInvoiceIndex++));

                }
                // Move to previous page            
                currentPage--;
            }

        } else {// You are at the last page
            //Move back 10 + last page elements
            currentInvoiceIndex -= (10 + this.viewableInvoiceList.size());
            // Clear list first
            this.viewableInvoiceList.clear();

            // Load previous ten invoices
            for (int i = 0; i <= 9; i++) {

                // Add the invoices
                this.viewableInvoiceList.add(this.invoiceList.get(currentInvoiceIndex++));

            }
            // Move to previous page            
            currentPage--;
        }

    }

    public void refreshInvoices() {
        this.initializeInvoiceList();;
    }

    public List<Invoice> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    public List<Invoice> getViewableInvoiceList() {
        return viewableInvoiceList;
    }

    public void setViewableInvoiceList(List<Invoice> viewableInvoiceList) {
        this.viewableInvoiceList = viewableInvoiceList;
    }

    public boolean isNoInvoicesRendered() {
        return invoiceList.isEmpty();
    }

    public void setNoInvoicesRendered(boolean noInvoicesRendered) {
        this.noInvoicesRendered = noInvoicesRendered;
    }

    public int getNoOfPages() {
        return noOfPages;
    }

    public void setNoOfPages(int noOfPages) {
        this.noOfPages = noOfPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentInvoiceIndex() {
        return currentInvoiceIndex;
    }

    public void setCurrentInvoiceIndex(int currentInvoiceIndex) {
        this.currentInvoiceIndex = currentInvoiceIndex;
    }

    public boolean isNextRendered() {
        return currentPage < noOfPages;
    }

    public void setNextRendered(boolean nextRendered) {
        this.nextRendered = nextRendered;
    }

    public boolean isPreviousRendered() {
        return currentPage != 1;
    }

    public void setPreviousRendered(boolean previousRendered) {
        this.previousRendered = previousRendered;
    }

}
