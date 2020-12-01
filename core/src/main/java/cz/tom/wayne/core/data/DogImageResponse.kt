package cz.tom.wayne.core.data

import android.provider.Settings
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Reflection of JSON body from GSON
 */
data class DogImageResponse(
    // URL to dog image picture
    val message: String
)

@Entity
data class DogImageEntity(
    @PrimaryKey
    // Unique database ID based on timestamp
    val id: Long,
    // URL to dog image picture
    val url: String
)

fun DogImageResponse.toDBEntity(): DogImageEntity {
    return DogImageEntity(System.currentTimeMillis(), this.message)
}

