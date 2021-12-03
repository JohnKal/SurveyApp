package com.example.surveyapp

import android.content.res.Resources
import android.view.View
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher


class ViewPagerMatcher(val viewPagerIdId: Int) {

    fun atPosition(position: Int): Matcher<View?> {
        return atPositionOnView(position, -1)
    }

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            var resources: Resources? = null
            var childView: View? = null
            override fun describeTo(description: Description) {
                var idDescription = Integer.toString(viewPagerIdId)
                if (resources != null) {
                    idDescription = try {
                        resources?.getResourceName(viewPagerIdId)
                    } catch (var4: Resources.NotFoundException) {
                        String.format(
                            "%s (resource name not found)",
                            Integer.valueOf(viewPagerIdId)
                        )
                    }
                }
                description.appendText("with id: $idDescription")
            }

            override fun matchesSafely(view: View?): Boolean {
                resources = view?.resources
                if (childView == null) {
                    val viewPager =
                        view?.rootView?.findViewById(viewPagerIdId) as ViewPager2
                    childView = if (viewPager != null && viewPager.id == viewPagerIdId) {
                        viewPager[position]
                    } else {
                        return false
                    }
                }
                return if (targetViewId == -1) {
                    view === childView
                } else {
                    val targetView: View = childView!!.findViewById(targetViewId)
                    view === targetView
                }
            }
        }
    }
}