package com.example.petlodge.data

import com.example.petlodge.data.dto.ApiResponse
import com.example.petlodge.data.dto.LoginRequest
import com.example.petlodge.data.dto.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PetLodgeApiService {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<ApiResponse<LoginResponse>>
}
