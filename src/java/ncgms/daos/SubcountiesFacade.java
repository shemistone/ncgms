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
import ncgms.entities.Subcounty;

/**
 * SubcountiesFacade
 * @author root
 */
public class SubcountiesFacade extends AbstractFacade{
    
    private Subcounty subcounty = new Subcounty();

    public SubcountiesFacade(){
        
    }
    
    public SubcountiesFacade(Subcounty subcounty){
        this.subcounty = subcounty;
    }
    
    /**
     * Get a map of subcounty names and ids
     * @return
     * @throws SQLException 
     */
    public  Map<String, Integer> loadSubcountyIDsMap() throws SQLException{
        connect();
        Map<String, Integer> subcountyIDsMap = new HashMap<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM Subcounties";
        ResultSet resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            subcountyIDsMap.put(resultSet.getString("subcountyName"), 
                    resultSet.getInt("subcountyID"));
        }
        disconnect();
        return subcountyIDsMap;
    }
    
    public  Map<Integer, String> loadSubcountyNamesMap() throws SQLException{
        connect();
        Map<Integer, String> subcountyNamesMap = new HashMap<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM Subcounties";
        ResultSet resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            subcountyNamesMap.put(resultSet.getInt("subcountyID"), 
                    resultSet.getString("subcountyName"));
        }
        disconnect();
        return subcountyNamesMap;
    }
    
    /**
     * Populate subcounty list
     * @return
     * @throws SQLException 
     */
    public ArrayList<String> populateSubcountyList() throws SQLException{
        connect();
        ArrayList<String> subcountyList = new ArrayList<>();
        subcountyList.add("--Select Sub-County--");
        Statement statement = connection.createStatement();
        String query = "SELECT `subcountyName` FROM `Subcounties`";
        ResultSet resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            subcountyList.add(resultSet.getString("subcountyName"));
        }
        disconnect();
        return subcountyList;
    }
    
    public ArrayList<Subcounty> loadAllSubcounties() throws SQLException{
        connect();
        ArrayList<Subcounty> subcountyList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Subcounties`";
        ResultSet resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            this.subcounty = new Subcounty(resultSet.getInt("subcountyID"), 
                    resultSet.getString("subcountyName"));
            subcountyList.add(subcounty);
        }
        disconnect();
        return subcountyList;
    }
    
    public Subcounty getSubcounty() {
        return subcounty;
    }

    public void setSubcounty(Subcounty subcounty) {
        this.subcounty = subcounty;
    }

    public Subcounty searchSubCountyById(int subcountyID) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Subcounties` WHERE `subcountyID` = \"" 
                + subcountyID + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if(resultSet.next()){
            this.subcounty = new Subcounty(resultSet.getInt("subcountyID"),
             resultSet.getString("subcountyName"));
        }
        return this.subcounty;
    }
    
    public Subcounty searchSubCountyByName(String subcountyName) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Subcounties` WHERE `subcountyName` = \"" 
                + subcountyName + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if(resultSet.next()){
            this.subcounty = new Subcounty(resultSet.getInt("subcountyID"),
             resultSet.getString("subcountyName"));
        }
        return this.subcounty;
    }
    
    
    
}
