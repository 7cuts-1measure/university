package simulation.model;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
* Class for loading configuration from properties files.
* First, settings are loaded from defaultConfig.properties, located in the .jar archive,
* then they are overridden by values ​​from config.properties in the current working directory.
*/
public class Config {
    private static final String THREADS_DEALERS_STR             = "ThreadsDealers";
    private static final String THREADS_ACCESSORY_SUPPLIERS_STR = "ThreadsAccessorySuppliers";
    private static final String THREADS_WORKERS_STR             = "ThreadsWorkers";
    private static final String CAR_STORAGE_SIZE_STR            = "carStorageSize";
    private static final String ACCESSORY_STORAGE_SIZE_STR      = "accessoryStorageSize";
    private static final String MOTOR_STORAGE_SIZE_STR          = "motorStorageSize";
    private static final String BODY_STORAGE_SIZE_STR           = "bodyStorageSize";

    private static final Properties defaultProperties = new Properties();
    private static final Properties userProperties = new Properties();
    private static final Set<String> overriddenKeys = new HashSet<>();
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    static {
        loadDefaultConfig();
        loadUserConfingAndOverride();
    }

    private static void loadUserConfingAndOverride() {
        File userFile = new File("config.properties");
        if (userFile.exists() && userFile.isFile()) {
            processUserFile(userFile);
        } else {
            log.warn("User config was not found in working directory. Using default config");
        }

        for (String key : getExpectedKeys()) {
            if (!overriddenKeys.contains(key)) {
                log.warn(format(
                        "Missing parameter in user config: '%s'. Using default: %s",
                        key, defaultProperties.getProperty(key))
                    );
            }
        }
    }

    private static List<String> getExpectedKeys() {
        return List.of(
            BODY_STORAGE_SIZE_STR,
            MOTOR_STORAGE_SIZE_STR,
            ACCESSORY_STORAGE_SIZE_STR,
            CAR_STORAGE_SIZE_STR,
            THREADS_WORKERS_STR,
            THREADS_ACCESSORY_SUPPLIERS_STR,
            THREADS_DEALERS_STR
        );
    }

    private static void processUserFile(File userFile) {
        try (InputStream userInput = new FileInputStream(userFile)) {
            userProperties.load(userInput);
            userProperties.stringPropertyNames()
                .forEach(key -> overriddenKeys.add(key));
        } catch (IOException e) {
            log.error("Cannot load config.properties: " + e.getMessage());
        }
    }

    private static void loadDefaultConfig() {
        try (InputStream defaultInput = Config.class.getResourceAsStream("/defaultConfig.properties")) {
            if (defaultInput == null) {
                throw new RuntimeException("Cannot find defaultConfig.properties in classPath");
            }
            defaultProperties.load(defaultInput);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load defaultConfig.properties", e);
        }
    }

    public static int getBodyStorageSize() {
        return getIntProperty(BODY_STORAGE_SIZE_STR);
    }

    public static int getMotorStorageSize() {
        return getIntProperty(MOTOR_STORAGE_SIZE_STR);
    }

    public static int getAccessoryStorageSize() {
        return getIntProperty(ACCESSORY_STORAGE_SIZE_STR);
    }

    public static int getCarStorageSize() {
        return getIntProperty(CAR_STORAGE_SIZE_STR);
    }

    public static int getThreadsWorkers() {
        return getIntProperty(THREADS_WORKERS_STR);
    }

    public static int getThreadsAccessorySuppliers() {
        return getIntProperty(THREADS_ACCESSORY_SUPPLIERS_STR);
    }

    public static int getThreadsDealers() {
        return getIntProperty(THREADS_DEALERS_STR);
    }

    private static int getIntProperty(String key) {
        String value = userProperties.getProperty(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.warn(format("Bad format in record '%s=%s'. Expects that value is digit", key, value));
                log.warn("Loading default property for " + key);
            }
        }
        value = defaultProperties.getProperty(key);
        return Integer.parseInt(value); // do not catch runtime exception
    }
}
