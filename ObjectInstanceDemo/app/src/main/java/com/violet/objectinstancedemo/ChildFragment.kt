package com.violet.objectinstancedemo

import android.os.Bundle
import com.violet.library.annotation.AgainInstance
import kotlinx.android.synthetic.main.fragment_child.*
import timber.log.Timber

/**
 * @Author: shalei
 * @Date: 2019-10-30 12:26
 * @Desc:
 **/
class ChildFragment : BaseStub() {

    @AgainInstance
    private lateinit var logList: ArrayList<Long>

    override fun getLayoutId(): Int {
        return R.layout.fragment_child
    }

    override fun onInitWidget() = Unit

    override fun onInitData() {
        super.onInitData()
        logList = arrayListOf()
        logList.add(22L)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("logList $logList")
        text_fragment.text = "value $logList"
    }
}