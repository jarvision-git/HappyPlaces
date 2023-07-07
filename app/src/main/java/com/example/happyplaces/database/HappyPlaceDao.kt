package com.example.happyplaces.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.happyplaces.models.HappyPlaceModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HappyPlaceDao {

    @Upsert
    suspend fun insertHappyPlace(happyPlaceModel:HappyPlaceModel)

    @Delete
    suspend fun deleteHappyPlace(happyPlaceModel:HappyPlaceModel)

    @Query("SELECT * FROM happyplacemodel")
    fun fetchAllHappyPlaces(): Flow<List<HappyPlaceModel>>

    @Query("Select * from happyplacemodel where id=:id")
    fun fetchHappyPlaceById(id:Int):Flow<HappyPlaceModel>


}