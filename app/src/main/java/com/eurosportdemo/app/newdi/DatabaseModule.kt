package com.eurosportdemo.app.newdi

import android.content.Context
import androidx.room.Room
import com.eurosportdemo.app.data.database.MyDatabase
import com.eurosportdemo.app.data.database.dao.BookDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): MyDatabase {
        return Room.databaseBuilder(appContext,
            MyDatabase::class.java,
            "MyDatabase.db")
            .build()
    }

    @Provides
    fun provideBookDao(database: MyDatabase): BookDao {
        return database.bookDao()
    }
}
