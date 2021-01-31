/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.core.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author NickM13
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CommandAnnotation {
    boolean hidden() default false;
    boolean disabled() default false;
    boolean confirmation() default false;
    String minRank() default "DEFAULT";
    String additionalRanks() default "";
    String description() default "";
}
