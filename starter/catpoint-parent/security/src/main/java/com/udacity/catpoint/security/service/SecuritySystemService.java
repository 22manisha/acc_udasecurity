package com.udacity.catpoint.security.service;

import com.udacity.catpoint.security.application.SecurityStatusListener;
import com.udacity.catpoint.security.data.AlarmState;
import com.udacity.catpoint.security.data.ArmingState;
import com.udacity.catpoint.security.data.SecurityDataRepository;
import com.udacity.catpoint.security.data.SecuritySensor;
import com.udacity.catpoint.image.service.ImageService;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Main service class for managing security system state.
 */
public class SecuritySystemService {

    private final ImageService imageAnalyzer;
    private final SecurityDataRepository securityRepos;
    private final Set<SecurityStatusListener> statusListeners = new HashSet<>();
    private boolean catDetected = false;

    public SecuritySystemService(SecurityDataRepository securityRepos, ImageService imageAnalyzer) {
        this.securityRepos = securityRepos;
        this.imageAnalyzer = imageAnalyzer;
    }

    public void armedStatus(ArmingState armingState) {
        if (armingState == ArmingState.ARMED_HOME && catDetected) {
            setAlarmStatus(AlarmState.ALARM);
        }
        if (armingState == ArmingState.DISARMED) {
            setAlarmStatus(AlarmState.NO_ALARM);
        } else {
            ConcurrentSkipListSet<SecuritySensor> sensors = new ConcurrentSkipListSet<>(getSensors());
            sensors.forEach(sensor -> changeSensorActivationStatus(sensor, false));
        }
        securityRepos.setArmingStatus(armingState);
        statusListeners.forEach(SecurityStatusListener::sensorStatusChanged);
    }

    private void handleCatDetection(Boolean cat) {
        catDetected = cat;

        if (cat && getArmingStatus() == ArmingState.ARMED_HOME) {
            setAlarmStatus(AlarmState.ALARM);
        } else if (!cat && allSensorsInactive()) {
            setAlarmStatus(AlarmState.NO_ALARM);
        }

        statusListeners.forEach(listener -> listener.catDetected(cat));
    }

    public void addStatusListener(SecurityStatusListener listener) {
        statusListeners.add(listener);
    }

    public void removeStatusListener(SecurityStatusListener listener) {
        statusListeners.remove(listener);
    }

    public void setAlarmStatus(AlarmState status) {
        securityRepos.setAlarmStatus(status);
        statusListeners.forEach(listener -> listener.notify(status));
    }

    private boolean allSensorsInactive() {
        return getSensors().stream().noneMatch(SecuritySensor::getActive);
    }

    private void processSensorActivation() {
        if (securityRepos.getArmingStatus() == ArmingState.DISARMED) return;

        switch (securityRepos.getAlarmStatus()) {
            case NO_ALARM -> setAlarmStatus(AlarmState.PENDING_ALARM);
            case PENDING_ALARM -> setAlarmStatus(AlarmState.ALARM);
            default -> {}
        }
    }

    private void processSensorDeactivation() {
        if (securityRepos.getAlarmStatus() == AlarmState.PENDING_ALARM && allSensorsInactive()) {
            setAlarmStatus(AlarmState.NO_ALARM);
        }
    }

    public void changeSensorActivationStatus(SecuritySensor sensor, Boolean active) {
        if (active) {
            sensor.setActive(true);
            securityRepos.updateSensor(sensor);
            processSensorActivation();
        } else if (sensor.getActive()) {
            sensor.setActive(false);
            securityRepos.updateSensor(sensor);
            processSensorDeactivation();
        }
    }

    public void processImage(BufferedImage currentCameraImage) {
        handleCatDetection(imageAnalyzer.imageContainsCat(currentCameraImage, 50.0f));
    }

    public AlarmState getAlarmStatus() {
        return securityRepos.getAlarmStatus();
    }

    public Set<SecuritySensor> getSensors() {
        return securityRepos.getSensors();
    }

    public void addSensor(SecuritySensor sensor) {
        securityRepos.addSensor(sensor);
    }

    public void removeSensor(SecuritySensor sensor) {
        securityRepos.removeSensor(sensor);
    }

    public ArmingState getArmingStatus() {
        return securityRepos.getArmingStatus();
    }
}
