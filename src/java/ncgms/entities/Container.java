/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one record in the Containers table
 *
 * @author root
 */
public class Container {

    private int containerID = 0;
    private String name = null;
    private String fileName = null;
    private double price = 0.0;
    private int noOfContainers = 0;
    private String realPrice = null;    
    // Relationships
    private List<OrderDetail> orderDetailList = new ArrayList<>();
    
    private boolean editable = false;

    /**
     * Default constructor
     */
    public Container() {
    }

    public Container(String fileName, double price) {
        this.fileName = fileName;
        this.price = price;
    }

    public Container(String fileName, String name, int noOfContainers, double price) {
        this.fileName = fileName;
        this.name = name;
        this.noOfContainers = noOfContainers;
        this.price = price;
    }

    /**
     * Overloaded constructor
     *
     * @param containerID
     * @param name
     * @param fileName
     * @param price
     */
    public Container(int containerID, String name, String fileName, int noOfContainers, double price) {
        this.containerID = containerID;
        this.name = name;
        this.fileName = fileName;
        this.noOfContainers = noOfContainers;
        this.price = price;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public int getContainerID() {
        return containerID;
    }

    public void setContainerID(int containerID) {
        this.containerID = containerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNoOfContainers() {
        return noOfContainers;
    }

    public void setNoOfContainers(int noOfContainers) {
        this.noOfContainers = noOfContainers;
    }

    public String getRealPrice() {
        realPrice = "KShs " + String.valueOf(price) + "0 ";
        return realPrice;
    }

    public void setRealPrice(String realPrice) {
        this.realPrice = realPrice;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    public String toString(){
        return "\nContainer ID - " + containerID +
                "\nName - " + name +
                "\nFile Name - " + fileName +
                "\nNo of Containers" + noOfContainers +
                "\nPrice - " + price;
    }
}
