package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.InMemorySecurityRepository;
import com.udacity.catpoint.security.data.SecurityDataRepository;
import com.udacity.catpoint.image.service.FakeImageService;
import com.udacity.catpoint.security.service.SecuritySystemService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;


public class CatPointGUI extends JFrame {

    public CatPointGUI() {
        SecurityDataRepository securityRepository = new InMemorySecurityRepository();
        FakeImageService imageService = new FakeImageService();
        SecuritySystemService securityService = new SecuritySystemService(securityRepository, imageService);

        DisplayPanel displayPanel = new DisplayPanel(securityService);
        SecurityControlPanel controlPanel = new SecurityControlPanel(securityService);
        SecurityDisplayPanel sensorPanel = new SecurityDisplayPanel(securityService); // previously SensorPanel
        CameraImagePanel imagePanel = new CameraImagePanel(securityService);

        setLocation(100, 100);
        setSize(600, 850);
        setTitle("Very Secure App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout());
        mainPanel.add(displayPanel, "wrap");
        mainPanel.add(imagePanel, "wrap");
        mainPanel.add(controlPanel, "wrap");
        mainPanel.add(sensorPanel);

        getContentPane().add(mainPanel);
    }
}
