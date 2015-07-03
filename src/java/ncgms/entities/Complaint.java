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
    private User user = null;
    private List<Response> responseList = new ArrayList<>();

    /**
     * Default constructor
     */
    public Complaint() {
    }

    /**
     * Overloaded constructor
     *
     * @param complaintID
     * @param complaint
     * @param isRead
     * @param dateAdded
     * @param user
     */
    public Complaint(int complaintID, String complaint, int isRead, long dateAdded,
            User user) {
        this.complaintID = complaintID;
        this.complaint = complaint;
        this.isRead = isRead;
        this.dateAdded = dateAdded;
        this.user = user;
    }

    public Complaint(String complaint, int isRead, long dateAdded, User user) {
        this.complaint = complaint;
        this.isRead = isRead;
        this.dateAdded = dateAdded;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "\nComplaint ID - " + complaintID
                + "\nComplaint - " + complaint
                + "\nisRead - " + isRead
                + "\nDate Added - " + realDateAdded;
    }

}
