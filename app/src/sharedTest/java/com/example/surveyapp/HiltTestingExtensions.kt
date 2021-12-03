package com.example.surveyapp

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.internal.util.Checks
import org.hamcrest.Description
import org.hamcrest.Matcher


inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.Theme_SurveyApp,
    fragmentFactory: FragmentFactory? = null,
    crossinline action: Fragment.() -> Unit = {}
) {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
        themeResId)

    ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        fragmentFactory?.let {
            activity.supportFragmentManager.fragmentFactory = it
        }
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )

        fragment.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()

        fragment.action()
    }
}

fun withBackgroundColor(color: Int): Matcher<View?> {
    Checks.checkNotNull(color)
    return object : BoundedMatcher<View?, ConstraintLayout>(ConstraintLayout::class.java) {
        override fun matchesSafely(layout: ConstraintLayout): Boolean {
            var colorBack: Int = Color.TRANSPARENT
            val background: Drawable = layout.background
            if (background is ColorDrawable) colorBack = background.color
            return color == colorBack
        }

        override fun describeTo(description: Description) {
            description.appendText("with background color: ")
        }
    }
}