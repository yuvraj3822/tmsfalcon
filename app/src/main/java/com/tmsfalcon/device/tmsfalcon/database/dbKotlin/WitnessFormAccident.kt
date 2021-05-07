package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "witness_accident_data")
data class WitnessFormAccident(
        @PrimaryKey val id:Int? =0,
        @ColumnInfo(name = "driver_id") val driverId:Int,
        @ColumnInfo(name = "accident_report_id") val accidentReportId: Int,
        @ColumnInfo(name = "witness_name") val witnessName: String,
        @ColumnInfo(name = "witness_phone") val witnessPhone: String,
        @ColumnInfo(name = "witness_statement") val witnessStatement:String,
        @ColumnInfo(name = "witness_audio_url") var witnessAudioUrl:String
)