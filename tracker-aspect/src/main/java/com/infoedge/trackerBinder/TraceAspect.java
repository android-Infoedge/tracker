package com.infoedge.trackerBinder;

import android.content.res.Resources;
import android.os.Build;
import android.os.Looper;
import android.os.Trace;
import android.util.Log;
import android.view.View;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Created by nishant on 20/7/16.
 */

@Aspect
public class TraceAspect {

    private static volatile boolean trackingEnabled = true;
    private static volatile boolean loggingEnabled = true;

    @Pointcut("within(@com.infoedge.tracker.TracePath *)")
    public void withinAnnotatedClass() {
    }

    @Pointcut("execution(!synthetic * *(..)) && withinAnnotatedClass()")
    public void methodInsideAnnotatedType() {
    }

    @Pointcut("execution(!synthetic *.new(..)) && withinAnnotatedClass()")
    public void constructorInsideAnnotatedType() {
    }

    @Pointcut("execution(@com.infoedge.tracker.TracePath * *(..)) || methodInsideAnnotatedType()")
    public void method() {
    }

    @Pointcut("execution(@com.infoedge.tracker.TracePath *.new(..)) || constructorInsideAnnotatedType()")
    public void constructor() {
    }


    public static void enableTracking(boolean enabled) {
        trackingEnabled = enabled;
    }

    public static void enableLogging(boolean enable) {
        loggingEnabled = enable;
    }

    @Around("method() || constructor()")
    public Object weaveJointPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        enterMethod(joinPoint);

        long startNanos = System.nanoTime();
        Object result = joinPoint.proceed();
        long stopNanos = System.nanoTime();
        long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);

        exitMethod(joinPoint, result, lengthMillis);

        return result;
    }

    private static void enterMethod(ProceedingJoinPoint joinPoint) {
        if (!trackingEnabled && !loggingEnabled) return;

        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();

        Class<?> cls = codeSignature.getDeclaringType();
        String methodName = codeSignature.getName();
        String[] parameterNames = codeSignature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        String tag = "";
        if (codeSignature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) codeSignature;
            Method m = methodSignature.getMethod();
            TracePath tracePath = m.getAnnotation(TracePath.class);
            if (tracePath != null) {
                tag = tracePath.TAG();
            }
        }

        StringBuilder builder = new StringBuilder(tag);
        builder.append(" \u21E2 ")
                .append("[")
                .append(asTag(cls))
                .append("] ")
                .append(methodName)
                .append('(');

        for (int i = 0; i < parameterValues.length; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(parameterNames[i]).append('=');
            if (/*parameterTypes[i] == View.class*/parameterValues[i] instanceof View) {
                View view = (View) parameterValues[i];
                Resources resources = view.getContext().getResources();
                int id = view.getId();
                String typename = resources.getResourceTypeName(id);
                String entryname = resources.getResourceEntryName(id);
                builder.append(" ");
                builder.append(typename);
                builder.append("/");
                builder.append(entryname);
                builder.append(" ");
            } else {
                builder.append(Strings.toString(parameterValues[i]));
            }
        }
        builder.append(')');

        if (Looper.myLooper() != Looper.getMainLooper()) {
            builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
        }

        if (loggingEnabled) {
            Log.v(asTag(cls), builder.toString());
        }
        if (trackingEnabled) {
            CapturedEventsContainer.getInstance().addEvent(builder.toString());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final String section = builder.toString().substring(2);
            Trace.beginSection(section);
        }
    }

    private static void exitMethod(JoinPoint joinPoint, Object result, long lengthMillis) {
        if (!loggingEnabled) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Trace.endSection();
        }

        Signature signature = joinPoint.getSignature();

        Class<?> cls = signature.getDeclaringType();
        String methodName = signature.getName();
        boolean hasReturnType = signature instanceof MethodSignature
                && ((MethodSignature) signature).getReturnType() != void.class;

        StringBuilder builder = new StringBuilder("\u21E0 ")
                .append(methodName)
                .append(" [")
                .append(lengthMillis)
                .append("ms]");

        if (hasReturnType) {
            builder.append(" = ");
            builder.append(Strings.toString(result));
        }

        if (loggingEnabled) {
            Log.v(asTag(cls), builder.toString());
        }
    }

    private static String asTag(Class<?> cls) {
        if (cls.isAnonymousClass()) {
            return asTag(cls.getEnclosingClass());
        }
        return cls.getSimpleName();
    }

}