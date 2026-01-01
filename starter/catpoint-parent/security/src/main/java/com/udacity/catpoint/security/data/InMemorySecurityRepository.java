package com.udacity.catpoint.security.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

public class InMemorySecurityRepository implements SecurityDataRepository {

    private Set<SecuritySensor> sensors;
    private AlarmState alarmStatus;
    private ArmingState armingStatus;

    // preference keys
    private static final String SENSORS = "SENSORS";
    private static final String ALARM_STATUS = "ALARM_STATUS";
    private static final String ARMING_STATUS = "ARMING_STATUS";

    private static final Preferences prefs = Preferences.userNodeForPackage(InMemorySecurityRepository.class);
    private static final Gson gson = new Gson(); // used to serialize objects into JSON

    public InMemorySecurityRepository() {
        // load system state from prefs, or else default
        alarmStatus = AlarmState.valueOf(prefs.get(ALARM_STATUS, AlarmState.NO_ALARM.toString()));
        armingStatus = ArmingState.valueOf(prefs.get(ARMING_STATUS, ArmingState.DISARMED.toString()));

        // load serialized sensors
        String sensorString = prefs.get(SENSORS, null);
        if (sensorString == null) {
            sensors = new TreeSet<>();
        } else {
            Type type = new TypeToken<Set<SecuritySensor>>() {}.getType();
            sensors = gson.fromJson(sensorString, type);
        }
    }

    @Override
    public void addSensor(SecuritySensor sensor) {
        sensors.add(sensor);
        prefs.put(SENSORS, gson.toJson(sensors));
    }

    @Override
    public void removeSensor(SecuritySensor sensor) {
        sensors.remove(sensor);
        prefs.put(SENSORS, gson.toJson(sensors));
    }

    @Override
    public void updateSensor(SecuritySensor sensor) {
        sensors.remove(sensor);
        sensors.add(sensor);
        prefs.put(SENSORS, gson.toJson(sensors));
    }

    @Override
    public void setAlarmStatus(AlarmState alarmStatus) {
        this.alarmStatus = alarmStatus;
        prefs.put(ALARM_STATUS, this.alarmStatus.toString());
    }

    @Override
    public void setArmingStatus(ArmingState armingStatus) {
        this.armingStatus = armingStatus;
        prefs.put(ARMING_STATUS, this.armingStatus.toString());
    }

    @Override
    public Set<SecuritySensor> getSensors() {
        return sensors;
    }

    @Override
    public AlarmState getAlarmStatus() {
        return alarmStatus;
    }

    @Override
    public ArmingState getArmingStatus() {
        return armingStatus;
    }
}
