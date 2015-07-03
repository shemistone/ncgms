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
public class LogisticsManagersFacade extends AbstractFacade{

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
            resultSet.getInt("isActive"));
            
        }
        disconnect();
        return this.logisticsManager;
    }


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

}
