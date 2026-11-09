package com.example.progr3ss

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progr3ss.databinding.ActivitySplashBinding
import com.example.progr3ss.repository.AuthRepository
import com.example.progr3ss.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"
    private lateinit var binding: ActivitySplashBinding

    companion object {
        private const val SPLASH_TIME_OUT = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: SplashActivity created.")

        // Indítási animáció: a logó fokozatosan megjelenik
        binding.logo.alpha = 0f
        binding.logo.animate().alpha(1f).setDuration(1000).start()

        // Automatikus belépés ellenőrzése, de legalább SPLASH_TIME_OUT ideig látszik a splash
        lifecycleScope.launch {
            val startedAt = SystemClock.elapsedRealtime()
            val loggedIn = checkAutoLogin()

            val elapsed = SystemClock.elapsedRealtime() - startedAt
            if (elapsed < SPLASH_TIME_OUT) delay(SPLASH_TIME_OUT - elapsed)

            openMain(loggedIn)
        }
    }

    // true, ha a mentett refresh tokennel sikerült új tokeneket kapni
    private suspend fun checkAutoLogin(): Boolean {
        val session = SessionManager(applicationContext)
        val refreshToken = session.fetchRefreshToken() ?: return false

        return try {
            val response = AuthRepository(applicationContext).refresh(refreshToken)
            val tokens = response.body()
            if (response.isSuccessful && tokens != null) {
                session.saveAuthToken(tokens.accessToken)
                session.saveRefreshToken(tokens.refreshToken)
                Log.d(TAG, "Auto-login successful")
                true
            } else {
                // A szerver elutasította: a mentett adatok érvénytelenek
                Log.d(TAG, "Auto-login rejected: ${response.code()}")
                session.clearSession()
                false
            }
        } catch (e: Exception) {
            // Pl. nincs internet: a mentett adatok megmaradnak, de most a Login jön
            Log.d(TAG, "Auto-login failed: ${e.message}")
            false
        }
    }

    private fun openMain(loggedIn: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("startAtHome", loggedIn)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: SplashActivity started.")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: SplashActivity resumed.")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: SplashActivity paused.")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: SplashActivity stopped.")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: SplashActivity restarted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: SplashActivity destroyed.")
    }
}