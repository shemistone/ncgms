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
import ncgms.entities.Package;

/**
 *
 * @author root
 */
public class PackagesFacade extends AbstractFacade{
    
    private Package packageObject = new Package();

    public PackagesFacade() {
    }

    public PackagesFacade(Package packageObject) {
        this.packageObject = packageObject;
    }

    public Package getPackageObject() {
        return packageObject;
    }

    public void setPackageObject(Package packageObject) {
        this.packageObject = packageObject;
    }
    
    public List<String> populatePackageList() throws SQLException{
        connect();
        ArrayList<String> packageList = new ArrayList<>();
        packageList.add("--Select Package--");
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Packages`";
        ResultSet resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            packageList.add(resultSet.getString("packageName"));
        }
        disconnect();
        return packageList;
    }
    
    public ArrayList<Package> loadAllPackages() throws SQLException{
        connect();
        ArrayList<Package> packageList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Packages`";
        ResultSet resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            this.packageObject = new Package(resultSet.getString("packageName"),
            resultSet.getDouble("monthlyRate"));
            packageList.add(this.packageObject);
        }
        disconnect();
        return packageList;
    }
    
}
