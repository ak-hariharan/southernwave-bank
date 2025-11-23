package com.southernwavebank.gateway_service.util;

public class ServicePathUtils {

	public static String getTargetServiceFromPath(String path) {
        if (path.contains("/account")) return "account-service";
        if (path.contains("/transaction")) return "transaction-service";
        if (path.contains("/user")) return "user-service";
        return null;
    }
}
