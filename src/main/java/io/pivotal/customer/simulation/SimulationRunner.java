package io.pivotal.customer.simulation;

import com.codahale.metrics.*;
import org.apache.log4j.Logger;
import org.coursera.metrics.datadog.DatadogReporter;
import org.coursera.metrics.datadog.transport.HttpTransport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
public class SimulationRunner {
  static final MetricRegistry metrics = new MetricRegistry();
  private static final Meter requests = metrics.meter("requests");
  private static final Timer writeTime = metrics.timer("gemfire.write");
  private static final Timer readTime = metrics.timer("gemfire.read");
  private final Counter cacheMisses = metrics.counter("gemfire.cache.miss");
  private final Counter corruptRead = metrics.counter("gemfire.corrupt.read");

  @Autowired
  private SimulationRepository repository;

  @Autowired
  private Environment environment;

  private static Logger log = Logger.getLogger(SimulationRunner.class.getName());

  @PostConstruct
  public void run() throws Exception {
    String datadogApiKey = environment.getProperty("DATADOG_API_KEY");

    if (datadogApiKey == null || datadogApiKey.equals("")) {
      throw new Exception("No DataDog Api key set in the environment");
    }

    HttpTransport transport = new HttpTransport.Builder().withApiKey(datadogApiKey).build();
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
        } else {
          corruptRead.inc();
        }
      } else {
        cacheMisses.inc();
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