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
    
    List<Client> clientList = new ArrayList<>();
    
    
    public Package(){        
    }

    public Package(String packageName, double monthlyRate) {
        this.packageName = packageName;
        this.monthlyRate = monthlyRate;
    }
    
    public Package(String packageName, double monthlyRate, List<Client> clientList) {
        this.packageName = packageName;
        this.monthlyRate = monthlyRate;
        this.clientList = clientList;
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

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    
    @Override
    public String toString(){
        return "\nPacakage Name - " + packageName +
                "\nMonthly Rate - " + monthlyRate;
    }
    
}
