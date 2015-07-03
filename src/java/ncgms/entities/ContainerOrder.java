/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents one record in the Orders table
 *
 * @author root
 */
public class ContainerOrder {

    private int orderID = 0;
    private long dateAdded = 0;
    private String realDateAdded = null;
    private double totalPrice = 0.0;
    private int isApproved = 0;
    // Relationships
    Client client = null;
    private List<OrderDetail> orderDetailList = new ArrayList<>(); 

    /**
     * Default constructor
     */
    public ContainerOrder() {
    }

    public ContainerOrder(int orderID, long dateAdded, double totalPrice,
            int isApproved, Client client, List<OrderDetail> orderDetailList) {
        this.orderID = orderID;
        this.dateAdded = dateAdded;
        this.totalPrice = totalPrice;
        this.isApproved = isApproved;
        this.client = client;
        this.orderDetailList = orderDetailList;
    }
    
    public ContainerOrder(int orderID, long dateAdded, double totalPrice,
            int isApproved) {
        this.orderID = orderID;
        this.dateAdded = dateAdded;
        this.totalPrice = totalPrice;
        this.isApproved = isApproved;
    }
    
    
    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getRealDateAdded() {
        realDateAdded = new Date(dateAdded).toString();
        realDateAdded = realDateAdded.substring(0, realDateAdded.
                lastIndexOf("EAT")) + realDateAdded.substring(realDateAdded.
                        lastIndexOf("EAT") + 4, realDateAdded.
                        lastIndexOf("EAT") + 8);
        return realDateAdded;
    }

    public void setRealDateAdded(String realDateAdded) {
        this.realDateAdded = realDateAdded;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(int isApproved) {
        this.isApproved = isApproved;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "\nOrder ID - " + orderID
                + "\nDate Added - " + new Date(dateAdded).toString()
                + "\nTotal Price - " + totalPrice
                + "\nIs Approved - " + isApproved;
    }
}
