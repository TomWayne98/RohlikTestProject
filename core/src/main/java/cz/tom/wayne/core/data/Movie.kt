package cz.tom.wayne.core.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Movie(
    @PrimaryKey
    val id: String,
    val title: String,
    val overview: String,
    val rating: Float
)
