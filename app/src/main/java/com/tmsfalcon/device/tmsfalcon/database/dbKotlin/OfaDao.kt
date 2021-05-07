package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OfaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(otherFormAccident:OtherFormAccident):Long

    @Query("Select  * from other_form_data where id = 1")
      fun getBasicFormData():OtherFormAccident


}