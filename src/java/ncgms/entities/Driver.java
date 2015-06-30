/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.Date;

/**
 * Represents one record in the Drivers table
 *
 * @author root
 */
public class Driver extends User{

    private int driverID = 0;
    private String firstName = null;
    private String lastName = null;
    private String phone = null;
    private String email = null;
    private String address = null;
    private long dateAdded = 0;
    private String realDateAdded = null;
    private int idNumber = 0;
    private String cvName = null;
    // Relationships
    private String plateNumber = null;
    private Truck trcuk = null;
    private int subcountyID = 0;
    private Subcounty subcounty = null;

    // Flags
    private boolean editable = false;
    /**
     * Default constructor
     */
    public Driver() {
    }

    /**
     * Overloaded constructor
     *
     * @param firstName
     * @param lastName
     * @param phone
     * @param email
     * @param address
     * @param dateAdded
     * @param idNumber
     * @param cvName
     * @param subcountyID
     */
    public Driver(String firstName, String lastName, String phone, String email,
            String address, long dateAdded, int idNumber, String cvName, int subcountyID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.dateAdded = dateAdded;
        this.idNumber = idNumber;
        this.cvName = cvName;
        this.subcountyID = subcountyID;
    }
    
    public Driver(int driverID, String firstName, String lastName, String phone,
            String email, String address, long dateAdded, int idNumber, String cvName,
            String plateNumber, int subcountyID, int isActive) {
        this.driverID = driverID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.dateAdded = dateAdded;
        this.idNumber = idNumber;
        this.cvName = cvName;
        this.plateNumber = plateNumber;
        this.subcountyID = subcountyID;
        this.isActive = isActive;
    }
    
    public Driver(int driverID, String firstName, String lastName, String phone,
            String email, String address, long dateAdded, int idNumber, String cvName,
            String plateNumber, int subcountyID) {
        this.driverID = driverID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.dateAdded = dateAdded;
        this.idNumber = idNumber;
        this.cvName = cvName;
        this.plateNumber = plateNumber;
        this.subcountyID = subcountyID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    public String getCvName() {
        return cvName;
    }

    public void setCvName(String cvName) {
        this.cvName = cvName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getDriverID() {
        return driverID;
    }

    public void setDriverID(int driverID) {
        this.driverID = driverID;
    }

    public int getSubcountyID() {
        return subcountyID;
    }

    public void setSubcountyID(int subcountyID) {
        this.subcountyID = subcountyID;
    }

    public Subcounty getSubcounty() {
        return subcounty;
    }

    public void setSubcounty(Subcounty subcounty) {
        this.subcounty = subcounty;
    }

    public Truck getTrcuk() {
        return trcuk;
    }

    public void setTrcuk(Truck trcuk) {
        this.trcuk = trcuk;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String toString() {
        return "\nDriver ID - " + driverID
                + "\nFirst name - " + firstName
                + "\nLast name - " + lastName
                + "\nAddress - " + address
                + "\nEmail - " + email
                + "\nPhone - " + phone
                + "\nID Number - " + idNumber
                + "\nCV Name - " + cvName 
                + "\nPlate Number - " + plateNumber 
                + "\nDate Added - " + new Date(dateAdded).toString()
                + "\nSub-County - " + subcountyID;
    }
}
