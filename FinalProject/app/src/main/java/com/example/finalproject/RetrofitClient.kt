// RetrofitClient.kt


// com.example.finalproject.data.RetrofitClient.kt
package com.example.finalproject.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Player::class.java, PlayerDeserializer())
        .create()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.jsonkeeper.com/") // API base URL'inizi buraya ekleyin
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
