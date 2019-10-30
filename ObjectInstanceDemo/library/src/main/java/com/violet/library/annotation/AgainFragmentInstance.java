package com.violet.library.annotation;

import androidx.annotation.Keep;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: shalei
 * @Date: 2019-10-29 11:39
 * @Desc: fragment 作为field 实例化对象，
 * 在app意外杀死 activity 或者fragment 中field 在重新实例化
 **/
@Keep
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AgainFragmentInstance {
    /**
     * 如果有多个相同类型必须使用不同的tag区分。
     * @return tag对应的就是fragment add的时候的tag
     */
    String tag() default "";
}
