package io.pivotal;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationRunner {
    public static void run() {
        ClientCache cache = gemfireCache(serviceInfo());
        Region r = cache.createClientRegionFactory(ClientRegionShortcut.PROXY)
                .setStatisticsEnabled(true)
                .create("Simulation");

        int count = 0;
        while(true) {
            r.put("ok" + count, "whyyyyy" + count);
            count++;

        }
    }

    private static ClientCache gemfireCache(ServiceInfo serviceInfo) {
        ClientCacheFactory factory = new ClientCacheFactory();
        for (URI locator : serviceInfo.getLocators()) {
            factory.addPoolLocator(locator.getHost(), locator.getPort());
        }

        return factory.create();
    }

    public static ServiceInfo serviceInfo() {
        List<String> locators = Arrays.asList("localhost[51590]");
        List<Map<String,String>> users = Arrays.asList(new HashMap<String, String>() {{
            put("username", "operator");
            put("password", "password"); }});

        ServiceInfo serviceInfo = new ServiceInfo("gemfire", locators, users);
        return serviceInfo;
    }
}