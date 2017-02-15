package io.pivotal.customer.simulation;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.apache.log4j.Logger;
import org.coursera.metrics.datadog.DatadogReporter;
import org.coursera.metrics.datadog.transport.HttpTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
public class SimulationRunner {
  static final MetricRegistry metrics = new MetricRegistry();
  private static final Meter requests = metrics.meter("requests");
  private static final Timer writeTime = metrics.timer("gemfire.write");
  private static final Timer readTime = metrics.timer("gemfire.read");

  @Autowired
  private SimulationRepository repository;

  private static Logger log = Logger.getLogger(SimulationRunner.class.getName());

  @PostConstruct
  public void run() {
    HttpTransport transport = new HttpTransport.Builder().withApiKey("6e02dcebb0c3efa7e14a0be383a0d378").build();
    DatadogReporter dataDog = DatadogReporter.forRegistry(metrics)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .withTransport(transport)
        .build();
    dataDog.start(1, TimeUnit.SECONDS);


    SystemInfo systemInfo = new SystemInfo();
    registerDiskStats(systemInfo);
    registerMemoryStats(systemInfo);
    registerCPUStats(systemInfo);


    int count = 0;

    while (true) {
      emitGemfireStats(count);

      requests.mark();
      count++;

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void emitGemfireStats(int count) {
    Simulation sim = new Simulation("K" + count, "V" + count);
    final Timer.Context writeTimer = writeTime.time();

    try {
      repository.save(sim);
    } finally {
      writeTimer.stop();
      log.info("Put => " + sim);
    }
    requests.mark();

    String rand = String.valueOf((int) (Math.random() * (count + 1)));
    String expectedKey = "K" + rand;
    String expectedValue = "V" + rand;

    Simulation actualSim = null;
    final Timer.Context readTimer = readTime.time();
    try {
      actualSim = repository.findOne(expectedKey);
    } finally {
      readTimer.stop();
      if (actualSim != null) {
        if (actualSim.getKey().equals(expectedKey) && actualSim.getValue().equals(expectedValue)) {
          log.info("Performed a get on " + expectedKey + " successfully");
        } else {
          log.error("Performed a get on " + expectedKey + " but got a value of " + actualSim.getValue());
        }
      } else {
        log.error("Value was null for " + expectedKey);
      }
    }
  }

  public void registerDiskStats(SystemInfo systemInfo) {
    metrics.register(MetricRegistry.name("app", "disk", "usage", "size"),
        new Gauge<Long>() {
          @Override
          public Long getValue() {
            return systemInfo.getDiskUsage();
          }
        });
  }

  public void registerMemoryStats(SystemInfo systemInfo) {
    metrics.register(MetricRegistry.name("app", "memory", "usage", "size"),
        new Gauge<Long>() {
          @Override
          public Long getValue() {
            return systemInfo.getMemoryUsage();
          }
        });
  }

  public void registerCPUStats(SystemInfo systemInfo) {
    metrics.register(MetricRegistry.name("app", "cpu" ,"usage", "size"),
        new Gauge<Double>() {
          @Override
          public Double getValue() {
            return systemInfo.getCpuUsage();
          }
        });
  }
}