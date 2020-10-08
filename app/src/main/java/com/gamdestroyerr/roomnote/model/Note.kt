package com.gamdestroyerr.roomnote.model

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gamdestroyerr.roomnote.utils.Converters
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Note(

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,
    val content: String,
    val date: String,
    val color: Int = -1,

    @field:TypeConverters(Converters::class)
    var image: Bitmap?,

    ): Parcelable