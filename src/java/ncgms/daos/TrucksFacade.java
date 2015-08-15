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
import java.util.Date;
import java.util.List;
import ncgms.entities.Client;
import ncgms.entities.Driver;
import ncgms.entities.Model;
import ncgms.entities.Tout;
import ncgms.entities.Truck;

/**
 *
 * @author root
 */
public class TrucksFacade extends AbstractFacade {

    private Truck truck = new Truck();

    public TrucksFacade() {

    }

    public TrucksFacade(Truck truck) {
        this.truck = truck;
    }

    public ArrayList<Truck> loadAllTrucks() throws SQLException {
        connect();
        ArrayList<Truck> truckList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Trucks` ORDER BY `dateAdded` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.truck = new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"),
                            new ModelsFacade().searchModelById(
                                    resultSet.getInt("modelID")).getModelName()));
            truckList.add(this.truck);
        }

        // Add the clients to each truck
        for (Truck newTruck : truckList) {
            // Create a client and set truck
            Client client = new Client();
            client.setTruck(newTruck);

            // Select all the clients served by this truck
            ClientsFacade clientsFacade = new ClientsFacade(client);
            newTruck.setClientList(clientsFacade.loadTruckClients());

        }

        // Add the touts to each truck
        for (Truck newTruck : truckList) {
            // Create tout and set plate number
            Tout tout = new Tout();
            tout.setTruck(newTruck);
            // Select all the touts who work with this truck
            ToutsFacade toutsFacade = new ToutsFacade(tout);
            newTruck.setToutList(toutsFacade.loadTruckTouts());

        }

        // Add the driver to each truck
        for (Truck newTruck : truckList) {
            // Create a new driver and set the plateNumber
            Driver driver = new Driver();
            driver.setTruck(newTruck);
            // Load the driver of this truck
            DriversFacade driversFacade = new DriversFacade(driver);
            newTruck.setDriver(driversFacade.loadTruckDriver());
        }

        disconnect();
        return truckList;
    }

    public boolean truckExists() throws SQLException {
        connect();
        boolean exists = false;
        Statement statement = connection.createStatement();
        String query = "SELECT `plateNumber` FROM `Trucks` WHERE "
                + "`plateNumber` = \"" + truck.getPlateNumber() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            exists = true;
        }
        disconnect();
        return exists;
    }

    public ArrayList<String> populatePlateNumberList() throws SQLException {
        connect();
        ArrayList<String> plateNumberList = new ArrayList<>();
        plateNumberList.add("None");
        Statement statement = connection.createStatement();
        String query = "SELECT `plateNumber` FROM `Trucks` WHERE `inService` = \"" + 1 + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            String plateNumber = resultSet.getString("plateNumber");
            plateNumberList.add(plateNumber);
        }
        disconnect();
        return plateNumberList;
    }

    public int suspendTruck() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `Trucks` SET `inService` = \"" + 0 + "\" WHERE "
                + " `plateNumber` = \"" + truck.getPlateNumber() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int unSuspendTruck() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `Trucks` SET `inService` = \"" + 1 + "\" WHERE "
                + " `plateNumber` = \"" + truck.getPlateNumber() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int removeTruck() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "DELETE FROM `Trucks` WHERE `plateNUmber` = \""
                + truck.getPlateNumber() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int insertTruck() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Trucks` (`plateNumber`, `inService`, "
                + " `dateAdded`, `modelID`) VALUES(\"" + truck.getPlateNumber()
                + "\", \"" + truck.getInService() + "\", \"" + new Date().getTime()
                + "\", \"" + truck.getModel().getModelID() + "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public List<Truck> searchTruckByPlateNumber(String searchTerm) throws SQLException {
        connect();
        ArrayList<Truck> truckList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Trucks` WHERE `plateNumber` = \"" + searchTerm
                + "\" ORDER BY `dateAdded` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.truck = new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null));
            truckList.add(this.truck);
        }

        // Add the clients to each truck
        for (Truck newTruck : truckList) {
            // Create a client and set plateNumber
            Client client = new Client();
            client.setTruck(newTruck);

            // Select all the clients served by this truck
            ClientsFacade clientsFacade = new ClientsFacade(client);
            newTruck.setClientList(clientsFacade.loadTruckClients());

        }

        // Add the touts to each truck
        for (Truck newTruck : truckList) {
            // Create tout and set plate number
            Tout tout = new Tout();
            tout.setTruck(newTruck);
            // Select all the touts who work with this truck
            ToutsFacade toutsFacade = new ToutsFacade(tout);
            newTruck.setToutList(toutsFacade.loadTruckTouts());

        }

        // Add the driver to each truck
        for (Truck newTruck : truckList) {
            // Create a new driver and set the plateNumber
            Driver driver = new Driver();
            driver.setTruck(newTruck);
            // Load the driver of this truck
            DriversFacade driversFacade = new DriversFacade(driver);
            newTruck.setDriver(driversFacade.loadTruckDriver());
        }
        disconnect();
        return truckList;
    }

    public List<Truck> searchTruckByModelName(String searchTerm) throws SQLException {
        connect();
        ArrayList<Truck> truckList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Trucks` WHERE `modelID` = \""
                + new ModelsFacade().searchModelByName(searchTerm).getModelID()
                + "\" ORDER BY `dateAdded` DESC";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.truck = new Truck(resultSet.getString("plateNumber"),
                    resultSet.getInt("inService"), resultSet.getLong("dateAdded"),
                    new Model(resultSet.getInt("modelID"), null));
            truckList.add(this.truck);
        }

        // Add the clients to each truck
        for (Truck newTruck : truckList) {
            // Create a client and set plateNumber
            Client client = new Client();
            client.setTruck(newTruck);

            // Select all the clients served by this truck
            ClientsFacade clientsFacade = new ClientsFacade(client);
            newTruck.setClientList(clientsFacade.loadTruckClients());

        }

        // Add the touts to each truck
        for (Truck newTruck : truckList) {
            // Create tout and set plate number
            Tout tout = new Tout();
            tout.setTruck(newTruck);
            // Select all the touts who work with this truck
            ToutsFacade toutsFacade = new ToutsFacade(tout);
            newTruck.setToutList(toutsFacade.loadTruckTouts());

        }

        // Add the driver to each truck
        for (Truck newTruck : truckList) {
            // Create a new driver and set the plateNumber
            Driver driver = new Driver();
            driver.setTruck(newTruck);
            // Load the driver of this truck
            DriversFacade driversFacade = new DriversFacade(driver);
            newTruck.setDriver(driversFacade.loadTruckDriver());
        }
        return truckList;
    }
}
