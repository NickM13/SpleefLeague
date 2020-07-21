/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.spleefleague.coreapi.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author NickM13
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@SuppressWarnings("final")
public @interface DBField {
    
    String fieldName() default "";
    
    Class<?> serializer() default Object.class;
    
}
