package com.violet.objectinstancedemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.violet.library.tools.ObjectInstanceManager

/**
 * @Author: shalei
 * @Date: 2019-10-30 10:20
 * @Desc: fragment基类
 **/
abstract class BaseStub : Fragment(), IDisplay {

    private val mObjectInstanceManager = ObjectInstanceManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(getLayoutId(), container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitWidget()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState == null) {
            onInitData()
        } else {
            onRestData(savedInstanceState)
        }
    }

//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        if(savedInstanceState != null) {
//            mObjectInstanceManager.saveField(savedInstanceState, this)
//        }
//    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mObjectInstanceManager.saveField(outState, this)
    }

    override fun onInitData() {

    }

    override fun onRestData(savedInstanceState: Bundle) {
        mObjectInstanceManager.againFragmentInstance(childFragmentManager, this)
        mObjectInstanceManager.againFieldInstance(savedInstanceState, this)
    }

}