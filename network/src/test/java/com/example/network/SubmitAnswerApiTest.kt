package com.example.network

import androidx.test.filters.SmallTest
import com.example.network.model.requests.SubmitQuestionRequest
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Assert
import org.junit.Test

@SmallTest
class SubmitAnswerApiTest : BaseNetworkTest() {

    override fun setUp() {
        setMockServerUrl()
    }

    @Test
    fun `given request body verify that submitAnswer should set up the request params as expected`() {

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
        )

        runBlocking {
            val expectedRequestBody = "{\"id\":\"1\",\"answer\":\"Green\"}"
            val submitQuestionRequest = SubmitQuestionRequest("1", "Green")
            mockService.submitAnswer(submitQuestionRequest)

            val actualRequestBody = server.takeRequest().body.readUtf8()

            Assert.assertEquals(expectedRequestBody, actualRequestBody)
        }
    }

    @Test
    fun `given mock response with 200 error code verify that submitAnswer call response is an empty list`() {

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{}")
        )

        runBlocking {
            val submitQuestionRequest = SubmitQuestionRequest("1", "Green")
            val mockResponseList = mockService.submitAnswer(submitQuestionRequest)

            Assert.assertEquals(200, mockResponseList.code())
            Assert.assertNull(mockResponseList.body())
        }
    }

    @Test
    fun `given mock response with 400 error code verify that submitAnswer call response is an empty list`() {

        server.enqueue(
            MockResponse()
                .setResponseCode(400)
                .setBody("{}")
        )

        runBlocking {
            val submitQuestionRequest = SubmitQuestionRequest("1", "Green")
            val mockResponseList = mockService.submitAnswer(submitQuestionRequest)

            Assert.assertEquals(400, mockResponseList.code())
            Assert.assertNull(mockResponseList.body())
        }
    }

    override fun tearDown() {}

    override fun setMockServerUrl(): String = "/question/submit/"
}