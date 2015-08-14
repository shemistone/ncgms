/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.admin.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import javax.validation.constraints.Pattern;
import ncgms.controllers.DriverApplicationController;
import ncgms.entities.Container;
import ncgms.daos.ContainersFacade;

/**
 *
 * @author root
 */
@ManagedBean
@SessionScoped
public class AdminContainerController implements Serializable {

    private List<Container> containerList = new ArrayList<>();

    @Pattern(regexp = "^[A-Z]{1}(([A-Za-z0-9]\\s*)|([A-Za-z0-9]\\-?[A-Za-z0-9])|([A-Za-z0-9]\\'?))+$",
            message = "Description format is invalid.")
    private String name;
    @Pattern(regexp = "^[0-9]+$",
            message = "Quantity format is invalid.")
    private String quantity;
    @Pattern(regexp = "^[0-9]+\\.?[0-9]{0,2}$", message = "Price format is invalid.")
    private String price;

    private Part file;

    /**
     * Creates a new instance of AdminContainerController
     */
    public AdminContainerController() {
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
            Logger.getLogger(AdminContainerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void validateFile(FacesContext context, UIComponent component, Object value) {
        String error = null;
        this.file = (Part) value;
        if (file != null) {
            // Check the file size
            if (file.getSize() > 1048576) {
                error = "File should be less than 1MB";
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, error));
            }
            // Check the file type
            if (!"image/jpeg".equals(file.getContentType())
                    && !"image/png".equals(file.getContentType())) {
                error = "File should be a jpg, jpeg or png file";
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, error));
            }
        } else {
            error = "Please select a jpg, jpeg or png file";
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    error, error));
        }
    }

    public void insertContainer() {
        // Get the absolute path of the container
        ServletContext sc = (ServletContext) FacesContext.
                getCurrentInstance().getExternalContext().getContext();
        String fileName = sc.getRealPath("/");

        try (InputStream is = file.getInputStream()) {

            /* Start file upload */
            byte[] b = new byte[(int) file.getSize()];
            is.read(b);
            // Get the absolute path of the uploaded file
            fileName = fileName + "resources/uploads/images/" + String.valueOf(
                    new Date().getTime()) + "_" + file.getSubmittedFileName();
            FileOutputStream os = new FileOutputStream(fileName);
            os.write(b);
            // Restore the relative path of the uploade file
            fileName = fileName.substring(fileName.indexOf("resources/uploads/images/")
                    + "resources/uploads/images/".length());
            /* Finish file upload */

            Container container = new Container(0, name, fileName, Integer.parseInt(quantity),
                    Double.parseDouble(price));
            ContainersFacade containersFacade = new ContainersFacade(container);
            int result = containersFacade.insertContainer();
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Container added", "Container added");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                this.price = this.name = this.quantity = null;
            }
        } catch (SQLException | IOException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not add container.",
                    "Could not add container.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(DriverApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Reload the containerList again
            initializeContainerList();
        }
    }

    public void removeContainer(Container container) {

        try {
            // Check if the cotainer has any order details
            if (!container.getOrderDetailList().isEmpty()) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "This container has been referenced in an order, delete the order first.",
                        "This container has been referenced in an order, delete the order first.");
                FacesContext.getCurrentInstance().addMessage("containers_form", facesMessage);
                return;
            }

            ContainersFacade containersFacade = new ContainersFacade(container);
            int result = containersFacade.removeContainer();
            if (result == 1) {
                /* Delete file from file system */
                // Get the absolute path of the container
                ServletContext sc = (ServletContext) FacesContext.getCurrentInstance().
                        getExternalContext().getContext();
                String fileName = sc.getRealPath("/");
                fileName = fileName + "resources/uploads/images/" + container.getFileName();
                Path path = Paths.get(fileName);
                Files.delete(path);
                /* Delete file from file system */
            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Could not remove container", 
                    "Could not add container");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminContainerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Could not remove container from file system",
                    "Could not remove container from file system");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            Logger.getLogger(AdminContainerController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Reload the containerList
            initializeContainerList();
        }
    }
    
    public void editContainer(Container container) {
        container.setEditable(true);
    }

    public void saveContainerChanges(Container container) {
        try {
            // Update
            container.setNoOfContainers(container.getNoOfContainers());
            ContainersFacade containersFacade = new ContainersFacade();
            int result = containersFacade.updateContainer(container);
            if (result == 1) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Changes saved.",
                        "Changes saved.");
                FacesContext.getCurrentInstance().addMessage("containers_form", facesMessage);

            } else {

            }
        } catch (SQLException ex) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not save changes.",
                    "Could not save changes.");
            FacesContext.getCurrentInstance().addMessage("containers_form", facesMessage);
            
            Logger.getLogger(AdminInvoiceController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            container.setEditable(false);
        }
    }

    public List<Container> getContainerList() {
        return containerList;
    }

    public void setContainerList(List<Container> containerList) {
        this.containerList = containerList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

}
