package com.tmsfalcon.device.tmsfalcon.database.dbKotlin

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters
import com.tmsfalcon.device.tmsfalcon.customtools.Converters
import kotlin.coroutines.CoroutineContext

@Database(entities = arrayOf(BasicFormAccident::class,OtherFormAccident::class,
WitnessFormAccident::class,CaptureScreenAccident::class,MultipleVehicleDetails::class,
        MultipleOtherVehicleDetails::class,DamageDetails::class),version = 30,exportSchema = false)
@TypeConverters(Converters::class)
public abstract  class AccidentModuleDb : RoomDatabase() {


    abstract fun bfaDao(): BfaDao
    abstract fun mvdDao(): MvdDao

    abstract fun ofaDao(): OfaDao
    abstract fun movdDao() : MovdDao

    abstract fun wfaDao(): WfaDao
    abstract fun csaDao(): CsaDao
    abstract fun damageDao(): DamageDetailsDao


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AccidentModuleDb? = null

        fun getDatabase(context: Context): AccidentModuleDb {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AccidentModuleDb::class.java,
                        "word_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}