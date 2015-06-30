/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * For database connection
 *
 * @author root
 */
public class AbstractFacade {

    protected static Connection connection = null;

    protected static void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/ncgms",
                    "root", "0-Mysql-Burton");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected static void disconnect(){
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
