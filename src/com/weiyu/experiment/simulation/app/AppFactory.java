package com.weiyu.experiment.simulation.app;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Shishir Bharathi
 */
public class AppFactory {
    private static Map<String, Application> appMap;
    
    static {
        appMap = new HashMap<String, Application>();
        appMap.put("LIGO", new LIGO());
        appMap.put("GENOME", new Genome());
        appMap.put("MONTAGE", new Montage());
        appMap.put("SIPHT", new SIPHT());
        appMap.put("CYBERSHAKE", new CyberShake());
    }
    
    public static Application getApp(String appName) throws Exception {
        Application app = (Application) appMap.get(appName.toUpperCase());
        if (app == null) {
            throw new Exception("Unknown application: " + appName);
        }
        return app;
    }
}
