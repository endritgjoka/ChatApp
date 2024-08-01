package com.endritgjoka.chatapp.presentation.views.activities

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.databinding.ActivityHomeBinding
import com.endritgjoka.chatapp.presentation.views.fragments.LogOutDialogFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.openPopup.setOnClickListener {
            openPopupMenu(binding.openPopup)
        }
    }

    private fun openPopupMenu(popupView: View) {
        popupView.setOnClickListener {
            val popupMenu = PopupMenu(this, popupView)
            popupMenu.menuInflater.inflate(R.menu.logout_popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_log_out -> {
                        val logoutDialogFragment = LogOutDialogFragment()
                        logoutDialogFragment.show(supportFragmentManager, "Log out")
                    }
                }
                true
            }
            popupMenu.show()
        }
    }
}

