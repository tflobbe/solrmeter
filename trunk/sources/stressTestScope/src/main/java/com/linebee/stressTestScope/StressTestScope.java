package com.linebee.stressTestScope;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.ScopeAnnotation;
/**
 * 
 * @author tflobbe
 *
 */
@Target({ TYPE, METHOD }) @Retention(RUNTIME) @ScopeAnnotation
public @interface StressTestScope {}