package com.eurosportdemo.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eurosportdemo.app.data.Converters
import com.eurosportdemo.app.data.database.dao.BookDao
import com.eurosportdemo.app.domain.model.Book

@Database(entities = [Book::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MyDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}

