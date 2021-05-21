package com.blamejared.crafttweaker.api.logger;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.zencode.PreprocessorMatch;
import com.blamejared.crafttweaker.api.zencode.impl.SourceFilePreprocessed;
import com.blamejared.crafttweaker.api.zencode.impl.preprocessors.PriorityPreprocessor;
import com.blamejared.crafttweaker.impl.logger.GroupLogger;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import org.openzen.zencode.java.logger.*;
import org.openzen.zencode.shared.*;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zenscript.validator.*;

import java.io.*;

/**
 * Base class used to interface with the crafttweaker.log file and other loggers (such as the player logger).
 * @docParam this logger
 */
@ZenRegister
@ZenCodeType.Name("crafttweaker.api.ILogger")
@Document("vanilla/api/logger/ILogger")
public interface ILogger extends ScriptingEngineLogger {
    
    /**
     * <p>
     * Sets the last level that will be logged<br>
     * E.g. a logLevel of INFO means that everything below
     * it (in this case{@code [DEBUG]}) will <strong>not</strong> be logged
     * </p>
     * <p>
     * In case of using SubLoggers (e.g. {@link GroupLogger}) propagates the
     * method call to <strong>all</strong> SubLoggers
     * </p>
     */
    void setLogLevel(LogLevel logLevel);
    
    /**
     * <p>
     * Returns the last level that will be logged<br>
     * E.g. a logLevel of INFO means that everything below
     * it (in this case{@code [DEBUG]}) will <strong>not</strong> be logged
     * </p>
     * <p>
     * In case of using SubLoggers (e.g. {@link GroupLogger}) returns the last level
     * at which <strong>anything</strong> will be logged
     * </p>
     */
    LogLevel getLogLevel();
    
    void log(LogLevel level, String message, boolean prefix);
    
    default void log(LogLevel level, String message) {
        log(level, message, true);
    }
    
    /**
     * Logs an info message.
     *
     * @param message message to be logged.
     * @docParam message "message"
     */
    @Override
    @ZenCodeType.Method
    default void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    /**
     * Logs a debug message.
     *
     * @param message message to be logged.
     * @docParam message "message"
     */
    @Override
    @ZenCodeType.Method
    default void debug(String message) {
        log(LogLevel.DEBUG, message);
    }
    
    /**
     * Logs a warning message.
     *
     * @param message message to be logged.
     * @docParam message "message"
     */
    @Override
    @ZenCodeType.Method
    default void warning(String message) {
        log(LogLevel.WARNING, message);
    }
    
    /**
     * Logs an error message.
     *
     * @param message message to be logged.
     * @docParam message "message"
     */
    @Override
    @ZenCodeType.Method
    default void error(String message) {
        log(LogLevel.ERROR, message);
    }
    
    /**
     * Logs a trace message.
     *
     * @param message message to be logged
     * @docParam message "message"
     */
    @Override
    @ZenCodeType.Method
    default void trace(String message) {
        log(LogLevel.TRACE, message);
    }
    
    @Override
    default void throwingErr(String message, Throwable throwable) {
        throwing(LogLevel.ERROR, message, throwable);
    }
    
    @Override
    default void throwingWarn(String message, Throwable throwable) {
        throwing(LogLevel.WARNING, message, throwable);
    }
    
    default void throwing(LogLevel logLevel, String message, Throwable throwable) {
        log(logLevel, message);
        final StringPrintStream errorStream = new StringPrintStream();
        throwable.printStackTrace(errorStream);
        log(logLevel, errorStream.getValue(), false);
    }
    
    class StringPrintStream extends PrintStream {
        
        public StringPrintStream() {
            super(new ByteArrayOutputStream());
        }
        
        public String getValue() {
            return out.toString();
        }
    }
    
    
    @Override
    default void logCompileException(CompileException exception) {
        error(exception.getMessage());
    }
    
    @Override
    default void logSourceFile(SourceFile file) {
        if(file instanceof SourceFilePreprocessed) {
            final SourceFilePreprocessed sourceFilePreprocessed = (SourceFilePreprocessed) file;
            final int priority = Integer.parseInt(sourceFilePreprocessed.getMatches()
                    .get(PriorityPreprocessor.INSTANCE)
                    .get(0)
                    .getContent());
            
            info(String.format("Loading file: '%s' with priority: %s", file.getFilename(), priority));
        } else {
            info("Loading file: " + file.getFilename());
        }
    }
    
    @Override
    default void logValidationError(ValidationLogEntry errorEntry) {
        error(errorEntry.position + " " + errorEntry.message);
    }
    
    @Override
    default void logValidationWarning(ValidationLogEntry warningEntry) {
        error(warningEntry.position + " " + warningEntry.message);
    }
}
