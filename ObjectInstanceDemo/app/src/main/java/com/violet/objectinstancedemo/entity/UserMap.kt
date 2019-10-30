package com.violet.objectinstancedemo.entity

import android.os.Parcel
import android.os.Parcelable

/**
 * @Author: shalei
 * @Date: 2019-10-30 11:47
 * @Desc:
 **/
data class UserMap(private val map: HashMap<String, User>) : Parcelable {

    constructor(source: Parcel) : this(
        source.readSerializable() as HashMap<String, User>
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeSerializable(map)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<UserMap> = object : Parcelable.Creator<UserMap> {
            override fun createFromParcel(source: Parcel): UserMap = UserMap(source)
            override fun newArray(size: Int): Array<UserMap?> = arrayOfNulls(size)
        }
    }
}