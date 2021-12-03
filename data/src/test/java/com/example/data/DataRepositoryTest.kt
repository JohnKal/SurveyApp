package com.example.data

import androidx.test.filters.SmallTest
import com.example.data.businessmodel.QuestionDomainModel
import com.example.data.extensions.mapToDomainModel
import com.example.data.repository.DataRepository
import com.example.data.repository.DataRepositoryImpl
import com.example.network.api.ServiceEndpoints
import com.example.network.helpers.NetworkHelper
import com.example.network.model.responses.QuestionResponse
import com.example.storage.session.SessionManagerImpl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type


@SmallTest
@RunWith(MockitoJUnitRunner::class)
class DataRepositoryTest {

    private lateinit var mockedResponse: String
    private lateinit var mockedResponseDomain: String

    @Mock
    lateinit var serviceEndpoints: ServiceEndpoints

    @InjectMocks
    lateinit var networkHelper: NetworkHelper

    @Mock
    lateinit var sessionManager: SessionManagerImpl

    lateinit var dataRepository: DataRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockedResponse = MockResponseFileReader("raw/question_responses.json").content
        mockedResponseDomain = MockResponseFileReader("raw/question_responses_domain.json").content

        dataRepository = DataRepository(serviceEndpoints, networkHelper, sessionManager)
    }

    @Test
    fun `given list of questions verify that mapToDomainModel should return a non empty list` () {

        val listType: Type = object : TypeToken<ArrayList<QuestionResponse?>?>() {}.type
        val mockedResponseList: List<QuestionResponse> = Gson().fromJson(mockedResponse, listType)

        val mappedQuestions = mockedResponseList.mapToDomainModel()

        Assert.assertFalse(mappedQuestions.isNullOrEmpty())
    }

    @Test
    fun `given an empty list of questions verify that mapToDomainModel should return an empty list`() {

        val mockedResponseList: List<QuestionResponse> = arrayListOf()

        val mappedQuestions = mockedResponseList.mapToDomainModel()

        Assert.assertTrue(mappedQuestions.isNullOrEmpty())
    }


    @Test
    fun `given a mock response verify that getQuestionsFromService should make the transformation on questions as expected`() = runBlocking {
        val listType: Type = object : TypeToken<ArrayList<QuestionResponse?>?>() {}.type
        val mockedResponseList: List<QuestionResponse> = Gson().fromJson(mockedResponse, listType)

        `when`(serviceEndpoints.getQuestions()).thenReturn(Response.success(mockedResponseList))

        val response = dataRepository.getQuestionsFromService()

        verify(serviceEndpoints, times(1)).getQuestions()

        Assert.assertTrue(response.size == 10)

        val listTypeDomain: Type = object : TypeToken<ArrayList<QuestionDomainModel?>?>() {}.type
        val mockedResponseDomainList: List<QuestionDomainModel> = Gson().fromJson(mockedResponseDomain, listTypeDomain)

        Assert.assertEquals(mockedResponseDomainList, response)
    }

    @Test(expected = HttpException::class)
    fun `given an error response verify that getQuestionsFromService should throw HttpException`() = runBlocking {

        `when`(serviceEndpoints.getQuestions()).thenReturn(Response.error(400, "{}".toResponseBody()))

        given(dataRepository.getQuestionsFromService()).willThrow(HttpException(null))

        val response = dataRepository.getQuestionsFromService()

        Assert.assertNull(response)
    }
}