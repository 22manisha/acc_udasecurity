package com.udacity.catpoint.security.data;

import java.util.Set;

public interface SecurityDataRepository {
    void addSensor(SecuritySensor sensor);
    void removeSensor(SecuritySensor sensor);
    void updateSensor(SecuritySensor sensor);
    void setAlarmStatus(AlarmState alarmStatus);
    void setArmingStatus(ArmingState armingStatus);
    Set<SecuritySensor> getSensors();
    AlarmState getAlarmStatus();
    ArmingState getArmingStatus();
}
