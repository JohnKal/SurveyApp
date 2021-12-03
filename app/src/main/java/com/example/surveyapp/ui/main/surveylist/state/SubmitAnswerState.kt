package com.example.surveyapp.ui.main.surveylist.state

import com.example.network.model.responses.ApiErrorResponse

sealed class SubmitAnswerState : RenderState {
    object Loading : SubmitAnswerState()
    object Success : SubmitAnswerState()
    data class Error(val code: Int? = null, val error: ApiErrorResponse? = null) : SubmitAnswerState()
}