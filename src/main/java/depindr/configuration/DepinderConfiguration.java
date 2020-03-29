package depindr.configuration;

import depindr.exceptions.DepinderException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

//singletoning at it's finest
public class DepinderConfiguration {
    private static DepinderConfiguration instance = new DepinderConfiguration();
    private Properties properties = new Properties();

    private DepinderConfiguration() {}

    public static DepinderConfiguration getInstance() {
        return instance;
    }

    public void loadProperties(File configurationFile) {
        try {
            properties.load(new FileReader(configurationFile));
        } catch (IOException e) {
            throw new DepinderException("Could not read property file " + configurationFile, e);
        }

    }

    public String getProperty(String property) {
        return properties.getProperty(property);
    }

    //for test purposes
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }
}
