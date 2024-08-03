package com.endritgjoka.chatapp.presentation.views.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.data.model.requests.LoginRequest
import com.endritgjoka.chatapp.data.model.requests.RegisterRequest
import com.endritgjoka.chatapp.data.utils.hideKeyboard
import com.endritgjoka.chatapp.data.utils.safeNavigate
import com.endritgjoka.chatapp.databinding.FragmentRegisterBinding
import com.endritgjoka.chatapp.presentation.viewmodel.AuthViewModel
import com.endritgjoka.chatapp.presentation.views.activities.HomeActivity

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    val authViewModel: AuthViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)
        register()
        observeResponses()
    }

    private fun register() {
        with(binding) {
            registerBtn.setOnClickListener {
                val fullName = fullNameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty() && fullName.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        if (password == confirmPassword && password.length >= 6) {
                            registerBtn.hideKeyboard()
                            val registerRequest =
                                RegisterRequest(fullName, email, password, confirmPassword)
                            authViewModel.register(registerRequest)
                        }else{
                            Toast.makeText(context,"Passwords doesn't match!",Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context,"Invalid email!",Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.fields_are_required),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun observeResponses() {
        with(authViewModel) {
            successResponse.observe(viewLifecycleOwner) { message ->
                message?.let {
                    successResponse.postValue(null)
                    safeNavigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                }
            }

            failedResponse.observe(viewLifecycleOwner) { message ->
                message?.let {
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_SHORT
                    ).show()
                    failedResponse.postValue(null)
                }
            }
        }
    }
}