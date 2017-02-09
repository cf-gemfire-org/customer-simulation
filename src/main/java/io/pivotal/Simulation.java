package io.pivotal;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.Region;

import java.io.Serializable;

@Region("Simulation")
public class Simulation implements Serializable {

    private static final long serialVersionUID = 42L;

    @Id
    String guid;
    String value;

    public Simulation() {

    }

    public Simulation(String guid, String value) {
        this.guid = guid;
        this.value = value;
    }

    public String getGuid() {
        return guid;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simulation)) return false;

        Simulation data = (Simulation) o;

        return guid.equals(data.getGuid());
    }

    @Override
    public int hashCode() {
        return guid.hashCode();
    }
}
