package depindr.configuration;

import java.util.Properties;

//singletoning at it's finest
public class DepinderConfiguration {
    private static DepinderConfiguration instance = new DepinderConfiguration();
    private Properties configuration;

    private DepinderConfiguration() {}

    public static DepinderConfiguration getInstance() {
        return instance;
    }

    public static void loadProperties(Properties properties){
        if(instance.configuration == null)
            instance.configuration = properties;
    }

    public String getProperty(String property) {
        return configuration.getProperty(property);
    }
}
