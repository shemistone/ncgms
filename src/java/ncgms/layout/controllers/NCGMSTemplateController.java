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
    

    /**
     * Creates a new instance of NCGMSTemplateController
     */
    public NCGMSTemplateController() {
    }

    
    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
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
