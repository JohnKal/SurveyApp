package com.example.surveyapp.ui.main.surveylist

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.DataRepositoryImpl
import com.example.network.helpers.ResultWrapper.*
import com.example.surveyapp.ui.main.surveylist.state.QuestionsListState
import com.example.surveyapp.ui.main.surveylist.state.SubmitAnswerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyListViewModel @Inject constructor(
    private val isForTesting: Boolean = false,
    private val dataRepository: DataRepositoryImpl
) : ViewModel() {

    @VisibleForTesting
    val questionsListState: MutableLiveData<QuestionsListState> = MutableLiveData()
    @VisibleForTesting
    val submitAnswerState: MutableLiveData<SubmitAnswerState> = MutableLiveData()
    @VisibleForTesting
    val submittedQuestionsNumber: MutableLiveData<Int> = MutableLiveData()
    @VisibleForTesting
    val storedAnswer: MutableLiveData<Boolean> = MutableLiveData()

    init {
        if (!isForTesting) {
            getQuestions()
        }
    }

    fun getQuestionsListState(): LiveData<QuestionsListState> = questionsListState
    fun submitAnswerState(): LiveData<SubmitAnswerState> = submitAnswerState
    fun getSubmittedQuestionsNumber(): LiveData<Int> = submittedQuestionsNumber
    fun getStoredAnswer(): LiveData<Boolean> = storedAnswer

    @VisibleForTesting
    fun getQuestions() = viewModelScope.launch {
        questionsListState.value = QuestionsListState.Loading

        val questionsResponse = dataRepository.getQuestions()

        when (questionsResponse) {
            is NetworkError -> questionsListState.value = QuestionsListState.Error()
            is GenericError ->  questionsListState.value = QuestionsListState.Error(questionsResponse.code, questionsResponse.error)
            is Success -> questionsListState.value = QuestionsListState.Success(questionsResponse.value)
        }
    }

    fun submitQuestion(id: String, answer: String) {
        viewModelScope.launch {
            submitAnswerState.value = SubmitAnswerState.Loading

            val submitAnswerResponse = dataRepository.submitAnswer(id, answer)

            when (submitAnswerResponse) {
                is NetworkError -> submitAnswerState.value = SubmitAnswerState.Error()
                is GenericError ->  submitAnswerState.value = SubmitAnswerState.Error(submitAnswerResponse.code, submitAnswerResponse.error)
                is Success -> submitAnswerState.value = SubmitAnswerState.Success
            }
        }
    }

    fun storeAnswer(id: String, answer: String) {
        storedAnswer.value = dataRepository.storeAnswer(id, answer)
    }

    fun getSubmittedAnswers() {
        submittedQuestionsNumber.value = dataRepository.getSubmittedAnswers()
    }

    fun retrieveAnswer(id: String): String? =
        dataRepository.retrieveAnswer(id)

    fun clearAnswers() {
        dataRepository.clearStorage()
    }
}