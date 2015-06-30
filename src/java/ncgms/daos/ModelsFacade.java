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
import java.util.Map;
import ncgms.daos.AbstractFacade;
import ncgms.entities.Model;

/**
 *
 * @author root
 */
public class ModelsFacade extends AbstractFacade {

    private Model model = new Model();

    public ModelsFacade() {

    }

    public ModelsFacade(Model model) {
        this.model = model;
    }

    public Map<String, Integer> loadModelIDsMap() throws SQLException {
        connect();
        Map<String, Integer> modelIDsMap = new HashMap<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM Models";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            modelIDsMap.put(resultSet.getString("modelName"), 
                    resultSet.getInt("modelID"));
        }
        disconnect();
        return modelIDsMap;
    }

    public Map<Integer, String> loadModelNamesMap() throws SQLException {
        connect();
        Map<Integer, String> modelNamesMap = new HashMap<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM Models";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            modelNamesMap.put(resultSet.getInt("modelID"), 
                    resultSet.getString("modelName"));
        }
        disconnect();
        return modelNamesMap;
    }

    public ArrayList<String> populateModelList() throws SQLException {
        connect();
        ArrayList<String> modelList = new ArrayList<>();
        modelList.add("--Select Model--");
        Statement statement = connection.createStatement();
        String query = "SELECT `modelName` FROM `Models`";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            modelList.add(resultSet.getString("modelName"));
        }
        disconnect();
        return modelList;
    }
    
    public Model searchModelById(int modelID) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `MModels` WHERE `modelID` = \"" 
                + modelID + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if(resultSet.next()){
            this.model = new Model(resultSet.getInt("modelID"),
             resultSet.getString("modelName"));
        }
        disconnect();
        return this.model;
    }
    
    public Model searchModelByName(String modelName) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Models` WHERE `modelName` = \"" 
                + modelName + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if(resultSet.next()){
            this.model = new Model(resultSet.getInt("modelID"),
             resultSet.getString("modelName"));
        }
        disconnect();
        return this.model;
    }
}
