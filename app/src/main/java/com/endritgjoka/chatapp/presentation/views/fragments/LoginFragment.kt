package com.endritgjoka.chatapp.presentation.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.endritgjoka.chatapp.R
import com.endritgjoka.chatapp.data.model.requests.LoginRequest
import com.endritgjoka.chatapp.databinding.FragmentLoginBinding
import com.endritgjoka.chatapp.presentation.viewmodel.AuthViewModel
import com.endritgjoka.chatapp.presentation.views.activities.HomeActivity

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    val authViewModel: AuthViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        login()
        observeResponses()
    }

    private fun login(){
        with(binding){
            loginBtn.setOnClickListener{
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if(email.isNotEmpty() && password.isNotEmpty()){
                    val loginRequest = LoginRequest(email, password)
                    authViewModel.login(loginRequest)
                }else{
                    Toast.makeText(context, getString(R.string.fields_are_required), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeResponses(){
        with(authViewModel){
            successResponse.observe(viewLifecycleOwner){message ->
                message?.let {
                    val intent = Intent(context, HomeActivity::class.java)
                    startActivity(intent)
                    successResponse.postValue(null)
                    activity?.finish()
                }
            }

            failedResponse.observe(viewLifecycleOwner){message ->
                message?.let {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    failedResponse.postValue(null)
                }
            }
        }
    }

}