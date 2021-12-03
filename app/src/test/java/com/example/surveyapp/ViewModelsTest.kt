package com.example.surveyapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.data.businessmodel.QuestionDomainModel
import com.example.data.repository.DataRepository
import com.example.network.helpers.ResultWrapper
import com.example.surveyapp.ui.main.surveylist.SurveyListViewModel
import com.example.surveyapp.ui.main.surveylist.state.QuestionsListState
import com.example.surveyapp.ui.main.surveylist.state.SubmitAnswerState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.rules.TestRule
import java.lang.reflect.Type

@ExtendWith(MockKExtension::class)
@SmallTest
class ViewModelsTest {

    private lateinit var viewModel: SurveyListViewModel
    private lateinit var mockedResponse: String
    val dispatcher = TestCoroutineDispatcher()

    @MockK
    private lateinit var dataRepository: DataRepository

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(dispatcher)

        viewModel = SurveyListViewModel(true, dataRepository)

        mockedResponse = MockResponseFileReader("raw/question_responses.json").content
    }

    @Test
    fun `given Success from data repository verify that getQuestions should change questionsListState to Success`()  {

        val listType: Type = object : TypeToken<ArrayList<QuestionDomainModel?>?>() {}.type
        val mockedResponseList: List<QuestionDomainModel> = Gson().fromJson(mockedResponse, listType)

        every {
            runBlocking {
                dataRepository.getQuestions()
            }
        } returns ResultWrapper.Success(mockedResponseList)

        viewModel.getQuestions()

        verify(exactly = 1) { runBlocking { dataRepository.getQuestions() } }

        Assert.assertEquals(viewModel.getQuestionsListState().value, QuestionsListState.Success(mockedResponseList))
    }

    @Test
    fun `given GenericError from data repository verify that getQuestions should change questionsListState to Error`()  {

        val genericError = ResultWrapper.GenericError(400, null)

        every {
            runBlocking {
                dataRepository.getQuestions()
            }
        } returns genericError

        viewModel.getQuestions()

        verify(exactly = 1) { runBlocking { dataRepository.getQuestions() } }

        Assert.assertEquals(viewModel.getQuestionsListState().value, QuestionsListState.Error(genericError.code, genericError.error))
    }

    @Test
    fun `given NetworkError from data repository verify that getQuestions should change questionsListState to Error`()  {

        val networkError = ResultWrapper.NetworkError

        every {
            runBlocking {
                dataRepository.getQuestions()
            }
        } returns networkError

        viewModel.getQuestions()

        verify(exactly = 1) { runBlocking { dataRepository.getQuestions() } }

        Assert.assertEquals(viewModel.getQuestionsListState().value, QuestionsListState.Error())
    }

    @Test
    fun `given success from data repository verify that submitQuestion should change submitAnswerState to Success`()  {

        val success = ResultWrapper.Success<Void?>(null)

        every {
            runBlocking {
                dataRepository.submitAnswer("1", "Green")
            }
        } returns success

        viewModel.submitQuestion("1", "Green")

        verify(exactly = 1) { runBlocking { dataRepository.submitAnswer("1", "Green") } }

        Assert.assertEquals(viewModel.submitAnswerState().value, SubmitAnswerState.Success)
    }

    @Test
    fun `given genericError from data repository verify that submitQuestion should change submitAnswerState to Error`()  {

        val genericError = ResultWrapper.GenericError(400, null)

        every {
            runBlocking {
                dataRepository.submitAnswer("1", "Green")
            }
        } returns genericError

        viewModel.submitQuestion("1", "Green")

        verify(exactly = 1) { runBlocking { dataRepository.submitAnswer("1", "Green") } }

        Assert.assertEquals(viewModel.submitAnswerState().value, SubmitAnswerState.Error(genericError.code, genericError.error))
    }

    @Test
    fun `given networkError from data repository verify that submitQuestion should change submitAnswerState to Error`()  {

        val networkError = ResultWrapper.NetworkError

        every {
            runBlocking {
                dataRepository.submitAnswer("1", "Green")
            }
        } returns networkError

        viewModel.submitQuestion("1", "Green")

        verify(exactly = 1) { runBlocking { dataRepository.submitAnswer("1", "Green") } }

        Assert.assertEquals(viewModel.submitAnswerState().value, SubmitAnswerState.Error())
    }

    @Test
    fun `verify that storeAnswer should call dataRepository storeAnswer only once`()  {

        every { dataRepository.storeAnswer("1", "Green") } returns true

        viewModel.storeAnswer("1", "Green")

        verify(exactly = 1) { dataRepository.storeAnswer("1", "Green") }

        Assert.assertEquals(true, viewModel.storedAnswer.value)
    }

    @Test
    fun `verify that getSubmittedAnswers should call dataRepository getSubmittedAnswers only once and the return value is 3`()  {

        every { dataRepository.getSubmittedAnswers() } returns 3

        viewModel.getSubmittedAnswers()

        verify(exactly = 1) { dataRepository.getSubmittedAnswers() }

        Assert.assertEquals(3, viewModel.submittedQuestionsNumber.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}