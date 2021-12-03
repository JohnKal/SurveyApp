package com.example.data.repository

import com.example.data.businessmodel.QuestionDomainModel
import com.example.network.helpers.ResultWrapper
import com.example.network.model.responses.QuestionResponse
import com.example.storage.model.AnswerModel

interface DataRepositoryImpl {

    /**
     * Get questions from API
     */
    suspend fun getQuestions(): ResultWrapper<List<QuestionDomainModel>?>

    suspend fun getQuestionsFromService(): List<QuestionDomainModel>

    /**
     * Get questions from API
     */
    suspend fun submitAnswer(id: String, answer: String): ResultWrapper<Void?>

    /**
     * Store answer to memory
     */
    fun storeAnswer(id: String, answer: String): Boolean

    /**
     * Store answer from memory
     */
    fun retrieveAnswer(id: String): String?

    /**
     * Store answer to memory
     */
    fun getSubmittedAnswers(): Int

    /**
     * Clear all answers
     */
    fun clearStorage()
}