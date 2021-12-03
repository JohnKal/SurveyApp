package com.example.surveyapp

import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.example.surveyapp.ui.main.survey.SurveyFragment
import com.example.surveyapp.ui.main.survey.SurveyFragmentDirections
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@MediumTest
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class NavigationTests {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun should_changePageToList_when_clickOnButton() {
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<SurveyFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.startSurvey)).perform(click())

        val action = SurveyFragmentDirections.navigateToSurveyList()

        verify(navController).navigate(action)
    }
}