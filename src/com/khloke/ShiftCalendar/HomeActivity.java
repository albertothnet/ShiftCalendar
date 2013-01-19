package com.khloke.ShiftCalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    MonthCollectionPagerAdapter mMonthCollectionPagerAdapter;
    ArrayList<ArrayList<String>> calendarDays;
    HashMap<Integer, GridView> currentGridViews = new HashMap<Integer, GridView>();

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;

    public static ArrayList<ArrayList<String>> createCalendarList() {
        ArrayList<ArrayList<String>> fullYearCal = new ArrayList<ArrayList<String>>();

        for (int i = 0; i < 12; i++) {
            ArrayList<String> month = new ArrayList<String>();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, i);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            while (calendar.get(Calendar.MONTH) != i || calendar.get(Calendar.DAY_OF_MONTH) != calendar.getActualMinimum(Calendar.DAY_OF_MONTH)) {
                //Do nothing
            }
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));

            for (int j = 0; j < 42; j++) {

                month.add(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            fullYearCal.add(month);
        }

        return fullYearCal;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        calendarDays = createCalendarList();

        // Create an adapter that when requested, will return a fragment representing an object in
        // the collection.
        //
        // ViewPager and its adapters use support library fragments, so we must use
        // getSupportFragmentManager.
        mMonthCollectionPagerAdapter = new MonthCollectionPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mMonthCollectionPagerAdapter);
        mViewPager.setCurrentItem(Calendar.getInstance().get(Calendar.MONTH));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, HomeActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    HashMap<Integer, Fragment> currentFragments = new HashMap<Integer, Fragment>();

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public class MonthCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public MonthCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {

            if (currentFragments.containsKey(i)) {
                return currentFragments.get(i);
            } else {
                Fragment fragment = createCalendarFragment(i);

                currentFragments.put(i, fragment);

                if (i-1 >= Calendar.JANUARY) {
                    currentFragments.put(i-1, createCalendarFragment(i-1));
                }
                if (i+1 <= Calendar.DECEMBER) {
                    currentFragments.put(i+1, createCalendarFragment(i+1));
                }

                return fragment;
            }
        }

        @Override
        public int getCount() {
            // For this contrived example, we have a 100-object collection.
            return 12;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.set(Calendar.MONTH, position);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
            return dateFormat.format(calendar.getTime());
        }
    }

    private Fragment createCalendarFragment(int i) {
        Fragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putInt(CalendarFragment.MONTH, i);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public class CalendarFragment extends Fragment {

        public CalendarFragment() {
        }

        public static final String MONTH = "month";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            Bundle args = getArguments();
            Integer month = (Integer) args.get(MONTH);

//            if (currentGridViews.containsKey(month)) {
//                return currentGridViews.get(month);
//            } else {
                GridView gridView = (GridView) inflater.inflate(R.layout.fragment_calendar, container, false);
    //            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
    //                    Integer.toString(args.getInt(ARG_OBJECT)));
                gridView.setAdapter(new CalendarAdapter(month));
//                currentGridViews.put(month, gridView);
//
//                if (month - 1 >= 0) {
//                    GridView gridView1 = (GridView) inflater.inflate(R.layout.fragment_calendar, container, false);
//                    gridView1.setAdapter(new CalendarAdapter(month-1));
//                    currentGridViews.put(month-1, gridView1);
//                }
//
//                if (month + 1 <= Calendar.DECEMBER) {
//                    GridView gridView1 = (GridView) inflater.inflate(R.layout.fragment_calendar, container, false);
//                    gridView1.setAdapter(new CalendarAdapter(month+1));
//                    currentGridViews.put(month+1, gridView1);
//                }

                return gridView;

//            }
        }
    }

    public class CalendarAdapter extends BaseAdapter {

        private final int mMonth;

        public CalendarAdapter(int aMonth) {
            mMonth = aMonth;
        }

        public int getCount() {
            return 42;
        }

        public Object getItem(int position) {
            return calendarDays.get(position);
//            return calendarDates.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
//            ListView listView = new ListView(HomeActivity.this);
//            listView.ad
            TextView textView = new TextView(HomeActivity.this);
            textView.setText(calendarDays.get(mMonth).get(position));
//            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            textView.setText(calendarDates.get(position));
            return textView;
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manageShiftAction:
                Intent manageShifts = new Intent(this, ManageShiftsActivity.class);
                startActivity(manageShifts);
                return true;

            case R.id.inputShiftAction:
                Intent shiftInput = new Intent(this, ShiftInputActivity.class);
                startActivity(shiftInput);
                return true;

            default:
                return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }
}
