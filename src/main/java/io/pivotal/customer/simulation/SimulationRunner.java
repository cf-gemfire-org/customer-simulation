package io.pivotal.customer.simulation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class SimulationRunner {
    @Autowired
    private SimulationRepository repository;

    private static Logger log = Logger.getLogger(SimulationRunner.class.getName());

    @PostConstruct
    public void run() {
        int count = 0;

        while(true) {
            try {

                Simulation sim = new Simulation("K" + count,"V" + count);
                repository.save(sim);

                log.info("Put => " + sim);


                String rand = String.valueOf( (int) (Math.random() * (count + 1)) );
                String expectedKey = "K" + rand;
                String expectedValue = "V" + rand;

                Simulation actualSim = repository.findOne(expectedKey);

                if (actualSim != null) {
                    if (actualSim.getKey().equals(expectedKey) && actualSim.getValue().equals(expectedValue)) {
                        log.info("Performed a get on " + expectedKey + " successfully" );
                    } else {
                        log.error("Performed a get on " + expectedKey + " but got a value of " + actualSim.getValue());
                    }
                } else {
                    log.error("Value was null for " + expectedKey);
                }

                count++;
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
   }

}