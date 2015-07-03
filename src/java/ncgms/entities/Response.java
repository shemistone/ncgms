/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.Date;

/**
 * Represents one record in the Responses table
 *
 * @author root
 */
public class Response {

    private int responseID = 0;
    private String response = null;
    private long dateAdded = 0;
    private int isRead = 0;
    private String realDateAdded = null;
    // Relationships
    private Complaint complaint = null;

    /**
     * Default constructor
     */
    public Response() {
    }

    public Response(int responseID, String response, long dateAdded, int isRead,
            Complaint complaint) {
        this.responseID = responseID;
        this.response = response;
        this.dateAdded = dateAdded;
        this.isRead = isRead;
        this.complaint = complaint;
    }
    

    public int getResponseID() {
        return responseID;
    }

    public void setResponseID(int responseID) {
        this.responseID = responseID;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
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

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    @Override
    public String toString() {
        return "\nResponse ID - " + responseID
                + "\nResponse - " + response
                + "\nDate Added - " + realDateAdded;
    }
    
}
