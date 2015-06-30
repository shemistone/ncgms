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
import java.util.List;
import ncgms.daos.AbstractFacade;
import ncgms.entities.Client;

/**
 * ClientsFacade
 *
 * @author root
 */
public class ClientsFacade extends AbstractFacade {

    private Client client = new Client();

    public ClientsFacade() {
    }

    public ClientsFacade(Client client) {
        this.client = client;
    }

    public int approveApplication() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `Users` SET `isActive` = \"" + 1
                + "\" WHERE `userID` = \"" + client.getClientID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int updateClient(Client client) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = null;
        int result = 0;
        if(client.getPlateNumber().equals("None")){
            query = "UPDATE `Clients` SET `plateNumber` = NULL"
                + " WHERE `clientID` = \"" + client.getClientID() + "\"";
        }else{
            query = "UPDATE `Clients` SET `plateNumber` = \"" + client.getPlateNumber() + "\""
                + " WHERE `clientID` = \"" + client.getClientID() + "\"";
        }        
        result += statement.executeUpdate(query);
        disconnect();
        return result;
    }

    /**
     * Check if the client already exists
     *
     * @return true if client exists
     * @throws SQLException
     */
    public boolean clientExists() throws SQLException {
        connect();
        boolean exists = false;
        Statement statement = connection.createStatement();
        String query = "SELECT `clientID` FROM `Clients` WHERE "
                + "`address` = \"" + client.getAddress() + "\" OR "
                + "`email` = \"" + client.getEmail() + "\" OR "
                + "`idNumber` = \"" + client.getIdNumber() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            exists = true;
        }
        disconnect();
        return exists;
    }

    /**
     * Insert the client into the database
     *
     * @return the number of affected rows
     * @throws SQLException
     */
    public int insertClient() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Clients` (`clientID`, `firstName`, "
                + " `lastName`, `address`, `phone`, `email`, `plotName`, "
                + " `dateAdded`, `idNumber`, `subcountyID`, `wantsToCancel`, "
                + " `packageName`) VALUES (\"" + client.getClientID() + "\","
                + " \"" + client.getFirstName() + "\", \""
                + client.getLastName() + " \", \"" + client.getAddress() + "\","
                + " \"" + client.getPhone() + "\", \"" + client.getEmail()
                + " \", \"" + client.getPlotName() + "\", \"" + client.getDateAdded()
                + " \", \"" + client.getIdNumber() + "\", "
                + " \"" + client.getSubcountyID() + "\", \"" + 0
                + " \", \"" + client.getPackageName() + "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int cancelService() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `Clients` SET `wantsToCancel` = \"" + 1 + "\" "
                + " WHERE `clientID` = \"" + client.getClientID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public ArrayList<Client> loadAllClients() throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON "
                + " `Clients`.`clientID` = `Users`.`userID` WHERE "
                + " `Users`.`userName` != \"admin\" ORDER BY `clientID` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"), resultSet.getInt("wantsToCancel"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(this.client);
        }
        disconnect();
        return clientList;
    }
  
    public boolean wantsToCancel() throws SQLException {
        connect();
        int wantsToCancel = 0;
        Statement statement = connection.createStatement();
        String query = "SELECT `wantsToCancel` FROM `Clients` INNER JOIN `Users` "
                + " ON `Clients`.`clientID` = `Users`.`userID` WHERE "
                + " `username` = '" + this.client.getUsername() + "' AND "
                + " `passwordHash` = '" + this.client.getPasswordHash() + "'";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            wantsToCancel = resultSet.getInt("wantsToCancel");
        }
        disconnect();
        return wantsToCancel == 1;
    }

    public ArrayList<Client> loadTruckClients() throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Users`.`isActive`, `Clients`.* FROM `Users` "
                + " INNER JOIN `Clients` ON `Users`.`userID` = `Clients`.`clientID`"
                + " INNER JOIN `Trucks` ON `Clients`.`plateNumber` = "
                + " `Trucks`.`plateNumber` WHERE `Trucks`.`plateNumber` = \""
                + client.getPlateNumber() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(client);
        }
        disconnect();
        return clientList;
    }
    
    public List<Client> searchClientByPlotName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON "
                + " `Clients`.`clientID` = `Users`.`userID` "
                + " WHERE `plotName` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"), resultSet.getInt("wantsTooCancel"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(this.client);
        }
        disconnect();
        return clientList;
    }

    public List<Client> searchClientByFirstName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON"
                + " `Clients`.`clientID` = `Users`.`userID` "
                + " WHERE `firstName` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(client);
        }
        disconnect();
        return clientList;
    }

    public List<Client> searchClientByLastName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON"
                + " `Clients`.`clientID` = `Users`.`userID` "
                + " WHERE `lastName` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(client);
        }
        disconnect();
        return clientList;
    }

    public List<Client> searchClientBySubcounty(String searchTerm) throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON"
                + " `Clients`.`clientID` = `Users`.`userID` "
                + " WHERE `subcountyID` = \"" + new SubcountiesFacade().searchSubCountyByName(
                        searchTerm).getSubcountyID() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(this.client);
        }
        disconnect();
        return clientList;
    }

    public List<Client> searchClientByAddress(String searchTerm) throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON"
                + " `Clients`.`clientID` = `Users`.`userID` "
                + " WHERE `address` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(client);
        }
        disconnect();
        return clientList;
    }

    public List<Client> searchClientByAssignedTruck(String searchTerm) throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON"
                + " `Clients`.`clientID` = `Users`.`userID` "
                + " WHERE `plateNumber` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(this.client);
        }
        disconnect();
        return clientList;
    }

    public List<Client> searchClientByPhoneNumber(String searchTerm) throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON"
                + " `Clients`.`clientID` = `Users`.`userID` "
                + " WHERE `phone` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(this.client);
        }
        disconnect();
        return clientList;
    }

    public List<Client> searchClientByEmail(String searchTerm) throws SQLException {
        connect();
        ArrayList<Client> clientList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON"
                + " `Clients`.`clientID` = `Users`.`userID` "
                + " WHERE `email` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            clientList.add(this.client);
        }
        disconnect();
        return clientList;
    }

    public Client searchClientByClientID(int searchTerm) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Clients` INNER JOIN `Users` ON"
                + " `Clients`.`clientID` = `Users`.`userID` "
                + " WHERE `Clients`.`clientID` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            this.client = new Client(resultSet.getInt("clientID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("address"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("plotName"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getInt("subcountyID"), resultSet.getString("plateNumber"),
                    resultSet.getInt("isActive"), resultSet.getInt("wantsToCancel"));
            this.client.setUsername(resultSet.getString("userName"));
            if (resultSet.getString("plateNumber") == null) {
                this.client.setPlateNumber("None");
            }
            // Set the subcounty
            this.client.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
        }
        disconnect();
        return this.client;
    }

    public int removeClient() throws SQLException {
        connect();
        int result = 0;
        Statement statement = connection.createStatement();
        String query = "DELETE FROM `Users` WHERE `userID` = \""
                + this.client.getClientID() + "\"";
        result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
