package com.example.finalproject
import android.app.ActivityOptions
import com.example.finalproject.data.UserScore

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finalproject.data.AppDatabase
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {

    private lateinit var tvScore: TextView
    private lateinit var tvHighScores: TextView
    private lateinit var btnPlayAgain: Button
    private lateinit var btnExit: Button
    private lateinit var btnResetScores: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_screen)

        // UI Bileşenlerini bağlama
        tvScore = findViewById(R.id.tvFinalScore)
        tvHighScores = findViewById(R.id.tvHighScores)
        btnPlayAgain = findViewById(R.id.btnPlayAgain)
        btnExit = findViewById(R.id.btnExit)
        btnResetScores = findViewById(R.id.btnResetScores)

        // Intent'ten verileri alma
        val finalScore = intent.getIntExtra("FINAL_SCORE", 0)
        val username = intent.getStringExtra("USERNAME") ?: "Unknown"

        tvScore.text = "Your Score: $finalScore"

        // Veritabanından high scores verilerini alıp göster
        val db = AppDatabase.getInstance(this)
        lifecycleScope.launch {
            val highScores: List<UserScore> = db.userScoreDao().getHighScores() // Türü açıkça belirtin
            val highScoreText = highScores.joinToString("\n") { "${it.username}: ${it.score}" }
            tvHighScores.text = highScoreText
        }

        // Kullanıcının skorunu kaydetme
        saveUserScore(db, username, finalScore)

        // "Tekrar Oyna" butonuna tıklama işlemi
        btnPlayAgain.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce)
            btnPlayAgain.startAnimation(animation)
            navigateToTeamSelection()
        }

        // "Çıkış" butonuna tıklama işlemi
        btnExit.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce)
            btnExit.startAnimation(animation)
            finishAffinity()
        }

        // "High Scores Sıfırla" butonuna tıklama işlemi
        btnResetScores.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce)
            btnResetScores.startAnimation(animation)
            resetHighScores(db)
        }
    }

    private fun saveUserScore(db: AppDatabase, username: String, score: Int) {
        lifecycleScope.launch {
            val highScores = db.userScoreDao().getAllScores()
            val highScoreText = highScores.joinToString("\n") { "${it.username}: ${it.score}" }
            tvHighScores.text = highScoreText
        }
    }

    private fun navigateToTeamSelection() {
        val intent = Intent(this, TeamSelectionActivity::class.java)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right, // Giriş animasyonu
            R.anim.slide_out_left  // Çıkış animasyonu
        )

        // Animasyonlu geçişle yeni Activity'yi başlat
        startActivity(intent, options.toBundle())

        finish()
    }

    private fun resetHighScores(db: AppDatabase) {
        lifecycleScope.launch {
            db.userScoreDao().deleteAllScores()
            tvHighScores.text = "" // Ekrandaki skor listesini temizle
            showToast("All scores have been reset!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}

