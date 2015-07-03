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
import ncgms.entities.Container;
import ncgms.entities.ContainerOrder;
import ncgms.entities.OrderDetail;

/**
 *
 * @author root
 */
public class OrderDetailsFacade extends AbstractFacade {

    private OrderDetail orderDetail = new OrderDetail();

    public OrderDetailsFacade() {
    }

    public OrderDetailsFacade(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public int insertOrderDetail() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        String query = "INSERT INTO `OrderDetails`(`quantity`, `price`, "
                + "`containerID`, `orderID`) VALUES (\"" + orderDetail.getQuantity()
                + "\", \"" + orderDetail.getPrice() + "\", \"" + orderDetail.
                getContainer().getContainerID() + "\", \"" + orderDetail.
                getContainerOrder().getOrderID() + "\")";
        int result = statement.executeUpdate(query);
        disconnect();
        return result;
    }

    public List<OrderDetail> loadContainerOrderDetails() throws SQLException {
        connect();
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `OrderDetails`.* FROM `Containers` INNER JOIN "
                + "`OrderDetails` ON `Containers`.`containerID` = "
                + "`OrderDetails`.`containerID` WHERE `OrderDetails`.`containerID`"
                + " = \"" + orderDetail.getContainer().getContainerID() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create a new OrderDetail
            this.orderDetail = new OrderDetail(resultSet.getInt("orderDetailID"),
                    resultSet.getInt("quantity"), resultSet.getDouble("price"),
                    orderDetail.getContainer(), null);
            // Add containerOrder to orderDetailList
            orderDetailList.add(this.orderDetail);
        }
        disconnect();
        return orderDetailList;
    }

    public ArrayList<OrderDetail> loadContainerOrderOrderDetails() throws SQLException {
        connect();
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT `Containers`.*, `OrderDetails`.`orderDetailID`, "
                + "`OrderDetails`.`quantity`, `OrderDetails`.`price`,"
                + " `OrderDetails`.`containerID`, `OrderDetails`.`orderID` FROM "
                + "`Containers` INNER JOIN `OrderDetails` ON `Containers`.`containerID`"
                + " = `OrderDetails`.`containerID` INNER JOIN `ContainerOrders` "
                + "ON `OrderDetails`.`orderID` = `ContainerOrders`.`orderID`"
                + " WHERE `ContainerOrders`.`orderID` = \"" + this.orderDetail.
                getContainerOrder().getOrderID() + "\"";
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            // Create a new container
            Container container = new Container(resultSet.getInt("containerID"),
                    resultSet.getString("name"), resultSet.getString("fileName"),
                    resultSet.getInt("noOfContainers"), resultSet.getDouble("price"));
            // Create a new OrderDetail
            this.orderDetail = new OrderDetail(resultSet.getInt("orderDetailID"),
                    resultSet.getInt("quantity"), resultSet.getDouble("price"),
                    new Container(resultSet.getInt("containerID"), null, null, 0, 0.0),
                    this.orderDetail.getContainerOrder());

            // Set the container
            this.orderDetail.setContainer(container);
            orderDetailList.add(this.orderDetail);
        }
        disconnect();
        return orderDetailList;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

}
