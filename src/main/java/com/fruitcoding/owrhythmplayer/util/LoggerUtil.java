package com.fruitcoding.owrhythmplayer.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {
    private static final Logger logger = LogManager.getLogger();

    public static void trace(Object message) {
        logger.trace(message);
    }

    public static void debug(Object message) {
        logger.debug(message);
    }

    public static void info(Object message) {
        logger.info(message);
    }

    public static void warn(Object message) {
        logger.warn(message);
    }

    public static void error(Object message) {
        logger.error(message);
    }

    public static void fatal(Object message) {
        logger.fatal(message);
    }
}
