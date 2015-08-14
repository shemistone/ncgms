/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

/**
 *
 * @author root
 */
public class LogisticsManager extends User {

    //int managerID = 0;
    String firstName, lastName, email, phoneNo;

    public LogisticsManager() {

    }

    public LogisticsManager(int userID, String username, String passwordHash,
            int isActive, String firstName, String lastName, String email, String phoneNo) {
        super(userID, username, passwordHash, isActive);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Override
    public String toString() {
        return "\nUsername - " + username
                + "\nManager ID - " + userID
                + "\nFirst Name - " + firstName
                + "\nLast Name - " + lastName
                + "\nEmail - " + email
                + "\nPhone No - " + phoneNo;
    }
}
