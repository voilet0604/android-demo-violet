package com.violet.objectinstancedemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.violet.library.annotation.AgainInstance
import kotlinx.android.synthetic.main.activity_seconday.*

/**
 * @Author: shalei
 * @Date: 2019-10-30 12:53
 * @Desc:
 **/
class SecondaryActivity : BaseAct() {

    companion object {

        @JvmStatic
        fun newIntent(act: Activity):Intent {
            return Intent(act, SecondaryActivity::class.java)
        }
    }

    @AgainInstance
    private lateinit var name:String

    override fun getLayoutId() = R.layout.activity_seconday

    override fun onInitWidget() = Unit

    override fun onInitData() {
        name = "this is SecondaryActivity"
    }

    override fun onStart() {
        super.onStart()
        text_name.text = name
    }

}