package com.violet.objectinstancedemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.violet.library.tools.ObjectInstanceManager

/**
 * @Author: shalei
 * @Date: 2019-10-30 10:07
 * @Desc: activity 基类
 **/
abstract class BaseAct: AppCompatActivity(), IDisplay {

    private var mOIM: ObjectInstanceManager = ObjectInstanceManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        onInitWidget()
        if(savedInstanceState == null) {
            onInitData()
        } else {
            onRestData(savedInstanceState)
        }
    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        mOIM.saveField(savedInstanceState, this)
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mOIM.saveField(outState, this)
    }

    override fun onRestData(savedInstanceState: Bundle) {
        mOIM.againFragmentInstance(supportFragmentManager, this)
        mOIM.againFieldInstance(savedInstanceState, this)
    }
}