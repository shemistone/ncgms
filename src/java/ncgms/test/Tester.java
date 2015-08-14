/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import ncgms.daos.TestsFacade;
import ncgms.daos.TestsFacade.Student;
import ncgms.entities.Client;
import ncgms.entities.Driver;
import ncgms.entities.Subcounty;
import ncgms.entities.Package;
import ncgms.entities.Tout;
import ncgms.entities.Truck;
import ncgms.entities.User;
import ncgms.daos.ClientsFacade;
import ncgms.daos.DriversFacade;
import ncgms.daos.InvoicesFacade;
import ncgms.daos.LogisticsManagersFacade;
import ncgms.daos.PackagesFacade;
import ncgms.daos.SubcountiesFacade;
import ncgms.daos.ToutsFacade;
import ncgms.daos.TrucksFacade;
import ncgms.daos.UsersFacade;
import ncgms.entities.LogisticsManager;
import ncgms.util.PasswordHasher;
import ncgms.util.SMSSender;

/**
 *
 * @author root
 */
public class Tester {

    private static List<Subcounty> subcountyList = new ArrayList<>();
    private static List<Truck> truckList = new ArrayList<>();
    private static List<Package> packageList = new ArrayList<>();
    private static List<Student> studentList = new ArrayList<>();

    public static void main(String[] args) {
        //insertAdmin();
        //insertUsers();
        processInvoices();
    }

    public static void loadAllStudents() {
        try {
            studentList = new TestsFacade().loadAllStudents();
        } catch (SQLException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void insertUsers() {
        loadAllStudents();
        loadAllSubcounties();
        loadAllTrucks();
        loadAllPackages();

        String firstName, lastName, phone, plotName, plateNumber, address, username,
                password, email, passwordHash;
        int idNumber;
        long dateAdded;
        Subcounty subcounty;
        Package packageObject;
        Truck truck;

        for (int i = 0; i < 140; i++) {
            try {
                TestsFacade testsFacade = new TestsFacade();
                TestsFacade.Student student = testsFacade.new Student(studentList.get(i).getFname(),
                        studentList.get(i).getLname(), studentList.get(i).getEmail(),
                        studentList.get(i).getPhone(), studentList.get(i).getIdNo());
                /**
                 * Set the client's attributes *
                 */
                firstName = student.getFname();
                lastName = student.getLname();
                address = (10 + new Random().nextInt(89)) + "-" + (10000 + new Random().nextInt(19000))
                        + " " + subcountyList.get(new Random().nextInt(10)).getSubcountyName();
                phone = student.getPhone();
                email = student.getEmail();
                plotName = student.getFname() + " " + student.getFname().substring(0, 1)
                        + student.getLname().substring(0, 1);
                dateAdded = new Date().getTime();
                idNumber = student.getIdNo();
                subcounty = subcountyList.get(new Random().nextInt(subcountyList.size()));
                plateNumber = truckList.get(new Random().nextInt(truckList.size())).getPlateNumber();
                username = student.getFname().toLowerCase();
                password = student.getFname().toLowerCase() + "2015";
                packageObject = packageList.get(new Random().nextInt(3));
                truck = truckList.get(new Random().nextInt(truckList.size()));
                passwordHash = PasswordHasher.createHash(password);

                // Create and insert user
                User user = new User(username, passwordHash, 0);
                UsersFacade usersFacade = new UsersFacade(user);
                usersFacade.insertUser();

                if (i < 100) {
                    // Create and insert client
                    Client client = new Client(usersFacade.loadUserID(), username,
                            passwordHash, 0, firstName, lastName, address, phone, email,
                            plotName, dateAdded, idNumber, 0, subcounty, null, packageObject);
                    ClientsFacade clientsFacade = new ClientsFacade(client);
                    clientsFacade.insertClient();
                } else if (i >= 100 && i < 120) {
                    // Create and insert driver
                    Driver driver = new Driver(usersFacade.loadUserID(), username, passwordHash, 0,
                            firstName, lastName, phone, email, address, dateAdded,
                            idNumber, "1434006529317_cv.docx", null,subcounty);
                    DriversFacade driversFacade = new DriversFacade(driver);
                    driversFacade.insertDriver();
                    System.out.print(driver);
                } else {
                    // Create and insert tout
                    Tout tout = new Tout(usersFacade.loadUserID(), username, passwordHash, 0,
                            firstName, lastName, phone, email, address, dateAdded,
                            idNumber, "1434006529317_cv.docx", null,subcounty);
                    ToutsFacade toutsFacade = new ToutsFacade(tout);
                    toutsFacade.insertTout();
                    System.out.print(tout);
                }
            } catch (SQLException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void loadAllSubcounties() {
        try {
            subcountyList = new SubcountiesFacade().loadAllSubcounties();
        } catch (SQLException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loadAllTrucks() {
        try {
            truckList = new TrucksFacade().loadAllTrucks();
        } catch (SQLException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loadAllPackages() {
        try {
            packageList = new PackagesFacade().loadAllPackages();
        } catch (SQLException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void insertAdmin() {
        try {
            LogisticsManager lm = new LogisticsManager(0, "admin", PasswordHasher.createHash("burton2015"),
                    1, "Burton", "Muchemi", "shemistone@gmail.com", "0721868821");
            UsersFacade usersFacade = new UsersFacade(lm);
            usersFacade.insertUser();
            lm.setUserID(usersFacade.loadAdminUserID());
            LogisticsManagersFacade lmf = new LogisticsManagersFacade(lm);
            lmf.insertLogisticsManager();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void sendSMS() {
        try {
            System.out.print("Sending...\n");
            if (SMSSender.sendSmsSynchronous("0724159903", "Hello Nduthi")) {
                System.out.print("Message sent\n");
            } else {
                System.out.print("Message not sent\n");
            }
        } catch (IOException | InterruptedException ex) {
            System.out.print("[error] Could not send message\n");
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void processInvoices() {
        try {
            new InvoicesFacade().createMonthlyInvoicesForAllClients();
        } catch (SQLException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
