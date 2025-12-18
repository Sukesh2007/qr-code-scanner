package com.example.qrcodescanner

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity
data class Scanner(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val content: String,
    val time : String,
    val date: String
)

@Dao
interface ScannerData {
    @Insert
    fun insertItem(item: Scanner)

    @Query("DELETE FROM Scanner WHERE id = :id")
    fun deleteItem(id: Int)

    @Query("Select * From Scanner ORDER BY id DESC")
    fun fetchAllItem() : LiveData<List<Scanner>>

    @Query("Select COUNT(*) FROM Scanner")
    fun getCount(): Int
}

@Database(entities = [Scanner::class], version = 1, exportSchema = false)
abstract class ScannerDatabase(): RoomDatabase() {
    abstract fun scannerData(): ScannerData
}

class ScannerInventory(context: Context){
    private val database = Room.databaseBuilder(
        context.applicationContext,
        ScannerDatabase::class.java,
        "databases.db"
    ).build()

    fun insertItem(item: Scanner) = database.scannerData().insertItem(item)

    fun deleteItem(id: Int) = database.scannerData().deleteItem(id)

    fun getAllItems(): LiveData<List<Scanner>> = database.scannerData().fetchAllItem()

    fun lengthOfData() = database.scannerData().getCount()
}