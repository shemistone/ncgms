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
import ncgms.entities.User;

/**
 * UsersFacade
 *
 * @author root
 */
public class UsersFacade extends AbstractFacade {

    private User user = new User();

    public UsersFacade() {
    }

    public UsersFacade(User user) {
        this.user = user;
    }

    public String loadUserCategory() throws SQLException {
        connect();
        String category = null;
        Statement statement = connection.createStatement();
        String query = "SELECT `userID` FROM `Users` INNER JOIN `Clients` ON "
                + " `Users`.`userID` = `Clients`.`clientID` WHERE `username` = '"
                + user.getUsername() + "'";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            category = "client";
        } else {
            category = "admin";
        }
        disconnect();
        return category;
    }

    public int loadAdminUserID() throws SQLException {
        connect();
        int userID = 0;
        Statement statement = connection.createStatement();
        String query = "SELECT `userID` FROM `Users` WHERE `username` = 'admin'";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            userID = resultSet.getInt("userID");
        }
        disconnect();
        return userID;
    }

    public void loadPasswordHash() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT `passwordHash` FROM `Users` WHERE `username` = '"
                + user.getUsername() + "'";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            user.setPasswordHash(resultSet.getString("passwordHash"));
        } else {
            // Set default hash to an empty string
            user.setPasswordHash("1000:000000000000000000000000000000000000000000000000:"
                    + "000000000000000000000000000000000000000000000000");
        }
        disconnect();
    }

    public int changePassword() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `Users` SET `passwordHash` = \""
                + this.user.getPasswordHash() + "\""
                + " WHERE `username` = \"" + user.getUsername() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public boolean isActive() throws SQLException {
        connect();
        int isActive = 0;
        Statement statement = connection.createStatement();
        String query = "SELECT `isActive` FROM `Users` WHERE `username` = '"
                + user.getUsername() + "' AND `passwordHash` = '"
                + user.getPasswordHash() + "'";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            isActive = resultSet.getInt("isActive");
        }
        disconnect();
        return isActive == 1;
    }

    public boolean userExists() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT `userID` FROM `Users` WHERE `username` = '"
                + user.getUsername() + "' and `paswordHash` = \""
                + user.getPasswordHash() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        disconnect();
        return resultSet.next();
    }

    public int insertUser() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Users` (`username`, `passwordHash`, `isActive`) "
                + " VALUES (\"" + user.getUsername() + "\",\""
                + user.getPasswordHash() + "\",\"" + user.getIsActive() + "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int loadUserID() throws SQLException {
        connect();
        int userID = 0;
        String query = null;
        Statement statement = connection.createStatement();
        if (this.user.getUsername() != null && this.user.getPasswordHash() != null) {
            query = "SELECT `userID` FROM `Users` WHERE `username` = '"
                    + this.user.getUsername() + "' AND `passwordHash` = '"
                    + this.user.getPasswordHash() + "'";
        } else if (this.user.getUsername() != null && this.user.getPasswordHash() == null) {
            query = "SELECT `userID` FROM `Users` WHERE `username` = '"
                    + this.user.getUsername() + "'";
        } else if (this.user.getUsername() != null) {
            query = "SELECT `userID` FROM `Users` WHERE `Users`.`username` = "
                    + " \" " + this.user.getUsername() + "\" ORDER BY `userID` DESC LIMIT 1";
        } else {
            query = "SELECT `userID` FROM `Users` ORDER BY `userID` DESC LIMIT 1";
        }

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            userID = resultSet.getInt("userID");
        }
        disconnect();
        return userID;
    }

    public User searchUserByUserID(int searchTerm) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` WHERE `Users`.`userID` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            this.user = new User(resultSet.getInt("userID"), resultSet.getString("username"),
                    resultSet.getString("passwordHash"), resultSet.getInt("isActive"));
        }
        disconnect();
        return this.user;
    }

    public User searchUserByUsername(String searchTerm) throws SQLException {
        connect();
        ArrayList<User> userList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Users` WHERE `username` = \"" + searchTerm + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            this.user = new User(resultSet.getInt("userID"), resultSet.getString("username"),
                    resultSet.getString("passwordHash"), resultSet.getInt("isActive"));
        }
        disconnect();
        return this.user;
    }

    /**
     * Get the user
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Set the user
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

}
