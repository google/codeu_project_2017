package com.google.codeu.chatme.view.message;

import android.support.test.rule.ActivityTestRule;

import com.google.codeu.chatme.R;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;

public class MessagesActivityTest {

    @Rule
    public ActivityTestRule<MessagesActivity> activityTestRule
            = new ActivityTestRule<>(MessagesActivity.class);

    @Test
    public void testIfSendMessageBtnDisabledOnActivityStart() {
        onView(withId(R.id.btnSend))
                .check(matches(not(isEnabled())));
    }

    @Test
    public void testSendMessageBtnStateChange() {
        onView(withId(R.id.etTypeMessage))
                .perform(typeText("Dummy message"));
        onView(withId(R.id.btnSend))
                .check(matches(isEnabled()));

        onView(withId(R.id.etTypeMessage))
                .perform(clearText());
        onView(withId(R.id.btnSend))
                .check(matches(not(isEnabled())));
    }
}
