package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE matchId = :matchId ORDER BY timestamp ASC")
    fun getMessagesForMatch(matchId: String): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages WHERE id = :id")
    suspend fun deleteMessage(id: Int)
}

@Dao
interface PredictionDao {
    @Query("SELECT * FROM prediction_cache WHERE matchId = :matchId LIMIT 1")
    suspend fun getPrediction(matchId: String): PredictionCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: PredictionCache)
}

@Dao
interface UserPointsDao {
    @Query("SELECT * FROM user_points ORDER BY points DESC")
    fun getAllUserPoints(): Flow<List<UserPoint>>

    @Query("SELECT * FROM user_points WHERE userEmail = :userEmail LIMIT 1")
    suspend fun getUserPoints(userEmail: String): UserPoint?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePoints(userPoint: UserPoint)
}

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE prefKey = :key LIMIT 1")
    suspend fun getPreference(key: String): UserPreference?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(preference: UserPreference)
}
