package dev.fiord.encrypted_communication_sample.data

import android.util.Base64
import android.util.Log
import com.example.client.data.model.LoggedInUser
import com.example.client.io.AppNetwork
import java.io.IOException
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.LoggingMXBean
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private fun encrypt(username: String): String {
        try {
            val decodedKey = KEY.toByteArray(StandardCharsets.UTF_8)
            val key: SecretKey = SecretKeySpec(decodedKey, "AES")
            val cipher = Cipher.getInstance("AES_128/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv.copyOf()
            if (iv.size != GCM_NONCE_SIZE) {
                Log.w("encrypt", "nonce size is different. expected: ${GCM_NONCE_SIZE}, actual: ${iv.size}")
            }
            val param = username.toByteArray(StandardCharsets.UTF_8)
            val cipherText = cipher.doFinal(param) // cipherText + tag
            if (cipherText.size != username.length + GCM_TAG_LENGTH) {
                Log.w("encrypt", "cipherText + TAG length is different. expected: ${username.length + GCM_TAG_LENGTH}, actual: ${cipherText.size}")
            }
            return Base64.encodeToString(iv + cipherText, Base64.NO_WRAP)
        } catch (e: Exception) {
            Log.e("encrypt", e.toString())
            throw e
        }
    }

    private fun decrypt(response: String): String {
        try {
            val decodedKey = KEY.toByteArray(StandardCharsets.UTF_8)
            val key: SecretKey =
                SecretKeySpec(decodedKey, "AES")
            val cipher = Cipher.getInstance("AES_128/GCM/NoPadding")
            val param: ByteArray =
                Base64.decode(response.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            val iv = param.copyOfRange(0, GCM_NONCE_SIZE)
            val encrypted = param.copyOfRange(GCM_NONCE_SIZE, param.size)

            val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val plainText = cipher.doFinal(encrypted)
            return String(plainText, Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e("decrypt", e.toString())
            throw e
        }
    }

    fun login(username: String?): Result<LoggedInUser> {
        return try {
            val name: String = username ?: "test_user"
            val encrypted = encrypt(name)
            val response = AppNetwork.greetPost(encrypted)
            Log.v("login", "response: ${response}")
            val decrypted = decrypt(response)
            val fakeUser = LoggedInUser(
                UUID.randomUUID().toString(),
                decrypted
            )
            Result.success<LoggedInUser>(fakeUser)
        } catch (e: Exception) {
            Result.failure<LoggedInUser>(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }

    companion object {
        private const val AES_KEY_SIZE = 16
        private const val GCM_NONCE_SIZE = 12
        private const val GCM_TAG_LENGTH = 16
        private const val KEY = "FLAG{5ecret_ke4}"
    }
}