package com.example.petlodge.data

import com.example.petlodge.data.dto.ApiResponse
import com.example.petlodge.data.dto.LoginRequest
import com.example.petlodge.data.dto.LoginResponse
import com.example.petlodge.data.dto.PetRequest
import com.example.petlodge.data.dto.PetResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PetLodgeApiService {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<ApiResponse<LoginResponse>>

    @GET("pets/")
    fun getPets(): Call<ApiResponse<List<PetResponse>>>

    @POST("pets/")
    fun createPet(@Body request: PetRequest): Call<ApiResponse<PetResponse>>

    @GET("pets/{petId}")
    fun getPetById(@Path("petId") petId: Int): Call<ApiResponse<PetResponse>>

    @PUT("pets/{petId}")
    fun updatePet(
        @Path("petId") petId: Int,
        @Body request: PetRequest
    ): Call<ApiResponse<PetResponse>>

    @DELETE("pets/{petId}")
    fun deletePet(@Path("petId") petId: Int): Call<ApiResponse<Unit>>
}
