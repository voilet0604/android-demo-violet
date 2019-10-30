package com.violet.objectinstancedemo

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * @Author: shalei
 * @Date: 2019-10-30 10:05
 * @Desc:
 **/
interface IDisplay {

    /**
     * 布局id 不能为null
     */
    @NonNull fun getLayoutId(): Int

    /**
     * 初始化view
     */
    fun onInitWidget()

    /**
     * 第一次初始化数据
     * 正常启动才会调用
     */
    fun onInitData()

    /**
     * app意外杀死重新初始化数据
     * 非正常启动才会调用
     */
    fun onRestData(savedInstanceState: Bundle)

}