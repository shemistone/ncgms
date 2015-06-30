/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.admin.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import ncgms.entities.Client;
import ncgms.daos.ClientsFacade;
import ncgms.daos.SubcountiesFacade;
import ncgms.daos.TrucksFacade;
import ncgms.util.SMSSender;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class AdminClientController implements Serializable {

    private String searchBy = null;
    private String searchTerm = null;
    private String[] searchByArray = new String[]{"Plot Name", "First Name",
        "Last Name", "Subcounty", "Address", "Assigned Truck", "Phone Number", "Email"};
    private List<String> plateNumberList = new ArrayList<>();
    private Map<Integer, String> subcountyNamesMap = new HashMap<>();
    private List<Client> clientList = new ArrayList<>();
    private List<Client> viewableClientList = new ArrayList<>();

    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentClientIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    /**
     * Creates a new instance of ClientController
     */
    public AdminClientController() {
        initializeClientList();
    }

    private void initializeClientList() {
        try {
            // Initialize the variables for navigation
            currentClientIndex = 0;
            currentPage = 1;
            // Populate the plateNumberList
            TrucksFacade trucksFacade = new TrucksFacade();
            this.plateNumberList = trucksFacade.populatePlateNumberList();

            // Load subcounty names into map
            SubcountiesFacade subcountiesFacade = new SubcountiesFacade();
            this.subcountyNamesMap = subcountiesFacade.loadSubcountyNamesMap();

            ClientsFacade clientsFacade = new ClientsFacade();
            this.clientList = clientsFacade.loadAllClients();

            // Set the number of pages
            this.noOfPages = this.clientList.size() / 10;
            // Set the last page
            if (this.clientList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableClientList = new ArrayList<>();

            /* Initialize the clients */
            int index;
            if (this.clientList.size() >= 10) {// if there are more than 10 clients

                // Set the first 10 clients
                for (index = 0; index <= 9; index++) {
                    this.viewableClientList.add(this.clientList.get(index));
                }
                currentClientIndex = index;
            } else {
                this.viewableClientList = this.clientList;
                currentClientIndex = this.viewableClientList.size();
            }
            /* Initialize the clients */

        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not initialize client list.",
                    "Could not initialize client list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void initializeResultList() {
        try {
            // Initialize the variables for navigation
            currentClientIndex = 0;
            currentPage = 1;
            // Populate the plateNumberList
            TrucksFacade trucksFacade = new TrucksFacade();
            this.plateNumberList = trucksFacade.populatePlateNumberList();

            // Load subcounty names into map
            SubcountiesFacade subcountiesFacade = new SubcountiesFacade();
            this.subcountyNamesMap = subcountiesFacade.loadSubcountyNamesMap();

            // Set the number of pages
            this.noOfPages = this.clientList.size() / 10;
            // Set the last page
            if (this.clientList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableClientList = new ArrayList<>();

            /* Initialize the clients */
            int index;
            if (this.clientList.size() >= 10) {// if there are more than 10 clients

                // Set the first 10 clients
                for (index = 0; index <= 9; index++) {
                    this.viewableClientList.add(this.clientList.get(index));
                }
                currentClientIndex = index;
            } else {
                this.viewableClientList = this.clientList;
                currentClientIndex = this.viewableClientList.size();
            }
            /* Initialize the clients */

        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not initialize result list.",
                    "Could not initialize result list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nextClientPage() {

        // Check if index is more than clientList size
        if (currentClientIndex < this.clientList.size()) {// You are not at the last page
            int nextPage = currentPage + 1;
            // Check if next page is last page
            if (nextPage == this.noOfPages) {// Load the remaining clients
                // Clear list first
                this.viewableClientList.clear();
                for (; currentClientIndex < this.clientList.size();) {
                    // Add the clients
                    this.viewableClientList.add(this.clientList.get(currentClientIndex++));
                }
                // Move to next page
                currentPage++;

            } else {// Load next 10 clients
                // Clear list first
                this.viewableClientList.clear();
                for (int i = 0; i <= 9; i++) {
                    // Add the clients
                    this.viewableClientList.add(this.clientList.get(currentClientIndex++));
                }

                // Move to next page            
                currentPage++;
            }
        } else {// You are at the last page
            // Set currentClientIndex to the size of the clientList
            currentClientIndex = this.clientList.size();
        }
    }

    public void previousClientPage() {

        if (currentClientIndex < this.clientList.size()) {// You are not at the last page
            int previousPage = currentPage - 1;
            // Check if previous page is the first page
            if (previousPage < 1) {
                // Pass
            } else {
                //Move back 20 elements
                currentClientIndex -= 20;
                // Clear list first
                this.viewableClientList.clear();

                // Load previous ten clients
                for (int i = 0; i <= 9; i++) {

                    // Add the clients
                    this.viewableClientList.add(this.clientList.get(currentClientIndex++));

                }
                // Move to previous page            
                currentPage--;
            }

        } else {// You are at the last page
            //Move back 10 + last page elements
            currentClientIndex -= (10 + this.viewableClientList.size());
            // Clear list first
            this.viewableClientList.clear();

            // Load previous ten clients
            for (int i = 0; i <= 9; i++) {

                // Add the clients
                this.viewableClientList.add(this.clientList.get(currentClientIndex++));

            }
            // Move to previous page            
            currentPage--;
        }

    }
    
    public void acceptAppliaction(Client client) {
        try {
            // Create a new ClientsFacade
            ClientsFacade clientsFacade = new ClientsFacade(client);
            int result = clientsFacade.approveApplication();
            if (result == 1) {
                // Set the client as active
                client.setIsActive(1);
                SMSSender.sendSmsSynchronous(client.getPhone(), "Hello "
                        + client.getFirstName() + " " + client.getLastName()
                        + ". Your application has been accepted,"
                        + " you may now log in. Thank you for your business support. NCGMS Inc.");
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not approve application.",
                    "Could not approve application.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeClient(Client client) {
        try {
            ClientsFacade clientsFacade = new ClientsFacade(client);
            int result = clientsFacade.removeClient();
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Client successfully removed.",
                        "Client successfully removed.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not remove client.",
                    "Could not remove client.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Get current page
            int page = this.currentPage;
            // Initialize client list
            this.initializeClientList();
            // Go back to current page
            for(int i = 1; i < page; i++){
                nextClientPage();
            }

        }

    }

    public void rejectApplication(Client client) {
        this.removeClient(client);
    }

    public void searchClients() {
        try {
            System.out.print(searchTerm);
            ClientsFacade clientsFacade = new ClientsFacade();

            switch (this.searchBy) {
                case "Plot Name":
                    this.clientList = clientsFacade.searchClientByPlotName(searchTerm);
                    break;
                case "First Name":
                    this.clientList = clientsFacade.searchClientByFirstName(searchTerm);
                    break;
                case "Last Name":
                    this.clientList = clientsFacade.searchClientByLastName(searchTerm);
                    break;
                case "Subcounty":
                    this.clientList = clientsFacade.searchClientBySubcounty(searchTerm);
                    break;
                case "Address":
                    this.clientList = clientsFacade.searchClientByAddress(searchTerm);
                    break;
                case "Assigned Truck":
                    this.clientList = clientsFacade.searchClientByAssignedTruck(searchTerm);
                    break;
                case "Phone Number":
                    this.clientList = clientsFacade.searchClientByPhoneNumber(searchTerm);
                    break;
                case "Email":
                    this.clientList = clientsFacade.searchClientByEmail(searchTerm);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (this.clientList.isEmpty()) {
                this.initializeClientList();
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "No Results",
                        "No Results");
                FacesContext.getCurrentInstance().addMessage("clients_form:search_button",
                        facesMessage);
            } else {
                //this.searchTerm = null;
                initializeResultList();
            }
        }
    }

    public void editClient(Client client) {
        client.setEditable(true);
    }

    public void saveClientChanges(Client client) {
        try {
            // Update
            client.setPlateNumber(client.getPlateNumber());
            ClientsFacade clientsFacade = new ClientsFacade();
            int result = clientsFacade.updateClient(client);
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        client.getPlotName() + " updated.",
                        client.getPlotName() + " updated.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);

            } else {

            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not update client.",
                    "Could not update client.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            
            Logger.getLogger(AdminInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            client.setEditable(false);
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

    public void refreshClients() {
        this.initializeClientList();
    }

    public List<String> getPlateNumberList() {
        return plateNumberList;
    }

    public void setPlateNumberList(List<String> plateNumberList) {
        this.plateNumberList = plateNumberList;
    }

    public Map<Integer, String> getSubcountyNamesMap() {
        return subcountyNamesMap;
    }

    public void setSubcountyNamesMap(Map<Integer, String> subcountyNamesMap) {
        this.subcountyNamesMap = subcountyNamesMap;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public List<Client> getViewableClientList() {
        return viewableClientList;
    }

    public void setViewableClientList(List<Client> viewableClientList) {
        this.viewableClientList = viewableClientList;
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

    public int getCurrentClientIndex() {
        return currentClientIndex;
    }

    public void setCurrentClientIndex(int currentClientIndex) {
        this.currentClientIndex = currentClientIndex;
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
