package com.violet.library.annotation;

import androidx.annotation.Keep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: shalei
 * @Date: 2019-10-29 11:39
 * @Desc: 重新实例下对象，在app意外杀死 activity 或者fragment 中field 在重新实例化
 **/
@Keep
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgainInstance {
    /**
     * 通过onSaveInstanceState保存
     * 不指定key的情况使用字段名作为key
     * @return key作为 Bundle的key
     */
    String key() default "";
}
