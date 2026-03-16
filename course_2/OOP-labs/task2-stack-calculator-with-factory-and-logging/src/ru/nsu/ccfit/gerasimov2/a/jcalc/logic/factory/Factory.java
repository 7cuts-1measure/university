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
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.CalculatorException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.FactoryConfigExcpetion;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.FactoryException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.exception.UnknownCommandException;
import ru.nsu.ccfit.gerasimov2.a.jcalc.logic.cmd.Command;

public class Factory {
    static Logger LOGGER = LogUtil.getLogger(Factory.class.getSimpleName());;


    private Map<String, Class<?>> classMap = new HashMap<>();

    public Factory() throws FactoryConfigExcpetion {
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
                    classMap.put(key, getClass(className));
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load configuration");
        }
    }
    // TODO: хранить мапу <String, Class> и загружать конфиг во время инициализации

    /**
     * 
     * @param cmdName
     * @return A calculator command
     * @throws UnknownCommandException if no class in factory for {@code cmdName}
     */
    private Class<?> getClass(String className) throws FactoryConfigExcpetion {
        LOGGER.fine("Creating class: " + className);

        try {
            Class<?> cls = Class.forName(className);
            LOGGER.fine("Get associated class: " + cls.getName()); 
            if (!Command.class.isAssignableFrom(cls)) throw new FactoryConfigExcpetion("Class" + className + "does not implement Command interface");

            // now we know that clazz has type Class<? extends ommand> -> we can cast it to Command safely
            return cls; 
        } catch (ClassNotFoundException e) {
            throw new FactoryConfigExcpetion(
                    "Class " + className + " does not exist");
        } catch (ReflectiveOperationException e) {
            throw new FactoryConfigExcpetion("Unfixable problems while instantiate command class");
        }
    }

    /**
     * 
     * @param cmdName
     * @return new instance of CommandClass that matches cmdName
     */
    public Command newCommand(String cmdName) throws UnknownCommandException {
        if (cmdName == null || cmdName.isEmpty()) {
            throw new IllegalArgumentException("Command name cannot be null or empty");
        }
        // check a comment
        if (cmdName.charAt(0) == '#') {
            cmdName = "#";
        }

        Class<?> cmdClass = classMap.get(cmdName.toUpperCase());
        if (cmdClass == null) throw new UnknownCommandException(cmdName);
        try {
            return (Command) cmdClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new UnknownCommandException("Failed to create  " + cmdName);
        }
    }

    public Collection<Class<?>> getCommandClassNames() {
        return classMap.values();
    }

    public String getKeywordForClass(Class<?> cls) {
        for (Entry<String, Class<?>> entry : classMap.entrySet()) {
            if (entry.getValue() == cls) return entry.getKey(); 
        }
        throw new IllegalArgumentException("Class " + cls + " not in the factory");
    }
}
