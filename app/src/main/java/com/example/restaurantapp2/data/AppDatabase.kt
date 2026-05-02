package com.example.restaurantapp2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.restaurantapp2.models.CartItem

@Database(entities = [CartItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}