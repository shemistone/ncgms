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
import ncgms.entities.Model;
import ncgms.entities.Tout;
import ncgms.entities.Truck;

/**
 * ToutsFacade
 *
 * @author root
 */
public class ToutsFacade extends AbstractFacade {

    private Tout tout = new Tout();

    public ToutsFacade() {
    }

    public ToutsFacade(Tout tout) {
        this.tout = tout;
    }

    public int approveApplication() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `Users` SET `isActive` = \"" + 1 + "\" WHERE "
                + " `userID` = \"" + tout.getUserID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int updateTout(Tout tout) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = null;
        int result = 0;
        if(tout.getTruck().getPlateNumber().equals("None")){
           query = "UPDATE `Touts` SET `plateNumber` = NULL"
                + " WHERE `toutID` = \"" + tout.getUserID() + "\"";         
        }else{
            query = "UPDATE `Touts` SET `plateNumber` = \"" + tout.getTruck().getPlateNumber() + "\""
                + " WHERE `toutID` = \"" + tout.getUserID() + "\"";
        }
        result += statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public ArrayList<Tout> loadAllTouts() throws SQLException {
        connect();
        ArrayList<Tout> toutList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN `Touts` ON "
                + " `Users`.`userID` = `Touts`.`toutID`  LEFT JOIN `Trucks` ON"
                + " `Touts`.`plateNumber` = `Trucks`.`plateNumber` WHERE "
                + " `Users`.`userName` != \"admin\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.tout = new Tout(resultSet.getInt("toutID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("address"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getString("cvName"));
            //Set the truck
            this.tout.setTruck(new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null)));
            //Set the plateNumber
            if (resultSet.getString("plateNumber") == null) {
                this.tout.getTruck().setPlateNumber("None");
            }
            // Set the subcounty
            this.tout.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            toutList.add(this.tout);
        }
        disconnect();
        return toutList;
    }

    public ArrayList<Tout> loadTruckTouts() throws SQLException {
        connect();
        ArrayList<Tout> toutList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN"
                + " `Touts` ON `Users`.`userID` = `Touts`.`toutID` INNER JOIN "
                + " `Trucks` ON `Touts`.`plateNumber` = `Trucks`.`plateNumber` WHERE "
                + " `Trucks`.`plateNumber` = \"" + tout.getTruck().getPlateNumber() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.tout = new Tout(resultSet.getInt("toutID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("address"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getString("cvName"));
            //Set the truck
            this.tout.setTruck(new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null)));
            //Set the plateNumber
            if (resultSet.getString("plateNumber") == null) {
                this.tout.getTruck().setPlateNumber("None");
            }
            // Set the subcounty
            this.tout.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            toutList.add(this.tout);
        }
        disconnect();
        return toutList;
    }

    public boolean toutExists() throws SQLException {
        connect();
        boolean exists = false;
        Statement statement = connection.createStatement();
        String query = "SELECT `toutID` FROM `Touts` WHERE "
                + "`address` = \"" + tout.getAddress() + "\" OR "
                + "`email` = \"" + tout.getEmail() + "\" OR "
                + "`idNumber` = \"" + tout.getIdNumber() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            exists = true;
        }
        disconnect();
        return exists;
    }

    public int insertTout() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Touts` (`toutID`, `firstName`, `lastName`, "
                + " `address`, `phone`, `email`, `cvName`, `dateAdded`, `idNumber`,"
                + " `subcountyID`)"
                + " VALUES (\"" + tout.getUserID() + "\", \"" + tout.getFirstName()
                + "\",\"" + tout.getLastName() + "\", \"" + tout.getAddress() + "\", "
                + "\"" + tout.getPhone() + "\", \"" + tout.getEmail() + "\", \""
                + tout.getCvName() + "\", \"" + tout.getDateAdded() + "\", \""
                + tout.getIdNumber() + "\", "
                + "\"" + tout.getSubcounty().getSubcountyID()+ "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public Tout getTout() {
        return tout;
    }

    public void setTout(Tout tout) {
        this.tout = tout;
    }

    public List<Tout> searchToutByEmail(String searchTerm) throws SQLException {
        connect();
        ArrayList<Tout> toutList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN"
                + " `Touts` ON `Users`.`userID` = `Touts`.`toutID` LEFT JOIN "
                + " `Trucks` ON `Touts`.`plateNumber` = `Trucks`.`plateNumber`"
                + "  WHERE `Users`.`userName` != \"admin\" AND `email` = \"" 
                + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.tout = new Tout(resultSet.getInt("toutID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("address"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getString("cvName"));
            //Set the truck
            this.tout.setTruck(new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null)));
            //Set the plateNumber
            if (resultSet.getString("plateNumber") == null) {
                this.tout.getTruck().setPlateNumber("None");
            }
            // Set the subcounty
            this.tout.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            toutList.add(this.tout);
        }
        disconnect();
        return toutList;
    }

    public List<Tout> searchToutByPhoneNumber(String searchTerm) throws SQLException {
        connect();
        ArrayList<Tout> toutList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN"
                + " `Touts` ON `Users`.`userID` = `Touts`.`toutID` LEFT JOIN "
                + " `Trucks` ON `Touts`.`plateNumber` = `Trucks`.`plateNumber`"
                + " WHERE `Users`.`userName` != \"admin\""
                + " AND `phone` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.tout = new Tout(resultSet.getInt("toutID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("address"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getString("cvName"));
            //Set the truck
            this.tout.setTruck(new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null)));
            //Set the plateNumber
            if (resultSet.getString("plateNumber") == null) {
                this.tout.getTruck().setPlateNumber("None");
            }
            // Set the subcounty
            this.tout.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            toutList.add(this.tout);
        }
        disconnect();
        return toutList;
    }

    public List<Tout> searchToutByAssignedTruck(String searchTerm) throws SQLException {
        connect();
        ArrayList<Tout> toutList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN"
                + " `Touts` ON `Users`.`userID` = `Touts`.`toutID` LEFT JOIN "
                + " `Trucks` ON `Touts`.`plateNumber` = `Trucks`.`plateNumber`"
                + " WHERE `Users`.`userName` != \"admin\""
                + " AND `plateNumber` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.tout = new Tout(resultSet.getInt("toutID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("address"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getString("cvName"));
            //Set the truck
            this.tout.setTruck(new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null)));
            //Set the plateNumber
            if (resultSet.getString("plateNumber") == null) {
                this.tout.getTruck().setPlateNumber("None");
            }
            // Set the subcounty
            this.tout.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            toutList.add(this.tout);
        }
        disconnect();
        return toutList;
    }

    public List<Tout> searchToutByAddress(String searchTerm) throws SQLException {
        connect();
        ArrayList<Tout> toutList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN"
                + " `Touts` ON `Users`.`userID` = `Touts`.`toutID` LEFT JOIN "
                + " `Trucks` ON `Touts`.`plateNumber` = `Trucks`.`plateNumber`"
                + " WHERE `Users`.`userName` != \"admin\""
                + " AND `address` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.tout = new Tout(resultSet.getInt("toutID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("address"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getString("cvName"));
            //Set the truck
            this.tout.setTruck(new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null)));
            //Set the plateNumber
            if (resultSet.getString("plateNumber") == null) {
                this.tout.getTruck().setPlateNumber("None");
            }
            // Set the subcounty
            this.tout.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            toutList.add(this.tout);
        }
        disconnect();
        return toutList;
    }

    public List<Tout> searchToutBySubcounty(String searchTerm) throws SQLException {
        connect();
        ArrayList<Tout> toutList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN"
                + " `Touts` ON `Users`.`userID` = `Touts`.`toutID` LEFT JOIN "
                + " `Trucks` ON `Touts`.`plateNumber` = `Trucks`.`plateNumber`"
                + " WHERE `Users`.`userName` != \"admin\""
                + " AND `subcountyID` = \"" + new SubcountiesFacade().searchSubCountyByName(
                        searchTerm).getSubcountyID() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.tout = new Tout(resultSet.getInt("toutID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("address"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getString("cvName"));
            //Set the truck
            this.tout.setTruck(new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null)));
            //Set the plateNumber
            if (resultSet.getString("plateNumber") == null) {
                this.tout.getTruck().setPlateNumber("None");
            }
            // Set the subcounty
            this.tout.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            toutList.add(this.tout);
        }
        disconnect();
        return toutList;
    }

    public List<Tout> searchToutByLastName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Tout> toutList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN"
                + " `Touts` ON `Users`.`userID` = `Touts`.`toutID` LEFT JOIN "
                + " `Trucks` ON `Touts`.`plateNumber` = `Trucks`.`plateNumber`"
                + " WHERE `Users`.`userName` != \"admin\""
                + " AND `lastName` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
           this.tout = new Tout(resultSet.getInt("toutID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("address"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getString("cvName"));
            //Set the truck
            this.tout.setTruck(new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null)));
            //Set the plateNumber
            if (resultSet.getString("plateNumber") == null) {
                this.tout.getTruck().setPlateNumber("None");
            }
            // Set the subcounty
            this.tout.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            toutList.add(this.tout);
        }
        disconnect();
        return toutList;
    }

    public List<Tout> searchToutByFirstName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Tout> toutList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` INNER JOIN"
                + " `Touts` ON `Users`.`userID` = `Touts`.`toutID` LEFT JOIN "
                + " `Trucks` ON `Touts`.`plateNumber` = `Trucks`.`plateNumber`"
                + " WHERE `Users`.`userName` != \"admin\""
                + " AND `firstName` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
           this.tout = new Tout(resultSet.getInt("toutID"),
                    resultSet.getString("username"), resultSet.getString("passwordHash"),
                    resultSet.getInt("isActive"), resultSet.getString("firstName"),
                    resultSet.getString("lastName"), resultSet.getString("phone"),
                    resultSet.getString("email"), resultSet.getString("address"),
                    resultSet.getLong("dateAdded"), resultSet.getInt("idNumber"),
                    resultSet.getString("cvName"));
            //Set the truck
            this.tout.setTruck(new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null)));
            //Set the plateNumber
            if (resultSet.getString("plateNumber") == null) {
                this.tout.getTruck().setPlateNumber("None");
            }
            // Set the subcounty
            this.tout.setSubcounty(new SubcountiesFacade().searchSubCountyById(
                    resultSet.getInt("subcountyID")));
            toutList.add(this.tout);
        }
        disconnect();
        return toutList;
    }

    public int removeTout() throws SQLException {
        connect();
        int result = 0;
        Statement statement = connection.createStatement();
        String query = "DELETE FROM `Users` WHERE `userID` = \"" + this.tout.getUserID() + "\"";
        result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

}
