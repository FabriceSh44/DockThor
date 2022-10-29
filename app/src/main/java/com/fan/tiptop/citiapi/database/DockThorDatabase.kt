package com.fan.tiptop.citiapi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fan.tiptop.citiapi.data.CitibikeStationInformationModel

@Database(entities = [CitibikeStationInformationModel::class], version = 3, exportSchema = false)
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
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}