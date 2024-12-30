package com.example.finalproject

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val lottieAnimation = findViewById<LottieAnimationView>(R.id.lottieAnimation)
        val logoImage = findViewById<ImageView>(R.id.logoImage)
        val appNameText = findViewById<TextView>(R.id.appNameText)

        // Lottie animasyonu tamamlandığında
        lottieAnimation.addLottieOnCompositionLoadedListener {
            lottieAnimation.playAnimation() // Lottie animasyonu başlatılır
        }

        lottieAnimation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // Animasyon bitince logoyu ve uygulama adını fade-in ile göster
                lottieAnimation.visibility = View.GONE // Lottie'yi gizle
                fadeInViews(logoImage, appNameText) {
                    navigateToMain()
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun fadeInViews(vararg views: View, onAnimationEnd: () -> Unit) {
        var animationsCompleted = 0

        views.forEach { view ->
            view.visibility = View.VISIBLE
            val fadeIn = AlphaAnimation(0f, 1f).apply {
                duration = 2000
                setAnimationListener(object : android.view.animation.Animation.AnimationListener {
                    override fun onAnimationStart(animation: android.view.animation.Animation?) {}
                    override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                        animationsCompleted++
                        if (animationsCompleted == views.size) {
                            onAnimationEnd()
                        }
                    }
                    override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
                })
            }
            view.startAnimation(fadeIn)
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, TeamSelectionActivity::class.java)
        startActivity(intent)
        finish()
    }
}
