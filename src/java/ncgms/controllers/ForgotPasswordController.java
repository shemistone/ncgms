/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.validation.constraints.Pattern;
import ncgms.client.controllers.ClientApplicationController;
import ncgms.daos.ClientsFacade;
import ncgms.daos.LogisticsManagersFacade;
import ncgms.daos.UsersFacade;
import ncgms.entities.Client;
import ncgms.entities.LogisticsManager;
import ncgms.util.EmailSenderTask;
import ncgms.util.PasswordHasher;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class ForgotPasswordController {

    private ExecutorService executorService;

    private LogisticsManager lm;
    private Client client;
    private static int verificationCode = -1;
    private Random random;

    @Pattern(regexp = "^.+$",
            message = "Input Verification Code.")
    private String userVerificationCode;
    @Pattern(regexp = "[a-zA-Z0-9_\\-\\.]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+",
            message = "Email format is invalid.")
    private String email;
    @Pattern(regexp = "^.*[a-zA-Z0-9]{8}+.*$", message = "Password must be at least"
            + " 8 alphanumeric characters.")
    private String newPassword;
    @Pattern(regexp = "^.*[a-zA-Z0-9]{8}+.*$", message = "Re-enter new password")
    private String newPasswordAgain;

    /**
     * Creates a new instance of ForgotPassword
     */
    /**
     * Creates a new instance of ForgotPasswordController
     */
    public ForgotPasswordController() {
    }

    private boolean verifyEmail() {
        try {
            ClientsFacade clientsFacade = new ClientsFacade();
            LogisticsManagersFacade logisticsManagersFacade = new LogisticsManagersFacade();

            if (!clientsFacade.searchClientByEmail(email).isEmpty()) {
                this.client = clientsFacade.searchClientByEmail(email).get(0);
            }
            this.lm = logisticsManagersFacade.searchLogisticsManagerByEmail(email);

            if (client == null && lm == null) {
                FacesMessage facesMessage = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Please enter the email you registered with.",
                        "Please enter the email you registered with.");
                FacesContext.getCurrentInstance().addMessage(
                        "verification_form:email",
                        facesMessage);
                return false;
            } else {
                return true;
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Could not verify email.",
                    "Could not verify email.");
            FacesContext.getCurrentInstance().addMessage(
                    "verification_form",
                    facesMessage);
            Logger.getLogger(ForgotPasswordController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public synchronized void sendVerificationCode() {
        if (verifyEmail()) {
            this.executorService = Executors.newCachedThreadPool();
            // Generate verification code
            random = new Random(new Date().getTime());
            verificationCode = random.nextInt(2000000);

            // Send verification to client----------------------------------------//
            String code = "<h3>Verification Code: " + String.valueOf(verificationCode)
                    + ".</h3> This code will expire in five minutes time. Any previously"
                    + " sent verification code have expired. NCGMS Inc";
            this.executorService.execute(new EmailSenderTask(email,
                    "NCGMS Activation Code.", code));
            FacesMessage facesMessage = new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "We have sent the activation code to your email."
                    + " Use it to reset your password below.",
                    "We have sent the activation code to your email."
                    + " Use it to reset your password below.");
            FacesContext.getCurrentInstance().addMessage(
                    "verification_form", facesMessage);
            // Expire verification code after 5 minutes------------------------//
            Thread thread = new Thread(new Verifier());
            thread.start();
            //-----------------------------------------------------------------//
        } else {

        }
    }

    public void changePassword() {

        UsersFacade usersFacade = new UsersFacade();
        if (this.client != null) {
            try {
                if (verificationCode == -1) {
                    FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Expired Verification Code. Try another code.",
                            "Expired Verification Code. Try another code.");
                    FacesContext.getCurrentInstance().addMessage(
                            "change_password_form:user_verification_code", facesMessage);
                    return;
                } else if (verificationCode != Integer.parseInt(this.userVerificationCode)) {
                    FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Invalid Verification Code.",
                            "Invalid Verification Code.");
                    FacesContext.getCurrentInstance().addMessage(
                            "change_password_form:user_verification_code", facesMessage);
                    return;
                }
                //Hash the password
                this.client.setPasswordHash(hashPassword(newPassword));
                usersFacade.setUser(this.client);
                int result = usersFacade.changePassword();
                if (result == 1) {
                    usersFacade.activateUser();
                    FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Password succesfully changed.",
                            "Password succesfully changed.");
                    FacesContext.getCurrentInstance().addMessage(
                            "change_password_form", facesMessage);
                    this.newPassword = this.newPasswordAgain = this.email
                            = this.userVerificationCode = null;
                    verificationCode = -1;

                } else {
                    FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Could not change password.",
                            "Could not change password.");
                    FacesContext.getCurrentInstance().addMessage(
                            "change_password_form", facesMessage);
                }
            } catch (SQLException ex) {
                Logger.getLogger(ForgotPasswordController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        } else if (this.lm != null) {
            try {
                if (verificationCode == -1) {
                    FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Expired Verification Code. Try another code.",
                            "ExpiredVerification Code. Try another code.");
                    FacesContext.getCurrentInstance().addMessage(
                            "change_password_form:user_verification_code", facesMessage);
                    return;
                } else if (verificationCode != Integer.parseInt(this.userVerificationCode)) {
                    FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Invalid Verification Code.",
                            "Invalid Verification Code.");
                    FacesContext.getCurrentInstance().addMessage(
                            "change_password_form:user_verification_code", facesMessage);
                    return;
                }

                //Hash the password
                this.lm.setPasswordHash(hashPassword(newPassword));
                usersFacade.setUser(this.lm);
                int result = usersFacade.changePassword();
                if (result == 1) {
                    usersFacade.activateUser();
                    FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Password succesfully changed.",
                            "Password succesfully changed.");
                    FacesContext.getCurrentInstance().addMessage(
                            "change_password_form", facesMessage);
                    this.newPassword = this.newPasswordAgain = this.email
                            = this.userVerificationCode = null;
                    verificationCode = -1;
                } else {
                    FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Could not change password.",
                            "Could not change password.");
                    FacesContext.getCurrentInstance().addMessage(
                            "change_password_form", facesMessage);
                }

            } catch (SQLException ex) {
                Logger.getLogger(ForgotPasswordController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        } else {
            FacesMessage facesMessage = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Please send the verification code first.",
                    "Please send the verification code first.");
            FacesContext.getCurrentInstance().addMessage(
                    "change_password_form", facesMessage);
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
        newPassword = (String) map.get("change_password_form:new_password");
        newPasswordAgain = value.toString();

        if (!newPassword.equals(newPasswordAgain)) {
            String error = "Your passwords do not match";
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    error, error));
        }
    }

    /**
     * Hash the user's password
     *
     * @return the salted hash
     */
    private String hashPassword(String password) {
        String passwordHash = null;
        try {
            passwordHash = PasswordHasher.createHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(ClientApplicationController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return passwordHash;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public int getVerificationCode() {
        return verificationCode;
    }

    public void setVerifivationCode(int verificationCode) {
        ForgotPasswordController.verificationCode = verificationCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPasswordAgain() {
        return newPasswordAgain;
    }

    public void setNewPasswordAgain(String newPasswordAgain) {
        this.newPasswordAgain = newPasswordAgain;
    }

    public String getUserVerificationCode() {
        return userVerificationCode;
    }

    public void setUserVerificationCode(String userVerificationCode) {
        this.userVerificationCode = userVerificationCode;
    }

    // Inner task class for expiring verification code
    private class Verifier implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(5000 * 60);
                ForgotPasswordController.verificationCode = -1;
            } catch (InterruptedException ex) {
                Logger.getLogger(ForgotPasswordController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
