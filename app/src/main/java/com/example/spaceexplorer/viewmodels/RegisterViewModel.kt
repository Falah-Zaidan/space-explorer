package com.example.spaceexplorer.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.remote.RegisterService
import com.example.spaceexplorer.remote.model.RegisterResponse
import com.example.spaceexplorer.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@OpenForTesting
class RegisterViewModel @Inject constructor(
    private val appExecutors: AppExecutors,
    private val registerService: RegisterService
) : ViewModel() {

    val _successLiveData = MutableLiveData<Boolean>()
    val successLiveData: LiveData<Boolean> = _successLiveData

    fun registerUser(email: String, username: String, password: String, password2: String) {
        appExecutors.networkIO().execute {
            val registerServiceResponse = registerService.registerUser(
                Constants.adminToken,
                email, username, password, password2
            )

            registerServiceResponse.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    _successLiveData.value = true
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    _successLiveData.value = false
                }
            })
        }
    }
}
