/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.client.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.validation.constraints.Pattern;

import ncgms.entities.Client;
import ncgms.entities.User;
import ncgms.util.PasswordHasher;
import ncgms.daos.ClientsFacade;
import ncgms.daos.PackagesFacade;
import ncgms.daos.SubcountiesFacade;
import ncgms.daos.UsersFacade;
import ncgms.util.SMSSender;

/**
 * Controller class for client registration page
 *
 *
 * @author root
 */
@ManagedBean
@RequestScoped
public class ClientApplicationController implements Serializable {

    private List<String> subcountyList = new ArrayList<>();
    private List<String> packageList = new ArrayList<>();

    @Pattern(regexp = "^[A-Z]{1}(([A-Za-z]\\'?)|(\\'?[A-Za-z]))+$",
            message = "Name format is invalid.")
    private String firstName;
    @Pattern(regexp = "^[A-Z]{1}(([A-Za-z]\\'?)|(\\'?[A-Za-z]))+$",
            message = "Name format is invalid.")
    private String lastName;
    @Pattern(regexp = "^[0-9]{6,8}$",
            message = "National ID number format is invalid.")
    private String idNumber;
    @Pattern(regexp = "[a-zA-Z0-9_\\-\\.]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+",
            message = "Email format is invalid.")
    private String email;
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Phone number format is invalid.")
    private String phone;
    @Pattern(regexp = "^[A-Z]{1}(([A-Za-z]\\s*)|([A-Za-z]\\-?[A-Za-z])|([A-Za-z]\\'?))+$",
            message = "Select Subcounty.")
    private String subcounty;
    @Pattern(regexp = "^[0-9]+\\-{0,1}[0-9]{5}\\s+[a-zA-Z\\-\\s]*[a-zA-Z]$",
            message = "Address format invalid.")
    private String address;
    @Pattern(regexp = "^[A-Z]{1}(([A-Za-z]\\s*)|([A-Za-z]\\-?[A-Za-z])|([A-Za-z]\\'?))+$",
            message = "Plot name format is invalid.")
    private String plotName;
    @Pattern(regexp = "^.+$", message = "Username format is invalid.")
    private String username;
    @Pattern(regexp = "^.*[a-zA-Z0-9]{8}+.*$",
            message = "Password must be at least 8 alphanumeric characters.")
    private String password;
    @Pattern(regexp = "^.*[a-zA-Z0-9]{8}+.*$", message = "")
    private String passwordAgain;
    @Pattern(regexp = "^[A-Z]{1}(([A-Za-z]\\s*)|([A-Za-z]\\-?[A-Za-z])|([A-Za-z]\\'?))+$",
            message = "Select Package.")
    private String packageName;

    /**
     * Creates a new instance of ClientRegistrationController
     */
    public ClientApplicationController() {
        // Populate sub-counties list
        initializeSubcountyList();
        initializePackageList();
    }

    private void initializeSubcountyList() {
        try {
            SubcountiesFacade subcountiesFacade = new SubcountiesFacade();
            this.subcountyList = subcountiesFacade.populateSubcountyList();
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not populate subcounty list.",
                    "Could not populate subcounty list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientApplicationController.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    private void initializePackageList() {
        try {
            PackagesFacade packagesFacade = new PackagesFacade();
            this.packageList = packagesFacade.populatePackageList();
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not populate package list.",
                    "Could not populate package list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientApplicationController.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
    }

    /**
     * Check if passwords are the same
     *
     * @param context the FacesContext to pass
     * @param component the UIComponent to validate
     * @param value the value in the UIComponent
     */
    public void validatePasswords(FacesContext context, UIComponent component, Object value) {
        Map map = context.getExternalContext().getRequestParameterMap();
        password = (String) map.get("client_application_form:password");
        passwordAgain = value.toString();

        if (!password.equals(passwordAgain)) {
            String error = "Your passwords do not match";
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, error, error));
        }
    }

    /**
     * Hash the user's password
     *
     * @return the salted hash
     */
    private String hashPassword() {
        String passwordHash = null;
        try {
            passwordHash = PasswordHasher.createHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(ClientApplicationController.class.getName()).log(
                    Level.SEVERE, null, ex);
        }
        return passwordHash;
    }

    /**
     * Insert user information into the database
     */
    public void insertClientAndUser() {

        try {

            // Check if username already exists
            UsersFacade usersFacade = new UsersFacade();
            User user = (User) usersFacade.searchUserByUsername(username);
            //Check if user exists
            if (user.getUserID() > 0) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        username + " is already taken, try another username.",
                        username + " is already taken, try another username.");
                FacesContext.getCurrentInstance().addMessage("client_application_form:username", 
                        facesMessage);
                return;
            }
            // Create a new user
            user = new User(username, hashPassword(), 0);
            usersFacade.setUser(user);
            Client client = new Client(firstName, lastName, address, phone,
                    email, plotName, new Date().getTime(), Integer.valueOf(idNumber),
                    new SubcountiesFacade().loadSubcountyIDsMap().get(subcounty),
                    packageName);
            ClientsFacade clientsFacade = new ClientsFacade(client);

            //Check whether client already exists
            if (!clientsFacade.clientExists()) {

                //Insert the user into the database
                int userResult = usersFacade.insertUser();

                if (userResult == 1) {
                    // Get the userID and set it as clientID
                    int userID = usersFacade.loadUserID();
                    // Set the clientID
                    clientsFacade.getClient().setClientID(userID);

                    // Now add client to database
                    int clientResult = clientsFacade.insertClient();
                    if (clientResult == 1) {
                        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Your application has been successfully received. "
                                + " You will be able to log in once your application has been approved.",
                                "Your application has been successfully received. "
                                + " You will be able to log in once your application has been approved.");
                        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                        this.address = this.email = this.firstName = this.idNumber = this.lastName
                                = this.password = this.passwordAgain = this.phone = this.plotName
                                = this.subcounty = this.username = null;
                        // Notify admin
                        SMSSender.sendSmsSynchronous("0721868821", "Hello admin, you have a new driver"
                                + " client application. NCGMS Inc.");
                    } else {
                        // Pass
                    }

                } else {
                    // Pass
                }

            } else {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Application failed - You have already applied",
                        "Application failed - You have already applied");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not process application.", "Could not process application.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ClientApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get the subcountyList
     *
     * @return the subcounty list to set
     */
    public List<String> getSubcountyList() {
        return subcountyList;
    }

    /**
     * Set the subcounty list
     *
     * @param subcountyList the subcounty list
     */
    public void setSubcountyList(List<String> subcountyList) {
        this.subcountyList = subcountyList;
    }

    public List<String> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<String> packageList) {
        this.packageList = packageList;
    }

    /**
     * Get the firstName
     *
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the firstName
     *
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the lastName
     *
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the lastName
     *
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the email
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the phone number
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the phone number
     *
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Get the subcounty
     *
     * @return the subcounty
     */
    public String getSubcounty() {
        return subcounty;
    }

    /**
     * Set the subcounty
     *
     * @param subcounty the subcounty to set
     */
    public void setSubcounty(String subcounty) {
        this.subcounty = subcounty;
    }

    /**
     * Get the address
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the address
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the plotName
     *
     * @return the plotName to set
     */
    public String getPlotName() {
        return plotName;
    }

    /**
     * Set the plotName
     *
     * @param plotName the plotName to set
     */
    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    /**
     * Get the idNumber
     *
     * @return the idNumber
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * Set the idNumber
     *
     * @param idNumber the idNumber to set
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * Get the username
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the password
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the passwordAgain
     *
     * @return the passwordAgain
     */
    public String getPasswordAgain() {
        return passwordAgain;
    }

    /**
     * Set the passwordAgain
     *
     * @param passwordAgain the passwordAgain to set
     */
    public void setPasswordAgain(String passwordAgain) {
        this.passwordAgain = passwordAgain;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
