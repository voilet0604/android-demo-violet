package com.violet.objectinstancedemo

import android.os.Bundle
import com.violet.library.annotation.AgainFragmentInstance
import com.violet.library.annotation.AgainInstance
import com.violet.objectinstancedemo.entity.User
import com.violet.objectinstancedemo.entity.UserMap
import timber.log.Timber

class MainActivity : BaseAct() {

    @AgainFragmentInstance
    private var mainFragment: MainFragment? = null

    @AgainInstance
    private val age = 10

    /**
     * 可以手动指定key
     */
    @AgainInstance(key = "price")
    private val mPrice = 2000

    @AgainInstance
    private lateinit var mUser: User

    @AgainInstance
    private lateinit var mUserLis: ArrayList<User>

    @AgainInstance
    private lateinit var mUserMap: UserMap

    @AgainInstance
    private lateinit var mMap:HashMap<String, String>

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onInitWidget() {
    }

    override fun onInitData() {
        mUser = User("xiaowang", 13)

        mUserLis = arrayListOf()
        mUserLis.add(mUser)

        val map: HashMap<String, User> = hashMapOf()
        mUserMap = UserMap(map)
        map["user"] = mUser

        mMap = hashMapOf()
        mMap["xx"] = "yyy"

        //fragment 只要初始化一次
        mainFragment = MainFragment.newInstance("this is main_fragment")
        supportFragmentManager.beginTransaction().replace(R.id.contentPanel, mainFragment!!)
            .commitAllowingStateLoss()
    }

    override fun onRestData(savedInstanceState: Bundle) {
        super.onRestData(savedInstanceState)
        Timber.d("${mainFragment == null}")
        Timber.d("age $age")
        Timber.d("user $mUser")
        Timber.d("price $mPrice")
        Timber.d("userList $mUserLis")
        Timber.d("userMap $mUserMap")
        Timber.d("map $mMap")
    }

}
