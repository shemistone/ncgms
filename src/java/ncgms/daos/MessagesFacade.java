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
import ncgms.daos.AbstractFacade;
import ncgms.entities.Message;

/**
 *
 * @author root
 */
public class MessagesFacade extends AbstractFacade {

    private Message message = new Message();

    public MessagesFacade() {

    }

    public MessagesFacade(Message message) {
        this.message = message;
    }

    public ArrayList<Message> loadUserMessages() throws SQLException {
        connect();
        // To store client messages
        ArrayList<Message> userMessageList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `messageID`, `message`, `Messages`.`dateAdded`, "
                + "`Messages`.`isRead`, `Messages`.`userID` FROM"
                + " `Messages` INNER JOIN `Users` ON `Messages`.`userID` = "
                + " `Users`.`userID` WHERE `Messages`.`userID` = \"" + 
                this.message.getUserID() + "\" ORDER BY `messageID` DESC";

        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            this.message = new Message(resultSet.getInt("messageID"), 
                    resultSet.getString("message"), resultSet.getLong("dateAdded"),
                    resultSet.getInt("isRead"), resultSet.getInt("userID"));
            userMessageList.add(this.message);
        }
        disconnect();
        return userMessageList;
    }

    public int insertMessage() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Messages`(`message`, `dateAdded`, `isRead`, "
                + "`userID`) VALUES(\"" + message.getMessage() + "\", \"" 
                + message.getDateAdded() + "\", \"" + message.getIsRead() 
                + "\", \"" + message.getUserID() + "\" )";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int markMessageAsRead() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "UPDATE `Messages` SET `isRead` = \"" + 1 + "\" WHERE "
                + " `messageID` = \"" + message.getMessageID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

}
