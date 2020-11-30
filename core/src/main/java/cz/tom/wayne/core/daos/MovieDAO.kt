package cz.tom.wayne.core.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import cz.tom.wayne.core.data.Movie

/**
 * DAO for campaign related stuff
 */
@Dao
interface MovieDAO {

    @Transaction
    suspend fun updateCampaigns(data: List<Movie>) {
        insertCampaigns(data)
    }

    @Query("DELETE FROM Movie WHERE id in (:ids)")
    suspend fun deleteCampaigns(ids: List<String>)

    @Query("SELECT * FROM Movie WHERE id = :campaignId")
    fun getMovieById(campaignId: String): LiveData<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaigns(excerpts: List<Movie>)
}
