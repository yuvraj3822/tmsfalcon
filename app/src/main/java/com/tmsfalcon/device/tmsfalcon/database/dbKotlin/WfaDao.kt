package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.*

@Dao
interface WfaDao {

      @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(witnessFormAccident:WitnessFormAccident):Long

      @Query("Select  * from witness_accident_data")
      fun getWitnessFormData():List<WitnessFormAccident>

    @Query("SELECT * from  witness_accident_data where  accident_report_id IN(:accidentReportId)")
    fun getWitnessDetailById(accidentReportId:Int):Long

    @Query("SELECT COUNT(*) FROM witness_accident_data")
    fun whetherDataAvailable():Long

    @Delete
    fun deleteSingleWitness(result:WitnessFormAccident):Int

    @Update
    fun updateAccident(result:WitnessFormAccident):Int


}