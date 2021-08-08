package com.example.youtube_clone.service

import com.example.youtube_clone.Dto.VideoDto
import retrofit2.Call
import retrofit2.http.GET

interface VideoService {

    @GET("/v3/15a561e3-b44d-4f44-b50e-519735e8cbac")
    fun getVideoList(): Call<VideoDto>
}