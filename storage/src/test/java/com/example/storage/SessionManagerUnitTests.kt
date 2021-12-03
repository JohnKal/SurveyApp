package com.example.storage

import androidx.test.filters.SmallTest
import com.example.storage.model.AnswerModel
import com.example.storage.session.SessionManager
import io.mockk.MockKAnnotations
import io.mockk.junit5.MockKExtension
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith

@SmallTest
@ExtendWith(MockKExtension::class)
class SessionManagerUnitTests {

    private lateinit var answerModel1: AnswerModel
    private lateinit var answerModel2: AnswerModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        answerModel1 = AnswerModel("1", "Green")
        answerModel2 = AnswerModel("2", "Red")
    }

    @After
    fun tearDown() {
        SessionManager.clearSession()
    }

    @Test
    fun `given an answerModel verify that storeAnswer should return true`() {
        val actualResult = SessionManager.storeAnswer(answerModel1)

        assertTrue(actualResult)
    }

    @Test
    fun `given an id that exists in SessionManager verify that retrieveAnswer should return the correct value`() {
        val expectedValue = "Green"

        SessionManager.storeAnswer(answerModel1)
        val actualResult = SessionManager.retrieveAnswer("1")

        assertEquals(expectedValue, actualResult)
    }

    @Test
    fun `given an id that not exists in SessionManager verify that retrieveAnswer should return null`() {
        val actualResult = SessionManager.retrieveAnswer("1")

        assertNull(actualResult)
    }

    @Test
    fun `store two answers in SessionManager verify that getNumberAnsweredQuestions should return the number of answers`() {
        val expectedResult = 2

        SessionManager.storeAnswer(answerModel1)
        SessionManager.storeAnswer(answerModel2)

        val actualResult = SessionManager.getNumberAnsweredQuestions()

        assertEquals(expectedResult, actualResult)
    }

    @Test
    fun `check that clearSession it removes all the answers from the array`() {
        val expectedResult = 0

        SessionManager.storeAnswer(answerModel1)
        SessionManager.storeAnswer(answerModel2)

        SessionManager.clearSession()

        assertEquals(expectedResult, SessionManager.getNumberAnsweredQuestions())
    }
}