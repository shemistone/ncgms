/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.client.controllers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Pattern;
import ncgms.controllers.LogInLogOutController;
import ncgms.entities.Complaint;
import ncgms.entities.User;
import ncgms.daos.ComplaintsFacade;
import ncgms.daos.UsersFacade;
import ncgms.entities.Client;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class ClientComplaintController implements Serializable {

    private List<Complaint> complaintList = null;
    private List<Complaint> viewableComplaintList = null;
    private boolean noComplaintsRendered = false;

    @Pattern(regexp = "^.+$", message = "The Complaint/Enquiry cannot be empty.")
    private String complaint;

    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentComplaintIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    /**
     * Creates a new instance of ComplaintController
     */
    public ClientComplaintController() {
        initializeComplaintList();
    }

    private void initializeComplaintList() {
        try {
            // Initialize the variables for navigation
            currentComplaintIndex = 0;
            currentPage = 1;

            // Create user object for current client            
            Client client = (Client) new LogInLogOutController().getClientFromSession();
            // Create a new complaint
            Complaint complaint = new Complaint();
            complaint.setUser(client);
            ComplaintsFacade complaintsFacade = new ComplaintsFacade(complaint);
            // Load all the complaints            
            this.complaintList = complaintsFacade.loadUserComplaints();

            // Set the number of pages
            this.noOfPages = this.complaintList.size() / 10;
            // Set the last page
            if (this.complaintList.size() % 10 > 0) {
                this.noOfPages = this.noOfPages + 1;
            }

            this.viewableComplaintList = new ArrayList<>();
            /* Initialize the complaints */
            int index;
            if (this.complaintList.size() >= 10) {// if there are more than 10 complaints

                // Set the first 10 complaints
                for (index = 0; index <= 9; index++) {
                    this.viewableComplaintList.add(this.complaintList.get(index));
                }
                currentComplaintIndex = index;
            } else {
                this.viewableComplaintList = this.complaintList;
                currentComplaintIndex = this.viewableComplaintList.size();
            }
            /* Initialize the complaints */
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not initialize complaint list.",
                    "Could not initialize complaint list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientComplaintController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nextComplaintPage() {

        // Check if index is more than complaintList size
        if (currentComplaintIndex < this.complaintList.size()) {// You are not at the last page
            int nextPage = currentPage + 1;
            // Check if next page is last page
            if (nextPage == this.noOfPages) {// Load the remaining complaints
                // Clear list first
                this.viewableComplaintList.clear();
                for (; currentComplaintIndex < this.complaintList.size();) {
                    // Add the complaints
                    this.viewableComplaintList.add(this.complaintList.get(currentComplaintIndex++));
                }
                // Move to next page
                currentPage++;

            } else {// Load next 10 complaints
                // Clear list first
                this.viewableComplaintList.clear();
                for (int i = 0; i <= 9; i++) {
                    // Add the complaints
                    this.viewableComplaintList.add(this.complaintList.get(currentComplaintIndex++));
                }

                // Move to next page            
                currentPage++;
            }
        } else {// You are at the last page
            // Set currentComplaintIndex to the size of the complaintList
            currentComplaintIndex = this.complaintList.size();
        }
    }

    public void previousComplaintPage() {

        if (currentComplaintIndex < this.complaintList.size()) {// You are not at the last page
            int previousPage = currentPage - 1;
            // Check if previous page is the first page
            if (previousPage < 1) {
                // Pass
            } else {
                //Move back 20 elements
                currentComplaintIndex -= 20;
                // Clear list first
                this.viewableComplaintList.clear();

                // Load previous ten complaints
                for (int i = 0; i <= 9; i++) {

                    // Add the complaints
                    this.viewableComplaintList.add(this.complaintList.get(currentComplaintIndex++));

                }
                // Move to previous page            
                currentPage--;
            }

        } else {// You are at the last page
            //Move back 10 + last page elements
            currentComplaintIndex -= (10 + this.viewableComplaintList.size());
            // Clear list first
            this.viewableComplaintList.clear();

            // Load previous ten complaints
            for (int i = 0; i <= 9; i++) {

                // Add the complaints
                this.viewableComplaintList.add(this.complaintList.get(currentComplaintIndex++));

            }
            // Move to previous page            
            currentPage--;
        }

    }

    public void insertComplaint() {
        try {
            // Create user object for user who launched the complaint
            User user = new User(null, null, 0);
            UsersFacade usersFacade = new UsersFacade(user);

            // Set the username of the new user
            usersFacade.getUser().setUsername(new LogInLogOutController().
                    getUserFromSession().getUsername());

            // Get the userID of the new user
            int userID = usersFacade.loadUserID();

            // Create a new complaint
            Complaint complaint = new Complaint(this.complaint, 0, new Date().getTime(),
                    new User(userID, null, null, 0));
            ComplaintsFacade complaintsFacade = new ComplaintsFacade(complaint);

            // Now insert the new complaint into the database
            int complaintResult = complaintsFacade.insertComplaint();

            if (complaintResult == 1) {

                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Your complaint has been successfully received.",
                        "Your complaint has been successfully received.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                // Initialize complaintList again
                initializeComplaintList();
                this.complaint = null;

            } else {
                // Pass
            }

        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not insert complaint.",
                    "Could not insert complaint.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientComplaintController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void refreshComplaints() {
        this.initializeComplaintList();
    }

    public List<Complaint> getComplaintList() {
        return complaintList;
    }

    public void setComplaintList(List<Complaint> complaintList) {
        this.complaintList = complaintList;
    }

    public List<Complaint> getViewableComplaintList() {
        return viewableComplaintList;
    }

    public void setViewableComplaintList(List<Complaint> viewableComplaintList) {
        this.viewableComplaintList = viewableComplaintList;
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

    public int getCurrentComplaintIndex() {
        return currentComplaintIndex;
    }

    public void setCurrnetComplaintIndex(int currentComplaintIndex) {
        this.currentComplaintIndex = currentComplaintIndex;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
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

    public boolean isNoComplaintsRendered() {
        return complaintList.isEmpty();
    }

    public void setNoComplaintsRendered(boolean noComplaintsRendered) {
        this.noComplaintsRendered = noComplaintsRendered;
    }

}
