/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.admin.controllers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Pattern;
import ncgms.entities.Truck;
import ncgms.daos.ModelsFacade;
import ncgms.daos.TrucksFacade;
import ncgms.entities.Model;
import ncgms.entities.Tout;
import ncgms.util.EmailSenderTask;
import ncgms.util.SMSSenderTask;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class AdminTruckController implements Serializable {

    private ExecutorService executorService;

    private String searchBy = null;
    private String searchTerm = null;
    private String[] searchByArray = new String[]{"Model Name", "Plate Number"};
    private Map<String, Integer> modelIDsMap = new HashMap<>();
    private Map<Integer, String> modelNamesMap = new HashMap<>();
    private List<String> modelList = new ArrayList<>();
    private List<Truck> truckList = new ArrayList<>();
    private List<Truck> viewableTruckList = new ArrayList<>();

    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentTruckIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    @Pattern(regexp = "^[A-Z]{3}\\s+[0-9]{3}[A-Z]?$",
            message = "Plate number format is invalid.e.g. KAL 887L")
    private String plateNumber;
    @Pattern(regexp = "^[a-zA-Z\\s]+[a-zA-Z_\\-\\']*[a-zA-Z\\']+$",
            message = "Select truck model.")
    private String modelName;

    /**
     * Creates a new instance of TruckController
     */
    public AdminTruckController() {
        initializeTruckList();
    }

    private void initializeTruckList() {
        try {
            // Initialize the variables for navigation
            currentTruckIndex = 0;
            currentPage = 1;
            // Populate modelList
            ModelsFacade modelsFacade = new ModelsFacade();
            this.modelList = modelsFacade.populateModelList();

            // Load all the trucks
            TrucksFacade trucksFacade = new TrucksFacade();
            this.truckList = trucksFacade.loadAllTrucks();

            // Set the number of pages
            this.noOfPages = this.truckList.size() / 10;
            // Set the last page
            if (this.truckList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableTruckList = new ArrayList<>();

            /* Initialize the trucks */
            int index;
            if (this.truckList.size() >= 10) {// if there are more than 10 trucks

                // Set the first 10 trucks
                for (index = 0; index <= 9; index++) {
                    this.viewableTruckList.add(this.truckList.get(index));
                }
                currentTruckIndex = index;
            } else {
                this.viewableTruckList = this.truckList;
                currentTruckIndex = this.viewableTruckList.size();
            }
            /* Initialize the trucks */
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not initialize truck list.",
                    "Could not initialize truck list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initializeResultList() {
        try {
            // Initialize the variables for navigation
            currentTruckIndex = 0;
            currentPage = 1;
            // Populate modelList
            ModelsFacade modelsFacade = new ModelsFacade();
            this.modelList = modelsFacade.populateModelList();

            // Set the number of pages
            this.noOfPages = this.truckList.size() / 10;
            // Set the last page
            if (this.truckList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableTruckList = new ArrayList<>();

            /* Initialize the trucks */
            int index;
            if (this.truckList.size() >= 10) {// if there are more than 10 trucks

                // Set the first 10 trucks
                for (index = 0; index <= 9; index++) {
                    this.viewableTruckList.add(this.truckList.get(index));
                }
                currentTruckIndex = index;
            } else {
                this.viewableTruckList = this.truckList;
                currentTruckIndex = this.viewableTruckList.size();
            }
            /* Initialize the trucks */
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not initialize truck list.",
                    "Could not initialize truck list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nextTruckPage() {

        // Check if index is more than truckList size
        if (currentTruckIndex < this.truckList.size()) {// You are not at the last page
            int nextPage = currentPage + 1;
            // Check if next page is last page
            if (nextPage == this.noOfPages) {// Load the remaining trucks
                // Clear list first
                this.viewableTruckList.clear();
                for (; currentTruckIndex < this.truckList.size();) {
                    // Add the touts
                    this.viewableTruckList.add(this.truckList.get(currentTruckIndex++));
                }
                // Move to next page
                currentPage++;

            } else {// Load next 10 trucks
                // Clear list first
                this.viewableTruckList.clear();
                for (int i = 0; i <= 9; i++) {
                    // Add the trucks
                    this.viewableTruckList.add(this.truckList.get(currentTruckIndex++));
                }

                // Move to next page            
                currentPage++;
            }
        } else {// You are at the last page
            // Set currentToutIndex to the size of the truckList
            currentTruckIndex = this.truckList.size();
        }
    }

    public void previousTruckPage() {

        if (currentTruckIndex < this.truckList.size()) {// You are not at the last page
            int previousPage = currentPage - 1;
            // Check if previous page is the first page
            if (previousPage < 1) {
                // Pass
            } else {
                //Move back 20 elements
                currentTruckIndex -= 20;
                // Clear list first
                this.viewableTruckList.clear();

                // Load previous ten trucks
                for (int i = 0; i <= 9; i++) {

                    // Add the trucks
                    this.viewableTruckList.add(this.truckList.get(currentTruckIndex++));

                }
                // Move to previous page            
                currentPage--;
            }

        } else {// You are at the last page
            //Move back 10 + last page elements
            currentTruckIndex -= (10 + this.viewableTruckList.size());
            // Clear list first
            this.viewableTruckList.clear();

            // Load previous ten touts
            for (int i = 0; i <= 9; i++) {

                // Add the trucks
                this.viewableTruckList.add(this.truckList.get(currentTruckIndex++));

            }
            // Move to previous page            
            currentPage--;
        }

    }

    public void insertTruck() {
        try {
            // Create a new truck
            Truck truck = new Truck(plateNumber, 1, 0,
                    new Model(new ModelsFacade().searchModelByName(modelName).getModelID(),
                            modelName));
            TrucksFacade trucksFacade = new TrucksFacade(truck);
            // Check if truck exists
            if (trucksFacade.truckExists()) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        truck.getPlateNumber() + " already exists.", truck.getPlateNumber()
                        + " already exists.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                return;
            }
            int result = trucksFacade.insertTruck();
            if (result == 1) {
                // Add truck to viewableTruckList
                initializeTruckList();
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Truck successfully added", "Truck successfully added");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                this.plateNumber = this.modelName = null;
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not add truck.",
                    "Could not add truck.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminTruckController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void suspendTruck(Truck truck) {
        try {
            this.executorService = Executors.newCachedThreadPool();
            // Create a new TrucksFacade
            TrucksFacade trucksFacade = new TrucksFacade(truck);
            int result = trucksFacade.suspendTruck();
            if (result == 1) {
                // Set the truck as not in service
                truck.setInService(0);
                //Notify Driver-------------------------------------------------//
                String message = "Hello " + truck.getDriver().getFirstName()
                        + " Truck : " + truck.getPlateNumber() + " has been suspended."
                        + " Please contact the Logistcis Manager for more information."
                        + " NCGMS Inc.";
                this.executorService.execute(new SMSSenderTask(truck.getDriver().getPhone(), message));
                this.executorService.execute(new EmailSenderTask(truck.getDriver().getEmail(),
                        "Truck Suspension", message));
                //--------------------------------------------------------------//
                //Notify Touts---------------------------------------------------//
                for (Tout tout : truck.getToutList()) {
                    message = "Hello " + tout.getFirstName()
                            + " Truck : " + truck.getPlateNumber() + " has been suspended."
                            + " Please contact the Logistcis Manager for more information."
                            + " NCGMS Inc.";
                    this.executorService.execute(new SMSSenderTask(tout.getPhone(), message));
                    this.executorService.execute(new EmailSenderTask(tout.getEmail(),
                            "Truck Suspension", message));
                }
                //--------------------------------------------------------------//
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not suspend truck.",
                    "Could not suspend truck.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void unSuspendTruck(Truck truck) {
        try {
            this.executorService = Executors.newCachedThreadPool();
            // Create a new TrucksFacade
            TrucksFacade trucksFacade = new TrucksFacade(truck);
            int result = trucksFacade.unSuspendTruck();
            if (result == 1) {
                // Set the truck as in service
                truck.setInService(1);
                //Notify Driver-------------------------------------------------//
                String message = "Hello " + truck.getDriver().getFirstName()
                        + " Truck : " + truck.getPlateNumber() + " has been unsuspended."
                        + " Please contact the Logistcis Manager for more information."
                        + " NCGMS Inc.";
                this.executorService.execute(new SMSSenderTask(truck.getDriver().getPhone(), message));
                this.executorService.execute(new EmailSenderTask(truck.getDriver().getEmail(),
                        "Truck Unsuspension", message));
                //--------------------------------------------------------------//
                //Notify Touts---------------------------------------------------//
                for (Tout tout : truck.getToutList()) {
                    message = "Hello " + tout.getFirstName()
                            + " Truck : " + truck.getPlateNumber() + " has been suspended."
                            + " Please contact the Logistcis Manager for more information."
                            + " NCGMS Inc.";
                    this.executorService.execute(new SMSSenderTask(tout.getPhone(), message));
                    this.executorService.execute(new EmailSenderTask(tout.getEmail(),
                            "Truck Unsuspension", message));
                }
                //--------------------------------------------------------------//
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not unsuspend truck.",
                    "Could not unsuspend truck.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeTruck(Truck truck) {
        try {
            this.executorService = Executors.newCachedThreadPool();
            
            // Create a new TrucksFacade
            TrucksFacade trucksFacade = new TrucksFacade(truck);
            int result = trucksFacade.removeTruck();
            if (result == 1) {
                //Notify Driver-------------------------------------------------//
                String message = "Hello " + truck.getDriver().getFirstName()
                        + " Truck : " + truck.getPlateNumber() + " has been permanetly removed."
                        + " Please contact the Logistcis Manager for more information."
                        + " NCGMS Inc.";
                this.executorService.execute(new SMSSenderTask(truck.getDriver().getPhone(), message));
                this.executorService.execute(new EmailSenderTask(truck.getDriver().getEmail(),
                        "Truck Removal", message));
                //--------------------------------------------------------------//
                //Notify Touts---------------------------------------------------//
                for (Tout tout : truck.getToutList()) {
                    message = "Hello " + tout.getFirstName()
                            + " Truck : " + truck.getPlateNumber() + " has been permanently removed."
                            + " Please contact the Logistcis Manager for more information."
                            + " NCGMS Inc.";
                    this.executorService.execute(new SMSSenderTask(tout.getPhone(), message));
                    this.executorService.execute(new EmailSenderTask(tout.getEmail(),
                            "Truck Removal", message));
                }
                //--------------------------------------------------------------//
                // Get current page
                int page = this.currentPage;
                // Initialize truck list
                this.initializeTruckList();
                // Go back to current page
                for (int i = 1; i <= page; i++) {
                    nextTruckPage();
                }
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not remove truck.",
                    "Could not remove truck.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminTruckController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }

    public void refreshTrucks() {
        this.initializeTruckList();
    }

    public void searchTrucks() {
        try {
            TrucksFacade trucksFacade = new TrucksFacade();

            switch (this.searchBy) {
                case "Model Name":
                    this.truckList = trucksFacade.searchTruckByModelName(searchTerm);
                    break;
                case "Plate Number":
                    this.truckList = trucksFacade.searchTruckByPlateNumber(searchTerm);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminClientController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (this.truckList.isEmpty()) {
                this.initializeTruckList();
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "No Results",
                        "No Results");
                FacesContext.getCurrentInstance().addMessage("trucks_form:search_button",
                        facesMessage);
            } else {
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

    public Map<String, Integer> getModelIDsMap() {
        return modelIDsMap;
    }

    public void setModelIDsMap(Map<String, Integer> modelIDsMap) {
        this.modelIDsMap = modelIDsMap;
    }

    public Map<Integer, String> getModelNamesMap() {
        return modelNamesMap;
    }

    public void setModelNamesMap(Map<Integer, String> modelNamesMap) {
        this.modelNamesMap = modelNamesMap;
    }

    public List<String> getModelList() {
        return modelList;
    }

    public void setModelList(List<String> modelList) {
        this.modelList = modelList;
    }

    public List<Truck> getTruckList() {
        return truckList;
    }

    public void setTruckList(List<Truck> truckList) {
        this.truckList = truckList;
    }

    public List<Truck> getViewableTruckList() {
        return viewableTruckList;
    }

    public void setViewableTruckList(List<Truck> viewableTruckList) {
        this.viewableTruckList = viewableTruckList;
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

    public int getCurrentTruckIndex() {
        return currentTruckIndex;
    }

    public void setCurrentTruckIndex(int currentTruckIndex) {
        this.currentTruckIndex = currentTruckIndex;
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

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

}
