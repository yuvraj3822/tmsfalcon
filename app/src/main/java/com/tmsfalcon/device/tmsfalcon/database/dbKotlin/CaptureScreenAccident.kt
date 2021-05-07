package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "capture_screen_data")
data class  CaptureScreenAccident(
        @PrimaryKey(autoGenerate = true) val id: Int? = 0,
        @ColumnInfo(name = "driver_id") val driverId:Int,
        @ColumnInfo(name = "doc_type") val doc_type: String,
        @ColumnInfo(name = "image_url") var image_url: String,
        @ColumnInfo(name = "time") var time: String,
        @ColumnInfo(name = "lat") var lat: String,
        @ColumnInfo(name = "lng") var lng: String,
        @ColumnInfo(name = "accident_report_id") val accident_report_id: Int,
        @ColumnInfo(name = "description") val description: String,
        @ColumnInfo(name = "capture_type") val capture_type: String,


        @ColumnInfo(name = "isItVideo") val isItVideo: Boolean
)