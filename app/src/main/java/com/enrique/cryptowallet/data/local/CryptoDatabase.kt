package com.enrique.cryptowallet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.enrique.cryptowallet.data.model.Crypto

@Database(entities = [Crypto::class], version = 1, exportSchema = false)
abstract class CryptoDatabase : RoomDatabase() {

    abstract fun cryptoDao(): CryptoDao

    companion object {

        // A anotação @Volatile garante que qualquer alteração na variável INSTANCE
        // seja visível imediatamente para todas as threads de execução.
        @Volatile
        private var INSTANCE: CryptoDatabase? = null

        fun getDatabase(context: Context): CryptoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CryptoDatabase::class.java,
                    "crypto_wallet_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}