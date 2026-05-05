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

import slf4jansi.AnsiLogger;


/**
* Class for loading configuration from properties files.
* First, settings are loaded from defaultConfig.properties, located in the .jar archive,
* then they are overridden by values ​​from config.properties in the current working directory.
*/
public class Config {
    private static final String LOG_SALE                  = "LogSale";
    private static final String NUM_DEALERS               = "NumDealers";
    private static final String NUM_ACCESORY_SUPPLIERS    = "NumAccessorySuppliers";
    private static final String NUM_WORKERS               = "NumWorkers";
    private static final String CAR_STORAGE_CAP           = "CarStorageSize";
    private static final String ACC_STORAGE_CAP           = "AccessoryStorageSize";
    private static final String MOTOR_STORAVE_CAP         = "MotorStorageSize";
    private static final String BODY_STORAGE_CAP          = "BodyStorageSize";
    private static final String CRITICAL_CAR_STORAGE_SIZE = "CriticalCarStorageSize";
    private static final String NUM_REQUESTS              = "NumRequests";
    private static final String LOG_FILE_NAME             = "LogFileName";

    private static final Properties defaultProperties = new Properties();
    private static final Properties userProperties = new Properties();
    private static final Set<String> overriddenKeys = new HashSet<>();
    private static final Logger log = AnsiLogger.of(LoggerFactory.getLogger(Config.class));

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
            BODY_STORAGE_CAP,
            MOTOR_STORAVE_CAP,
            ACC_STORAGE_CAP,
            CAR_STORAGE_CAP,
            NUM_WORKERS,
            NUM_ACCESORY_SUPPLIERS,
            NUM_DEALERS
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

    public static int getBodyStorageCap() {
        return getIntProperty(BODY_STORAGE_CAP);
    }

    public static int getMotorStorageCap() {
        return getIntProperty(MOTOR_STORAVE_CAP);
    }

    public static int getAccessoryStorageCap() {
        return getIntProperty(ACC_STORAGE_CAP);
    }

    public static int getCarStorageCap() {
        return getIntProperty(CAR_STORAGE_CAP);
    }

    public static int getThreadsWorkers() {
        return getIntProperty(NUM_WORKERS);
    }

    public static int getThreadsAccessorySuppliers() {
        return getIntProperty(NUM_ACCESORY_SUPPLIERS);
    }

    public static int getThreadsDealers() {
        return getIntProperty(NUM_DEALERS);
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

    private static String getStringProperty(String key) {
        String value = userProperties.getProperty(key);
        if (value != null) {
            return value;
        }
        value = defaultProperties.getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Wrong default config. It should have '" + key + "' property");
        }
        return value;
        
    }

    public static boolean logSale() {
        return getIntProperty(LOG_SALE) == 1;
    }

    public static int getCriticalCarStorageSize() {
        return getIntProperty(CRITICAL_CAR_STORAGE_SIZE);
    }

    public static int numRequests() {
        return getIntProperty(NUM_REQUESTS);
    }

    public static String logFileName() {
        return getStringProperty(LOG_FILE_NAME);
    }
}
