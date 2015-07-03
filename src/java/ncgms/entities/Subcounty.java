/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one record in the Subcounties table
 *
 * @author root
 */
public class Subcounty {

    private int subcountyID = 0;
    private String subcountyName = null;
    // Relationships
    List<Client> clientList = new ArrayList<>();
    List<Driver> driverList = new ArrayList<>();
    List<Tout> toutList = new ArrayList<>();

    /**
     * Default constructor
     */
    public Subcounty() {
    }

    public Subcounty(int subcountyID, String subcountyName) {
        this.subcountyID = subcountyID;
        this.subcountyName = subcountyName;
    }
    
    public Subcounty(int subcountyID, String subcountyName, List<Client> clientList,
            List<Driver> driverList, List<Tout> toutList) {
        this.subcountyID = subcountyID;
        this.subcountyName = subcountyName;
        this.clientList = clientList;
        this.driverList = driverList;
        this.toutList = toutList;
    }

    public int getSubcountyID() {
        return subcountyID;
    }

    public void setSubcountyID(int subcountyID) {
        this.subcountyID = subcountyID;
    }

    public String getSubcountyName() {
        return subcountyName;
    }

    public void setSubcountyName(String subcountyName) {
        this.subcountyName = subcountyName;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    public List<Driver> getDriverList() {
        return driverList;
    }

    public void setDriverList(List<Driver> driverList) {
        this.driverList = driverList;
    }

    public List<Tout> getToutList() {
        return toutList;
    }

    public void setToutList(List<Tout> toutList) {
        this.toutList = toutList;
    }

    @Override
    public String toString() {
        return "\nSubcounty ID - " + subcountyID
                + "\nSubcounty Name - " + subcountyName;
    }

}
