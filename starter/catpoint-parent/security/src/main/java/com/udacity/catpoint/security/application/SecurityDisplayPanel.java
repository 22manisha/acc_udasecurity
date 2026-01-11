package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmState;
import com.udacity.catpoint.security.data.SecuritySensor;
import com.udacity.catpoint.security.data.SecuritySensorType;
import com.udacity.catpoint.security.service.SecuritySystemService;
import com.udacity.catpoint.security.service.UIStyleService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class SecurityDisplayPanel extends JPanel implements SecurityStatusListener {

    private SecuritySystemService securityService;

    private JLabel panelLabel = new JLabel("Sensor Management");
    private JLabel newSensorName = new JLabel("Name:");
    private JLabel newSensorType = new JLabel("Sensor Type:");
    private JTextField newSensorNameField = new JTextField();
    private JComboBox<SecuritySensorType> newSensorTypeDropdown =
            new JComboBox<>(SecuritySensorType.values());
    private JButton addNewSensorButton = new JButton("Add New Sensor");

    private JPanel sensorListPanel;
    private JPanel newSensorPanel;

    public SecurityDisplayPanel(SecuritySystemService securityService) {
        super();
        setLayout(new MigLayout());
        this.securityService = securityService;
        securityService.addStatusListener(this);

        panelLabel.setFont(UIStyleService.HEADING_FONT);

        addNewSensorButton.addActionListener(e ->
                addSensor(new SecuritySensor(
                        newSensorNameField.getText(),
                        (SecuritySensorType) newSensorTypeDropdown.getSelectedItem()
                ))
        );

        newSensorPanel = buildAddSensorPanel();
        sensorListPanel = new JPanel(new MigLayout());

        updateSensorList(sensorListPanel);

        add(panelLabel, "wrap");
        add(newSensorPanel, "span");
        add(sensorListPanel, "span");
    }

    private JPanel buildAddSensorPanel() {
        JPanel panel = new JPanel(new MigLayout());
        panel.add(newSensorName);
        panel.add(newSensorNameField, "width 50:100:200");
        panel.add(newSensorType);
        panel.add(newSensorTypeDropdown, "wrap");
        panel.add(addNewSensorButton, "span 3");
        return panel;
    }

    /**
     * Updates the sensor list UI.
     */
    private void updateSensorList(JPanel panel) {
        panel.removeAll();

        securityService.getSensors()
                .stream()
                .sorted()
                .forEach(sensor -> {
                    JLabel sensorLabel = new JLabel(
                            String.format(
                                    "%s (%s): %s",
                                    sensor.getName(),
                                    sensor.getSensorType(),
                                    sensor.getActive() ? "Active" : "Inactive"
                            )
                    );

                    JButton toggleButton =
                            new JButton(sensor.getActive() ? "Deactivate" : "Activate");
                    JButton removeButton = new JButton("Remove Sensor");

                    toggleButton.addActionListener(
                            e -> setSensorActivity(sensor, !sensor.getActive())
                    );
                    removeButton.addActionListener(
                            e -> removeSensor(sensor)
                    );

                    panel.add(sensorLabel, "width 300:300:300");
                    panel.add(toggleButton, "width 100:100:100");
                    panel.add(removeButton, "wrap");
                });

        repaint();
        revalidate();
    }

    private void setSensorActivity(SecuritySensor sensor, Boolean active) {
        securityService.changeSensorActivationStatus(sensor, active);
        updateSensorList(sensorListPanel);
    }

    private void addSensor(SecuritySensor sensor) {
        if (securityService.getSensors().size() < 4) {
            securityService.addSensor(sensor);
            updateSensorList(sensorListPanel);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "To add more than 4 sensors, please subscribe to Premium Membership!"
            );
        }
    }

    private void removeSensor(SecuritySensor sensor) {
        securityService.removeSensor(sensor);
        updateSensorList(sensorListPanel);
    }

    @Override
    public void notify(AlarmState status) {
        // no action needed
    }

    @Override
    public void catDetected(boolean catDetected) {
        // no action needed
    }

    @Override
    public void sensorStatusChanged() {
        updateSensorList(sensorListPanel);
    }
}
