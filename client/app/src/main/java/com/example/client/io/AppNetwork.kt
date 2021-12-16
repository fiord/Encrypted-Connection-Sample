package com.example.client.io

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception

class AppNetwork {
     companion object {
         fun greetPost(encryptedName: String): String {
             try {
                 Log.d("Network", "send name: ${encryptedName}")

                 val client = OkHttpClient()
                 val req = Request.Builder().apply {
                     url("https://encrypted-connection-sample.herokuapp.com/greet")
                     post(FormBody.Builder()
                         .add("name", encryptedName)
                         .build())
                 }.build()

                 client.newCall(req).execute().use {
                     val str = it.body?.string()
                     Log.d("Network", "response: ${str}")
                     return str!!
                 }
             } catch (e: Exception) {
                 Log.e("AppNetwork", e.toString())
                 throw e
             }
         }
    }
}