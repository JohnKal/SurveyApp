package com.example.storage.session

import com.example.storage.model.AnswerModel

interface SessionManagerImpl {

    fun storeAnswer(answerModel: AnswerModel): Boolean
    fun retrieveAnswer(id: String): String?
    fun getNumberAnsweredQuestions(): Int
    fun clearSession()
}