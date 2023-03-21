package com.company.commands;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandLoader {
    public static Map<String, Command> loadCommands() {
        Map<String, Command> commands = new HashMap<>();

        // Load all command classes in the plugins directory
        File pluginsDir = new File(System.getProperty("user.dir") + "\\src\\main\\java\\com\\company\\plugins");
        File[] pluginFiles = pluginsDir.listFiles();
        if (pluginFiles != null) {
            for (File pluginFile : pluginFiles) {
                try {
                    String className = "com.company.plugins." + pluginFile.getName().replace(".java", "");
                    Class<?> commandClass = Class.forName(className);
                    Constructor<?> constructor = commandClass.getConstructor();
                    Command command = (Command) constructor.newInstance();
                    commands.put(command.getCommandName(), command);
                } catch (ClassNotFoundException | NoSuchMethodException |
                        InstantiationException | IllegalAccessException |
                        InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return commands;
    }
}