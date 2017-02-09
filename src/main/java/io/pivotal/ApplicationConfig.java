package io.pivotal;

/**
 * Created by pivotal on 2017-02-09.
 */

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableGemfireRepositories
public class ApplicationConfig /*extends AbstractCloudConfig */{

    static Logger log = Logger.getLogger(ApplicationConfig.class.getName());

    private static final String SECURITY_CLIENT = "security-client-auth-init";
    private static final String SECURITY_USERNAME = "security-username";
    private static final String SECURITY_PASSWORD = "security-password";

//    @Bean
//    public ClientCache gemfireCache() {
//        Cloud cloud = new CloudFactory().getCloud();
//        ServiceInfo serviceInfo = (ServiceInfo) cloud.getServiceInfos().get(0);
//
//        ClientCacheFactory factory = new ClientCacheFactory();
//        for (URI locator : serviceInfo.getLocators()) {
//            factory.addPoolLocator(locator.getHost(), locator.getPort());
//        }
//
//        factory.set(SECURITY_CLIENT, "io.pivotal.UserAuthInitialize.create");
//        factory.set(SECURITY_USERNAME, serviceInfo.getUsername());
//        factory.set(SECURITY_PASSWORD, serviceInfo.getPassword());
//
//        return factory.create();
//    }

    @Bean
    public ServiceInfo serviceInfo() {
        final String operatorPass = "pfqrSBEjjgjmU1RuT3oevA";
        List<String> locators = Arrays.asList("localhost[51590]");
        List<Map<String,String>> users = Arrays.asList(new HashMap<String, String>() {{
            put("username", "operator");
            put("password", operatorPass); }});

        ServiceInfo serviceInfo = new ServiceInfo("gemfire", locators, users);
        return serviceInfo;
    }

    @Bean
    public ClientCache gemfireCache(@Autowired ServiceInfo serviceInfo) {
//        Cloud cloud = new CloudFactory().getCloud();

        ClientCacheFactory factory = new ClientCacheFactory();
        for (URI locator : serviceInfo.getLocators()) {
            factory.addPoolLocator(locator.getHost(), locator.getPort());
        }

//        factory.set(SECURITY_CLIENT, "io.pivotal.UserAuthInitialize.create");
//        factory.set(SECURITY_USERNAME, serviceInfo.getUsername());
//        factory.set(SECURITY_PASSWORD, serviceInfo.getPassword());

        return factory.create();
    }

    @Bean
    public ClientRegionFactoryBean<String, Simulation> simulationRegion(@Autowired ClientCache gemfireCache) {
        ClientRegionFactoryBean<String, Simulation> region = new ClientRegionFactoryBean<>();
        region.setName("Simulation");
        region.setCache(gemfireCache);
        region.setShortcut(ClientRegionShortcut.PROXY);
        region.setLookupEnabled(true);
        region.setClose(false);
        return region;
    }
}