/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */
public class TestsFacade{

    private static Connection connection;
    //Connect to the database
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/sovos", 
                    "root", "0-Mysql-Burton");
        } catch (ClassNotFoundException | SQLException ex) {
            //Log any error
            Logger.getLogger(AbstractFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Student student = null;

    public List<Student> loadAllStudents() throws SQLException {
        ArrayList<Student> studentList = new ArrayList<>();
        Statement statement = connection.createStatement();
        String query = "SELECT DISTINCT * FROM `student` ORDER BY `student_id` ASC";
        ResultSet resultSet = statement.executeQuery(query);
        while(resultSet.next()){
            this.student = new Student(resultSet.getString("first_name"), 
                    resultSet.getString("last_name"), resultSet.getString("email"), 
                    resultSet.getString("phone_no"), resultSet.getInt("id_no"));
            studentList.add(student);
        }
        return studentList;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public class Student {

        private String fname;
        private String lname;
        private String email;
        private String phone;
        private int idNo;

        public Student(String fname, String lname, String email, 
                String phone, int idNo) {
            this.fname = fname;
            this.lname = lname;
            this.email = email;
            this.phone = phone;
            this.idNo = idNo;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getLname() {
            return lname;
        }

        public void setLname(String lname) {
            this.lname = lname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getIdNo() {
            return idNo;
        }

        public void setIdNo(int idNo) {
            this.idNo = idNo;
        }

        @Override
        public String toString(){
            return "\nFirst Name - " + fname +
                    "\nLast Name - " + lname +
                    "\nEmail - " + email +
                    "\nPhone - " + phone +
                    "\nId Number - " + idNo +
                    "\n";
        }
    }

}
