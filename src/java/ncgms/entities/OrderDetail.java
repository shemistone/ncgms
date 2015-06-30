/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

/**
 * Represents on record in the OrderDetails table
 *
 * @author root
 */
public class OrderDetail {

    private int orderDetailID = 0;
    private int quantity = 0;
    private double price = 0.0;
    // Relationships
    private int orderID;
    private ContainerOrder containerOrder = null;
    private int containerID = 0;
    Container container = null;

    /**
     * Default constructor
     */
    public OrderDetail() {
    }

    /**
     * Overloaded constructor
     *
     * @param quantity
     * @param price
     * @param containerID
     */
    public OrderDetail(int quantity, double price, int containerID) {
        this.quantity = quantity;
        this.price = price;
        this.containerID = containerID;
    }

    public OrderDetail(int orderDetailID, int quantity, double price,
            int containerID, int orderID) {
        this.orderDetailID = orderDetailID;
        this.quantity = quantity;
        this.price = price;
        this.containerID = containerID;
        this.orderID = orderID;
    }

    public int getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(int orderDetailID) {
        this.orderDetailID = orderDetailID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getContainerID() {
        return containerID;
    }

    public void setContainerID(int containerID) {
        this.containerID = containerID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public ContainerOrder getContainerOrder() {
        return containerOrder;
    }

    public void setContainerOrder(ContainerOrder containerOrder) {
        this.containerOrder = containerOrder;
    }

    @Override
    public String toString() {
        return "\nOrder Detail ID - " + orderDetailID
                + "\nquantity - " + quantity
                + "\nPrice - " + price
                + "\nContainer ID - " + containerID
                + "\nOrder ID- " + orderID;
    }
}
