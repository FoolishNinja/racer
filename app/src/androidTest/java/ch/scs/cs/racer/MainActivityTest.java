package ch.scs.cs.racer;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import ch.scs.cs.racer.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

/**
 * Test only works on this branch, because the balance is fixed to 4500$
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.coinsText), withText("Coins:4500$"),
                        withParent(withParent(withId(R.id.container))),
                        isDisplayed()));
        textView.check(matches(withText("Coins:4500$")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.highscoreText), withText("Highscore:0m"),
                        withParent(withParent(withId(R.id.container))),
                        isDisplayed()));
        textView2.check(matches(withText("Highscore:0m")));

        ViewInteraction textView3 = onView(
                allOf(withText("Home"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        textView3.check(matches(withText("Home")));

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_shop), withContentDescription("Shop"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withText("Shop"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        textView4.check(matches(withText("Shop")));

        ViewInteraction button = onView(
                allOf(withText("2000$"),
                        childAtPosition(
                                allOf(withId(R.id.carLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                5),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.coinsText), withText("Coins:2500$"),
                        withParent(withParent(withId(R.id.container))),
                        isDisplayed()));
        textView5.check(matches(withText("Coins:2500$")));

        ViewInteraction button2 = onView(
                allOf(withText("2500$"),
                        childAtPosition(
                                allOf(withId(R.id.carLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                8),
                        isDisplayed()));
        button2.perform(click());

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.coinsText), withText("Coins:0$"),
                        withParent(withParent(withId(R.id.container))),
                        isDisplayed()));
        textView6.check(matches(withText("Coins:0$")));

        ViewInteraction button3 = onView(
                allOf(withText("4000$"),
                        childAtPosition(
                                allOf(withId(R.id.carLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                11),
                        isDisplayed()));
        button3.perform(click());

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.coinsText), withText("Coins:0$"),
                        withParent(withParent(withId(R.id.container))),
                        isDisplayed()));
        textView7.check(matches(withText("Coins:0$")));

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.navigation_home), withContentDescription("Home"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction textView8 = onView(
                allOf(withText("Home"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        textView8.check(matches(withText("Home")));

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.rightButton), withText("❯"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        1),
                                2),
                        isDisplayed()));
        materialButton.perform(click());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.rightButton), withText("❯"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        1),
                                2),
                        isDisplayed()));
        materialButton2.perform(click());

        ViewInteraction materialButton3 = onView(
                allOf(withId(R.id.leftButton), withText("❮"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        1),
                                1),
                        isDisplayed()));
        materialButton3.perform(click());

        ViewInteraction materialButton4 = onView(
                allOf(withId(R.id.leftButton), withText("❮"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        1),
                                1),
                        isDisplayed()));
        materialButton4.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
