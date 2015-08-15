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
import ncgms.controllers.MessageController;
import ncgms.entities.ContainerOrder;
import ncgms.daos.ContainerOrdersFacade;
import ncgms.daos.MessagesFacade;
import ncgms.entities.Message;
import ncgms.entities.User;
import ncgms.util.EmailSenderTask;
import ncgms.util.SMSSenderTask;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class AdminContainerOrderController implements Serializable {

    private ExecutorService executorService;

    private String searchBy = null;
    private String searchTerm = null;
    private String[] searchByArray = new String[]{"Order No", "Plot Name",
        "First Name", "Last Name"};

    private List<ContainerOrder> containerOrderList = new ArrayList<>();
    private List<ContainerOrder> viewableContainerOrderList = new ArrayList<>();

    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentContainerOrderIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    /**
     * Creates a new instance of AdminContainerOrderController
     */
    public AdminContainerOrderController() {
        initializeContainerOrderList();
    }

    private void initializeContainerOrderList() {
        try {
            // Initialize the variables for navigation
            currentContainerOrderIndex = 0;
            currentPage = 1;
            // Create a new ContainerOrdersFacade
            ContainerOrdersFacade containerOrdersFacade = new ContainerOrdersFacade();
            this.containerOrderList = containerOrdersFacade.loadAllContainerOrders();
            // Set the number of pages
            this.noOfPages = this.containerOrderList.size() / 10;
            // Set the last page
            if (this.containerOrderList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableContainerOrderList = new ArrayList<>();

            /* Initialize the containerOrders */
            int index;
            if (this.containerOrderList.size() >= 10) {// if there are more than 10 containerOrders

                // Set the first 10 containerOrders
                for (index = 0; index <= 9; index++) {
                    this.viewableContainerOrderList.add(this.containerOrderList.get(index));
                }
                currentContainerOrderIndex = index;
            } else {
                this.viewableContainerOrderList = this.containerOrderList;
                currentContainerOrderIndex = this.viewableContainerOrderList.size();
            }
            /* Initialize the containerOrders */
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not populate continer order list",
                    "Could not populate continer order list");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void initializeResultList() {
        currentContainerOrderIndex = 0;
        currentPage = 1;
        this.noOfPages = this.containerOrderList.size() / 10;
        if (this.containerOrderList.size() % 10 > 0) {
            this.noOfPages = this.noOfPages + 1;
        }
        this.viewableContainerOrderList = new ArrayList<>();
        int index;
        if (this.containerOrderList.size() >= 10) {// if there are more than 10 containerOrders

            // Set the first 10 containerOrders
            for (index = 0; index <= 9; index++) {
                this.viewableContainerOrderList.add(this.containerOrderList.get(index));
            }
            currentContainerOrderIndex = index;
        } else {
            this.viewableContainerOrderList = this.containerOrderList;
            currentContainerOrderIndex = this.viewableContainerOrderList.size();
        }
    }

    public void nextContainerOrderPage() {

        // Check if index is more than containerOrderList size
        if (currentContainerOrderIndex < this.containerOrderList.size()) {// You are not at the last page
            int nextPage = currentPage + 1;
            // Check if next page is last page
            if (nextPage == this.noOfPages) {// Load the remaining containerOrders
                // Clear list first
                this.viewableContainerOrderList.clear();
                for (; currentContainerOrderIndex < this.containerOrderList.size();) {
                    // Add the containerOrders
                    this.viewableContainerOrderList.add(this.containerOrderList.
                            get(currentContainerOrderIndex++));
                }
                // Move to next page
                currentPage++;

            } else {// Load next 10 containerOrders
                // Clear list first
                this.viewableContainerOrderList.clear();
                for (int i = 0; i <= 9; i++) {
                    // Add the containerOrder
                    this.viewableContainerOrderList.add(this.containerOrderList.
                            get(currentContainerOrderIndex++));
                }

                // Move to next page            
                currentPage++;
            }
        } else {// You are at the last page
            // Set currentContainerIndex to the size of the containerOrderList
            currentContainerOrderIndex = this.containerOrderList.size();
        }
    }

    public void previousContainerOrderPage() {

        if (currentContainerOrderIndex < this.containerOrderList.size()) {// You are not at the last page
            int previousPage = currentPage - 1;
            // Check if previous page is the first page
            if (previousPage < 1) {
                // Pass
            } else {
                //Move back 20 elements
                currentContainerOrderIndex -= 20;
                // Clear list first
                this.viewableContainerOrderList.clear();

                // Load previous ten containerOrders
                for (int i = 0; i <= 9; i++) {

                    // Add the containerOrders
                    this.viewableContainerOrderList.add(this.containerOrderList.
                            get(currentContainerOrderIndex++));

                }
                // Move to previous page            
                currentPage--;
            }

        } else {// You are at the last page
            //Move back 10 + last page elements
            currentContainerOrderIndex -= (10 + this.viewableContainerOrderList.size());
            // Clear list first
            this.viewableContainerOrderList.clear();

            // Load previous ten containerOrders
            for (int i = 0; i <= 9; i++) {

                // Add the containerOrders
                this.viewableContainerOrderList.add(this.containerOrderList.
                        get(currentContainerOrderIndex++));

            }
            // Move to previous page            
            currentPage--;
        }

    }

    public void approveContainerOrder(ContainerOrder containerOrder) {
        try {
            this.executorService = Executors.newCachedThreadPool();
            // Create a new ContainerOrdersFacade
            ContainerOrdersFacade containerOrdersFacade = new ContainerOrdersFacade(containerOrder);
            int result = containerOrdersFacade.approveContainerOrder();
            if (result == 1) {
                // Set the order as approved
                containerOrder.setStatus("Approved");
                // Notify client----------------------------------------------//
                String mobileMessage = "Hello "
                        + containerOrder.getClient().getFirstName() + " "
                        + containerOrder.getClient().getLastName()
                        + ". Your order with Order ID: " + containerOrder.getOrderID()
                        + " has been approved. The items will be delivered within 1-7"
                        + " business days. Thank you. NCGMS Inc.";
                this.executorService.execute(new SMSSenderTask(containerOrder.getClient().getPhone(),
                        mobileMessage));
                this.executorService.execute(new EmailSenderTask(containerOrder.getClient().getEmail(),
                        "Order Approval", mobileMessage));
                String systemMessage = "Hello "
                        + containerOrder.getClient().getFirstName() + " "
                        + containerOrder.getClient().getLastName()
                        + ". Your order with Order ID: " + containerOrder.getOrderID()
                        + " has been approved. The items will be delivered during the next "
                        + " garbage pick up. Thank you. NCGMS Inc.";
                Message newMessage = new Message(systemMessage, new Date().getTime(), 
                       0, new User(containerOrder.getClient().getClientID(), null, null, 1));
                MessagesFacade messagesFacade = new MessagesFacade(newMessage);
                messagesFacade.insertMessage();
                //--------------------------------------------------------------//
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not approve order. Please contact the system administrator.",
                    "Could not approve order. Please contact the system administrator.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminContainerOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cancelContainerOrder(ContainerOrder containerOrder) {
        try {
            this.executorService = Executors.newCachedThreadPool();
            ContainerOrdersFacade containerOrderFacade = new ContainerOrdersFacade(containerOrder);
            int result = containerOrderFacade.cancelContainerOrder();
            if (result == 1) {
                containerOrder.setStatus("Canceled");
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Order successfully canceled.",
                        "Order successfully canceled.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                // Notify client----------------------------------------------//
                String mobileMessage = "Hello "
                        + containerOrder.getClient().getFirstName() + " "
                        + containerOrder.getClient().getLastName()
                        + ". Your order with Order ID: " + containerOrder.getOrderID()
                        + " has been canceled, Please contact us for any further follow up."
                        + " Thank you. NCGMS Inc.";
                this.executorService.execute(new SMSSenderTask(containerOrder.getClient().getPhone(),
                        mobileMessage));

                this.executorService.execute(new EmailSenderTask(containerOrder.getClient().getEmail(),
                        "Order Cancelation", mobileMessage));
                
                String systemMessage = "Hello "
                        + containerOrder.getClient().getFirstName() + " "
                        + containerOrder.getClient().getLastName()
                        + ". Your order with Order ID: " + containerOrder.getOrderID()
                        + " has been canceled, Please contact us for any further follow up."
                        + " Thank you. NCGMS Inc.";
                Message newMessage = new Message(systemMessage, new Date().getTime(), 
                       0, new User(containerOrder.getClient().getClientID(), null, null, 1));
                MessagesFacade messagesFacade = new MessagesFacade(newMessage);
                messagesFacade.insertMessage();
                //--------------------------------------------------------------//
                
            } else {
                //
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not cancel order. Please contact the system administrator.",
                    "Could not cancel order. Please contact the system administrator.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminContainerOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void clearContainerOrder(ContainerOrder containerOrder) {
        try {
            ContainerOrdersFacade containerOrderFacade = new ContainerOrdersFacade(containerOrder);
            containerOrderFacade.clearContainerOrder();
            containerOrder.setStatus("Delivered");
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not clear order. Please contact the system administrator.",
                    "Could not clear order. Please contact the system administrator.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminContainerOrderController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    

    public void removeContainerOrder(ContainerOrder containerOrder) {
        try {
            ContainerOrdersFacade containerOrderFacade = new ContainerOrdersFacade(containerOrder);
            int result = containerOrderFacade.removeContainerOrder();
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Order successfully trashed.",
                        "Order successfully trashed.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            } else {
                //
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not approve order. Please contact the system administrator.",
                    "Could not approve order. Please contact the system administrator.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminContainerOrderController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Get current page
            int page = this.currentPage;
            // Initialize container order list
            this.initializeContainerOrderList();
            // Go back to current page
            for (int i = 1; i < page; i++) {
                nextContainerOrderPage();
            }

        }

    }

    public void refreshContainerOrders() {
        this.initializeContainerOrderList();
    }

    public void searchContainerOrders() {
        try {
            ContainerOrdersFacade contaierOrdersFacade = new ContainerOrdersFacade();

            switch (this.searchBy) {
                case "Order No":
                    this.containerOrderList = contaierOrdersFacade.
                            searchContainerOrderByOrderNumber(searchTerm);
                    break;
                case "Plot Name":
                    this.containerOrderList = contaierOrdersFacade.
                            searchContainerOrderByPlotName(searchTerm);
                    break;
                case "First Name":
                    this.containerOrderList = contaierOrdersFacade.
                            searchContainerOrderByFirstName(searchTerm);
                    break;
                case "Last Name":
                    this.containerOrderList = contaierOrdersFacade.
                            searchContainerOrderByLastName(searchTerm);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminContainerOrderController.class.getName()).
                    log(Level.SEVERE, null, ex);
        } finally {
            if (this.containerOrderList.isEmpty()) {
                this.initializeContainerOrderList();
                FacesMessage facesMessage = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "No Results",
                        "No Results");
                FacesContext.getCurrentInstance().addMessage(
                        "container_orders_form:search_button",
                        facesMessage);
            } else {
                this.searchTerm = null;
                initializeResultList();
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

    public List<ContainerOrder> getContainerOrderList() {
        return containerOrderList;
    }

    public void setContainerOrderList(List<ContainerOrder> containerOrderList) {
        this.containerOrderList = containerOrderList;
    }

    public List<ContainerOrder> getViewableContainerOrderList() {
        return viewableContainerOrderList;
    }

    public void setViewableContainerOrderList(List<ContainerOrder> viewableContainerOrderList) {
        this.viewableContainerOrderList = viewableContainerOrderList;
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

    public int getCurrentContainerOrderIndex() {
        return currentContainerOrderIndex;
    }

    public void setCurrentContainerOrderIndex(int currentContainerOrderIndex) {
        this.currentContainerOrderIndex = currentContainerOrderIndex;
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
