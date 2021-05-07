package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.*

@Dao
interface CsaDao {

      @Insert
     fun insert(captureScreenAccident:CaptureScreenAccident):Long

    @Query("SELECT *  FROM capture_screen_data")
     fun getImageListss():List<CaptureScreenAccident>

    @Delete
    fun deleteImage(csa:CaptureScreenAccident):Int

    @Update
    fun updateImageData(csa:CaptureScreenAccident)


//
//      @Query("Select  * from witness_accident_data")
//      fun getWitnessFormData():List<WitnessFormAccident>
//
//    @Query("SELECT * from  witness_accident_data where  accident_report_id IN(:accidentReportId)")
//    fun getWitnessDetailById(accidentReportId:Int):Long
//
//    @Query("SELECT COUNT(*) FROM witness_accident_data")
//    fun whetherDataAvailable():Long
//
//    @Delete
//    fun deleteSingleWitness(result:WitnessFormAccident):Int

}