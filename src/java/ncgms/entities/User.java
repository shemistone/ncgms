/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one record in the Users table
 * @author root
 */
public class User {

    protected int userID = 0;
    protected String username = null;
    protected String passwordHash = null;
    protected int isActive = 0;
    
    List<Complaint> complaintList = new ArrayList<>();
    List<Message> messageList = new ArrayList<>();

    /**
     * Default constructor
     */
    public User() {
    }
    
    /**
     * Overloaded constructor
     * @param username the username to set
     * @param passwordHash the password hash to set
     * @param isActive the status to set { 0 or 1 }
     */
    public User(String username, String passwordHash, int isActive){
        this.username = username;
        this.passwordHash = passwordHash;
        this.isActive = isActive;
    }

     public User(int userID, String username, String passwordHash, int isActive){
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.isActive = isActive;
    }
    /**
     * Get the userID
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Set the userID
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    /**
     * Get the username
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the passwordHash
     * @return the passwordHash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Set the passwordHash
     * @param passwordHash the passwordHash to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Get the status of the user
     * @return the status of the user { 0 | 1}
     */
    public int getIsActive() {
        return isActive;
    }

    /**
     * Set the user's status
     * @param isActive the status to set { 0 | 1 }
     */
    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public List<Complaint> getComplaintList() {
        return complaintList;
    }

    public void setComplaintList(List<Complaint> complaintList) {
        this.complaintList = complaintList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }
    
    @Override
    public String toString(){
        return "\nUser ID - " + userID + 
               "\nUsername - " + username +
               "\npasswordHash - " + passwordHash +
               "\nisActive - " + isActive;
    }

}
