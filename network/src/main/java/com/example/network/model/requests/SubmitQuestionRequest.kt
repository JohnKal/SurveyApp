package com.example.network.model.requests

import com.google.gson.annotations.SerializedName

class SubmitQuestionRequest(
    @SerializedName("id")
    val id: String?,
    @SerializedName("answer")
    val answer: String?,
)