package com.ourapp.iseaiapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("search")
    fun searchProjects(
        @Query("query") query: String
    ): Call<List<Project>>

    @GET("fsearch")
    fun searchFaculty(
        @Query("query") query: String
    ): Call<List<Teacher>>

    @POST("saveProject/{userId}")
    fun saveFavorite(
        @Path("userId") userId: String, // User's ID from Firebase
        @Body project: Project // Project to be saved as a favorite
    ): Call<Void>

    @DELETE("removeProject/{userId}/{projectId}")
    fun removeFavorite(
        @Path("userId") userId: String, // User's ID from Firebase
        @Path("projectId") projectId: String // ID of the project to be removed
    ): Call<Void>

    @GET("getFavoriteProjects/{userId}")
    fun getFavoriteProjects(
        @Path("userId") userId: String
    ): Call<List<Project>>

    @POST("saveTeacher/{userId}")
    fun saveFavoriteTeacher(
        @Path("userId") userId: String, // User's ID from Firebase
        @Body teacher: Teacher // Teacher to be saved as a favorite
    ): Call<Void>

    @DELETE("removeTeacher/{userId}/{teacherId}")
    fun removeFavoriteTeacher(
        @Path("userId") userId: String, // User's ID from Firebase
        @Path("teacherId") teacherId: String // ID of the Teacher to be removed
    ): Call<Void>

    @GET("getFavoriteTeacher/{userId}")
    fun getFavoriteTeacher(
        @Path("userId") userId: String
    ): Call<List<Teacher>>

    @GET("colleges")
    fun getColleges(): Call<List<College>>

}