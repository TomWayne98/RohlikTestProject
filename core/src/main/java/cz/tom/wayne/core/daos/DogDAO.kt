package cz.tom.wayne.core.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cz.tom.wayne.core.data.DogImageEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for doggo related stuff
 */
@Dao
interface DogDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDogImage(dog: DogImageEntity)

    @Query("SELECT * FROM DogImageEntity")
    fun getAllDogImages(): Flow<List<DogImageEntity>?>

    @Query("SELECT * FROM DogImageEntity LIMIT 1")
    fun getLastDog(): Flow<List<DogImageEntity>?>
}