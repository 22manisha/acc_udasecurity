package com.udacity.catpoint.security.data;

import com.google.common.collect.ComparisonChain;

import java.util.Objects;
import java.util.UUID;

public class SecuritySensor implements Comparable<SecuritySensor> {
    private UUID sensorId;
    private String name;
    private Boolean active;
    private SecuritySensorType sensorType;

    public SecuritySensor(String name, SecuritySensorType sensorType) {
        this.name = name;
        this.sensorType = sensorType;
        this.sensorId = UUID.randomUUID();
        this.active = Boolean.FALSE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecuritySensor sensor = (SecuritySensor) o;
        return sensorId.equals(sensor.sensorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public SecuritySensorType getSensorType() {
        return sensorType;
    }

    public void setSensorType(SecuritySensorType sensorType) {
        this.sensorType = sensorType;
    }

    public UUID getSensorId() {
        return sensorId;
    }

    public void setSensorId(UUID sensorId) {
        this.sensorId = sensorId;
    }

    @Override
    public int compareTo(SecuritySensor o) {
        return ComparisonChain.start()
                .compare(this.name, o.name)
                .compare(this.sensorType.toString(), o.sensorType.toString())
                .compare(this.sensorId, o.sensorId)
                .result();
    }
}
