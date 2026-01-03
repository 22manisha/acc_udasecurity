package com.udacity.catpoint.security.service;

import com.udacity.catpoint.image.service.ImageService;
import com.udacity.catpoint.security.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecuritySystemServiceTest {

    private SecuritySystemService serviceUnderTest;
    private SecuritySensor testSensor;

    @Mock
    private SecurityDataRepository repository;

    @Mock
    private ImageService imageService;

    @BeforeEach
    void setup() {
        testSensor = createSensor();
        serviceUnderTest = new SecuritySystemService(repository, imageService);
    }

    private SecuritySensor createSensor() {
        return new SecuritySensor(UUID.randomUUID().toString(), SecuritySensorType.DOOR);
    }

    private Set<SecuritySensor> createSensors(int count, boolean active) {
        Set<SecuritySensor> sensors = new HashSet<>();
        for (int i = 0; i < count; i++) {
            SecuritySensor s = new SecuritySensor(UUID.randomUUID().toString(), SecuritySensorType.DOOR);
            s.setActive(active);
            sensors.add(s);
        }
        return sensors;
    }

    // Requirement 1
    @ParameterizedTest
    @EnumSource(value = ArmingState.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void armedSystem_sensorActivated_setsPendingAlarm(ArmingState state) {
        when(repository.getArmingStatus()).thenReturn(state);
        when(repository.getAlarmStatus()).thenReturn(AlarmState.NO_ALARM);

        serviceUnderTest.changeSensorActivationStatus(testSensor, true);

        verify(repository).setAlarmStatus(AlarmState.PENDING_ALARM);
    }

    // Requirement 2
    @ParameterizedTest
    @EnumSource(value = ArmingState.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void pendingAlarm_sensorActivated_setsAlarm(ArmingState state) {
        when(repository.getArmingStatus()).thenReturn(state);
        when(repository.getAlarmStatus()).thenReturn(AlarmState.PENDING_ALARM);

        serviceUnderTest.changeSensorActivationStatus(testSensor, true);

        verify(repository).setAlarmStatus(AlarmState.ALARM);
    }

    // Requirement 3
    @Test
    void pendingAlarm_allSensorsInactive_setsNoAlarm() {
        Set<SecuritySensor> sensors = createSensors(4, false);
        SecuritySensor activeSensor = sensors.iterator().next();
        activeSensor.setActive(true);

        when(repository.getSensors()).thenReturn(sensors);
        when(repository.getAlarmStatus()).thenReturn(AlarmState.PENDING_ALARM);

        serviceUnderTest.changeSensorActivationStatus(activeSensor, false);

        verify(repository).setAlarmStatus(AlarmState.NO_ALARM);
    }

    // Requirement 4
    @Test
    void alarmState_sensorChanges_doNotAffectAlarm() {
        when(repository.getAlarmStatus()).thenReturn(AlarmState.ALARM);

        testSensor.setActive(false);
        serviceUnderTest.changeSensorActivationStatus(testSensor, true);

        testSensor.setActive(true);
        serviceUnderTest.changeSensorActivationStatus(testSensor, false);

        verify(repository, never()).setAlarmStatus(any());
    }

    // Requirement 5
    @Test
    void pendingAlarm_activatingAlreadyActiveSensor_setsAlarm() {
        testSensor.setActive(true);
        when(repository.getAlarmStatus()).thenReturn(AlarmState.PENDING_ALARM);

        serviceUnderTest.changeSensorActivationStatus(testSensor, true);

        verify(repository).setAlarmStatus(AlarmState.ALARM);
    }

    // Requirement 6
    @Test
    void deactivatingInactiveSensor_noAlarmChange() {
        testSensor.setActive(false);

        serviceUnderTest.changeSensorActivationStatus(testSensor, false);

        verify(repository, never()).setAlarmStatus(any());
    }

    // Requirement 7
    @Test
    void armedHome_catDetected_setsAlarm() {
        when(repository.getArmingStatus()).thenReturn(ArmingState.ARMED_HOME);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        serviceUnderTest.processImage(mock(BufferedImage.class));

        verify(repository).setAlarmStatus(AlarmState.ALARM);
    }

    // Requirement 8
    @Test
    void noCatAndNoActiveSensors_setsNoAlarm() {
        when(repository.getSensors()).thenReturn(createSensors(3, false));
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);

        serviceUnderTest.processImage(mock(BufferedImage.class));

        verify(repository).setAlarmStatus(AlarmState.NO_ALARM);
    }

    // Requirement 9
    @Test
    void disarmingSystem_setsNoAlarm() {
        serviceUnderTest.armedStatus(ArmingState.DISARMED);

        verify(repository).setAlarmStatus(AlarmState.NO_ALARM);
    }

    // Requirement 10
    @ParameterizedTest
    @EnumSource(value = ArmingState.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void armingSystem_resetsSensors(ArmingState state) {
        Set<SecuritySensor> sensors = createSensors(4, true);

        when(repository.getSensors()).thenReturn(sensors);
        when(repository.getAlarmStatus()).thenReturn(AlarmState.PENDING_ALARM);

        serviceUnderTest.armedStatus(state);

        sensors.forEach(sensor -> assertFalse(sensor.getActive()));
    }

    // Requirement 11
    @Test
    void catDetectedBeforeArmingHome_setsAlarm() {
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);

        serviceUnderTest.processImage(mock(BufferedImage.class));
        serviceUnderTest.armedStatus(ArmingState.ARMED_HOME);

        verify(repository).setAlarmStatus(AlarmState.ALARM);
    }
}
