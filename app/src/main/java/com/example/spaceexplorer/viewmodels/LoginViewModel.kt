package com.example.spaceexplorer.viewmodels

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spaceexplorer.AppExecutors
import com.example.spaceexplorer.OpenForTesting
import com.example.spaceexplorer.R
import com.example.spaceexplorer.cache.dao.LoginDao
import com.example.spaceexplorer.cache.model.User
import com.example.spaceexplorer.remote.LoginService
import com.example.spaceexplorer.remote.model.APIUser
import com.example.spaceexplorer.ui.model.login.LoginFormState
import com.example.spaceexplorer.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@OpenForTesting
class LoginViewModel @Inject constructor(
    private val appExecutors: AppExecutors,
    private val loginService: LoginService,
    private val loginDao: LoginDao
) :
    ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    val _loginResult = MutableLiveData<Boolean>(false)
    val loginResult: LiveData<Boolean> = _loginResult

    val _userLiveData = loginDao.getUser()
    val userLiveData: LiveData<User> = _userLiveData

    fun login(username: String, password: String) {
        // handle login using Retrofit
        appExecutors.networkIO().execute {
            loginService.login(username, password).enqueue(object : Callback<APIUser> {
                override fun onResponse(call: Call<APIUser>, response: Response<APIUser>) {
                    //store the token in the user object - available throughout the user session (until the user logs out)
                    if (response.body() != null) {
                        setUser(response.body()!!, username)
                        _loginResult.value = true

                    } else {
                        _loginResult.value = false
                    }
                }

                override fun onFailure(call: Call<APIUser>, t: Throwable) {
                    //don't store anything in token - user will not be able to continue...
                }
            })
        }
    }

    fun logout() {
        //wipe the table of the user credentials
        removeUser()
    }

    fun removeUser() {
        appExecutors.diskIO().execute {
            loginDao.deleteUser()
        }
    }

    private fun setUser(user: APIUser, username: String) {
        val applicationUser = User(
            user.user_id,
            user.user_name,
            //using a random image from lorem picsum at the moment
            "https://picsum.photos/200/300"
        )

        applicationUser.authToken = user.authToken
        Constants.authToken = user.authToken

        saveUser(applicationUser)

    }

    private fun saveUser(user: User) {
        appExecutors.diskIO().execute {
            loginDao.insertUser(user)
        }
    }

    fun proceed() {
        _loginResult.value = true

        Constants.authToken = Constants.proceedToken

        val user = APIUser(
            Constants.proceedToken,
            "anonymous@hotmail.com",
            "anonymous",
            -1
        )

        setUser(user, "anonymous")

    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}