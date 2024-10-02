package com.dplauder.todoapp

import android.os.Parcelable
import android.os.Parcel

data class TodoList (
    val title: String,
    val items: MutableList<Item>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.createTypedArrayList(Item.CREATOR) ?: mutableListOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeTypedList(items)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<TodoList>{
        override fun createFromParcel(parcel: Parcel): TodoList = TodoList(parcel)
        override fun newArray(size: Int): Array<TodoList?> = arrayOfNulls(size)
    }
}