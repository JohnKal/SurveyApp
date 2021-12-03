package com.example.network

import androidx.test.filters.SmallTest
import com.example.network.helpers.NetworkHelper
import com.example.network.helpers.ResultWrapper
import com.example.network.model.responses.ApiErrorResponse
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@SmallTest
class NetworkHelperTest : BaseNetworkTest() {

    private lateinit var networkHelper: NetworkHelper

    override fun setUp() {
        networkHelper = NetworkHelper()
    }

    @Test
    fun `given true mock response verify that safeApiCall should emit the result as success`() {
        runBlockingTest {
            val mockResult = true
            val result = networkHelper.safeApiCall { mockResult }

            Assert.assertEquals(ResultWrapper.Success(mockResult), result)
        }
    }

    @Test
    fun `throw IOException verify that safeApiCall should emit the result as NetworkError`() {
        runBlockingTest {

            val result = networkHelper.safeApiCall { throw IOException() }

            Assert.assertEquals(ResultWrapper.NetworkError, result)
        }
    }

    @Test
    fun `throw HttpException verify that safeApiCall should emit the result as GenericError with server code 400`() {
        val errorBody = "".toResponseBody("application/json".toMediaTypeOrNull())

        runBlockingTest {

            val result = networkHelper.safeApiCall {
                throw HttpException(Response.error<Any>(400, errorBody))
            }

            Assert.assertEquals(ResultWrapper.GenericError(400, null), result)
        }
    }

    @Test
    fun `throw IllegalStateException verify that safeApiCall should emit the result as GenericError`() {
        runBlockingTest {

            val result = networkHelper.safeApiCall {
                throw IllegalStateException()
            }

            Assert.assertEquals(ResultWrapper.GenericError(), result)
        }
    }

    override fun tearDown() {}

    override fun setMockServerUrl(): String = ""
}