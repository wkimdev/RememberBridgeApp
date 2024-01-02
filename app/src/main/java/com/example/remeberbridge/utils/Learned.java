package com.example.remeberbridge.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Learned.java
 * @desc : 커스텀 주석 사용법 잠깐 컴토
 *
 * @author : wkimdev
 * @created : 2023/12/31
 * @version 1.0
 * @modifyed
 **/
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Learned {
    String value() default "";
}
