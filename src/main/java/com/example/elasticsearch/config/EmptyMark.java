package com.example.elasticsearch.config;

import java.lang.annotation.*;

/**
 * 標記空值檢測
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface EmptyMark {
}
