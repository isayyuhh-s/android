package slugapp.com.ucscstudentapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

import io.fabric.sdk.android.Fabric;
import slugapp.com.ucscstudentapp.R;
import slugapp.com.ucscstudentapp.interfaces.ActivityCallback;
import slugapp.com.ucscstudentapp.models.BottomToolbarButton;
import slugapp.com.ucscstudentapp.models.Date;
import slugapp.com.ucscstudentapp.fragments.EventListFragment;
import slugapp.com.ucscstudentapp.enums.MonthEnum;

/**
 * Created by isaiah on 6/27/2015.
 * <p/>
 * This file contains the MainActivity. It handles all of the pages of the app in the form of
 * Fragments, and contains Top and Bottom Toolbars.
 */

public class MainActivity extends AppCompatActivity implements ActivityCallback {

    /**
     * Fields
     */
    private FragmentManager fm;
    private TextView title;
    private Timer timer;
    private Gson gson;
    private boolean init = true;

    private static final String TWITTER_KEY = "pkpaLGZDDFZyBViV2ScOOcz2R";
    private static final String TWITTER_SECRET = "8GqvJRMgLgbQpphUKfnUx7WLZaK2iRHxZ0VU27uYwtO1GrT82a";

    /**
     * On activity created
     *
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This is the Twitter Authorization for fabric to use Twitter services in the app
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        this.gson = new Gson();

        // sets views
        setContentView(R.layout.activity_main);
        setTopToolbar();
        setBottomToolbar();
        setFragment(new EventListFragment());
    }

    /**
     * Initializes top toolbar
     */
    private void setTopToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        this.title = (TextView) findViewById(R.id.toolbar_title);
    }

    /**
     * Initializes bottom toolbar
     */
    private void setBottomToolbar() {
        LinearLayout bottom = (LinearLayout) findViewById(R.id.bottom_toolbar);
        View child = getLayoutInflater().inflate(R.layout.toolbar_bottom, bottom, false);

        // Bottom Buttons
        BottomToolbarButton events_button = (BottomToolbarButton) child.findViewById(R.id.events_button);
        BottomToolbarButton dining_button = (BottomToolbarButton) child.findViewById(R.id.dining_button);
        BottomToolbarButton map_button = (BottomToolbarButton) child.findViewById(R.id.map_button);
        events_button.setImageResource(R.drawable.ic_events);
        dining_button.setImageResource(R.drawable.ic_dining);
        map_button.setImageResource(R.drawable.ic_map);
        BottomToolbarButton.setIds(events_button, dining_button, map_button); //, social_button, settings_button);
        bottom.addView(child);
        events_button.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_on));
    }

    /**
     * Get current fragment manager
     *
     * @return Current fragment manager
     */
    @Override
    public FragmentManager fm() {
        return fm;
    }

    /**
     * Sets the current fragment
     *
     * @param fragment Fragment to set
     */
    @Override
    public void setFragment(Fragment fragment) {
        this.fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.listFragment, fragment);
        if (!this.init) ft.addToBackStack(null);
        ft.commit();
        this.init = false;
    }

    /**
     * Set bottom toolbar buttons
     *
     * @param buttonId Id of button to set
     */
    @Override
    public void setButtons(int buttonId) {
        findViewById(R.id.events_button).setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_off));
        findViewById(R.id.dining_button).setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_off));
        findViewById(R.id.map_button).setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_off));
        findViewById(buttonId).setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_on));
    }

    /**
     * Hide soft keyboard
     */
    @Override
    public void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Get today's date
     *
     * @return Today's date
     */
    @Override
    public Date getToday() {
        java.util.Date date = Calendar.getInstance().getTime();
        String month = new SimpleDateFormat("MM").format(date);
        String day = new SimpleDateFormat("dd").format(date);
        String hour = new SimpleDateFormat("hh").format(date);
        String tod = new SimpleDateFormat("aa").format(date);
        String todayMonth = MonthEnum.JANUARY.getVal();
        for (MonthEnum currMonth : MonthEnum.values()) {
            if (currMonth.getOrder() == Integer.valueOf(month)) {
                todayMonth = currMonth.getVal();
                break;
            }
        }
        String string = todayMonth + " " + day + " " + hour + tod + " " + hour + tod;
        Date today = new Date(string);
        return today;
    }

    /**
     * Set new toolbar title
     *
     * @param newTitle New toolbar title
     */
    @Override
    public void setTitle(String newTitle) {
        this.title.setText(newTitle);
    }

    /**
     * Initializes Timer
     */
    @Override
    public void initTimer() {
        this.timer = new Timer();
    }

    /**
     * Get timer
     *
     * @return Timer
     */
    @Override
    public Timer getTimer() {
        return this.timer;
    }

    /**
     * Get resource string
     *
     * @param id Id of resource
     * @return String
     */
    @Override
    public String toStr(int id) {
        return this.getResources().getString(id);
    }

    /**
     * Get resource bitmap
     *
     * @param id Id of bitmap
     * @return Bitmap
     */
    @Override
    public BitmapDescriptor toBitMap(int id) {
        return BitmapDescriptorFactory.fromResource(id);
    }

    /**
     * Get Gson
     *
     * @return Gson
     */
    @Override
    public Gson getGson() {
        return this.gson;
    }
}
