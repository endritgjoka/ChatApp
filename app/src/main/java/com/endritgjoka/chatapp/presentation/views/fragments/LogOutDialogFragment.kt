package com.endritgjoka.chatapp.presentation.views.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.data.utils.AppPreferences
import com.endritgjoka.chatapp.data.utils.navigate
import com.endritgjoka.chatapp.databinding.DialogLogoutBinding
import com.endritgjoka.chatapp.presentation.views.activities.AuthActivity


class LogOutDialogFragment : DialogFragment() {
    lateinit var binding: DialogLogoutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setStyle(STYLE_NORMAL, R.style.RoundedCornersDialog)
        return inflater.inflate(R.layout.dialog_logout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogLogoutBinding.bind(view)
        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        val marginInDp = 16 // Horizontal margin in dp
        val scale = resources.displayMetrics.density
        val marginInPx = (marginInDp * scale + 0.5f).toInt() // Convert dp to pixels

        dialog?.window?.setLayout(
            resources.displayMetrics.widthPixels - 2 * marginInPx,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    private fun setupListeners(){
        with(binding){
            cancelBtn.setOnClickListener{
                dismiss()
            }

            logOutBtn.setOnClickListener{
                dismiss()
                AppPreferences.deleteUserData()
                navigate(AuthActivity::class.java, requireContext())
                activity?.finish()
            }
        }
    }

}