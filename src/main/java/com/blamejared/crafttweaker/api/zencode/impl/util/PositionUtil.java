package com.blamejared.crafttweaker.api.zencode.impl.util;

import com.google.common.annotations.VisibleForTesting;
import org.openzen.zencode.shared.CodePosition;
import org.openzen.zencode.shared.VirtualSourceFile;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class PositionUtil {
    
    /**
     * Reads the current script position based on the current Thread's stacktrace
     * Works by grabbing the first zs file from the stacktrace.
     * <p>
     * The Position's file will always be a {@link VirtualSourceFile} so you cannot access the script content!
     *
     * @return The position, or {@link CodePosition#UNKNOWN}
     */
    public static CodePosition getZCScriptPositionFromStackTrace() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return getZCScriptPositionFromStackTrace(stackTrace);
    }
    
    /**
     * Reads the current script position based on the provided stacktrace elements
     * Works by grabbing the first zs file from the stacktrace.
     * <p>
     * The Position's file will always be a {@link VirtualSourceFile} so you cannot access the script content!
     *
     * @return The position, or {@link CodePosition#UNKNOWN}
     */
    @Nonnull
    public static CodePosition getZCScriptPositionFromStackTrace(StackTraceElement[] stackTrace) {
        
        final StackTraceElement stackTraceElement = Arrays.stream(stackTrace)
                .filter(element -> element.getFileName() != null)
                .filter(element -> element.getFileName().endsWith(".zs"))
                .findAny()
                .orElse(null);
        
        if(stackTraceElement == null) {
            return CodePosition.UNKNOWN;
        }
        
        final String fileName = stackTraceElement.getFileName();
        final int lineNumber = stackTraceElement.getLineNumber();
        final VirtualSourceFile virtualSourceFile = new VirtualSourceFile(fileName);
        return new CodePosition(virtualSourceFile, lineNumber, 0, lineNumber, 0);
    }
    
}
