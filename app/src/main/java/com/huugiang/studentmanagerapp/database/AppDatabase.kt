package com.huugiang.studentmanagerapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.huugiang.studentmanagerapp.database.dao.StudentDao
import com.huugiang.studentmanagerapp.database.entity.ClassEntity
import com.huugiang.studentmanagerapp.database.entity.StudentEntity

@Database(
    entities = [StudentEntity::class, ClassEntity::class],
    version = AppDatabase.DB_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    companion object {
        const val DB_NAME = "student_manager.db"
        const val DB_VERSION = 1

        @Volatile
        private var instance: AppDatabase? = null

        operator fun invoke(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).build()
        }
    }
}