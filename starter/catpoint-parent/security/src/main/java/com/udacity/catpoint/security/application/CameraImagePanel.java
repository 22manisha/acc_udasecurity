package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmState;
import com.udacity.catpoint.security.service.SecuritySystemService;
import com.udacity.catpoint.security.service.UIStyleService;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Panel containing the camera output.
 * Allows users to refresh the camera image and scan it for analysis.
 */
public class CameraImagePanel extends JPanel implements SecurityStatusListener {

    private SecuritySystemService securityService;

    private JLabel cameraHeader;
    private JLabel cameraLabel;
    private BufferedImage currentCameraImage;

    private static final int IMAGE_WIDTH = 300;
    private static final int IMAGE_HEIGHT = 225;

    public CameraImagePanel(SecuritySystemService securityService) {
        super();
        setLayout(new MigLayout());
        this.securityService = securityService;
        securityService.addStatusListener(this);

        cameraHeader = new JLabel("Camera Feed");
        cameraHeader.setFont(UIStyleService.HEADING_FONT);

        cameraLabel = new JLabel();
        cameraLabel.setBackground(Color.WHITE);
        cameraLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        cameraLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JButton addPictureButton = new JButton("Refresh Camera");
        addPictureButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Select Picture");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }

            try {
                currentCameraImage = ImageIO.read(chooser.getSelectedFile());
                Image tmp = new ImageIcon(currentCameraImage).getImage();
                cameraLabel.setIcon(
                        new ImageIcon(tmp.getScaledInstance(
                                IMAGE_WIDTH,
                                IMAGE_HEIGHT,
                                Image.SCALE_SMOOTH))
                );
            } catch (IOException | NullPointerException ex) {
                JOptionPane.showMessageDialog(this, "Invalid image selected.");
            }
            repaint();
        });

        JButton scanPictureButton = new JButton("Scan Picture");
        scanPictureButton.addActionListener(
                e -> securityService.processImage(currentCameraImage)
        );

        add(cameraHeader, "span 3, wrap");
        add(cameraLabel, "span 3, wrap");
        add(addPictureButton);
        add(scanPictureButton);
    }

    @Override
    public void notify(AlarmState status) {
        // no behavior required
    }

    @Override
    public void catDetected(boolean catDetected) {
        cameraHeader.setText(
                catDetected
                        ? "DANGER - CAT DETECTED"
                        : "Camera Feed - No Cats Detected"
        );
    }

    @Override
    public void sensorStatusChanged() {
        // no behavior required
    }
}
