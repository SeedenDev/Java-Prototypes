package fr.seeden.core;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class AppLogger {

    private final Logger logger;

    private boolean enabled = false;

    public AppLogger(String appName){
        logger = Logger.getLogger(appName);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.OFF);
    }

    /**
     * Log a message
     * @param str A message or a format string
     * @param args Optional arguments to be used with a format string
     */
    public void info(String str, Object... args){
        if(!enabled) return;
        logger.log(Level.INFO, formatLog(str, args));
    }

    /**
     * Log a debug message
     * @param str A message or a format string
     * @param args Optional arguments to be used with a format string
     */
    public void debug(String str, Object... args){
        if(!enabled) return;
        logger.log(Level.FINE, formatLog(str, args));
    }

    /**
     * Log a warning
     * @param str A message or a format string
     * @param args Optional arguments to be used with a format string
     */
    public void warn(String str, Object... args){
        if(!enabled) return;
        logger.log(Level.WARNING, formatLog(str, args));
    }

    /**
     * Log an error
     * @param str A message or a format string
     * @param args Optional arguments to be used with a format string
     */
    public void error(String str, Object... args){
        if(!enabled) return;
        logger.log(Level.SEVERE, formatLog(str, args));
    }

    private String formatLog(String str, Object... args){
        return args.length>0 ? String.format(str, args) : str;
    }

    public void enable(boolean debugLevel){
        enabled = true;
        if(debugLevel) logger.setLevel(Level.ALL);
        else logger.setLevel(Level.INFO);
    }
    public void enable(){
        enable(false);
    }
    public void disable(){
        enabled = false;
        logger.setLevel(Level.OFF);
    }
    public boolean isEnabled() {
        return enabled;
    }
}