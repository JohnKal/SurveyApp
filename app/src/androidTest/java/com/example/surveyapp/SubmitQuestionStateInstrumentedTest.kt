package com.example.surveyapp

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.surveyapp.ui.main.surveylist.SurveyListFragment
import com.example.surveyapp.ui.main.surveylist.state.RenderState
import com.example.surveyapp.ui.main.surveylist.state.SubmitAnswerState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SubmitQuestionStateInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var fragment: SurveyListFragment
    private lateinit var appContext: Context

    @Before
    fun setUp() {
        hiltRule.inject()
        appContext = getApplicationContext<SurveyApplication>()
    }

    @Test
    fun assert_the_question_submit_answer_renderState_according_to_SubmitAnswerState_Loading() {
        launchSurveyListFragment(SubmitAnswerState.Loading)

        onView((withId(R.id.animationView)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView((withId(R.id.submitResultArea)))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView((withId(R.id.questionsViewPager)))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun assert_the_question_submit_answer_renderState_according_to_SubmitAnswerState_Success() {
        launchSurveyListFragment(SubmitAnswerState.Success)

        onView((withId(R.id.animationView)))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView((withId(R.id.questionsViewPager)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        // View pager checks
        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.questionText))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.editAnswerLayout))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.submitAnswerButton))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.submitAnswerButton))
            .check(matches(isNotEnabled()))


        // Result area checks
        onView((withId(R.id.submitResultArea)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView((withId(R.id.submitResultArea)))
            .check(matches(withBackgroundColor(appContext.getColor(R.color.teal_700))))

        onView((withId(R.id.submitResultText)))
            .check(matches(withText(R.string.success)))

        onView((withId(R.id.submitResultRetry)))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun assert_the_question_submit_answer_renderState_according_to_SubmitAnswerState_Error() {
        launchSurveyListFragment(SubmitAnswerState.Error(400, null))

        onView((withId(R.id.animationView)))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView((withId(R.id.questionsViewPager)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView((withId(R.id.questionsSubmitted)))
            .check(matches(withText("Questions submitted: 0")))

        // View pager checks
        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.questionText))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.editAnswerLayout))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.submitAnswerButton))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.submitAnswerButton))
            .check(matches(isNotEnabled()))


        // Result area checks
        onView((withId(R.id.submitResultArea)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView((withId(R.id.submitResultArea)))
            .check(matches(withBackgroundColor(appContext.getColor(R.color.red))))

        onView((withId(R.id.submitResultText)))
            .check(matches(withText(R.string.failure)))

        onView((withId(R.id.submitResultRetry)))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    private fun launchSurveyListFragment(renderState: RenderState) {
        launchFragmentInHiltContainer<SurveyListFragment> {
            (this as SurveyListFragment).renderState(
                state = renderState
            )
        }
    }
}