package com.example.finalproject

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.load
import com.example.finalproject.adapters.TeamAdapter
import com.example.finalproject.services.MusicService
import com.example.finalproject.workers.DataSyncWorker
import startDataSyncWorker
import java.util.concurrent.TimeUnit

class TeamSelectionActivity : AppCompatActivity() {

    private val teamLogoUrls = mapOf(
        "Galatasaray" to "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f6/Galatasaray_Sports_Club_Logo.png/822px-Galatasaray_Sports_Club_Logo.png",
        "Fenerbahçe" to "https://upload.wikimedia.org/wikipedia/tr/archive/8/86/20210127193236%21Fenerbah%C3%A7e_SK.png",
        "Beşiktaş" to "https://upload.wikimedia.org/wikipedia/commons/0/08/Be%C5%9Fikta%C5%9F_Logo_Be%C5%9Fikta%C5%9F_Amblem_Be%C5%9Fikta%C5%9F_Arma.png",
        "Trabzonspor" to "https://upload.wikimedia.org/wikipedia/tr/archive/a/ab/20220929150220%21TrabzonsporAmblemi.png"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_selection)
        startDataSyncWorker(this)
        fun schedulePeriodicDataSync(context: Context) {
            val periodicWorkRequest = PeriodicWorkRequestBuilder<DataSyncWorker>(1, TimeUnit.HOURS).build()
            WorkManager.getInstance(context).enqueue(periodicWorkRequest)
        }
        // Kullanıcı adı sor
        showUsernameDialog()

        // RecyclerView Ayarları
        val teams = teamLogoUrls.keys.toList()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewTeams)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TeamAdapter(teams, teamLogoUrls) { selectedTeam ->
            navigateToGameActivity(selectedTeam)
        }
        // Pulse animasyonu uygulama
        val logoImageView = findViewById<ImageView>(R.id.teamLogoImageView)
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse)
        logoImageView.startAnimation(pulseAnimation)

        // Müzik Servisini Başlat
        val musicIntent = Intent(this, MusicService::class.java)
        startService(musicIntent)

        // Ses Kontrolü
        val seekBarVolume = findViewById<SeekBar>(R.id.seekBarVolume)
        seekBarVolume.progress = 50
        seekBarVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val volume = progress / 100f
                MusicService.mediaPlayer?.setVolume(volume, volume)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun showUsernameDialog() {
        val builder = AlertDialog.Builder(this)
        val input = android.widget.EditText(this)
        input.hint = "Enter your username"

        builder.setTitle("User Login")
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            val username = input.text.toString()
            if (username.isNotBlank()) {
                saveUsername(username)
            } else {
                showToast("Username cannot be empty!")
                showUsernameDialog()
            }
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun saveUsername(username: String) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("USERNAME", username)
            apply()
        }
    }

    private fun navigateToGameActivity(teamName: String) {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("TEAM_NAME", teamName)
        val options = ActivityOptions.makeCustomAnimation(
            this,
            R.anim.slide_in_right, // Giriş animasyonu
            R.anim.slide_out_left  // Çıkış animasyonu
        )

        // Animasyonlu geçişle yeni Activity'yi başlat
        startActivity(intent, options.toBundle())
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Servisi durdur
        val musicIntent = Intent(this, MusicService::class.java)
        stopService(musicIntent)
    }
}
