package io.pivotal.customer.simulation;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;

import java.io.Serializable;

@Region("simulation")
public class Simulation implements Serializable {

    private static final long serialVersionUID = 42L;

    @Id
    String key;
    String value;

    public Simulation() {}

    public Simulation(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simulation)) return false;

        Simulation data = (Simulation) o;

        return key.equals(data.getKey()) && value.equals(data.getValue());
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    public String toString() {
        return "{K:" + key + ", V:" + value + "}";
    }
}
