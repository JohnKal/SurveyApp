package com.example.surveyapp

import android.view.View
import androidx.test.filters.SmallTest
import com.example.surveyapp.extensions.gone
import com.example.surveyapp.extensions.invisible
import com.example.surveyapp.extensions.visible
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@SmallTest
class ViewExtensionsTests {

    @Mock
    lateinit var view: View

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `given a mock view verify method visible() should change the view visibility to VISIBLE`() {
        view.visible()

        verify(view).visibility = View.VISIBLE
    }

    @Test
    fun `given a mock view verify method invisible() should change the view visibility to INVISIBLE`() {
        view.invisible()

        verify(view).visibility = View.INVISIBLE
    }

    @Test
    fun `given a mock view verify method gone() should change the view visibility to GONE`() {
        view.gone()

        verify(view).visibility = View.GONE
    }
}