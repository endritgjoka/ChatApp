package com.endritgjoka.chatapp.presentation.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.data.utils.safeNavigate
import com.endritgjoka.chatapp.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWelcomeBinding.bind(view)
        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        with(binding){
            loginBtn.setOnClickListener{
                safeNavigate(WelcomeFragmentDirections.welcomeToLogin())
            }
            registerBtn.setOnClickListener{
                safeNavigate(WelcomeFragmentDirections.welcomeToRegister())
            }
        }
    }
}