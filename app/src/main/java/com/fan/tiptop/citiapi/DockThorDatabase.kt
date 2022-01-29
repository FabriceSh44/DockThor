package com.fan.tiptop.citiapi

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CitibikeStationInformationModel::class], version = 1, exportSchema = false)
abstract class DockThorDatabase : RoomDatabase() {
    abstract val citibikeStationInformationDao: CitibikeStationInformationDao

    companion object {
        @Volatile
        private var INSTANCE: DockThorDatabase? = null

        fun getInstance(context: Context): DockThorDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DockThorDatabase::class.java,
                        "dockthor_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}