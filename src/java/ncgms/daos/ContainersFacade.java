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
import ncgms.entities.Container;
import ncgms.entities.OrderDetail;

/**
 *
 * @author root
 */
public class ContainersFacade extends AbstractFacade {

    private Container container = null;

    public ContainersFacade() {
    }

    public ContainersFacade(Container container) {
        this.container = container;
    }
    
    public int updateContainer(Container container) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = null;
        int result = 0;
        query = "UPDATE `Containers` SET `noOfContainers` = \"" 
                + container.getNoOfContainers() + "\""
                + " WHERE `containerID` = \"" + container.getContainerID() + "\"";

        result += statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public ArrayList<Container> loadAllContainers() throws SQLException {
        connect();
        // Container list to return
        ArrayList<Container> containerList = new ArrayList<>();

        Statement statement = connection.createStatement();
        String query = "SELECT * FROM `Containers` ORDER BY `containerID` DESC";
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            this.container = new Container(resultSet.getInt("containerID"),
                    resultSet.getString("name"), resultSet.getString("fileName"),
                    resultSet.getInt("noOfContainers"), resultSet.getLong("price"));
            containerList.add(this.container);
        }
        // Get all the containerOrders of this each container
        for (Container newContainer : containerList) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setContainerID(newContainer.getContainerID());
            OrderDetailsFacade orderDetailsFacade = new OrderDetailsFacade(orderDetail);
            newContainer.setOrderDetailList(orderDetailsFacade.loadContainerOrderDetails());
        }
        disconnect();
        return containerList;
    }

    public long loadContainerPrice() throws SQLException {
        connect();
        long price = 0;
        Statement statement = connection.createStatement();
        String query = "SELECT `price` FROM `Containers` WHERE `containerID`"
                + " = \"" + container.getContainerID() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            price = resultSet.getLong("price");
        }
        disconnect();
        return price;
    }

    public int insertContainer() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `Containers` (`fileName`, `name`,"
                + " `noOfContainers`, `price`) VALUES("
                + " \"" + container.getFileName() + "\", \"" + container.getName()
                + "\",\"" + container.getNoOfContainers() + "\", \"" + container.getPrice() + "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public int removeContainer() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "DELETE FROM `Containers` WHERE `containerID` = \""
                + container.getContainerID() + "\"";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    
}
