package com.example.storage.session

import androidx.collection.SparseArrayCompat
import com.example.storage.model.AnswerModel

object SessionManager : SessionManagerImpl {

    private val answersArray by lazy { SparseArrayCompat<String>() }

    override fun storeAnswer(answerModel: AnswerModel): Boolean {
        return try {
            answersArray.put(answerModel.id.toInt(), answerModel.answer)
            true
        } catch (ex: Exception) {
            false
        }
    }

    override fun retrieveAnswer(id: String): String? = answersArray[id.toInt()]

    override fun getNumberAnsweredQuestions(): Int = answersArray.size()

    override fun clearSession() {
        answersArray.clear()
    }
}