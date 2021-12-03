package com.example.data.repository

import com.example.data.businessmodel.QuestionDomainModel
import com.example.data.extensions.mapToDomainModel
import com.example.network.api.ServiceEndpoints
import com.example.network.helpers.NetworkHelper
import com.example.network.helpers.ResultWrapper
import com.example.network.model.requests.SubmitQuestionRequest
import com.example.storage.model.AnswerModel
import com.example.storage.session.SessionManagerImpl
import javax.inject.Inject
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.coroutines.coroutineContext

class DataRepository @Inject constructor(
    val serviceEndpoints: ServiceEndpoints,
    val networkHelper: NetworkHelper,
    val sessionManager: SessionManagerImpl
) : DataRepositoryImpl {

    override suspend fun getQuestions(): ResultWrapper<List<QuestionDomainModel>?> =
        withContext(coroutineContext) {
            networkHelper.safeApiCall {
                getQuestionsFromService()
            }
        }

    override suspend fun getQuestionsFromService(): List<QuestionDomainModel> {
        val response = serviceEndpoints.getQuestions()

        if (!response.isSuccessful)
            throw HttpException(response)

        return response.body().mapToDomainModel()
    }

    override suspend fun submitAnswer(id: String, answer: String): ResultWrapper<Void?> =
        withContext(coroutineContext) {

            val submitQuestionRequest = SubmitQuestionRequest(id, answer)

            networkHelper.safeApiCall {
                val response = serviceEndpoints.submitAnswer(submitQuestionRequest)

                    if (!response.isSuccessful)
                        throw HttpException(response)

                response.body()
            }
        }

    override fun storeAnswer(id: String, answer: String): Boolean =
        sessionManager.storeAnswer(AnswerModel(id, answer))

    override fun retrieveAnswer(id: String): String? =
        sessionManager.retrieveAnswer(id)


    override fun getSubmittedAnswers(): Int =
        sessionManager.getNumberAnsweredQuestions()

    override fun clearStorage() {
        sessionManager.clearSession()
    }
}