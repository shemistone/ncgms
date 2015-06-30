/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.admin.controllers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author root
 */
@ManagedBean
@RequestScoped
public class AdminIndexController {

    /**
     * Creates a new instance of AdminController
     */
    public AdminIndexController() {
    }
    
    public String toHome() {
        return "/admin/index?faces-redirect=true";
    }
    
    public String toClients() {
        return "/admin/clients?faces-redirect=true";
    }
    
    public String toDrivers() {
        return "/admin/drivers?faces-redirect=true";
    }
    
    public String toTouts() {
        return "/admin/touts?faces-redirect=true";
    }
    
    public String toTrucks() {
        return "/admin/trucks?faces-redirect=true";
    }
    
    public String toContainers() {
        return "/admin/containers?faces-redirect=true";
    }
    
    public String toContainerOrders() {
        return "/admin/container_orders?faces-redirect=true";
    }
    
    public String toComplaints() {
        return "/admin/complaints?faces-redirect=true";
    }
    
    public String toInvoices() {
        return "/admin/invoices?faces-redirect=true";
    }
    
    public String toMessages(){
        return "/admin/messages?faces-redirect=true";
    }
}
