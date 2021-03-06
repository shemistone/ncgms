/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.client.controllers;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import ncgms.daos.LogisticsManagersFacade;
import ncgms.daos.UsersFacade;
import ncgms.util.SMSSenderTask;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class ClientContainerController implements Serializable {

    ExecutorService executorService;
    
    private List<Container> containerList = new ArrayList<>();
    private List<OrderDetail> orderDetailList = new ArrayList<>();

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
            OrderDetail orderDetail = new OrderDetail(0, Integer.valueOf(quantity),
                    containersFacade.loadContainerPrice() * Integer.valueOf(quantity),
                    container, null);
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
            client.setUserID(userID);

            // Create a new container order
            ContainerOrder containerOrder = new ContainerOrder(0, new Date().getTime(),
                    totalPrice, "Processing", client, orderDetailList);
            ContainerOrdersFacade containersOrdersFacade = new ContainerOrdersFacade(containerOrder);

            int containerOrderResult = containersOrdersFacade.insertContainerOrder();
            if (containerOrderResult == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Your order has been received. You will be contacted to confirm your order.",
                        "Your order has been received. You will be contacted to confirm your order.");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                noOfContainers = 0;
                orderDetailList.clear();
                // Notify the admin------------------------------------------------------//
                String message = "Hello admin, you have received a new order from "
                        + new ClientsFacade().searchClientByClientID(containerOrder.getClient().getUserID())
                        + " NCGMS Inc.";
                this.executorService = Executors.newCachedThreadPool();
                this.executorService.execute(new SMSSenderTask(new LogisticsManagersFacade().
                        searchLogisticsManagerByUsername("admin").getPhoneNo(), message));
                //-----------------------------------------------------------------------//
            } else {
                // Pass
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not insert container order",
                    "Could not insert container order.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientContainerController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            setTotalPrice(0.0);
            this.executorService.shutdown();
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
