package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.*

@Dao
interface DamageDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(damageDetails:DamageDetails):Long

    @Query("SELECT *  FROM damage_details")
    fun getDamageDetails():List<DamageDetails>

    @Delete
    fun deleteDamageDetail(obj:DamageDetails):Int


}