package com.example.surveyapp.ui.main.surveylist.state

import com.example.data.businessmodel.QuestionDomainModel
import com.example.network.model.responses.ApiErrorResponse

sealed class QuestionsListState : RenderState {
    object Loading : QuestionsListState()
    data class Success(val questionsResponse: List<QuestionDomainModel>?) : QuestionsListState()
    data class Error(val code: Int? = null, val error: ApiErrorResponse? = null) : QuestionsListState()
}