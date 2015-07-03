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
import ncgms.entities.Complaint;
import ncgms.entities.Response;

/**
 * ResponsesFacade
 *
 * @author root
 */
public class ResponsesFacade extends AbstractFacade {

    private Response response = new Response();

    public ResponsesFacade() {
    }

    public ResponsesFacade(Response response) {
        this.response = response;
    }

    public ArrayList<Response> loadUserResponses(int userID) throws SQLException {
        connect();
        // To store client responses
        ArrayList<Response> userResponseList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `responseID`, `response`, `Responses`.`dateAdded`,"
                + " `Responses`.`isRead`, `Responses`.`complaintID` FROM "
                + " `Responses` INNER JOIN `Complaints` ON `Responses`.`complaintID`"
                + " = `Complaints`.`complaintID` WHERE `userID` = \"" + userID
                + "\" ORDER BY `responseID` DESC";

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.response = new Response(resultSet.getInt("responseID"),
                    resultSet.getString("response"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("isRead"), new Complaint(resultSet.getInt("complaintID"),
                            null, 0, 0, null));
            userResponseList.add(this.response);
        }
        disconnect();
        return userResponseList;
    }

    public ArrayList<Response> loadComplaintResponses() throws SQLException {
        connect();
        ArrayList<Response> complaintResponseList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Responses` WHERE  "
                + " `complaintID` = \"" + response.getComplaint().getComplaintID() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.response = new Response(resultSet.getInt("responseID"),
                    resultSet.getString("response"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("isRead"), new Complaint(resultSet.getInt("complaintID"),
                            null, 0, 0, null));
            complaintResponseList.add(this.response);
        }
        disconnect();
        return complaintResponseList;
    }

    public int insertResponse() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Responses`(`response`, `dateAdded`, `isRead`,"
                + " `complaintID`) VALUES(\"" + response.getResponse() + "\", \""
                + response.getDateAdded() + "\", \"" + response.getIsRead() + "\","
                + " \"" + response.getComplaint().getComplaintID() + "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int markReponseAsRead() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `Responses` SET `isRead` = \"" + 1 + "\" WHERE "
                + "`responseID` = \"" + response.getResponseID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
