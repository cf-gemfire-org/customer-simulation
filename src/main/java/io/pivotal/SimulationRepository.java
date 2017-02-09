package io.pivotal;

import org.springframework.data.gemfire.repository.GemfireRepository;

public interface SimulationRepository extends GemfireRepository<Simulation, String> {
}
