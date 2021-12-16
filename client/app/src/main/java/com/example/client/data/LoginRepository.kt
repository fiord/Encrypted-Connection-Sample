package com.example.client.data

import com.example.client.data.model.LoggedInUser
import dev.fiord.encrypted_communication_sample.data.LoginDataSource
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    private var user: LoggedInUser? = null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    @DelicateCoroutinesApi
    fun login(username: String): Result<LoggedInUser> {
        // handle login
        val result = runBlocking {
            var x: Result<LoggedInUser> = Result.failure(Exception("something is wrong"))
            GlobalScope.launch {
                x = dataSource.login(username)
            }.join()
            x
        }

        if (result.isSuccess) {
            setLoggedInUser(result.getOrThrow())
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}