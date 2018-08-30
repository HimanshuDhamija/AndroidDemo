package com.mytaxi.android_demo.activities;

import android.content.res.Resources;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.internal.util.Checks;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.v7.widget.AppCompatTextView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.mytaxi.android_demo.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTest {


    //Need to provide access to runtime permissions
    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    private Resources resources;
    private MainActivity mainActivity = mActivityRule.getActivity();

    @Before
    public void init(){
        resources = mActivityRule.getActivity().getResources();
    }

    @Test
    public void performLogin() throws InterruptedException{

        //Create CustomMatcher and check for the title on the Login screen.
        onView(isAssignableFrom(AppCompatTextView.class))
                .check(matches(withPageTitle(Matchers.containsString(resources.getString(R.string.title_activity_authentication)))));

        //Validate a view with id edt_username is displayed
        onView(withId(R.id.edt_username))
                .check(matches(isDisplayed()));

        //Validate a view with id edt_password is displayed
        onView(withId(R.id.edt_password))
                .check(matches(isDisplayed()));

        //Checks login button is disabled by default
        onView(withId(R.id.btn_login))
                .check(matches(not(isEnabled())));

        //Find a view with id edt_username and enter username on that view.
        onView(withId(R.id.edt_username))
                .perform(typeText(resources.getString(R.id.username)));

        // Close the keyword, otherwise for actual device LoginButton may not be visible to Espresso
        // and it could throw an exception.
        closeSoftKeyboard();

        //Find a view with id edt_password and enter password on that view.
        onView(withId(R.id.edt_password))
                .perform(typeText(resources.getString(R.id.password)));

        closeSoftKeyboard();

        // Check if button gets enabled after entering username and password
        onView(withId(R.id.btn_login))
                .check(matches(isEnabled()));

        // click on login button
        onView(withId(R.id.btn_login))
                .perform(click());

        Thread.sleep(4000);

        //check for the title - "mytaxi demo" on the main screen.
        onView(allOf(isAssignableFrom(AppCompatTextView.class)
                ,withParent(isAssignableFrom(android.support.v7.widget.Toolbar.class))))
                .check(matches(withPageTitle(Matchers.containsString(resources.getString(R.string.title_activity_main)))));

    }

    @Test
    public void searchDriver() throws InterruptedException{

    if(!mainActivity.isAuthenticated()) performLogin();

        Thread.sleep(4000);

        //check for the title - "mytaxi demo" on the main screen.
        onView(allOf(isAssignableFrom(AppCompatTextView.class),withParent(isAssignableFrom(android.support.v7.widget.Toolbar.class))))
                .check(matches(withPageTitle(Matchers.containsString(resources.getString(R.string.title_activity_main)))));

        //Type starting text to get driver names
        onView(isAssignableFrom(EditText.class))
                .perform(click())
                .perform(pressKey(KeyEvent.KEYCODE_S), pressKey(KeyEvent.KEYCODE_A));

        closeSoftKeyboard();

        //Selecting name from the autoCompleteTextView
        onView(withText(resources.getString(R.id.driverName)))
                .inRoot(withDecorView(not(is(mainActivity.getWindow().getDecorView()))))
                .perform(click());

        //Check Driver name is name as selected and is displayed
        onView(withId(R.id.textViewDriverName))
                .check(matches(withText(resources.getString(R.id.driverName))));

        //Click Call button
        onView(withId(R.id.fab)).perform(click());

    }

    //Create CustomMatcher method
    private static Matcher<View> withPageTitle(final Matcher<String> expectedTitle){
        Checks.checkNotNull(expectedTitle);
        return new BoundedMatcher<View, AppCompatTextView>(AppCompatTextView.class){

            @Override
            public void describeTo(final Description description) {
                description.appendText("with title: ");
                expectedTitle.describeTo(description);
            }

            @Override
            public boolean matchesSafely(final AppCompatTextView appCompatTextView) {
                return expectedTitle.matches(appCompatTextView.getText());
            }
        };
    }

}