package io.pivotal.customer.simulation;

import org.springframework.cloud.cloudfoundry.CloudFoundryServiceInfoCreator;
import org.springframework.cloud.cloudfoundry.Tags;

import java.util.List;
import java.util.Map;

public class ServiceInfoCreator extends CloudFoundryServiceInfoCreator<CloudCacheServiceInfo> {
    public ServiceInfoCreator() {
        super(new Tags("p-cloudcache", "cloudcache"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public CloudCacheServiceInfo createServiceInfo(Map<String, Object> serviceData) {
        String id = (String) serviceData.get("name");

        Map<String, Object> credentials = getCredentials(serviceData);

        List<String> locators = (List<String>) credentials.get("locators");
        List<Map<String, String>> users = (List<Map<String, String>>) credentials.get("users");

        return new CloudCacheServiceInfo(id, locators, users);
    }

    @Override
    public boolean accept(Map<String, Object> serviceData) {
        return containsLocators(serviceData) || super.accept(serviceData);
    }

    private boolean containsLocators(Map<String, Object> serviceData){
        Object locators = getCredentials(serviceData).get("locators");
        return locators != null;
    }
}

