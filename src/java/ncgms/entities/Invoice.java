/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.Date;

/**
 * Represents one record in the Invoices table
 *
 * @author root
 */
public class Invoice {

    
    private int invoiceID = 0;
    private long dateAdded = 0;
    private String realDateAdded = null;
    private long dateDue = 0;
    private String realDateDue = null;
    private long datePaid = 0;
    private String realDatePaid =null;
    private double amountDue = 0.0;
    private double amountPaid = 0;
    private double balance = 0;
    private int isPaid = 0;
    // Relationships
    private int clientID = 0; 
    Client client = null;
    // Flags
    private boolean editable = false;


    /**
     * Default constructor
     */
    public Invoice() {
    }

    public Invoice(long dateAdded, double amountDue, int isPaid) {
        this.dateAdded = dateAdded;
        this.amountDue = amountDue;
        this.isPaid = isPaid;
    }

    public Invoice(long dateAdded, double amountDue, int isPaid, int clientID) {
        this.dateAdded = dateAdded;
        this.amountDue = amountDue;
        this.isPaid = isPaid;
        this.clientID = clientID;
    }
    
    public Invoice(int invoiceID, long dateAdded, double amountDue, int isPaid,
            int clientID) {
        this.invoiceID = invoiceID;
        this.dateAdded = dateAdded;
        this.amountDue = amountDue;
        this.isPaid = isPaid;
        this.clientID = clientID;
    }

    public Invoice(int invoiceID, long dateAdded, long dateDue, long datePaid,
            double amountDue, double amountPaid, double balance, 
            int isPaid, int clientID) {
        this.invoiceID = invoiceID;
        this.dateAdded = dateAdded;
        this.dateDue = dateDue;
        this.datePaid = datePaid;
        this.amountDue = amountDue;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.isPaid = isPaid;
        this.clientID = clientID;
    }


    
    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
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

    public long getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(long datePaid) {
        this.datePaid = datePaid;
    }

    public String getRealDatePaid() {
        if(datePaid != 0){
        realDatePaid = new Date(datePaid).toString();
        realDatePaid = realDatePaid.substring(0, realDatePaid.
                lastIndexOf("EAT")) + realDatePaid.substring(realDatePaid.
                        lastIndexOf("EAT") + 4, realDatePaid.
                                lastIndexOf("EAT") + 8);
        }else{
            realDatePaid = " ";
        }
        return realDatePaid;
    }

    public void setRealDatePaid(String realDatePaid) {
        this.realDatePaid = realDatePaid;
    }

    public int getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(int isPaid) {
        this.isPaid = isPaid;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public long getDateDue() {
        return dateDue;
    }

    public void setDateDue(long dateDue) {
        this.dateDue = dateDue;
    }

    public String getRealDateDue() {
        realDateDue = new Date(dateDue).toString();
        realDateDue = realDateDue.substring(0, realDateDue.
                lastIndexOf("EAT")) + realDateDue.substring(realDateDue.
                        lastIndexOf("EAT") + 4, realDateDue.
                                lastIndexOf("EAT") + 8);
        return realDateDue;
    }

    public void setRealDateDue(String realDateDue) {
        this.realDateDue = realDateDue;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    
    @Override
    public String toString() {
        return "\nInvoice ID - " + invoiceID
                + "\nDate Added - " + new Date(dateAdded).toString()
                + "\nDate Due - " + new Date(dateDue).toString()
                + "\nDate Paid - " + new Date(datePaid).toString()
                + "\nAmount Due - " + amountDue
                + "\nAmount Paid - " + amountPaid
                + "\nBalance - " + balance
                + "\nIs Paid - " + isPaid
                + "\nClient ID - " + clientID;
    }
}
