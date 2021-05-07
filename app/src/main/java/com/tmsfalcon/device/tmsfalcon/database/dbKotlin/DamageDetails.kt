package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.*
import java.util.*
import kotlin.collections.ArrayList

@Entity(tableName = "damage_details")
data class DamageDetails (
        @PrimaryKey val id:Int? =0,

        @ColumnInfo(name = "injury_type") val injuryType:String,
        @ColumnInfo(name = "name") val firstName:String,
        @ColumnInfo(name = "damage_description") val damageDescription:String,
        @ColumnInfo(name = "address") val address:String,
        @ColumnInfo(name = "city") val city:String,
        @ColumnInfo(name = "state") val state: String,
        @ColumnInfo(name = "zipcode") val zipcode:String,
        @ColumnInfo(name = "phone_no") val phoneNo:String,
        @ColumnInfo(name = "email") val email:String,
        @ColumnInfo(name = "type_of_people") val listTypePeople:ArrayList<String>
)