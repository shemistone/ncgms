/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.Date;

/**
 *
 * @author root
 */
public class Message {

    private int messageID = 0;
    private String message = null;
    private long dateAdded = 0;
    private int isRead = 0;
    private String realDateAdded = null;
    // Relationships
    private User user = null;

    public Message() {
    }

    public Message(int messageID, String message, long dateAdded, int isRead, User user) {
        this.messageID = messageID;
        this.message = message;
        this.dateAdded = dateAdded;
        this.isRead = isRead;
        this.user = user;
    }
    
    public Message(String message, long dateAdded, int isRead, User user) {
        this.message = message;
        this.dateAdded = dateAdded;
        this.isRead = isRead;
        this.user = user;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "\nMessage ID - " + messageID
                + "\nMessage - " + message
                + "\nIs Read - " + isRead
                + "\nDate Added - " + realDateAdded
                + "\nUser - " + user;
    }

}
