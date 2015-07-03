/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import ncgms.entities.Client;
import ncgms.entities.Complaint;
import ncgms.entities.Response;

/**
 * ComplaintsFacade
 *
 * @author root
 */
public class ComplaintsFacade extends AbstractFacade {

    private Complaint complaint = new Complaint();

    public ComplaintsFacade() {
    }

    public ComplaintsFacade(Complaint complaint) {
        this.complaint = complaint;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public int insertComplaint() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Complaints` (`complaint`, `isRead`, "
                + " `dateAdded`, `userID`) VALUES (\"" + complaint.getComplaint()
                + " \", \"" + complaint.getIsRead() + "\", \"" 
                + complaint.getDateAdded() + "\", \"" + complaint.getUser().getUserID() + "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public ArrayList<Complaint> loadUserComplaints() throws SQLException {
        connect();
        // Complaint list to be returned
        ArrayList<Complaint> complaintList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `complaintID`, `complaint`, `dateAdded`, `isRead` "
                + " FROM `Complaints` WHERE `userID` = \"" + complaint.getUser().getUserID()
                + " \" ORDER BY `complaintID` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create a new complaint
            this.complaint = new Complaint(resultSet.getInt("complaintID"), 
                    resultSet.getString("complaint"), resultSet.getInt("isRead"), 
                    resultSet.getLong("dateAdded"), null);

            // Add complaints to the complaintList
            complaintList.add(this.complaint);
        }

        // Set the responses for each complaint in complaintList
        for(Complaint newComplaint: complaintList){
            Response response = new Response();
            response.setComplaint(newComplaint);
            ResponsesFacade responsesFacade = new ResponsesFacade(response);
            newComplaint.setResponseList(responsesFacade.loadComplaintResponses());
        }
        disconnect();
        return complaintList;
    }

    public ArrayList<Complaint> loadAllComplaints() throws SQLException {
        connect();
        // Complaint list to be returned
        ArrayList<Complaint> complaintList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Complaints` "
                + " INNER JOIN `Clients` ON `Complaints`.`userID` = "
                + "`Clients`.`clientID` INNER JOIN `Users` ON `Clients`.`clientID` ="
                + " `Users`.`userID` ORDER BY `complaintID` DESC";
        ResultSet resultSet = statement.executeQuery(query);System.out.print(query);

        while (resultSet.next()) {
            // Create a new complaint
            this.complaint = new Complaint(resultSet.getInt("complaintID"),
                    resultSet.getString("complaint"), resultSet.getInt("isRead"), 
                    resultSet.getLong("dateAdded"), null);
            // Create a new client
            Client client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("address"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("plotName"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getInt("wantsToCancel"));
            // Set the client
            this.complaint.setUser(client);
            // Add complaints to the complaintList
            complaintList.add(this.complaint);
        }

        // Set the responses for each complaint in complaintList
        for(Complaint newComplaint: complaintList){
            Response response = new Response();
            response.setComplaint(newComplaint);
            ResponsesFacade responsesFacade = new ResponsesFacade(response);
            newComplaint.setResponseList(responsesFacade.loadComplaintResponses());
        }
        disconnect();
        return complaintList;
    }

}
