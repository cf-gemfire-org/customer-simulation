package io.pivotal.customer.simulation;

/**
 * Created by pivotal on 2017-02-09.
 */

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

import java.net.URI;


@Configuration
@EnableGemfireRepositories
public class ApplicationConfig extends AbstractCloudConfig {

    static Logger log = Logger.getLogger(ApplicationConfig.class.getName());

    private static final String SECURITY_CLIENT = "security-client-auth-init";
    private static final String SECURITY_USERNAME = "security-username";
    private static final String SECURITY_PASSWORD = "security-password";

    @Bean
    public ClientCache gemfireCache() {
        Cloud cloud = new CloudFactory().getCloud();
        CloudCacheServiceInfo cloudCacheServiceInfo = (CloudCacheServiceInfo)cloud.getServiceInfos().stream().filter(si -> si instanceof CloudCacheServiceInfo).findFirst().get();

        ClientCacheFactory factory = new ClientCacheFactory();
        for (URI locator : cloudCacheServiceInfo.getLocators()) {
            factory.addPoolLocator(locator.getHost(), locator.getPort());
        }

        factory.set(SECURITY_CLIENT, "io.pivotal.customer.simulation.UserAuthInitialize.create");
        factory.set(SECURITY_USERNAME, cloudCacheServiceInfo.getUsername());
        factory.set(SECURITY_PASSWORD, cloudCacheServiceInfo.getPassword());

        return factory.create();
    }

    @Bean
    public ClientRegionFactoryBean<String, Simulation> simulationRegion(@Autowired ClientCache gemfireCache) {
        ClientRegionFactoryBean<String, Simulation> region = new ClientRegionFactoryBean<>();
        region.setName("simulation");
        region.setCache(gemfireCache);
        region.setShortcut(ClientRegionShortcut.PROXY);
        region.setLookupEnabled(true);
        region.setClose(false);
        return region;
    }
}