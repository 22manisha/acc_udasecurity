package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.ArmingState;
import com.udacity.catpoint.security.service.SecuritySystemService;
import com.udacity.catpoint.security.service.UIStyleService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class SecurityControlPanel extends JPanel {

    private final SecuritySystemService securityService;
    private final Map<ArmingState, JButton> buttonMap;

    public SecurityControlPanel(SecuritySystemService securityService) {
        super();
        setLayout(new MigLayout());
        this.securityService = securityService;

        JLabel panelLabel = new JLabel("System Control");
        panelLabel.setFont(UIStyleService.HEADING_FONT);
        add(panelLabel, "span 3, wrap");

        buttonMap = Arrays.stream(ArmingState.values())
                .collect(Collectors.toMap(
                        state -> state,
                        state -> new JButton(state.getDescription())
                ));

        buttonMap.forEach((state, button) -> button.addActionListener(e -> {
            securityService.armedStatus(state); // <-- updated method
            buttonMap.forEach((s, b) -> b.setBackground(s == state ? s.getColor() : null));
        }));

        Arrays.stream(ArmingState.values()).forEach(state -> add(buttonMap.get(state)));

        ArmingState currentStatus = securityService.getArmingStatus();
        buttonMap.get(currentStatus).setBackground(currentStatus.getColor());
    }
}
