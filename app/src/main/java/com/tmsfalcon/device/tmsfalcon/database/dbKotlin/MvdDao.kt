package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.*

@Dao
interface MvdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(multipleVehicleDetails:MultipleVehicleDetails):Long

    @Query("SELECT *  FROM multiple_vehicle_details")
    fun getVehicleDetailList():List<MultipleVehicleDetails>

    @Delete
    fun deleteSingleVehicleDetail(obj:MultipleVehicleDetails):Int


}