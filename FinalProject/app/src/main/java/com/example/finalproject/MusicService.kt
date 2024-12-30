package com.example.finalproject.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import com.example.finalproject.R

class MusicService : Service() {

    companion object {
        var mediaPlayer: MediaPlayer? = null
        var isPlaying: Boolean = false

        /**
         * Ses seviyesi ayarlamak için MediaPlayer'a erişim sağlayan bir fonksiyon.
         */
        fun setVolume(leftVolume: Float, rightVolume: Float) {
            mediaPlayer?.setVolume(leftVolume, rightVolume)
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music) // Müzik dosyanız
            mediaPlayer?.isLooping = true // Döngüye al
            mediaPlayer?.setVolume(0.5f, 0.5f) // Varsayılan ses seviyesi
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isPlaying) {
            mediaPlayer?.start()
            isPlaying = true
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlaying) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
