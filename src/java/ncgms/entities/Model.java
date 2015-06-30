/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.entities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class Model {
    
    private int modelID = 0;
    private String modelName = null;
    // Relationships
    private List<Truck> truckList = new ArrayList<>();

    public Model() {
    }
    
    public Model(int modelID) {
        this.modelID = modelID;
    }
    
    public Model(String modelName) {
        this.modelName = modelName;
    }
    
    
    public Model(int modelID, String modelName) {
        this.modelID = modelID;
        this.modelName = modelName;
    }
    
    
    public int getModelID() {
        return modelID;
    }

    public void setModelID(int modelID) {
        this.modelID = modelID;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<Truck> getTruckList() {
        return truckList;
    }

    public void setTruckList(List<Truck> truckList) {
        this.truckList = truckList;
    }
    
    @Override
    public String toString(){
        return "\nModel ID - " + modelID +
                "\nModel Name - " + modelName;
    }
    
}
