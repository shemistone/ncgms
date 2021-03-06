/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import javax.validation.constraints.Pattern;
import ncgms.client.controllers.ClientApplicationController;
import ncgms.entities.Driver;
import ncgms.daos.DriversFacade;
import ncgms.daos.LogisticsManagersFacade;
import ncgms.daos.MessagesFacade;
import ncgms.daos.SubcountiesFacade;
import ncgms.daos.UsersFacade;
import ncgms.entities.Message;
import ncgms.entities.Truck;
import ncgms.entities.User;
import ncgms.util.EmailSenderTask;
import ncgms.util.SMSSenderTask;

/**
 *
 * @author root
 */
@ManagedBean
@RequestScoped
public class DriverApplicationController {

    private ExecutorService execturoService;
    private List<String> subcountyList = null;

    private Part file;

    @Pattern(regexp = "^[A-Z]{1}(([A-Za-z]\\'?)|(\\'?[A-Za-z]))+$",
            message = "Name format is invalid. eg. John")
    private String firstName;
    @Pattern(regexp = "^[A-Z]{1}(([A-Za-z]\\'?)|(\\'?[A-Za-z]))+$",
            message = "Name format is invalid. eg. Doe")
    private String lastName;
    @Pattern(regexp = "^[0-9]{6,8}$",
            message = "National ID number format is invalid. eg 12345678")
    private String idNumber;
    @Pattern(regexp = "[a-z0-9_\\-\\.]+@[a-z0-9]+\\.[a-z0-9]+",
            message = "Email format is invalid. eg. johndoe@example.com")
    private String email;
    @Pattern(regexp = "^[0-9]{10}$",
            message = "Phone number format is invalid. 0712345678")
    private String phone;
    @Pattern(regexp = "^[A-Z]{1}(([A-Za-z]\\s*)|([A-Za-z]\\-?[A-Za-z])|([A-Za-z]\\'?))+$",
            message = "Select Subcounty.")
    private String subcounty;
    @Pattern(regexp = "^[0-9]+\\-{0,1}[0-9]{5}\\s+[a-zA-Z\\-\\s]*[a-zA-Z]$",
            message = "Address format invalid. eg. 12-12345 Town")
    private String address;

    /**
     * Creates a new instance of DriverApplicationController
     */
    public DriverApplicationController() {
        // Populate sub-counties list
        initializeSubcountyList();
    }

    private void initializeSubcountyList() {
        try {
            SubcountiesFacade subcountiesFacade = new SubcountiesFacade();
            this.subcountyList = subcountiesFacade.populateSubcountyList();
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Could not populate subcounty list.",
                    "Could not populate subcounty list.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(ClientApplicationController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public String uploadFile() throws IOException {
        // Get the absolute path of the application
        ServletContext sc = (ServletContext) FacesContext.getCurrentInstance()
                .getExternalContext().getContext();
        String cvName = sc.getRealPath("/");

        try (InputStream is = file.getInputStream()) {
            /* Start file upload */
            byte[] b = new byte[(int) file.getSize()];
            is.read(b);
            // Get the absolute path of the uploaded file
            cvName = cvName + "resources/uploads/cvs/" + String.valueOf(
                    new Date().getTime()) + "_" + file.getSubmittedFileName();
            FileOutputStream os = new FileOutputStream(cvName);
            os.write(b);
            System.out.print(cvName);
            // Restore the relative path of the uploade file
            cvName = cvName.substring(cvName.indexOf("resources/uploads/cvs/")
                    + "resources/uploads/cvs/".length());
            /* Finish file upload */
        }
        return cvName;
    }

    public synchronized void insertDriver() {

        try {
            this.execturoService = Executors.newCachedThreadPool();
            /* Start file upload */
            String cvName = uploadFile();
            /* Finish file upload */

            // Create a new driver
            Driver driver = new Driver(0, null, null, 0, firstName, lastName,
                    phone, email, address, new Date().getTime(), Integer.valueOf(idNumber),
                    cvName, new Truck(), new SubcountiesFacade().searchSubCountyByName(subcounty));
            UsersFacade usersFacade = new UsersFacade(driver);
            DriversFacade driversFacade = new DriversFacade(driver);

            // CHeck if driver exists
            if (!driversFacade.driverExists()) {

                // Insert a new user into the database
                int userResult = usersFacade.insertUser();

                if (userResult == 1) {
                    // Get the userID and set it as the driverID
                    int userID = usersFacade.loadUserID();
                    // Set the driverID
                    driversFacade.getDriver().setUserID(userID);

                    // Now add driver to the database
                    int driverResult = driversFacade.insertDriver();
                    if (driverResult == 1) {
                        FacesMessage facesMessage = new FacesMessage(
                                FacesMessage.SEVERITY_INFO,
                                "Your application has been received.",
                                "Your application has been  received.");
                        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                        // Notify admin---------------------------------------------//                       
                        String mobileMessage = "Hello admin, you have a new"
                                + " driver application. NCGMS Inc.";
                        this.execturoService.execute(new SMSSenderTask(new LogisticsManagersFacade().
                                searchLogisticsManagerByUsername("admin").getPhoneNo(),
                                mobileMessage));
                        this.execturoService.execute(new EmailSenderTask(new LogisticsManagersFacade().
                                searchLogisticsManagerByUsername("admin").getEmail(),
                                "Driver Application", mobileMessage));
                        
                        String systemMessage = "Hello admin, you have a new"
                                + " driver application.";
                        User admin = new User(new UsersFacade().loadAdminUserID(),
                                "admin", null, 1);
                        Message message = new Message(systemMessage, new Date().getTime(),
                                0, admin);
                        MessagesFacade messagesFacade = new MessagesFacade(message);
                        messagesFacade.insertMessage();
                        // -----------------------------------------------------------//
                        // Notify client-----------------------------------------------//
                        mobileMessage = "Your application has been successfully received. "
                                + " You will be notified on the interview date. Thank you"
                                + ". NCGMS inc";
                        this.execturoService.execute(new SMSSenderTask(phone, mobileMessage));
                        this.execturoService.execute(new EmailSenderTask(email,
                                "Driver Application", mobileMessage));
                        //-------------------------------------------------------//
                        this.address = this.email = this.firstName = this.idNumber
                                = this.lastName = this.phone = this.subcounty = null;
                    } else {
                        // Pass
                    }

                } else {
                    // Pass
                }

            } else {
                FacesMessage facesMessage = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Application failed - You have already applied",
                        "Application failed - You have already applied");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            }

        } catch (SQLException | IOException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not process application.", "Could not process application.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            java.util.logging.Logger.getLogger(
                    DriverApplicationController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void validateFile(FacesContext context, UIComponent component, Object value) {
        String error = null;
        Part file = (Part) value;
        if (file != null) {
            // Check the file size
            if (file.getSize() > 1048576) {
                error = "File should be less than 1Mb";
                throw new ValidatorException(new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, error, error));
            }
            // Check the file type
            if (!"application/msword".equals(file.getContentType())
                    && !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".
                    equals(file.getContentType())) {
                error = "File should be a microsoft word file";
                throw new ValidatorException(new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, error, error));
            }
        } else {
            error = "Please select a microsoft word file";
            throw new ValidatorException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, error, error));
        }
    }

    public List<String> getSubcountyList() {
        return subcountyList;
    }

    public void setSubcountyList(List<String> subcountyList) {
        this.subcountyList = subcountyList;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
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

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubcounty() {
        return subcounty;
    }

    public void setSubcounty(String subcounty) {
        this.subcounty = subcounty;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
