package com.example.finalproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.finalproject.adapters.PlayerAdapter
import com.example.finalproject.data.*
import com.example.finalproject.utils.NumberRangeFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import kotlin.math.abs

class GameActivity : AppCompatActivity() {

    private var currentScore = 0
    private var currentPlayerIndex = 0
    private var players: MutableList<Player> = mutableListOf()

    private lateinit var gestureDetector: GestureDetector
    private lateinit var db: AppDatabase
    private lateinit var username: String

    // UI Bileşenleri
    private lateinit var tvPlayerName: TextView
    private lateinit var tvTeamName: TextView
    private lateinit var tvScore: TextView
    private lateinit var etGuess: EditText
    private lateinit var btnSubmit: Button
    private lateinit var recyclerViewPlayers: RecyclerView
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var ivTeamLogo: ImageView

    private val teamLogoUrls = mapOf(
        "Galatasaray" to "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Galatasaray_Sports_Club_Logo.png/822px-Galatasaray_Sports_Club_Logo.png",
        "Fenerbahçe" to "https://upload.wikimedia.org/wikipedia/tr/archive/8/86/20210127193236%21Fenerbah%C3%A7e_SK.png",
        "Beşiktaş" to "https://upload.wikimedia.org/wikipedia/commons/0/08/Be%C5%9Fikta%C5%9F_Logo_Be%C5%9Fikta%C5%9F_Amblem_Be%C5%9Fikta%C5%9F_Arma.png",
        "Trabzonspor" to "https://upload.wikimedia.org/wikipedia/tr/archive/a/ab/20220929150220%21TrabzonsporAmblemi.png"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_screen)

        db = AppDatabase.getInstance(this)

        username = getUsername()
        Toast.makeText(this, "Hoş geldiniz, $username!", Toast.LENGTH_SHORT).show()

        tvPlayerName = findViewById(R.id.tvPlayerName)
        tvTeamName = findViewById(R.id.tvTeamName)
        tvScore = findViewById(R.id.tvScore)
        etGuess = findViewById(R.id.etGuess)
        btnSubmit = findViewById(R.id.btnSubmit)
        recyclerViewPlayers = findViewById(R.id.recyclerViewPlayers)
        ivTeamLogo = findViewById(R.id.teamLogoImageView)

        etGuess.filters = arrayOf(NumberRangeFilter(1, 99))

        val selectedTeam = intent.getStringExtra("TEAM_NAME") ?: ""
        loadTeamLogo(selectedTeam)

        recyclerViewPlayers.layoutManager = LinearLayoutManager(this)
        playerAdapter = PlayerAdapter(players)
        recyclerViewPlayers.adapter = playerAdapter

        fetchTeamsFromJson(selectedTeam)

        btnSubmit.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce)
            btnSubmit.startAnimation(animation)
            handleSubmitGuess()
        }

        gestureDetector = GestureDetector(this, gestureListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun loadTeamLogo(teamName: String) {
        val logoUrl = teamLogoUrls[teamName]
        ivTeamLogo.load(logoUrl) {
            placeholder(R.drawable.placeholder_logo)
            error(R.drawable.placeholder_logo)
        }
    }

    private fun fetchTeamsFromJson(selectedTeam: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getAllTeams()
                if (response.isSuccessful) {
                    val allTeams: AllTeams? = response.body()
                    withContext(Dispatchers.Main) {
                        if (allTeams != null) {
                            val teamFound = allTeams.teams.find { it.teamName.equals(selectedTeam, ignoreCase = true) }
                            if (teamFound != null) {
                                players = teamFound.players.toMutableList()
                                playerAdapter = PlayerAdapter(players)
                                recyclerViewPlayers.adapter = playerAdapter
                                displayPlayerInfo()
                                tvScore.text = "Score: $currentScore"
                            } else {
                                showToast("Takım bulunamadı!")
                            }
                        } else {
                            showToast("Veri alınamadı!")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Hata Kodu: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Hata: ${e.message}")
                }
            }
        }
    }

    private fun handleSubmitGuess() {
        if (players.isEmpty()) {
            showToast("Oyuncular henüz yüklenmedi!")
            return
        }

        val guessStr = etGuess.text.toString()
        if (guessStr.isEmpty()) {
            showToast("Lütfen bir sayı girin!")
            return
        }

        val guessedNumber = guessStr.toIntOrNull()
        if (guessedNumber == null) {
            showToast("Geçersiz sayı girdiniz!")
            return
        }

        val currentPlayer = players[currentPlayerIndex]
        val correctNumber = currentPlayer.kitNumber

        if (guessedNumber == correctNumber) {
            currentScore += 10
            tvPlayerName.setTextColor(Color.GREEN)
            showToast("Doğru! +10 puan")
            updatePlayerStatus(currentPlayerIndex, GuessStatus.CORRECT)
        } else {
            tvPlayerName.setTextColor(Color.RED)
            showToast("Yanlış! Doğru sayı: $correctNumber")
            updatePlayerStatus(currentPlayerIndex, GuessStatus.INCORRECT)
        }

        tvScore.text = "Score: $currentScore"
        etGuess.text.clear()

        lifecycleScope.launch {
            delay(1000)
            moveToNextPlayer()
        }
    }

    private fun updatePlayerStatus(index: Int, status: GuessStatus) {
        players[index].guessStatus = status
        playerAdapter.updatePlayers(players)
    }

    private fun displayPlayerInfo() {
        tvPlayerName.setTextColor(Color.BLACK)
        val currentPlayer = players[currentPlayerIndex]
        tvPlayerName.text = "Oyuncu: ${currentPlayer.name}"
        tvTeamName.text = "Takım: ${currentPlayer.team}"
    }

    private fun moveToNextPlayer() {
        var nextIndex = currentPlayerIndex
        do {
            nextIndex = (nextIndex + 1) % players.size
        } while (players[nextIndex].guessStatus != GuessStatus.UNANSWERED && nextIndex != currentPlayerIndex)

        if (players[nextIndex].guessStatus == GuessStatus.UNANSWERED) {
            currentPlayerIndex = nextIndex
            displayPlayerInfo()
        } else {
            navigateToResult()
        }
    }

    private fun moveToPreviousPlayer() {
        var previousIndex = currentPlayerIndex
        do {
            previousIndex = if (previousIndex - 1 < 0) players.size - 1 else previousIndex - 1
        } while (players[previousIndex].guessStatus != GuessStatus.UNANSWERED && previousIndex != currentPlayerIndex)

        if (players[previousIndex].guessStatus == GuessStatus.UNANSWERED) {
            currentPlayerIndex = previousIndex
            displayPlayerInfo()
        } else {
            showToast("Cevaplanmamış oyuncu yok!")
        }
    }

    private fun navigateToResult() {
        saveScore(username, currentScore)
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("FINAL_SCORE", currentScore)
            putExtra("TEAM_NAME", players[currentPlayerIndex].team)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left) // Animasyonları ekledik
        finish()
    }


    private fun saveScore(username: String, score: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val existingUserScore = db.userScoreDao().getUserHighScore(username)
            if (existingUserScore == null || score > existingUserScore.score) {
                val userScore = UserScore(username = username, score = score)
                db.userScoreDao().insertUserScore(userScore)
            }
        }
    }
    private fun getUsername(): String {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("USERNAME", "Bilinmiyor") ?: "Bilinmiyor"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        moveToPreviousPlayer()
                    } else {
                        moveToNextPlayer()
                    }
                    return true
                }
            }
            return false
        }
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }
}
