package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.*

@Dao
interface MovdDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(multipleOtherVehicleDetails:MultipleOtherVehicleDetails):Long

    @Query("SELECT *  FROM multiple_other_vehicle_details")
    fun getOtherVehicleDetailList():List<MultipleOtherVehicleDetails>

    @Delete
    fun deleteOhterSingleVehicleDetail(obj:MultipleOtherVehicleDetails):Int

//    @Query("UPDATE multiple_other_vehicle_details Set MultipleOtherVehicleDetails =:obj  WHERE id = :id")
//    fun update(id:Int,obj:MultipleOtherVehicleDetails)



}