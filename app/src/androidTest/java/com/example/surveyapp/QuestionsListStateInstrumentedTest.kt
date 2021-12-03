package com.example.surveyapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.data.businessmodel.QuestionDomainModel
import com.example.surveyapp.ui.main.surveylist.SurveyListFragment
import com.example.surveyapp.ui.main.surveylist.state.QuestionsListState
import com.example.surveyapp.ui.main.surveylist.state.RenderState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class QuestionsListStateInstrumentedTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun assert_the_question_list_renderState_according_to_QuestionsListState_Loading() {
        launchSurveyListFragment(QuestionsListState.Loading)

       onView((withId(R.id.animationView))).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
       onView((withId(R.id.submitResultArea))).check(matches(withEffectiveVisibility(Visibility.GONE)))
       onView((withId(R.id.questionsViewPager))).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun assert_the_question_list_renderState_according_to_QuestionsListState_Success() {
        val mockedResponseList = arrayListOf(
            QuestionDomainModel("1", "What's your favourite color?", "",true),
            QuestionDomainModel("2", "What's your favourite food?", "",true)
        )

        launchSurveyListFragment(QuestionsListState.Success(mockedResponseList))

        onView((withId(R.id.animationView))).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView((withId(R.id.submitResultArea))).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView((withId(R.id.questionsViewPager))).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView((withId(R.id.questionsSubmitted)))
            .check(matches(withText("Questions submitted: 0")))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.questionText))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.questionText))
            .check(matches(withText("What's your favourite color?")))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.editAnswerLayout))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.submitAnswerButton))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.submitAnswerButton))
            .check(matches(isNotEnabled()))

        onView(ViewPagerMatcher(R.id.questionsViewPager)
            .atPositionOnView(0, R.id.editAnswerText))
            .perform(replaceText("Random text"))
            .check(matches(isEnabled()))
    }

    @Test
    fun assert_the_question_list_renderState_according_to_QuestionsListState_Error() {

        launchSurveyListFragment(QuestionsListState.Error(400, null))

        onView((withId(R.id.animationView))).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView((withId(R.id.submitResultArea))).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView((withId(R.id.questionsViewPager))).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    private fun launchSurveyListFragment(renderState: RenderState) {
        launchFragmentInHiltContainer<SurveyListFragment> {
            (this as SurveyListFragment).renderState(
                state = renderState
            )
        }
    }
}