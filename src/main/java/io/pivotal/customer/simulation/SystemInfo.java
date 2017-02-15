package io.pivotal.customer.simulation;

import javax.management.*;
import java.io.File;
import java.lang.management.ManagementFactory;

public class SystemInfo {

  public Long getDiskUsage() {
    File root = new File("/");
    return root.getTotalSpace() - root.getFreeSpace();
  }

  public Long getMemoryUsage() {
    return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
  }

  public double getCpuUsage() {
    MBeanServer mbs    = ManagementFactory.getPlatformMBeanServer();
    ObjectName name    = null;
    try {
      name = ObjectName.getInstance("java.lang:type=OperatingSystem");
    } catch (MalformedObjectNameException e) {
      e.printStackTrace();
    }
    AttributeList list = null;
    try {
      list = mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });
    } catch (InstanceNotFoundException e) {
      e.printStackTrace();
    } catch (ReflectionException e) {
      e.printStackTrace();
    }

    if (list.isEmpty())     return Double.NaN;

    Attribute att = (Attribute)list.get(0);
    Double value  = (Double)att.getValue();

    if (value == -1.0)      return Double.NaN;
    return ((int)(value * 1000) / 10.0);
  }
}
