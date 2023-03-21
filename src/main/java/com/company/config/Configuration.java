package com.company.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

    private static final String CONFIG_FILE_PATH = "config.properties";

    public static Properties load() {
        Properties properties = new Properties();
        try {
            InputStream propsInput = Configuration.class.getClassLoader().getResourceAsStream(CONFIG_FILE_PATH);
            properties.load(propsInput);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
