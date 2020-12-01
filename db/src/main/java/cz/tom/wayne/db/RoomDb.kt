package cz.tom.wayne.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cz.tom.wayne.core.daos.DogDAO
import cz.tom.wayne.core.data.DogImageEntity

@Database(
    entities = [
        DogImageEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters()
abstract class RoomDb : RoomDatabase() {

    abstract fun dogDAO(): DogDAO
}