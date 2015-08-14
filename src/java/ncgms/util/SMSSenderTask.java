/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class SMSSenderTask implements Runnable{

    private String phone;
    private String message;

    public SMSSenderTask(String phone, String message) {
        this.phone = phone;
        this.message = message;
    }
    
    @Override
    public void run() {
        try {
            SMSSender.sendSmsSynchronous(this.phone, this.message);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(SMSSenderTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
