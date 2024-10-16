package by.polikarpov.util;

import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for loading and accessing properties from a properties file.
 * This class loads the properties defined in "application.properties"
 * and provides a method to retrieve property values by key.
 */
public class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();

    // Static block to load properties when the class is loaded
    static {
        loadProperties();
    }

    /**
     * Loads properties from the "application.properties" file into the
     * PROPERTIES object. This method is called during the static initialization
     * of the class.
     */
    private static void loadProperties() {
        try (var inputStream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the value associated with the specified key from the loaded properties.
     *
     * @param key the key whose associated value is to be returned
     * @return the value associated with the specified key, or null if the key does not exist
     */
    public static String get(String key) { return PROPERTIES.getProperty(key); }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private PropertiesUtil() {}
}
