/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents one record in the Trucks table
 *
 * @author root
 */
public class Truck {

    private String plateNumber = null;
    private int inService = 1;
    private long dateAdded = 0;
    private String realDateAdded = null;
    // Relationships
    private int modelID = 0;
    //private Model model = null;
    private List<Client> clientList = new ArrayList<>();
    private List<Tout> toutList = new ArrayList<>();
    private Driver driver = null;

    /**
     * Default constructor
     */
    public Truck() {
    }

    public Truck(String plateNumber, int inService, int modelID) {
        this.plateNumber = plateNumber;
        this.inService = inService;
        this.modelID = modelID;
    }

    /**
     * Overloaded constructor
     *
     * @param plateNumber the plateNumber to set
     * @param inService the inService to set
     * @param dateAdded the dateAdded to set
     * @param modelID the modelID to set
     */
    public Truck(String plateNumber, int inService, long dateAdded, int modelID) {
        this.plateNumber = plateNumber;
        this.inService = inService;
        this.modelID = modelID;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public List<Tout> getToutList() {
        return toutList;
    }

    public void setToutList(List<Tout> toutList) {
        this.toutList = toutList;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    /**
     * Get the plateNumber
     *
     * @return the plateNUmber
     */
    public String getPlateNumber() {
        return plateNumber;
    }

    /**
     * Set the plateNumber
     *
     * @param plateNumber the plateNumber to set
     */
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getInService() {
        return inService;
    }

    public void setInService(int inService) {
        this.inService = inService;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getRealDateAdded() {
        realDateAdded = new Date(dateAdded).toString();
        realDateAdded = realDateAdded.substring(0, realDateAdded.
                lastIndexOf("EAT")) + realDateAdded.substring(realDateAdded.
                        lastIndexOf("EAT") + 4, realDateAdded.
                        lastIndexOf("EAT") + 8);
        return realDateAdded;
    }

    public void setRealDateAdded(String realDateAdded) {
        this.realDateAdded = realDateAdded;
    }

    /**
     * Get the modelID
     *
     * @return the modelID
     */
    public int getModelID() {
        return modelID;
    }

    /**
     * Set the modelID
     * @param modelID the modelID to set
     */
    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    @Override
    public String toString() {
        return "\nPlate Number - " + plateNumber
                + "\nIn Service - " + inService
                + "\nDate Added - " + new Date(dateAdded).toString()
                + "\nModel ID - " + modelID;
    }
}
