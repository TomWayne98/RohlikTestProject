package cz.tom.wayne.core.data

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
    // Unique database ID
    val id: String,
    // URL to dog image picture
    val url: String
) {
    companion object {
        const val URL_SUFFIX = ".jpg"
        const val IMAGE_ID_PREFIX = "/"
    }
}

fun DogImageResponse.toDBEntity(): DogImageEntity {
    val id = message.split(DogImageEntity.IMAGE_ID_PREFIX).last().removeSuffix(DogImageEntity.URL_SUFFIX)
    return DogImageEntity(id, this.message)
}

