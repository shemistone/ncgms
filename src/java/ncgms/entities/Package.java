/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class Package {
    
    private String packageName;
    private double monthlyRate;
    
    List<Package> clientList = new ArrayList<>();
    
    
    public Package(){        
    }

    public Package(String packageName, double monthlyRate) {
        this.packageName = packageName;
        this.monthlyRate = monthlyRate;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public double getMonthlyRate() {
        return monthlyRate;
    }

    public void setMonthlyRate(double monthlyRate) {
        this.monthlyRate = monthlyRate;
    }

    public List<Package> getClientList() {
        return clientList;
    }

    public void setClientList(List<Package> clientList) {
        this.clientList = clientList;
    }
    
    @Override
    public String toString(){
        return "\nPacakage Name - " + packageName +
                "\nMonthly Rate - " + monthlyRate;
    }
    
}
