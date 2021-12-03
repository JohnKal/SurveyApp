package com.example.network

import androidx.test.filters.SmallTest
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert
import org.junit.Test

@SmallTest
class QuestionsApiTest : BaseNetworkTest() {

    private lateinit var mockedResponse: String

    override fun setUp() {
        setMockServerUrl()
        mockedResponse = MockResponseFileReader("raw/question_responses.json").content
    }

    @Test
    fun `verify that API questions call actual response is not null`() = runBlocking {
            val actualResponseList = service.getQuestions()

            Assert.assertNotNull(actualResponseList)
        }

    @Test
    fun `given mock response from resources verify that API questions call actual response is the expected one`() = runBlocking {

        val actualResponseList = service.getQuestions()
        val actualResponseJson = Gson().toJson(actualResponseList.body())
        val actualResponse = JsonParser().parse(actualResponseJson)

        val expectedResponse = JsonParser().parse(mockedResponse)

        Assert.assertTrue(actualResponse.equals(expectedResponse))
    }

    @Test
    fun `given mock response with 200 code verify that API questions call response is not null or empty list`() {

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockedResponse)
        )

        runBlocking {
            val mockResponseList = mockService.getQuestions()

            Assert.assertFalse(mockResponseList.body().isNullOrEmpty())
        }
    }

    @Test
    fun `given mock response with 400 error code verify that API questions call response is not null or empty list`() {

        server.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("{}")
        )

        runBlocking {
            val mockResponseList = mockService.getQuestions()

            Assert.assertTrue(mockResponseList.body().isNullOrEmpty())
        }
    }

    override fun tearDown() {}

    override fun setMockServerUrl(): String = "/questions/"
}