package com.example.data.extensions

import com.example.data.businessmodel.QuestionDomainModel
import com.example.network.model.responses.QuestionResponse

fun List<QuestionResponse>?.mapToDomainModel(): List<QuestionDomainModel> {
    return this?.let { list ->
        list.map { question ->
            question.mapToDomainModel()
        }
    } ?: arrayListOf()
}

fun QuestionResponse.mapToDomainModel(): QuestionDomainModel =
    QuestionDomainModel(this.id.toString(), this.question ?: "")