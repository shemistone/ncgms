/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.controllers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import ncgms.entities.Message;
import ncgms.entities.User;
import ncgms.daos.MessagesFacade;
import ncgms.daos.UsersFacade;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class MessageController implements Serializable {

    private List<Message> messageList = new ArrayList<>();
    private List<Message> viewableMessageList = new ArrayList<>();

    private int noOfUnreadMessages;

    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentMessageIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    /**
     * Creates a new instance of MessageController
     */
    public MessageController() {
        initializeMessageList();
    }

    private void initializeMessageList() {
        try {
            // Initialize the variables for navigation
            currentMessageIndex = 0;
            currentPage = 1;
            // Reset the no of unread messages
            this.noOfUnreadMessages = 0;
            // Get user from session
            User user = new LogInLogOutController().getUserFromSession();
            if(user == null){
                user = new User();
            }
            UsersFacade usersFacade = new UsersFacade(user);

            // Create a new Message MessagesFacade for loading messages
            Message message = new Message();
            message.setUserID(user.getUserID());
            MessagesFacade messagesFacade = new MessagesFacade(message);
            this.messageList = messagesFacade.loadUserMessages();

            // Set the number of pages
            this.noOfPages = this.messageList.size() / 10;
            // Set the last page
            if (this.messageList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableMessageList = new ArrayList<>();

            /* Initialize the messages */
            int index;
            if (this.messageList.size() >= 10) {// if there are more than 10 messages

                // Set the first 10 messages
                for (index = 0; index <= 9; index++) {
                    this.viewableMessageList.add(this.messageList.get(index));
                }
                currentMessageIndex = index;
            } else {
                this.viewableMessageList = this.messageList;
                currentMessageIndex = this.viewableMessageList.size();
            }
            /* Initialize the messages */
        } catch (SQLException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Set number of unread messages
        for (Message message : messageList) {
            if (message.getIsRead() == 0) {
                noOfUnreadMessages++;
            }
        }
    }

    public void nextMessagePage() {

        // Check if index is more than messageList size
        if (currentMessageIndex < this.messageList.size()) {// You are not at the last page
            int nextPage = currentPage + 1;
            // Check if next page is last page
            if (nextPage == this.noOfPages) {// Load the remaining messages
                // Clear list first
                this.viewableMessageList.clear();
                for (; currentMessageIndex < this.messageList.size();) {
                    // Add the messages
                    this.viewableMessageList.add(this.messageList.get(currentMessageIndex++));
                }
                // Move to next page
                currentPage++;

            } else {// Load next 10 messages
                // Clear list first
                this.viewableMessageList.clear();
                for (int i = 0; i <= 9; i++) {
                    // Add the messages
                    this.viewableMessageList.add(this.messageList.get(currentMessageIndex++));
                }

                // Move to next page            
                currentPage++;
            }
        } else {// You are at the last page
            // Set currentMessageIndex to the size of the messageList
            currentMessageIndex = this.messageList.size();
        }
    }

    public void previousMessagePage() {

        if (currentMessageIndex < this.messageList.size()) {// You are not at the last page
            int previousPage = currentPage - 1;
            // Check if previous page is the first page
            if (previousPage < 1) {
                // Pass
            } else {
                //Move back 20 elements
                currentMessageIndex -= 20;
                // Clear list first
                this.viewableMessageList.clear();

                // Load previous ten messages
                for (int i = 0; i <= 9; i++) {

                    // Add the messages
                    this.viewableMessageList.add(this.messageList.get(currentMessageIndex++));

                }
                // Move to previous page            
                currentPage--;
            }

        } else {// You are at the last page
            //Move back 10 + last page elements
            currentMessageIndex -= (10 + this.viewableMessageList.size());
            // Clear list first
            this.viewableMessageList.clear();

            // Load previous ten messages
            for (int i = 0; i <= 9; i++) {

                // Add the messages
                this.viewableMessageList.add(this.messageList.get(currentMessageIndex++));

            }
            // Move to previous page            
            currentPage--;
        }

    }

    public void markMessageAsRead(Message message) {

        try {
            // Create a new MessagesFacade
            MessagesFacade messagesFacade = new MessagesFacade(message);
            int result = messagesFacade.markMessageAsRead();
            if (result == 1) {
                // Set the message as read also
                message.setIsRead(1);
                // Decrement the number of unread messages
                noOfUnreadMessages--;
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void refreshMessages() {
        this.initializeMessageList();
    }

    public List<Message> getViewableMessageList() {
        return viewableMessageList;
    }

    public void setViewableMessageList(List<Message> viewableMessageList) {
        this.viewableMessageList = viewableMessageList;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public int getNoOfUnreadMessages() {
        return noOfUnreadMessages;
    }

    public void setNoOfUnreadMessages(int noOfUnreadMessages) {
        this.noOfUnreadMessages = noOfUnreadMessages;
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

    public int getCurrentMessageIndex() {
        return currentMessageIndex;
    }

    public void setCurrentMessageIndex(int currentMessageIndex) {
        this.currentMessageIndex = currentMessageIndex;
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

}
