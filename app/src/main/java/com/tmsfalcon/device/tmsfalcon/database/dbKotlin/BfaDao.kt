package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BfaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(basicFormAccident:BasicFormAccident):Long

    @Query("Select  * from basic_form_data where id = 1")
      fun getBasicFormData():BasicFormAccident


}