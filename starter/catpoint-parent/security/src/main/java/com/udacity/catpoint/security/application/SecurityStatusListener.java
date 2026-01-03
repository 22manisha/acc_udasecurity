package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmState;

public interface SecurityStatusListener {
    void notify(AlarmState status);
    void catDetected(boolean catDetected);
    void sensorStatusChanged();
}
