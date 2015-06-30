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
import ncgms.entities.ContainerOrder;
import ncgms.entities.User;
import ncgms.daos.ContainerOrdersFacade;
import ncgms.daos.UsersFacade;
import ncgms.entities.Client;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class ClientContainerOrderController implements Serializable{

   private List<ContainerOrder> containerOrderList = new ArrayList<>();
    private List<ContainerOrder> viewableContainerOrderList = new ArrayList<>();
    private boolean noContainerOrdersRendered = false;

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
    public ClientContainerOrderController() {
        initializeContainerOrderList();
    }

    private void initializeContainerOrderList(){
        try {
            // Initialize the variables for navigation
            currentContainerOrderIndex = 0;
            currentPage = 1;
            // Create user object for current client            
            Client client = (Client) new LogInLogOutController().getClientFromSession();
            
            // Create a new containerOrder and set the clientID
            ContainerOrder containerOrder = new ContainerOrder();
            containerOrder.setClientID(client.getClientID());
            
            // Create a new ContainerOrdersFacade
            ContainerOrdersFacade containerOrdersFacade = new ContainerOrdersFacade(containerOrder);
            this.containerOrderList = containerOrdersFacade.loadClientContainerOrders();
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
                    "Could not initialize container order list.",
                    "Could not initialize container orders list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientContainerOrderController.class.getName()).log(Level.SEVERE, null, ex);
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

    
    public void refreshContainerOrders(){
        this.initializeContainerOrderList();;
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

    public boolean isNoContainerOrdersRendered() {
        return containerOrderList.isEmpty();
    }

    public void setNoContainerOrdersRendered(boolean noContainerOrdersRendered) {
        this.noContainerOrdersRendered = noContainerOrdersRendered;
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
