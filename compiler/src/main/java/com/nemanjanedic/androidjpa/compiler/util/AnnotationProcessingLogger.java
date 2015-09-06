/**
 *
 */
package com.nemanjanedic.androidjpa.compiler.util;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;

public class AnnotationProcessingLogger {

    private enum Level {
        TRACE, INFO, WARN, ERROR
    }

    private static Level DEFAULT_LEVEL = Level.WARN;

    private int logLevel;

    private Messager messager;

    public AnnotationProcessingLogger(final ProcessingEnvironment env, final String level) {
        messager = env.getMessager();

        if (level == null) {
            logLevel = DEFAULT_LEVEL.ordinal();
            return;
        }
        if ("trace".equalsIgnoreCase(level)) {
            logLevel = Level.TRACE.ordinal();
        } else if ("info".equalsIgnoreCase(level)) {
            logLevel = Level.INFO.ordinal();
        } else if ("warn".equalsIgnoreCase(level)) {
            logLevel = Level.WARN.ordinal();
        } else if ("error".equalsIgnoreCase(level)) {
            logLevel = Level.ERROR.ordinal();
        } else {
            logLevel = DEFAULT_LEVEL.ordinal();
        }
    }

    public void info(final String message) {
        log(Level.INFO, message, Diagnostic.Kind.NOTE);
    }

    public void trace(final String message) {
        log(Level.TRACE, message, Diagnostic.Kind.NOTE);
    }

    public void warn(final String message) {
        log(Level.WARN, message, Diagnostic.Kind.MANDATORY_WARNING);
    }

    public void error(final String message) {
        error(message, null);
    }

    public void error(final String message, final Throwable t) {
        log(Level.ERROR, message, Diagnostic.Kind.ERROR);
        if (t != null) {
            t.printStackTrace();
        }
    }

    private void log(final Level level, final String message, final Diagnostic.Kind kind) {
        if (logLevel <= level.ordinal()) {
            messager.printMessage(kind, message);
        }
    }
}
