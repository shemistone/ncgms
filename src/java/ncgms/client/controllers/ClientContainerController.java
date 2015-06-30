/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.client.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Pattern;
import ncgms.controllers.LogInLogOutController;
import ncgms.entities.Client;
import ncgms.entities.Container;
import ncgms.entities.ContainerOrder;
import ncgms.entities.OrderDetail;
import ncgms.entities.User;
import ncgms.daos.ClientsFacade;
import ncgms.daos.ContainerOrdersFacade;
import ncgms.daos.ContainersFacade;
import ncgms.daos.UsersFacade;
import ncgms.util.SMSSender;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class ClientContainerController implements Serializable {

    private List<Container> containerList = new ArrayList<>();
    private List<OrderDetail> orderDetailList = new ArrayList<>();
    private boolean noContainersRendered = false;

    private double price;
    private boolean cartRendered = false;

    public int noOfContainers;
    private double totalPrice;

    @Pattern(regexp = "^[1-9]{1,9}$", message = "*")
    private String quantity = "1";

    /**
     * Creates a new instance of ContainerController
     */
    public ClientContainerController() {
        initializeContainerList();
    }

    private void initializeContainerList() {
        try {
            // Load the container list
            ContainersFacade containersFacade = new ContainersFacade();
            this.containerList = containersFacade.loadAllContainers();
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not initialize container list.",
                    "Could not initialize container list");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientContainerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addToCart(Container container) {
        try {
            if (Integer.valueOf(quantity) > container.getNoOfContainers()) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Sorry only " + container.getNoOfContainers() + " in stock.",
                        "Sorry only " + container.getNoOfContainers() + " in stock.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                return;
            } else {
                noOfContainers += Integer.valueOf(quantity);
                // Update the number of containers
                container.setNoOfContainers(container.getNoOfContainers() - noOfContainers);

            }
            ContainersFacade containersFacade = new ContainersFacade(container);
            // Deduct the number of containers
            containersFacade.updateContainer(container);
            OrderDetail orderDetail = new OrderDetail(Integer.valueOf(quantity),
                    containersFacade.loadContainerPrice() * Integer.valueOf(quantity),
                    container.getContainerID());
            // Set the container
            orderDetail.setContainer(container);
            orderDetailList.add(orderDetail);
            // Add price to totalPrice
            totalPrice += orderDetail.getPrice();

        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not add container to cart.",
                    "Could not add container to cart.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientContainerController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.quantity = "1";
        }
    }

    public void removeFromCart(int orderDetailID) {

        for (OrderDetail orderDetail : orderDetailList) {
            if (orderDetail.getOrderDetailID() == orderDetailID) {
                orderDetailList.remove(orderDetail);
                // Remove price from totalPrice and decrement noOfContainers
                totalPrice -= orderDetail.getPrice() * orderDetail.getQuantity();
                noOfContainers -= orderDetail.getQuantity();
                break;
            }
        }
    }

    public void insertContainerOrder() {

        try {
            // Create a user so as to get userID
            User user = new User(null, null, 0);
            UsersFacade usersFacade = new UsersFacade(user);

            // Set the username of the current user
            usersFacade.getUser().setUsername(new LogInLogOutController().
                    getUserFromSession().getUsername());

            // Get the userID of the new user
            int userID = usersFacade.loadUserID();
            // Create a client with the above userID
            Client client = new Client();
            client.setClientID(userID);

            // Create a new container order
            ContainerOrder containerOrder = new ContainerOrder(new Date().getTime(),
                    totalPrice, client.getClientID(), orderDetailList);
            ContainerOrdersFacade containersOrdersFacade = new ContainerOrdersFacade(containerOrder);

            int containerOrderResult = containersOrdersFacade.insertContainerOrder();
            if (containerOrderResult == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Your order has been received. The containers will be delivered during the "
                        + " next collection schedule. The amount due will be added to your monthly bill.",
                        "Your order has been received. The containers will be delivered during the "
                        + " next collection schedule. The amount due will be added to your monthly bill.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                noOfContainers = 0;
                orderDetailList.clear();
                SMSSender.sendSmsSynchronous("072186821", "You have received a new order from "
                        + new ClientsFacade().searchClientByClientID(containerOrder.getClientID())
                        + " NCGMS Inc.");
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not insert container order",
                    "Could not insert container order.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientContainerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ClientContainerController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            setTotalPrice(0.0);
        }

    }

    public void refreshContainers() {
        this.initializeContainerList();;
    }

    public void setCartRendered(boolean cartRendered) {
        this.cartRendered = cartRendered;
    }

    public boolean isCartRendered() {
        cartRendered = !orderDetailList.isEmpty();
        return cartRendered;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Container> getContainerList() {
        return containerList;
    }

    public void setContainerList(List<Container> containerList) {
        this.containerList = containerList;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public boolean isNoContainersRendered() {
        return this.containerList.isEmpty();
    }

    public void setNoContainersRendered(boolean noContainersRendered) {
        this.noContainersRendered = noContainersRendered;
    }

    public int getNoOfContainers() {
        return noOfContainers;
    }

    public void setNoOfContainers(int noOfContainers) {
        this.noOfContainers = noOfContainers;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
