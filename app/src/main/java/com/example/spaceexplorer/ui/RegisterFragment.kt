package com.example.spaceexplorer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.spaceexplorer.R
import com.example.spaceexplorer.binding.FragmentDataBindingComponent
import com.example.spaceexplorer.databinding.FragmentRegisterBinding
import com.example.spaceexplorer.di.util.Injectable
import com.example.spaceexplorer.util.autoCleared
import com.example.spaceexplorer.viewmodels.RegisterViewModel
import javax.inject.Inject

class RegisterFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProvider.Factory

    var databindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<FragmentRegisterBinding>()

    val registerViewModel: RegisterViewModel by viewModels {
        viewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate<FragmentRegisterBinding>(
            inflater,
            R.layout.fragment_register,
            container,
            false,
            databindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initDataBindingLayout()
        observeLiveData()

    }

    private fun initDataBindingLayout() {
        binding.registerUserBtn.setOnClickListener {
            saveUserInput()
        }
    }

    private fun observeLiveData() {
        registerViewModel.successLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                //display success to the UI
                Toast.makeText(context, getString(R.string.toast_user_created), Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
            } else {
                //display error to the UI
                Toast.makeText(
                    context,
                    getString(R.string.toast_invalid_user_creation),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        })
    }

    private fun saveUserInput() {

        //check to see if (all) the entries are valid
        //if not display a Toast
        //else store in the api through the ViewModel

        val email = binding.emailInput.text.toString()
        val username = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()

        if (!email.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            registerViewModel.registerUser(email, username, password, password)
        } else {
            Toast.makeText(context, getString(R.string.toast_empty_field), Toast.LENGTH_SHORT)
                .show()
        }
    }
}