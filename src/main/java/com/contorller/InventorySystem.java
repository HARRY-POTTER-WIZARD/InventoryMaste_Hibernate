package com.contorller;

import java.io.File;
import java.io.IOException;

import org.apache.catalina.Context;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

import com.util.HibernateUtil;

public class InventorySystem {
	public static void main(String[] args) throws Exception {
		System.out.println("Initializing Hibernate...");
        HibernateUtil.getSessionFactory(); // This should create tables if configured correctly
        System.out.println("Hibernate initialized");
        
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        String projectRoot = new File(".").getCanonicalPath();
        String docBase = new File(projectRoot, "src/main/webapp").getAbsolutePath();
        Context context = tomcat.addContext("", docBase);
        context.addWelcomeFile("/displayProduct.html");

        // Add DefaultServlet for static files correctly
        Tomcat.addServlet(context, "default", new DefaultServlet());
        context.addServletMappingDecoded("/*", "default");

        // Ensure static files are served properly
        context.setResources(new org.apache.catalina.webresources.StandardRoot(context));

     // Add servlets with mappings
        tomcat.addServlet("", "InventoryServlet", "com.contorller.InventoryServlet");
        context.addServletMappingDecoded("/produtcs/*", "InventoryServlet");

        
     // Ensure connector is initialized
        tomcat.getConnector(); // This forces connector setup
        
        System.out.println("Starting Tomcat...");
        tomcat.start();
        System.out.println("Tomcat started on http://localhost:");
        tomcat.getServer().await();
	}
}
