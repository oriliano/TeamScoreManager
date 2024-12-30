// com.example.finalproject.data.Player.kt
package com.example.finalproject.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("name")
    @Expose
    val name: String,

    @SerializedName("team")
    @Expose
    val team: String,

    @SerializedName("kitNumber")
    @Expose
    val kitNumber: Int,

    @Transient // Gson tarafından göz ardı edilir
    var guessStatus: GuessStatus = GuessStatus.UNANSWERED
)
