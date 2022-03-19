package com.example.spaceexplorer.ui

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.R
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.remote.model.APIUser
import com.example.spaceexplorer.ui.model.login.LoggedInUserView
import com.example.spaceexplorer.viewmodels.LoginViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    @Inject
    lateinit var appExecutors: AppExecutors

    val loginViewModel: LoginViewModel by viewModels {
        viewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        activity.bottom_navigation.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLiveData()
        setUpListeners()
    }

    private fun setUpListeners() {
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    username_input.text.toString(),
                    password_input.text.toString()
                )
            }
        }
        username_input.addTextChangedListener(afterTextChangedListener)
        password_input.addTextChangedListener(afterTextChangedListener)
        password_input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                    username_input.text.toString(),
                    password_input.text.toString()
                )
            }
            false
        }

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.login(
                username_input.text.toString(),
                password_input.text.toString()
            )
        }

        proceed.setOnClickListener {
            loading.visibility = View.VISIBLE
            loginViewModel.proceed()
        }

        register.setOnClickListener {
            loading.visibility = View.VISIBLE
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }

    private fun observeLiveData() {
        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                login.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    username_input.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    password_input.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loading.visibility = View.GONE
                if (loginResult == true) {
                    //proceed the app (the user is authenticated - and his value is stored in the DB)
                    // pass in an empty value to APODFragment
                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToApodFragment("")
                    )
                } else {
                    //display an error message/toast
                    Toast.makeText(context, getString(R.string.toast_invalid_credentials), Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

}