// ApiService.kt
package com.example.finalproject.data

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    // JSON örneğin: https://www.jsonkeeper.com/b/XXXX
    @GET("b/2DAP")  // 'XXXX' -> size özel kod
    suspend fun getAllTeams(): Response<AllTeams>
}
