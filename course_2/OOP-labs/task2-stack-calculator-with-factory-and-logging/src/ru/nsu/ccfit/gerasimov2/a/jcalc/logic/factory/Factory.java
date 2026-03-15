package ru.nsu.ccfit.gerasimov2.a.jcalc.logic.factory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import ru.nsu.ccfit.gerasimov2.a.jcalc.LogUtil;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.UnknownCommandException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.Command;

public class Factory {
    static Logger LOGGER = LogUtil.getLogger(Factory.class.getSimpleName());;


    private Map<String, String> classMap = new HashMap<>();

    public Factory() {
        try (InputStream is = Factory.class.getResourceAsStream("CommandList.properties")) {
            if (is == null) {
                throw new IllegalStateException("Configuration file 'CommandList.properties' was not founded in pacgake " + Factory.class.getPackageName());
            }
            LOGGER.info("Load factory properties");
            Properties props = new Properties();
            props.load(is);

            for (String key : props.stringPropertyNames()) {
                String className = props.getProperty(key);
                if (!key.isEmpty() && !className.isEmpty()) {
                    classMap.put(key, className);
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load configuration");
        }
    }

    /**
     * 
     * @param cmdName
     * @return A calculator command
     * @throws UnknownCommandException if no class in factory for {@code cmdName}
     */
    private Command tryCreateCommand(String cmdName) throws UnknownCommandException {
        LOGGER.fine("Creating command: " + cmdName);
        String className = classMap.get(cmdName.toUpperCase());

        if (className == null) throw new UnknownCommandException(cmdName);
        try {
            Class<?> cls = Class.forName(className);
            LOGGER.fine("Get associated class: " + cls.getName());
            
            if (!Command.class.isAssignableFrom(cls)) throw new IllegalStateException("Class" + className + "does not implement Command interface");

            // now we know that clazz has type Class<? extends ommand> -> we can cast it to Command safely
            return (Command) cls.getDeclaredConstructor().newInstance(); 
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Comand '" + cmdName + "' is in config, but class " + className + " does not exist");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unfixable problems while instantiate command class");
        }
    }

    /**
     * 
     * @param cmdName
     * @return new instance of CommandClass that matches cmdName
     * @throws IllegalArgumentException if cannot create class for cmdName
     * 
     */
    public Command newCommand(String cmdName) throws IllegalArgumentException {
        if (cmdName == null || cmdName.isEmpty()) {
            throw new IllegalArgumentException("Command name cannot be null or empty");
        }
        // check a comment
        if (cmdName.charAt(0) == '#') {
            cmdName = "#";
        }
        return tryCreateCommand(cmdName);
    }

    public Collection<String> getCommandClassNames() {
        return classMap.values();
    }

    public String getKeywordForClassName(String className) {
        for (Entry<String, String> entry : classMap.entrySet()) {
            if (entry.getValue() == className) return entry.getKey(); 
        }
        throw new IllegalArgumentException("Class " + className + " not in the factory");
    }
}
