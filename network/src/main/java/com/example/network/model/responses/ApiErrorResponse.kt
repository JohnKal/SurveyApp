package com.example.network.model.responses

import com.google.gson.annotations.SerializedName

data class ApiErrorResponse(@SerializedName("code") val code: String,
                            @SerializedName("message") val message: String,
                            @SerializedName("reason") val reason: String,
                            @SerializedName("referenceError") val referenceError: String,
                            @SerializedName("status") val status: String)