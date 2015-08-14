/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import ncgms.entities.LogisticsManager;

/**
 *
 * @author root
 */
public class LogisticsManagersFacade extends AbstractFacade {
    
    private LogisticsManager logisticsManager = null;

    
    public LogisticsManagersFacade() {
    }

    public LogisticsManagersFacade(LogisticsManager logisticsManager) {
        this.logisticsManager = logisticsManager;
    }

    public LogisticsManager getLogisticsManager() {
        return logisticsManager;
    }

    public void setLogisticsManager(LogisticsManager logisticsManager) {
        this.logisticsManager = logisticsManager;
    }

    public int insertLogisticsManager() throws SQLException {
        connect();
        Statement statment = connection.createStatement();
        String query = "INSERT INTO `LogisticsManagers` (`managerID`, `firstName`, "
                + "`lastName`, `email`, `phoneNo`) VALUES(\""
                + this.logisticsManager.getUserID() + "\","
                + " \"" + this.logisticsManager.getFirstName() + "\","
                + " \"" + this.logisticsManager.getLastName() + "\", "
                + " \"" + this.logisticsManager.getEmail() + "\", "
                + " \"" + this.logisticsManager.getPhoneNo() + "\")";
        int result = statment.executeUpdate(query);
        disconnect();
        return result;
    }

    public LogisticsManager searchLogisticsManagerByPhone(String searchTerm) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN `LogisticsManagers`  ON "
                + " `Users`.`userID` = `LogisticsManagers`.`managerID` WHERE"
                + " `phoneNo` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            this.logisticsManager = new LogisticsManager(resultSet.getInt("managerID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("email"),
                    resultSet.getString("phoneNo"));
        }
        return this.logisticsManager;
    }
    
    public LogisticsManager searchLogisticsManagerByEmail(String searchTerm) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN `LogisticsManagers` ON "
                + " `Users`.`userID` = `LogisticsManagers`.`managerID` WHERE"
                + " `email` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            this.logisticsManager = new LogisticsManager(resultSet.getInt("managerID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("email"),
                    resultSet.getString("phoneNo"));
        }
        return this.logisticsManager;
    }
    
    public LogisticsManager searchLogisticsManagerByID(int searchTerm) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `LogisticsManagers` INNER JOIN `Users` ON"
                + " `LogisticsManagers`.`managerID` = `Users`.`userID` "
                + " WHERE `LogisticsManagers`.`managerID` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            this.logisticsManager = new LogisticsManager(resultSet.getInt("managerID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("email"),
                    resultSet.getString("phoneNo"));

        }
        disconnect();
        return this.logisticsManager;
    }

    public LogisticsManager searchLogisticsManagerByUsername(String searchTerm) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN `LogisticsManagers`  ON "
                + " `Users`.`userID` = `LogisticsManagers`.`managerID` WHERE"
                + " `username` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            this.logisticsManager = new LogisticsManager(resultSet.getInt("managerID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("email"),
                    resultSet.getString("phoneNo"));
        }
        return this.logisticsManager;
    }


}
