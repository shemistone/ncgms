package ncgms.controllers;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Pattern;
import ncgms.entities.Client;
import ncgms.entities.User;
import ncgms.daos.ClientsFacade;
import ncgms.daos.LogisticsManagersFacade;
import ncgms.util.PasswordHasher;
import ncgms.daos.UsersFacade;
import ncgms.entities.LogisticsManager;

/**
 * Controller class for user log in page
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class LogInLogOutController implements Serializable {

    private ExecutorService executorService;
    private HttpSession httpSession = null;
    private Client client = new Client();
    private LogisticsManager logisticsManager = new LogisticsManager();
    private User user = new User();
    private int failedTries;
    private String previousUsername = null;
    @Pattern(regexp = "^.+$", message = "Input Username.")
    private String username;
    @Pattern(regexp = "^.+$", message = "Input Password.")
    private String password;

    /**
     * Creates a new instance of UserLogInController
     */
    public LogInLogOutController() {
    }

    public String validateUser() {

        /**
         * Get request URI and referer *
         */
        HttpServletRequest hsr = (HttpServletRequest) FacesContext.
                getCurrentInstance().getExternalContext().getRequest();
        // String referer = hsr.getHeader("referer");
        String requestURI = hsr.getRequestURI();
        /**
         * Get request URI and referer *
         */

        // Get the user
        this.user = (User) getUserFromSession();
        UsersFacade usersFacade = new UsersFacade();
        usersFacade.setUser(this.user);
        if (this.user != null) {
            try {
                //Return view based on user category
                String userCategory = usersFacade.loadUserCategory();
                switch (userCategory) {
                    case "client":
                        return "/client/index.xhtml";
                    case "admin":
                        return "/admin/index.xhtml";
                    default:
                        break;
                }
            } catch (SQLException ex) {
                Logger.getLogger(LogInLogOutController.class.getName()).
                        log(Level.SEVERE, null, ex);
            }

        }
        return "/log_in";
    }

    public String validateClient() {

        /**
         * Get request URI and referer *
         */
        HttpServletRequest hsr = (HttpServletRequest) FacesContext.
                getCurrentInstance().getExternalContext().getRequest();
        // String referer = hsr.getHeader("referer");
        String requestURI = hsr.getRequestURI();
        /**
         * Get request URI and referer *
         */

        // Get the client
        this.client = (Client) getClientFromSession();
        UsersFacade usersFacade = new UsersFacade();
        usersFacade.setUser(this.client);
        if (this.client != null) {
            if (requestURI.contains("log_in")) {
                try {
                    //Return view based on user category
                    String userCategory = usersFacade.loadUserCategory();
                    switch (userCategory) {
                        case "client":
                            return "/client/index.xhtml";
                        case "admin":
                            return "/admin/index.xhtml";
                        default:
                            break;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LogInLogOutController.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            } else {
                //System.out.print(requestURI);
                int startIndex = requestURI.lastIndexOf("faces") + 6;
                int endIndex = requestURI.lastIndexOf(".");
                return "/" + requestURI.substring(startIndex, endIndex);
            }
        }
        return "/log_in";
    }

    public String validateLogisticsManager() {

        /**
         * Get request URI and referer *
         */
        HttpServletRequest hsr = (HttpServletRequest) FacesContext.
                getCurrentInstance().getExternalContext().getRequest();
        // String referer = hsr.getHeader("referer");
        String requestURI = hsr.getRequestURI();
        /**
         * Get request URI and referer *
         */

        // Get the logistics manager
        this.logisticsManager = (LogisticsManager) getLogisticsManagerFromSession();
        UsersFacade usersFacade = new UsersFacade();
        usersFacade.setUser(this.logisticsManager);
        if (this.logisticsManager != null) {
            if (requestURI.contains("log_in")) {
                try {
                    //Return view based on user category
                    String userCategory = usersFacade.loadUserCategory();
                    switch (userCategory) {
                        case "client":
                            return "/client/index.xhtml";
                        case "admin":
                            return "/admin/index.xhtml";
                        default:
                            break;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LogInLogOutController.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            } else {
                //System.out.print(requestURI);
                int startIndex = requestURI.lastIndexOf("faces") + 6;
                int endIndex = requestURI.lastIndexOf(".");
                return "/" + requestURI.substring(startIndex, endIndex);
            }
        }
        return "/log_in";
    }

    public String logIn() {
        try {
            this.executorService = Executors.newCachedThreadPool();
            UsersFacade usersFacade = new UsersFacade();
            // Get the user
            this.user = (User) usersFacade.searchUserByUsername(username);

            boolean valid = false;
            if (this.user.getUserID() > 0) {
                valid = PasswordHasher.validatePassword(password,
                        this.user.getPasswordHash());
                usersFacade.setUser(this.user);
                // Check if account is active
                if (!usersFacade.isActive()) {
                    FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Your account is inactive.",
                            "Your account is inactive.");
                    FacesContext.getCurrentInstance().addMessage("user_log_in_form:password",
                            facesMessage);
                    return "/log_in";
                }

            } else {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Invalid Log In Credentials.",
                        "Invalid Log In Credentials.");
                FacesContext.getCurrentInstance().addMessage("user_log_in_form:username",
                        facesMessage);
                return "/log_in";
            }

            // The user credentials are valid
            if (valid) {
                
                // Get session and set an attribute
                String userCategory = usersFacade.loadUserCategory();
                switch (userCategory) {
                    case "client":
                        // Create a new ClientsFacade
                        ClientsFacade clientsFacade = new ClientsFacade();
                        // Load the client
                        this.client = clientsFacade.searchClientByClientID(this.user.getUserID());
                        // Check if the user wants to cancel
                        if (this.client.getWantsToCancel() == 1) {
                            FacesMessage facesMessage = new FacesMessage(
                                    FacesMessage.SEVERITY_ERROR,
                                    "Your deactivated your account.",
                                    "Your deactivated your account.");
                            FacesContext.getCurrentInstance().addMessage(
                                    "user_log_in_form:password",
                                    facesMessage);
                            return "/log_in";
                        }

                        // Get session and set and set an attribute
                        httpSession = getSession();
                        httpSession.setAttribute("client", this.client);
                        httpSession.setAttribute("user", this.client);

                        // Return view based on user category
                        return "/client/index?faces-redirect=true";
                    case "admin":
                        // Create new LogisticsManager
                        LogisticsManagersFacade lmFacade = new LogisticsManagersFacade();
                        // Load the Logistics Manager
                        this.logisticsManager = lmFacade.searchLogisticsManagerByID(
                                this.user.getUserID());
                        // Get session and set and set an attribute
                        httpSession = getSession();
                        httpSession.setAttribute("logisticsManager", this.logisticsManager);
                        httpSession.setAttribute("user", this.logisticsManager);

                        // Return view based on user category
                        return "/admin/index?faces-redirect=true";
                    default:
                        break;
                }
                this.failedTries = 0;
            } else {// User credentials invalid
                this.failedTries += 1;
                // Deactivate account if tries are more or equal to three
                if (this.failedTries >= 3 && this.previousUsername.equals(this.username)) {
                    usersFacade.deactivateUser();
                    FacesMessage facesMessage = new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Your account has been blocked. Use the"
                            + " \"Forgot Password?\" link to unblock it.",
                            "Your account has been blocked. Use the "
                            + "\"Forgot Password?\" link to unblock it.");
                    FacesContext.getCurrentInstance().addMessage(
                            null,
                            facesMessage);
                    return "/log_in";
                }else if(this.failedTries >= 3 && !this.previousUsername.equals(this.username)){
                    this.failedTries = 0;
                }
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Invalid Log In Credentials",
                        "Invalid Log In Credentials");
                FacesContext.getCurrentInstance().addMessage("user_log_in_form:password",
                        facesMessage);
                return "/log_in";
            }

        } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not connect to database",
                    "Could not connecct to database");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(LogInLogOutController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Set previous username
            this.previousUsername = this.username;
        }
        return "/log_in";
    }

    public String logOut() {
        httpSession = getSession();
        httpSession.invalidate();
        return "/log_in?faces-redirect=true";
    }

    public HttpSession getSession() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) facesContext.
                getExternalContext().getRequest();
        httpSession = request.getSession();
        return httpSession;
    }

    public User getUserFromSession() {
        httpSession = getSession();
        this.user = (User) httpSession.getAttribute("user");
        return this.user;
    }

    public User getClientFromSession() {
        httpSession = getSession();
        this.client = (Client) httpSession.getAttribute("client");
        return this.client;
    }

    public User getLogisticsManagerFromSession() {
        httpSession = getSession();
        this.logisticsManager = (LogisticsManager) httpSession.
                getAttribute("logisticsManager");
        return this.logisticsManager;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public LogisticsManager getLogisticsManager() {
        return logisticsManager;
    }

    public void setLogisticsManager(LogisticsManager logisticsManager) {
        this.logisticsManager = logisticsManager;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getFailedTries() {
        return failedTries;
    }

    public void setFailedTries(int failedTries) {
        this.failedTries = failedTries;
    }

}
