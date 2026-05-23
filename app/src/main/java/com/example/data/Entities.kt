package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val matchId: String, // "global" or specific match e.g. "M1"
    val userEmail: String,
    val userNickname: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val avatarIndex: Int = 0
)

@Entity(tableName = "prediction_cache")
data class PredictionCache(
    @PrimaryKey val matchId: String,
    val predictionText: String,
    val analysisText: String,
    val tacticsText: String,
    val searchQueriesText: String = "",
    val searchSourcesText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_points")
data class UserPoint(
    @PrimaryKey val userEmail: String,
    val userNickname: String,
    val points: Int,
    val badgeName: String
)

@Entity(tableName = "user_preferences")
data class UserPreference(
    @PrimaryKey val prefKey: String,
    val prefValue: String
)
