package com.endritgjoka.chatapp.presentation.views.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.data.utils.AppPreferences
import com.endritgjoka.chatapp.data.utils.navigate
import com.endritgjoka.chatapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (AppPreferences.isLoggedIn) {
            navigate(HomeActivity::class.java, this)
        } else {
            navigate(AuthActivity::class.java, this)
        }

        finish()
    }
}