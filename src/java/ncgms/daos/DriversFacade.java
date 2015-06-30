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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ncgms.daos.AbstractFacade;
import ncgms.entities.Driver;

/**
 * DriversFacade
 *
 * @author root
 */
public class DriversFacade extends AbstractFacade {

    private Driver driver = new Driver();

    public DriversFacade() {
    }

    public DriversFacade(Driver driver) {
        this.driver = driver;
    }

    public Map<Integer, String> loadDriverNamesMap() throws SQLException {
        connect();
        Map<Integer, String> subcountyNamesMap = new HashMap<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM Drivers";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            subcountyNamesMap.put(resultSet.getInt("driverID"),
                    resultSet.getString("firstName")
                    + " " + resultSet.getString("lastName"));
        }
        disconnect();
        return subcountyNamesMap;
    }

    public int updateDriver(Driver driver) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = null;
        int result = 0;
        if(driver.getPlateNumber().equals("None")){
             query = "UPDATE `Drivers` SET `plateNumber` = NULL"
                + " WHERE `driverID` = \"" + driver.getDriverID() + "\""; 
        }else{
           query = "UPDATE `Drivers` SET `plateNumber` = \"" + driver.getPlateNumber() + "\""
                + " WHERE `driverID` = \"" + driver.getDriverID() + "\""; 
        }
        result += statement.executeUpdate(query);
        disconnect();
        return result;
    }
    
    public int approveApplication() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `Users` SET `isActive` = \"" + 1 + "\" WHERE"
                + " `userID` = \"" + driver.getDriverID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public boolean driverExists() throws SQLException {
        connect();
        boolean exists = false;
        Statement statement = connection.createStatement();
        String query = "SELECT `driverID` FROM `Drivers` WHERE "
                + "`address` = \"" + driver.getAddress() + "\" OR "
                + "`email` = \"" + driver.getEmail() + "\" OR "
                + "`idNumber` = \"" + driver.getIdNumber() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            exists = true;
        }
        disconnect();
        return exists;
    }

    public int insertDriver() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Drivers` (`driverID`, `firstName`, "
                + " `lastName`, `address`, `phone`, `email`, `cvName`, `dateAdded`,"
                + " `idNumber`, `subcountyID`)"
                + " VALUES (\"" + driver.getDriverID() + "\", \"" + driver.getFirstName()
                + "\",\"" + driver.getLastName() + "\", \"" + driver.getAddress() + "\", "
                + "\"" + driver.getPhone() + "\", \"" + driver.getEmail() + "\", \""
                + driver.getCvName() + "\", \"" + driver.getDateAdded() + "\", \""
                + driver.getIdNumber() + "\", \"" + driver.getSubcountyID() + "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public ArrayList<Driver> loadAllDrivers() throws SQLException {
        connect();
        ArrayList<Driver> driverList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Drivers` INNER JOIN `Users` ON "
                + " `Drivers`.`driverID` = `Users`.`userID` WHERE "
                + " `Users`.`userName` != \"admin\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.driver = new Driver(resultSet.getInt("driverID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("address"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getString("cvName"),
                    resultSet.getString("plateNumber"), resultSet.getInt("subcountyID"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.driver.setPlateNumber("None");
            }
            // Set the subcounty
            this.driver.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            driverList.add(this.driver);
        }
        disconnect();
        return driverList;
    }

    public Driver loadTruckDriver() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT `Users`.`isActive`, `Drivers`.* FROM "
                + "`Users` INNER JOIN `Drivers` ON `Users`.`userID` = `Drivers`.`driverID`"
                + " INNER JOIN `Trucks` ON `Drivers`.`plateNumber` = `Trucks`.`plateNumber`"
                + " WHERE `Trucks`.`plateNumber` = \"" + this.driver.getPlateNumber() + "\"";
        ResultSet resultSet = statement.executeQuery(query);

        if (resultSet.next()) {
            this.driver = new Driver(resultSet.getInt("driverID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("address"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getString("cvName"),
                    resultSet.getString("plateNumber"), resultSet.getInt("subcountyID"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.driver.setPlateNumber("None");
            }
            // Set the subcounty
            this.driver.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
        }
        disconnect();
        return this.driver;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public List<Driver> searchDriverByFirstName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Driver> driverList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Drivers` INNER JOIN `Users` ON"
                + " `Drivers`.`driverID` = `Users`.`userID` WHERE `Users`.`userName` != \"admin\""
                + " AND `firstName` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.driver = new Driver(resultSet.getInt("driverID"),
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("address"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getString("cvName"),
                    resultSet.getString("plateNumber"), resultSet.getInt("subcountyID"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.driver.setPlateNumber("None");
            }
            // Set the subcounty
            this.driver.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            driverList.add(this.driver);
        }
        disconnect();
        return driverList;
    }

    public List<Driver> searchDriverByLastName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Driver> driverList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Drivers` INNER JOIN `Users` ON"
                + " `Drivers`.`driverID` = `Users`.`userID` WHERE `Users`.`userName` != \"admin\""
                + " AND `lastName` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.driver = new Driver(resultSet.getInt("driverID"), 
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("phone"), resultSet.getString("email"), 
                    resultSet.getString("address"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getString("cvName"),
                    resultSet.getString("plateNumber"), resultSet.getInt("subcountyID"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.driver.setPlateNumber("None");
            }
            // Set the subcounty
            this.driver.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            driverList.add(this.driver);
        }
        disconnect();
        return driverList;
    }

    public List<Driver> searchDriverBySubcounty(String searchTerm) throws SQLException {
        connect();
        ArrayList<Driver> driverList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Drivers` INNER JOIN `Users` ON"
                + " `Drivers`.`driverID` = `Users`.`userID` WHERE `Users`.`userName` != \"admin\""
                + " AND `subcountyID` = \"" + new SubcountiesFacade().searchSubCountyByName(
                        searchTerm).getSubcountyID() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.driver = new Driver(resultSet.getInt("driverID"), 
                    resultSet.getString("firstName"), resultSet.getString("lastName"), 
                    resultSet.getString("phone"), resultSet.getString("email"), 
                    resultSet.getString("address"), resultSet.getLong("dateAdded"), 
                    resultSet.getInt("idNumber"), resultSet.getString("cvName"),
                    resultSet.getString("plateNumber"), resultSet.getInt("subcountyID"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.driver.setPlateNumber("None");
            }
            // Set the subcounty
            this.driver.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            driverList.add(this.driver);
        }
        disconnect();
        return driverList;
    }

    public List<Driver> searchDriverByAddress(String searchTerm) throws SQLException {
        connect();
        ArrayList<Driver> driverList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Drivers` INNER JOIN `Users` ON"
                + " `Drivers`.`driverID` = `Users`.`userID` WHERE `Users`.`userName` != \"admin\""
                + " AND `address` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.driver = new Driver(resultSet.getInt("driverID"), 
                    resultSet.getString("firstName"), resultSet.getString("lastName"), 
                    resultSet.getString("phone"), resultSet.getString("email"), 
                    resultSet.getString("address"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getString("cvName"),
                    resultSet.getString("plateNumber"), resultSet.getInt("subcountyID"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.driver.setPlateNumber("None");
            }
            // Set the subcounty
            this.driver.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            driverList.add(this.driver);
        }
        disconnect();
        return driverList;
    }

    public List<Driver> searchDriverByAssignedTruck(String searchTerm) throws SQLException {
        connect();
        ArrayList<Driver> driverList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Drivers` INNER JOIN `Users` ON"
                + " `Drivers`.`driverID` = `Users`.`userID` WHERE `Users`.`userName` != \"admin\""
                + " AND `plateNumber` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.driver = new Driver(resultSet.getInt("driverID"), 
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("phone"), resultSet.getString("email"),
                    resultSet.getString("address"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getString("cvName"),
                    resultSet.getString("plateNumber"), resultSet.getInt("subcountyID"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.driver.setPlateNumber("None");
            }
            // Set the subcounty
            this.driver.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            driverList.add(this.driver);
        }
        disconnect();
        return driverList;
    }

    public List<Driver> searchDriverByPhoneNumber(String searchTerm) throws SQLException {
        connect();
        ArrayList<Driver> driverList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Drivers` INNER JOIN `Users` ON"
                + " `Drivers`.`driverID` = `Users`.`userID` WHERE `Users`.`userName` != \"admin\""
                + " AND `phone` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.driver = new Driver(resultSet.getInt("driverID"), 
                    resultSet.getString("firstName"), resultSet.getString("lastName"), 
                    resultSet.getString("phone"), resultSet.getString("email"), 
                    resultSet.getString("address"), resultSet.getLong("dateAdded"), 
                    resultSet.getInt("idNumber"), resultSet.getString("cvName"),
                    resultSet.getString("plateNumber"), resultSet.getInt("subcountyID"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.driver.setPlateNumber("None");
            }
            // Set the subcounty
            this.driver.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            driverList.add(this.driver);
        }
        disconnect();
        return driverList;
    }

    public List<Driver> searchDriverByEmail(String searchTerm) throws SQLException {
        connect();
        ArrayList<Driver> driverList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Drivers` INNER JOIN `Users` ON "
                + " `Drivers`.`driverID` = `Users`.`userID` WHERE `Users`.`userName` != \"admin\""
                + " AND `email` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.driver = new Driver(resultSet.getInt("driverID"), 
                    resultSet.getString("firstName"), resultSet.getString("lastName"),
                    resultSet.getString("phone"), resultSet.getString("email"), 
                    resultSet.getString("address"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("idNumber"), resultSet.getString("cvName"),
                    resultSet.getString("plateNumber"), resultSet.getInt("subcountyID"),
                    resultSet.getInt("isActive"));
            if (resultSet.getString("plateNumber") == null) {
                this.driver.setPlateNumber("None");
            }
            // Set the subcounty
            this.driver.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            driverList.add(this.driver);
        }
        disconnect();
        return driverList;
    }

    public int removeDriver() throws SQLException {
        connect();
        int result = 0;
        Statement statement = connection.createStatement();
        String query = "DELETE FROM `Users` WHERE `userID` = \"" + this.driver.getDriverID() + "\"";
        result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

}
