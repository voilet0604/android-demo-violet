package com.violet.objectinstancedemo.entity

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * @Author: shalei
 * @Date: 2019-10-30 11:34
 * @Desc: 实现 Serializable接口原因是作为HashMap需要, HashMap本身实现Serializable接口，所以他存储数据也是需要Serializable
 **/
data class User(private val name: String, private val age: Int) : Parcelable, Serializable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(age)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}