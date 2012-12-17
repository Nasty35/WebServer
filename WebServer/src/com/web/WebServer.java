package com.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

/**
*
* @author Nasty(Daniel)
*/
public class WebServer {
	
	public static String mError;
    public static List<String> bans;
    public static String port;
    public static String folder;
	
	public static void main(String[] args) {
        new GUI().setVisible(true);
		Environment();
	}
	
	public static void Environment() {
		Properties prop = new Properties();
		try {
            loadBans();
			prop.load(new FileInputStream(new File("server.properties")));
		} catch (FileNotFoundException e) {
			System.err.println("Not found configuration file: \"server.properties\"");
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		mError = prop.getProperty("error.pagenotfound");
        port = prop.getProperty("server.port");
        folder = prop.getProperty("server.foldersource");
		com.web.network.Server.run();
		prop.clear();
		prop = null;
	}
        
    public static void Write(final String text) {
        if(GUI.jCheckBox1.isSelected()) return;
        GUI.panel.setText(GUI.panel.getText().concat(text).concat("\n"));
    }
        
    public static void loadBans() throws IOException {
	    bans = new ArrayList<String>();
        BufferedReader in = new BufferedReader(new FileReader("bans.txt"));
        for(String linea = ""; (linea = in.readLine()) != null; ) {
            if(!linea.replace(" ", "").isEmpty())
                bans.add(linea);
        }
        in.close();
        in = null;
    }

}
