package com.example.finalproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserScore(userScore: UserScore)

    @Query("SELECT * FROM UserScore ORDER BY score DESC")
    suspend fun getAllScores(): List<UserScore>

    @Query("DELETE FROM UserScore")
    suspend fun deleteAllScores()

    @Query("SELECT * FROM UserScore WHERE username = :username ORDER BY score DESC LIMIT 1")
    suspend fun getUserHighScore(username: String): UserScore?
    @Query("SELECT * FROM UserScore ORDER BY score DESC LIMIT 3")
    suspend fun getHighScores(): List<UserScore>

}


