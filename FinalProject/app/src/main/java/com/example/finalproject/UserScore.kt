package com.example.finalproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserScore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val score: Int
)
