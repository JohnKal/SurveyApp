package com.example.network.api

import com.example.network.model.requests.SubmitQuestionRequest
import com.example.network.model.responses.QuestionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServiceEndpoints {

    @GET("questions")
    suspend fun getQuestions(): Response<List<QuestionResponse>>

    @POST("question/submit")
    suspend fun submitAnswer(@Body submitQuestionRequest: SubmitQuestionRequest): Response<Void?>
}