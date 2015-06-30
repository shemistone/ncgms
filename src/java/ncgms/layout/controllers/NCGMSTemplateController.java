package ncgms.layout.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
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
import ncgms.client.controllers.ClientApplicationController;
import ncgms.controllers.LogInLogOutController;
import ncgms.entities.User;
import ncgms.daos.UsersFacade;
import ncgms.util.PasswordHasher;

/**
 *
 * @author root
 */
@ManagedBean (name = "ncgmsTemplateController")
@RequestScoped
public class NCGMSTemplateController {

    private String applicationName = "Nakuru County Garbage Collection";
    @Pattern(regexp = "^.*[a-zA-Z0-9]{8}+.*$", message = "Input current password")
    private String currentPassword;
    @Pattern(regexp = "^.*[a-zA-Z0-9]{8}+.*$", message = "Password must be at least"
            + " 8 alphanumeric characters.")
    private String newPassword;
    @Pattern(regexp = "^.*[a-zA-Z0-9]{8}+.*$", message = "Re-enter new password")
    private String newPasswordAgain;

    /**
     * Creates a new instance of NCGMSTemplateController
     */
    public NCGMSTemplateController() {
    }

    public void changePassword() {
        try {
            // Get the username
            String username = new LogInLogOutController().getUserFromSession().getUsername();
            User user = new User(username, null, 0);
            UsersFacade usersFacade = new UsersFacade(user);
            usersFacade.loadPasswordHash();
            // Check if password will produce expected hash
            boolean passwordExistent = PasswordHasher.validatePassword(
                    currentPassword, usersFacade.getUser().getPasswordHash());
            
            if (passwordExistent) {
                //Hash the password
                usersFacade.getUser().setPasswordHash(hashPassword(newPassword));
                int result = usersFacade.changePassword();
                if (result == 1) {
                    FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Password succesfully changed.",
                            "Password succesfully changed.");
                    FacesContext.getCurrentInstance().addMessage("password_form", facesMessage);
                } else {
                    FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Could not change password.",
                            "Could not change password.");
                    FacesContext.getCurrentInstance().addMessage("password_form", facesMessage);
                }
            } else {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Enter correct password",
                        "Enter correct password");
                FacesContext.getCurrentInstance().addMessage("password_form", facesMessage);
            }
        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Could not change password.",
                    "Could not change password.");
            FacesContext.getCurrentInstance().addMessage("password_form", facesMessage);
            Logger.getLogger(NCGMSTemplateController.class.getName()).log(Level.SEVERE, null, ex);
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
        newPassword = (String) map.get("password_form:new_password");
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

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordAgain() {
        return newPasswordAgain;
    }

    public void setNewPasswordAgain(String newPasswordAgain) {
        this.newPasswordAgain = newPasswordAgain;
    }

    /**
     * Return log in URL
     *
     * @return the login URL
     */
    public String toLogIn() {
        return "/log_in?faces-redirect=true";
    }

    public String toClientApplication() {
        return "/client/application?faces-redirect=true";
    }

    public String toDriverApplication() {
        return "/driver_application?faces-redirect=true";
    }

    public String toToutApplication() {
        return "/tout_application?faces-redirect=true";
    }

}
