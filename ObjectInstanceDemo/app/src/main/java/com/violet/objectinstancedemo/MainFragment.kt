package com.violet.objectinstancedemo

import android.os.Bundle
import android.widget.TextView
import androidx.annotation.NonNull
import com.violet.library.annotation.AgainFragmentInstance
import com.violet.library.annotation.AgainInstance
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber

/**
 * @Author: shalei
 * @Date: 2019-10-30 10:30
 * @Desc:
 **/
class MainFragment: BaseStub() {

    companion object {

        private const val KEY_NAME = "key_name"

        @JvmStatic
        fun newInstance(name: String): MainFragment {
            val bundle = Bundle()
            bundle.putString(KEY_NAME, name)
            val mainFragment = MainFragment()
            mainFragment.arguments = bundle
            return mainFragment
        }
    }

    @AgainInstance
    private lateinit var name: String

    @AgainFragmentInstance
    private var mChildFragment: ChildFragment? = null

    private val mTextView: TextView? by lazy { view?.findViewById<TextView>(R.id.text_fragment) }

    override fun getLayoutId(): Int {
        return R.layout.fragment_main
    }

    override fun onInitWidget() {
        mTextView?.setOnClickListener {
            startActivity(SecondaryActivity.newIntent(activity!!))
        }
    }

    override fun onInitData() {
        super.onInitData()
        name = arguments!!.getString(KEY_NAME)!!
        mTextView!!.text = "初始化数据: $name"
        mChildFragment = ChildFragment()
        childFragmentManager.beginTransaction().replace(R.id.contentPanel, mChildFragment!!).commitAllowingStateLoss()
    }

    override fun onRestData(savedInstanceState: Bundle) {
        super.onRestData(savedInstanceState)
        mTextView?.text = "数据还原：$name"
        Timber.d("mChildFragment: ${mChildFragment == null}")
    }

}