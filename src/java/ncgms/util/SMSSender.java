/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.util;

import java.io.IOException;

/**
 *
 * @author root
 */
public class SMSSender {

    public static boolean sendSmsSynchronous(final String phone, final String message) 
            throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder().command("/usr/bin/gsmsendsms",
                "-d", "/dev/ttyUSB0", "-b", "19200", phone, message);
        Process gsmsendsmsProcess;
        gsmsendsmsProcess = processBuilder.start();
        gsmsendsmsProcess.waitFor();

        return gsmsendsmsProcess.exitValue() == 0;
    }

    public static int sendSmsSynchronousWithExit(final String phone, final String message)
            throws IOException, InterruptedException {
        final ProcessBuilder processBuilder = new ProcessBuilder().command("/usr/bin/gsmsendsms",
                "-d", "/dev/ttyUSB0", "-b", "19200", phone, message);
        Process gsmsendsmsProcess;

        gsmsendsmsProcess = processBuilder.start();
        gsmsendsmsProcess.waitFor();

        return gsmsendsmsProcess.exitValue();
    }

    /*
     public static boolean sendSmsSynchronous(final String phone, final String message) {
     final ProcessBuilder processBuilder = new ProcessBuilder().command("/usr/bin/gsmsendsms",
     "-d", "/dev/ttyUSB0", "-b", "19200", phone, message);
     Process gsmsendsmsProcess;
     try {
     gsmsendsmsProcess = processBuilder.start();
     gsmsendsmsProcess.waitFor();
     } catch (final IOException | InterruptedException e) {
     return false;
     }

     return gsmsendsmsProcess.exitValue() == 0;
     }*/
    /*
     public static int sendSmsSynchronousWithExit(final String phone, final String message)
     throws IOException, InterruptedException {
     final ProcessBuilder processBuilder = new ProcessBuilder().command("/usr/bin/gsmsendsms",
     "-d", "/dev/ttyUSB0", "-b", "19200", phone, message);

     Process gsmsendsmsProcess = null;
     try {
     gsmsendsmsProcess = processBuilder.start();
     gsmsendsmsProcess.waitFor();
     } catch (final IOException | InterruptedException e) {
     return false;
     }
     return gsmsendsmsProcess.exitValue();
     }
     */
}
