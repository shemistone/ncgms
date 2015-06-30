/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ncgms.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import ncgms.test.Tester;

/**
 *
 * @author root
 */
public class CharacterStream{
    
    public static String readLine(String filePath) throws IOException{
        StringBuilder sb = new StringBuilder();
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        sb.append(br.readLine());
        br.close();
        return sb.toString();
    }
    
    public static void write(String filePath, String content) throws IOException{
        File file = new File(filePath);        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        bw.write(content);
        bw.close();
    }
    
    public static void main(String[] args){
        try {
            System.out.printf(CharacterStream.readLine("/etc/ncgms/invoice.conf"));
        } catch (IOException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
