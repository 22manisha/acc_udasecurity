package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmState;
import com.udacity.catpoint.security.service.SecuritySystemService;
import com.udacity.catpoint.security.service.UIStyleService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class DisplayPanel extends JPanel implements SecurityStatusListener {

    private JLabel currentStatusLabel;

    public DisplayPanel(SecuritySystemService securityService) {
        super();
        setLayout(new MigLayout());

        securityService.addStatusListener(this);

        JLabel panelLabel = new JLabel("Very Secure Home Security");
        JLabel systemStatusLabel = new JLabel("System Status:");
        currentStatusLabel = new JLabel();

        panelLabel.setFont(UIStyleService.HEADING_FONT);

        // Initialize current status
        notify(securityService.getAlarmStatus());

        add(panelLabel, "span 2, wrap");
        add(systemStatusLabel);
        add(currentStatusLabel, "wrap");
    }

    @Override
    public void notify(AlarmState status) {
        currentStatusLabel.setText(status.getDescription());
        currentStatusLabel.setBackground(status.getColor());
        currentStatusLabel.setOpaque(true);
    }

    @Override
    public void catDetected(boolean catDetected) {
        // no behavior necessary for display panel
    }

    @Override
    public void sensorStatusChanged() {
        // no behavior necessary for display panel
    }
}
