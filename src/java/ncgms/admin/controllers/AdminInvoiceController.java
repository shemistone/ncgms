/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.admin.controllers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import ncgms.client.controllers.ClientInvoiceController;
import ncgms.entities.Invoice;
import ncgms.daos.InvoicesFacade;
import ncgms.util.EmailSenderTask;
import ncgms.util.SMSSenderTask;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class AdminInvoiceController implements Serializable {

    private ExecutorService executorService;
    private String searchBy = null;
    private String searchTerm = null;
    private String[] searchByArray = new String[]{
        "Invoice No", "Plot Name", "First Name", "Last Name"};

    private List<Invoice> invoiceList = new ArrayList<>();
    private List<Invoice> viewableInvoiceList = new ArrayList<>();

    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentInvoiceIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    /**
     * Creates a new instance of ClientInvoiceController
     */
    public AdminInvoiceController() {
        initializeInvoiceList();
    }

    private void initializeInvoiceList() {
        try {
            // Initialize the variables for navigation
            currentInvoiceIndex = 0;
            currentPage = 1;
            // Create a new InvoicesFacade object
            InvoicesFacade invoicesFacade = new InvoicesFacade();
            this.invoiceList = invoicesFacade.loadAllInvoices();

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
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not populate invoice list.",
                    "Could not populate invoice list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initializeResultList() {
        currentInvoiceIndex = 0;
        currentPage = 1;
        this.noOfPages = this.invoiceList.size() / 10;
        if (this.invoiceList.size() % 10 > 0) {
            this.noOfPages = this.noOfPages + 1;
        }
        this.viewableInvoiceList = new ArrayList<>();
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
        this.initializeInvoiceList();
    }

    public void searchInvoices() {
        try {
            InvoicesFacade invoicesFacade = new InvoicesFacade();

            switch (this.searchBy) {
                case "Invoice No":
                    this.invoiceList = invoicesFacade.searchInvoiceByInvoiceNumber(searchTerm);
                    break;
                case "Plot Name":
                    this.invoiceList = invoicesFacade.searchInvoiceByPlotName(searchTerm);
                    break;
                case "First Name":
                    this.invoiceList = invoicesFacade.searchInvoiceByFirstName(searchTerm);
                    break;
                case "Last Name":
                    this.invoiceList = invoicesFacade.searchInvoiceByLastName(searchTerm);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (this.invoiceList.isEmpty()) {
                this.initializeInvoiceList();
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "No Results",
                        "No Results");
                FacesContext.getCurrentInstance().addMessage("invoices_form:search_button",
                        facesMessage);
            } else {
                this.searchTerm = null;
                initializeResultList();
            }
        }
    }

    public void editInvoice(Invoice invoice) {
        invoice.setEditable(true);
    }

    public void saveInvoiceChanges(Invoice invoice) {
        try {
            this.executorService = Executors.newCachedThreadPool();
            int result = 0;
            if (invoice.getAmountPaid() > 0) {
                // Update balance and date paid
                invoice.setBalance(invoice.getBalance() - invoice.getAmountPaid());
                invoice.setDatePaid(new Date().getTime());
                InvoicesFacade invoicesFacade = new InvoicesFacade();
                result = invoicesFacade.updateInvoice(invoice);
                if (result == 1) {
                    FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Invoice No  " + invoice.getInvoiceID() + " updated.",
                            "Invoice No : " + invoice.getInvoiceID() + " updated.");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    // Notify client-------------------------------------------//
                    String message = "Hello " + invoice.getClient().getFirstName()
                            + ", Your invoice with invoice ID: " + invoice.getInvoiceID()
                            + " has been credited with KShs: " + invoice.getAmountPaid()
                            + ". NCGMS Inc.";
                    this.executorService.execute(new SMSSenderTask(invoice.getClient().getPhone(),
                            message));
                    this.executorService.execute(new EmailSenderTask(invoice.getClient().getEmail(),
                            "Payment Confirmation", message));
                    //---------------------------------------------------------//
                } else {
                    // Pass
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(AdminInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            invoice.setEditable(false);
        }

    }

    public void removeInvoice(Invoice invoice) {
        try {
            // Check if invoice is cleared
            if (invoice.getBalance() > 0) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Cannot delete uncleared invoice.",
                        "Cannot delete uncleared invoice.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                return;
            }
            InvoicesFacade invoicesFacade = new InvoicesFacade(invoice);
            int result = invoicesFacade.removeInvoice();
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Invoice successfully removed.",
                        "Invoice successfully removed.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not remove invoice. Please contact the system administrator",
                    "Could not remove invoice. Please contact the system administrator");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Get current page
            int page = this.currentPage;
            // Initialize driver list
            this.initializeInvoiceList();
            // Go back to current page
            for (int i = 1; i < page; i++) {
                nextInvoicePage();
            }

        }

    }

    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String[] getSearchByArray() {
        return searchByArray;
    }

    public void setSearchByArray(String[] searchByArray) {
        this.searchByArray = searchByArray;
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
