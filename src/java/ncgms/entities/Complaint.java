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
 * Represents one record in the Complaints table
 *
 * @author root
 */
public class Complaint {

    private int complaintID = 0;
    private String complaint = null;
    private int isRead = 0;
    private long dateAdded = 0;
    private String realDateAdded = null;
    // Relationships
    private int userID = 0;
    private Client client = null;
    private List<Response> responseList = new ArrayList<>();    

    /**
     * Default constructor
     */
    public Complaint() {
    }

    /**
     * Overloaded constructor
     *
     * @param complaint
     * @param isRead
     * @param dateAdded
     * @param userID
     */
    public Complaint(String complaint, int isRead, long dateAdded, int userID) {
        this.complaint = complaint;
        this.isRead = isRead;
        this.dateAdded = dateAdded;
        this.userID = userID;
    }

    /**
     * Overloaded constructor     *
     * @param complaint
     * @param isRead
     * @param dateAdded
     */
    public Complaint(String complaint, int isRead, long dateAdded) {
        this.complaint = complaint;
        this.isRead = isRead;
        this.dateAdded = dateAdded;
    }

    /**
     * Overloaded constructor
     *
     * @param complaintID
     * @param complaint
     * @param isRead
     * @param dateAdded
     */
    public Complaint(int complaintID, String complaint, int isRead, long dateAdded) {
        this.complaintID = complaintID;
        this.complaint = complaint;
        this.isRead = isRead;
        this.dateAdded = dateAdded;
    }

    public List<Response> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<Response> responseList) {
        this.responseList = responseList;
    }

    public int getComplaintID() {
        return complaintID;
    }

    public void setComplaintID(int complaintID) {
        this.complaintID = complaintID;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
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

    @Override
    public String toString() {
        return "\nComplaint ID - " + complaintID
                + "\nComplaint - " + complaint
                + "\nisRead - " + isRead
                + "\nDate Added - " + realDateAdded
                + "\nUser ID - " + userID
                + "\nClient - " + client;
    }

}
