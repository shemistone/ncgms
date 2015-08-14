/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

/**
 *
 * @author root
 */
public class EmailSenderTask implements Runnable{
    
    
    EmailSender es = new EmailSender();
    
    String to;
    String subject;
    String message;

    public EmailSenderTask(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }
    
    @Override
    public void run() {
        try {
            es.sendMail(to, subject, message);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailSenderTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EmailSender getEs() {
        return es;
    }

    public void setEs(EmailSender es) {
        this.es = es;
    }
   
    
}
