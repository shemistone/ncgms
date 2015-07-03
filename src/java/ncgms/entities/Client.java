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
 * Represents one record in the Clients table
 *
 * @author root
 */
public class Client extends User {

    private int clientID = 0;
    private String firstName = null;
    private String lastName = null;
    private String address = null;
    private String phone = null;
    private String email = null;
    private String plotName = null;
    private long dateAdded = 0;
    private String realDateAdded = null;
    private int idNumber = 0;
    private int wantsToCancel = 0;
    // Relationships
    private Subcounty subcounty = null;
    private Package packageObject = null;
    private Truck truck = null;
    private List<Invoice> invoiceList = new ArrayList<>();
    private List<ContainerOrder> containerOrderList = new ArrayList<>();

    // Flags
    private boolean editable = false;
    
    /**
     * Default constructor
     */
    public Client() {
    }
    
    public Client(int userID, String username, String passwordHash,int isActive, String firstName, 
            String lastName, String address, String phone, String email, String plotName,
            long dateAdded, int idNumber, int wantsToCancel){
        super(userID, username, passwordHash, isActive);
        this.clientID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.plotName = plotName;
        this.dateAdded = dateAdded;
        this.idNumber = idNumber;
        this.wantsToCancel = wantsToCancel;
    }
    
    public Client(int userID, String username, String passwordHash,int isActive, String firstName, 
            String lastName, String address, String phone, String email, String plotName,
            long dateAdded, int idNumber, int wantsToCancel, Subcounty subcounty, 
            Truck truck, Package packageObject){
        super(userID, username, passwordHash, isActive);
        this.clientID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.plotName = plotName;
        this.dateAdded = dateAdded;
        this.idNumber = idNumber;
        this.wantsToCancel = wantsToCancel;
        this.subcounty = subcounty;
        this.truck = truck;
        this.packageObject = packageObject;
    }

    public Client(int userID, String username, String passwordHash,int isActive, String firstName, 
            String lastName, String address, String phone, String email, String plotName,
            long dateAdded, int idNumber, int wantsToCancel, Subcounty subcounty, 
            Truck truck, Package packageObject, List<Invoice> invoiceList, 
            List<ContainerOrder> containerOrderList){
        super(userID, username, passwordHash, isActive);
        this.clientID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.plotName = plotName;
        this.dateAdded = dateAdded;
        this.idNumber = idNumber;
        this.wantsToCancel = wantsToCancel;
        this.subcounty = subcounty;
        this.truck = truck;
        this.packageObject = packageObject;
        this.invoiceList = invoiceList;
        this.containerOrderList = containerOrderList;
    }

    
    public List<Invoice> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    public List<ContainerOrder> getContainerOrderList() {
        return containerOrderList;
    }

    public void setContainerOrderList(List<ContainerOrder> containerOrderList) {
        this.containerOrderList = containerOrderList;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
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

    public int getWantsToCancel() {
        return wantsToCancel;
    }

    public void setWantsToCancel(int wantsToCancel) {
        this.wantsToCancel = wantsToCancel;
    }

    public Subcounty getSubcounty() {
        return subcounty;
    }

    public void setSubcounty(Subcounty subcounty) {
        this.subcounty = subcounty;
    }

    public Package getPackageObject() {
        return packageObject;
    }

    public void setPackageObject(Package packageObject) {
        this.packageObject = packageObject;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        this.truck = truck;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    
    @Override
    public String toString() {
        return "\nFirst name - " + firstName
                + "\nLast name - " + lastName
                + "\nAddress - " + address
                + "\nEmail - " + email
                + "\nPhone - " + phone
                + "\nPlot name - " + plotName
                + "\nID Number - " + idNumber
                + "\nWants to Cancel - " + wantsToCancel
                + "\nDate Added - " + dateAdded
                + "\nClient ID - " + clientID
                + "\nUsername - " + username
                + "\nPassword Hash- " + passwordHash
                + "\nIs Active - " + isActive;
    }
}
