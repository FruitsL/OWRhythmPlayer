package com.fruitcoding.owrhythmplayer.util;

import org.junit.jupiter.api.Test;

import static com.fruitcoding.owrhythmplayer.util.LoggerUtil.*;

class LoggerUtilTest {
    @Test
    public void logTest() {
        // Trace < Debug < Info < Warn < Error < Fatal
        trace("Trace Log");
        debug("Debug Log");
        info("Info Log");
        warn("Warning Log");
        error("Error Log");
        fatal("Fatal Log");
    }
}