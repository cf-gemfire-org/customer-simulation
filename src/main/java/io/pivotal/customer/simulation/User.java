package io.pivotal.customer.simulation;

import java.util.Map;

public class User {
    private String username;
    private String password;

    public User(Map<String, String> map) {
        username = map.get("username");
        password = map.get("password");
    }

    public boolean isOperator() {
        return username != null && username.equals("cluster_operator");
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
