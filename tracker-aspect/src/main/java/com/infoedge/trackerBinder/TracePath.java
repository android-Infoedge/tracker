package com.infoedge.trackerBinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by nishant on 20/7/16.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface TracePath {
    String TAG() default "";
    String methodName() default "";
    String className() default "";
}
