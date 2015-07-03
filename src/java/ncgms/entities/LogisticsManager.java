/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

/**
 *
 * @author root
 */
public class LogisticsManager extends User {

    int managerID = 0;

    public LogisticsManager() {

    }

    public LogisticsManager(int managerID, String username, String passwordHash, int isActive) {
        super(managerID, username, passwordHash, isActive);
    }

    public int getManagerID() {
        return managerID;
    }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }
    
    
    @Override
    public String toString(){
        return "Manager ID - " + managerID;
    }
}
