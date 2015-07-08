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
import ncgms.entities.Tout;
import ncgms.daos.SubcountiesFacade;
import ncgms.daos.ToutsFacade;
import ncgms.daos.TrucksFacade;
import ncgms.util.SMSSender;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class AdminToutController implements Serializable {

    private String searchBy = null;
    private String searchTerm = null;
    private String[] searchByArray = new String[]{"First Name", "Last Name",
        "Subcounty", "Address", "Assigned Truck", "Phone Number", "Email"};
    private List<String> plateNumberList = new ArrayList<>();
    private Map<Integer, String> subcountyNamesMap = new HashMap<>();
    private List<Tout> toutList = new ArrayList<>();
    private List<Tout> viewableToutList = new ArrayList<>();
    private boolean noToutsRendered = false;

    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentToutIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    /**
     * Creates a new instance of ToutController
     */
    public AdminToutController() {
        initializeToutList();
    }

    private void initializeToutList() {
        try {
            // Initialize the variables for navigation
            currentToutIndex = 0;
            currentPage = 1;
            // Populate the plateNumberList
            TrucksFacade trucksFacade = new TrucksFacade();
            this.plateNumberList = trucksFacade.populatePlateNumberList();

            // Load subcounty names into map
            SubcountiesFacade subcountiesFacade = new SubcountiesFacade();
            //this.subcountyNamesMap = subcountiesFacade.loadSubcountyNamesMap();

            ToutsFacade toutsFacade = new ToutsFacade();
            this.toutList = toutsFacade.loadAllTouts();

            // Set the number of pages
            this.noOfPages = this.toutList.size() / 10;
            // Set the last page
            if (this.toutList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableToutList = new ArrayList<>();

            /* Initialize the touts */
            int index;
            if (this.toutList.size() >= 10) {// if there are more than 10 touts

                // Set the first 10 touts
                for (index = 0; index <= 9; index++) {
                    this.viewableToutList.add(this.toutList.get(index));
                }
                currentToutIndex = index;
            } else {
                this.viewableToutList = this.toutList;
                currentToutIndex = this.viewableToutList.size();
            }
            /* Initialize the touts */
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Could not initialize sanitation worker list.",
                    "Could not initialize sanitation worker list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminToutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initializeResultList() {
        try {
            // Initialize the variables for navigation
            currentToutIndex = 0;
            currentPage = 1;
            // Populate the plateNumberList
            TrucksFacade trucksFacade = new TrucksFacade();
            this.plateNumberList = trucksFacade.populatePlateNumberList();

            // Load subcounty names into map
            SubcountiesFacade subcountiesFacade = new SubcountiesFacade();
            //this.subcountyNamesMap = subcountiesFacade.loadSubcountyNamesMap();
            /*
             ToutsFacade toutsFacade = new ToutsFacade();
             this.toutList = toutsFacade.loadAllTouts();
             */
            // Set the number of pages
            this.noOfPages = this.toutList.size() / 10;
            // Set the last page
            if (this.toutList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableToutList = new ArrayList<>();

            /* Initialize the touts */
            int index;
            if (this.toutList.size() >= 10) {// if there are more than 10 touts

                // Set the first 10 touts
                for (index = 0; index <= 9; index++) {
                    this.viewableToutList.add(this.toutList.get(index));
                }
                currentToutIndex = index;
            } else {
                this.viewableToutList = this.toutList;
                currentToutIndex = this.viewableToutList.size();
            }
            /* Initialize the touts */
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not initialize sanitation worker list.",
                    "Could not initialize sanitation worker list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminToutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nextToutPage() {

        // Check if index is more than toutList size
        if (currentToutIndex < this.toutList.size()) {// You are not at the last page
            int nextPage = currentPage + 1;
            // Check if next page is last page
            if (nextPage == this.noOfPages) {// Load the remaining touts
                // Clear list first
                this.viewableToutList.clear();
                for (; currentToutIndex < this.toutList.size();) {
                    // Add the touts
                    this.viewableToutList.add(this.toutList.get(currentToutIndex++));
                }
                // Move to next page
                currentPage++;

            } else {// Load next 10 touts
                // Clear list first
                this.viewableToutList.clear();
                for (int i = 0; i <= 9; i++) {
                    // Add the touts
                    this.viewableToutList.add(this.toutList.get(currentToutIndex++));
                }

                // Move to next page            
                currentPage++;
            }
        } else {// You are at the last page
            // Set currentToutIndex to the size of the toutList
            currentToutIndex = this.toutList.size();
        }
    }

    public void previousToutPage() {

        if (currentToutIndex < this.toutList.size()) {// You are not at the last page
            int previousPage = currentPage - 1;
            // Check if previous page is the first page
            if (previousPage < 1) {
                // Pass
            } else {
                //Move back 20 elements
                currentToutIndex -= 20;
                // Clear list first
                this.viewableToutList.clear();

                // Load previous ten touts
                for (int i = 0; i <= 9; i++) {

                    // Add the touts
                    this.viewableToutList.add(this.toutList.get(currentToutIndex++));

                }
                // Move to previous page            
                currentPage--;
            }

        } else {// You are at the last page
            //Move back 10 + last page elements
            currentToutIndex -= (10 + this.viewableToutList.size());
            // Clear list first
            this.viewableToutList.clear();

            // Load previous ten touts
            for (int i = 0; i <= 9; i++) {

                // Add the touts
                this.viewableToutList.add(this.toutList.get(currentToutIndex++));

            }
            // Move to previous page            
            currentPage--;
        }

    }

     public void editTout(Tout tout) {
        tout.setEditable(true);
    }

    public void saveToutChanges(Tout tout) {
        try {
            // Update
            tout.getTruck().setPlateNumber(tout.getTruck().getPlateNumber());
            ToutsFacade toutsFacade = new ToutsFacade();
            int result = toutsFacade.updateTout(tout);
            System.out.print(result);
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        tout.getFirstName() + " " + tout.getLastName() + " updated.",
                         tout.getFirstName() + " " + tout.getLastName() + " updated.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);

            } else {

            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not update sanitation worker.",
                    "Could not update sanitation worker.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            tout.setEditable(false);
        }
    }
    
    public void refreshTouts() {
        this.initializeToutList();
    }

    public void acceptApplication(Tout tout) {
        try {
            // Create a new ToutsFacade
            ToutsFacade toutsFacade = new ToutsFacade(tout);
            int result = toutsFacade.approveApplication();
            if (result == 1) {
                // Set the tout as active
                tout.setIsActive(1);
                SMSSender.sendSmsSynchronous(tout.getPhone(), "Hello "
                        + tout.getFirstName() + " " + tout.getLastName()
                        + ". Your application has been accepted,"
                        + " you will be notified of the interview date. Thank you. NCGMS Inc.");
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not approve application. Please contact the system administrator",
                    "Could not approve application. Please contact the system administrator");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(AdminToutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeTout(Tout tout) {
        try {
            ToutsFacade toutsFacade = new ToutsFacade(tout);
            int result = toutsFacade.removeTout();
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, 
                        "Tout successfully removed.",
                        "Tout successfully removed.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Could not remove tout. Please contact the system administrator",
                    "Could not remove tout. Please contact the system administrator");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Get current page
            int page = this.currentPage;
            // Initialize tout list
            this.initializeToutList();
            // Go back to current page
            for(int i = 1; i < page; i++){
                nextToutPage();
            }

        }

    }

    public void rejectApplication(Tout tout) {
        this.removeTout(tout);
    }

    public void searchTouts() {
        try {
            ToutsFacade toutsFacade = new ToutsFacade();

            switch (this.searchBy) {
                case "First Name":
                    this.toutList = toutsFacade.searchDriverByFirstName(searchTerm);
                    break;
                case "Last Name":
                    this.toutList = toutsFacade.searchDriverByLastName(searchTerm);
                    break;
                case "Subcounty":
                    this.toutList = toutsFacade.searchDriverBySubcounty(searchTerm);
                    break;
                case "Address":
                    this.toutList = toutsFacade.searchDriverByAddress(searchTerm);
                    break;
                case "Assigned Truck":
                    this.toutList = toutsFacade.searchDriverByAssignedTruck(searchTerm);
                    break;
                case "Phone Number":
                    this.toutList = toutsFacade.searchDriverByPhoneNumber(searchTerm);
                    break;
                case "Email":
                    this.toutList = toutsFacade.searchDriverByEmail(searchTerm);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (this.toutList.isEmpty()) {
                this.initializeToutList();
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Results",
                        "No Results");
                FacesContext.getCurrentInstance().addMessage("touts_form:search_button", facesMessage);
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

    public List<Tout> getToutList() {
        return toutList;
    }

    public void setToutList(List<Tout> toutList) {
        this.toutList = toutList;
    }

    public List<Tout> getViewableToutList() {
        return viewableToutList;
    }

    public void setViewableToutList(List<Tout> viewableToutList) {
        this.viewableToutList = viewableToutList;
    }

    public boolean isNoToutsRendered() {
        return toutList.isEmpty();
    }

    public void setNoToutsRendered(boolean noToutsRendered) {
        this.noToutsRendered = noToutsRendered;
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

    public int getCurrentToutIndex() {
        return currentToutIndex;
    }

    public void setCurrentToutIndex(int currentToutIndex) {
        this.currentToutIndex = currentToutIndex;
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
