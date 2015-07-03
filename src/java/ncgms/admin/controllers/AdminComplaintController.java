/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.admin.controllers;

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
import ncgms.client.controllers.ClientComplaintController;
import ncgms.entities.Complaint;
import ncgms.entities.Response;
import ncgms.daos.ComplaintsFacade;
import ncgms.daos.ResponsesFacade;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class AdminComplaintController implements Serializable {
        
    @Pattern(regexp = "^.+$", message = "Please enter response.")
    private String response;
    
    private List<Complaint> complaintList = new ArrayList<>();
    private List<Complaint> viewableComplaintList = null;
    
    /* For navigation */
    private int noOfPages = 0;
    private int currentPage = 1;
    private int currentComplaintIndex = 0;
    private boolean nextRendered = false;
    private boolean previousRendered = false;
    /* For navigation */

    /**
     * Creates a new instance of ClientComplaintController
     */
    public AdminComplaintController() {
        initializeComplaintList();
    }

    private void initializeComplaintList() {
        try {
            // Initialize the variables for navigation
            currentComplaintIndex = 0;
            currentPage = 1;
            
            // Load all the complaints 
            ComplaintsFacade complaintsFacade = new ComplaintsFacade();
            this.complaintList = complaintsFacade.loadAllComplaints();

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
                    "Could not initialize complaint list");
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
                    this.viewableComplaintList.add(
                            this.complaintList.get(currentComplaintIndex++));
                }
                // Move to next page
                currentPage++;

            } else {// Load next 10 complaints
                // Clear list first
                this.viewableComplaintList.clear();
                for (int i = 0; i <= 9; i++) {
                    // Add the complaints
                    this.viewableComplaintList.add(
                            this.complaintList.get(currentComplaintIndex++));
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
                    this.viewableComplaintList.add(
                            this.complaintList.get(currentComplaintIndex++));

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
                this.viewableComplaintList.add(
                        this.complaintList.get(currentComplaintIndex++));

            }
            // Move to previous page            
            currentPage--;
        }

    }

    public void insertResponse(Complaint complaint) {
        try {
            Response newResponse = new Response(0, this.response, new Date().getTime(),
                    0, complaint);
            ResponsesFacade responsesFacade = new ResponsesFacade(newResponse);
            int result = responsesFacade.insertResponse();
            if (result == 1) {
                complaint.getResponseList().add(newResponse);
                this.response = null;
            } else {
                // Pass
            }

        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Could not add response. Please contact the system administrator.",
                    "Could not add response. Please contact the system administrator.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminComplaintController.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            this.initializeComplaintList();
        }
    }

    public void refreshComplaints(){
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

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}
