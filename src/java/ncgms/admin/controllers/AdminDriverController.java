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
import ncgms.entities.Driver;
import ncgms.daos.DriversFacade;
import ncgms.daos.SubcountiesFacade;
import ncgms.daos.TrucksFacade;
import ncgms.util.SMSSender;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class AdminDriverController implements Serializable {

    private String searchBy = null;
    private String searchTerm = null;
    private String[] searchByArray = new String[]{"First Name", "Last Name",
        "Subcounty", "Address", "Assigned Truck", "Phone Number", "Email"};
    private List<String> plateNumberList = new ArrayList<>();
    private Map<Integer, String> subcountyNamesMap = new HashMap<>();
    private List<Driver> driverList = new ArrayList<>();
    private List<Driver> viewableDriverList = new ArrayList<>();
    private boolean noDriversRendered = false;

    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentDriverIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    // Injections
    /**
     * Creates a new instance of DriverController
     */
    public AdminDriverController() {
        initializeDriverList();
    }

    private void initializeDriverList() {
        try {
            // Initialize the variables for navigation
            currentDriverIndex = 0;
            currentPage = 1;
            // Populate the plateNumberList
            TrucksFacade trucksFacade = new TrucksFacade();
            this.plateNumberList = trucksFacade.populatePlateNumberList();

            // Load subcounty names into map
            SubcountiesFacade subcountiesFacade = new SubcountiesFacade();
            this.subcountyNamesMap = subcountiesFacade.loadSubcountyNamesMap();

            DriversFacade driversFacade = new DriversFacade();
            this.driverList = driversFacade.loadAllDrivers();

            // Set the number of pages
            this.noOfPages = this.driverList.size() / 10;
            // Set the last page
            if (this.driverList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableDriverList = new ArrayList<>();

            /* Initialize the drivers */
            int index;
            if (this.driverList.size() >= 10) {// if there are more than 10 drivers

                // Set the first 10 drivers
                for (index = 0; index <= 9; index++) {
                    this.viewableDriverList.add(this.driverList.get(index));
                }
                currentDriverIndex = index;
            } else {
                this.viewableDriverList = this.driverList;
                currentDriverIndex = this.viewableDriverList.size();
            }
            /* Initialize the drivers */

        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not initialize driver list.",
                    "Could not initialize driver list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminDriverController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initializeResultList() {
        try {
            // Initialize the variables for navigation
            currentDriverIndex = 0;
            currentPage = 1;
            // Populate the plateNumberList
            TrucksFacade trucksFacade = new TrucksFacade();
            this.plateNumberList = trucksFacade.populatePlateNumberList();

            // Load subcounty names into map
            SubcountiesFacade subcountiesFacade = new SubcountiesFacade();
            this.subcountyNamesMap = subcountiesFacade.loadSubcountyNamesMap();
            /*
             DriversFacade driversFacade = new DriversFacade();
             this.driverList = driversFacade.loadAllDrivers();
             */
            // Set the number of pages
            this.noOfPages = this.driverList.size() / 10;
            // Set the last page
            if (this.driverList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableDriverList = new ArrayList<>();

            /* Initialize the drivers */
            int index;
            if (this.driverList.size() >= 10) {// if there are more than 10 drivers

                // Set the first 10 drivers
                for (index = 0; index <= 9; index++) {
                    this.viewableDriverList.add(this.driverList.get(index));
                }
                currentDriverIndex = index;
            } else {
                this.viewableDriverList = this.driverList;
                currentDriverIndex = this.viewableDriverList.size();
            }
            /* Initialize the drivers */

        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not initialize driver list.",
                    "Could not initialize driver list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminDriverController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nextDriverPage() {

        // Check if index is more than driverList size
        if (currentDriverIndex < this.driverList.size()) {// You are not at the last page
            int nextPage = currentPage + 1;
            // Check if next page is last page
            if (nextPage == this.noOfPages) {// Load the remaining drivers
                // Clear list first
                this.viewableDriverList.clear();
                for (; currentDriverIndex < this.driverList.size();) {
                    // Add the drivers
                    this.viewableDriverList.add(this.driverList.get(currentDriverIndex++));
                }
                // Move to next page
                currentPage++;

            } else {// Load next 10 drivers
                // Clear list first
                this.viewableDriverList.clear();
                for (int i = 0; i <= 9; i++) {
                    // Add the drivers
                    this.viewableDriverList.add(this.driverList.get(currentDriverIndex++));
                }

                // Move to next page            
                currentPage++;
            }
        } else {// You are at the last page
            // Set currentDriverIndex to the size of the driverList
            currentDriverIndex = this.driverList.size();
        }
    }

    public void previousDriverPage() {

        if (currentDriverIndex < this.driverList.size()) {// You are not at the last page
            int previousPage = currentPage - 1;
            // Check if previous page is the first page
            if (previousPage < 1) {
                // Pass
            } else {
                //Move back 20 elements
                currentDriverIndex -= 20;
                // Clear list first
                this.viewableDriverList.clear();

                // Load previous ten drivers
                for (int i = 0; i <= 9; i++) {

                    // Add the drivers
                    this.viewableDriverList.add(this.driverList.get(currentDriverIndex++));

                }
                // Move to previous page            
                currentPage--;
            }

        } else {// You are at the last page
            //Move back 10 + last page elements
            currentDriverIndex -= (10 + this.viewableDriverList.size());
            // Clear list first
            this.viewableDriverList.clear();

            // Load previous ten drivers
            for (int i = 0; i <= 9; i++) {

                // Add the drivers
                this.viewableDriverList.add(this.driverList.get(currentDriverIndex++));

            }
            // Move to previous page            
            currentPage--;
        }

    }
    
    public void editDriver(Driver driver) {
        driver.setEditable(true);
    }

    public void saveDriverChanges(Driver driver) {
        try {
            // Update
            driver.setPlateNumber(driver.getPlateNumber());
            DriversFacade driversFacade = new DriversFacade();
            int result = driversFacade.updateDriver(driver);
            System.out.print(result);
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        driver.getFirstName() + " " + driver.getLastName() + " updated.",
                        driver.getFirstName() + " " + driver.getLastName() + " updated.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);

            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not update driver.",
                    "Could not update driver.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            driver.setEditable(false);
        }
    }

    public void acceptApplication(Driver driver) {

        try {
            // Create a new DriversFacade
            DriversFacade driversFacade = new DriversFacade(driver);
            int result = driversFacade.approveApplication();
            if (result == 1) {
                // Set the driver as active
                driver.setIsActive(1);
                SMSSender.sendSmsSynchronous(driver.getPhone(), "Hello "
                        + driver.getFirstName() + " " + driver.getLastName()
                        + ". Your application has been accepted,"
                        + " you will be notified of the interview date. Thank you. NCGMS Inc.");
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not approve applications. Please contact the system administrator",
                    "Could not approve applications. Please contact the system administrator");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(AdminDriverController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeDriver(Driver driver) {
        try {
            DriversFacade driversFacade = new DriversFacade(driver);
            int result = driversFacade.removeDriver();
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, "Driver successfully removed.",
                        "Driver successfully removed.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not remove driver. Please contact the system administrator",
                    "Could not remove driver. Please contact the system administrator");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Initialize driver list
            this.initializeDriverList();
        }

    }

    public void rejectApplication(Driver driver) {
        this.removeDriver(driver);
    }

    public void refreshDrivers() {
        this.initializeDriverList();
    }

    public void searchDrivers() {
        try {
            DriversFacade driversFacade = new DriversFacade();

            switch (this.searchBy) {
                case "First Name":
                    this.driverList = driversFacade.searchDriverByFirstName(searchTerm);
                    break;
                case "Last Name":
                    this.driverList = driversFacade.searchDriverByLastName(searchTerm);
                    break;
                case "Subcounty":
                    this.driverList = driversFacade.searchDriverBySubcounty(searchTerm);
                    break;
                case "Address":
                    this.driverList = driversFacade.searchDriverByAddress(searchTerm);
                    break;
                case "Assigned Truck":
                    this.driverList = driversFacade.searchDriverByAssignedTruck(searchTerm);
                    break;
                case "Phone Number":
                    this.driverList = driversFacade.searchDriverByPhoneNumber(searchTerm);
                    break;
                case "Email":
                    this.driverList = driversFacade.searchDriverByEmail(searchTerm);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (this.driverList.isEmpty()) {
                this.initializeDriverList();
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "No Results",
                        "No Results");
                FacesContext.getCurrentInstance().addMessage("drivers_form:search_button", facesMessage);
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

    public List<Driver> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<Driver> driverList) {
        this.driverList = driverList;
    }

    public List<Driver> getViewableDriverList() {
        return viewableDriverList;
    }

    public void setViewableDriverList(List<Driver> viewableDriverList) {
        this.viewableDriverList = viewableDriverList;
    }

    public boolean isNoDriversRendered() {
        return driverList.isEmpty();
    }

    public void setNoDriversRendered(boolean noDriversRendered) {
        this.noDriversRendered = noDriversRendered;
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

    public int getCurrentDriverIndex() {
        return currentDriverIndex;
    }

    public void setCurrentDriverIndex(int currentDriverIndex) {
        this.currentDriverIndex = currentDriverIndex;
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
