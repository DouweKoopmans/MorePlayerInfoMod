package com.fallingdutchman.playerlist2;

import org.apache.logging.log4j.Logger;

/**
 * Created by Douwe Koopmans on 18-12-15.
 */
public class LogHelper {
    private static LogHelper ourInstance = new LogHelper();

    public static LogHelper getInstance() {
        return ourInstance;
    }

    private LogHelper() {}

    private static Logger modLog;

    public void setModLog(Logger modLog) {
        LogHelper.modLog = modLog;
    }

    public Logger getLogger() {
        return modLog;
    }

    public void error(Throwable thr) {
        modLog.error("", thr);
    }

    public void warn(Throwable thr) {
        modLog.warn("", thr);
    }
}
