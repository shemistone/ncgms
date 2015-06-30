/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author root
 */
@WebListener
public class ServletStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        InvoicerTask invoicerTask = new InvoicerTask();
        // Set the date to run the invoiceTask
        Calendar futureCalendar = new GregorianCalendar();        
        int month = futureCalendar.get(Calendar.MONTH);
        int day = futureCalendar.get(Calendar.DAY_OF_MONTH);
        if (day == 1) {
            // Do nothing
        } else {
            if (month < 12) {
                month++;
                // Set calendar month to next month first day        
                futureCalendar.set(Calendar.MONTH, month);
                futureCalendar.set(Calendar.DAY_OF_MONTH, 1);
            } else {
                month = 1;
                // Set calendar month to January first day        
                futureCalendar.set(Calendar.MONTH, month);
                futureCalendar.set(Calendar.DAY_OF_MONTH, 1);
            }
        }

        Calendar otherFutureCalendar = new GregorianCalendar();
        int otherMonth = futureCalendar.get(Calendar.MONTH);
        if(otherMonth < 12){
            otherMonth++;
            // Set calendar month of othweFutureCalendar first day
            otherFutureCalendar.set(Calendar.MONTH, otherMonth);
            otherFutureCalendar.set(Calendar.DAY_OF_MONTH, 1);
        }else{
            otherMonth = 1;
            // Set calendar month of othweFutureCalendar
            otherFutureCalendar.set(Calendar.MONTH, otherMonth);
            otherFutureCalendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        long initialDelay = futureCalendar.getTime().getTime() - new GregorianCalendar().getTime().getTime();
        long period = otherFutureCalendar.getTime().getTime() - futureCalendar.getTime().getTime();
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(invoicerTask, initialDelay, period, TimeUnit.MILLISECONDS);
        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
