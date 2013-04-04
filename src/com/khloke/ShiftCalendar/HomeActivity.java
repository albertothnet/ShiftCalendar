package com.khloke.ShiftCalendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.khloke.ShiftCalendar.objects.ShiftCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.khloke.ShiftCalendar.utils.CalendarUtil.roundMillisToDate;

public class HomeActivity extends FragmentActivity {

    MonthCollectionPagerAdapter mMonthCollectionPagerAdapter;
    Calendar[][][] calendarDays = new Calendar[12][7][7];
    HashMap<Long, ShiftCalendar> plottedShifts = new HashMap<Long, ShiftCalendar>();
    HashMap<Integer, Fragment> currentFragments = new HashMap<Integer, Fragment>();

    ViewPager mViewPager;

    public Calendar[][][] createCalendarList() {
        Calendar[][][] result = new Calendar[12][7][7];

        for (int i = 0; i < 12; i++) {
            Calendar calendar = Calendar.getInstance();
            //Set the calendar to i - 6 months ago
            calendar.add(Calendar.MONTH, (i-6));

            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            if (dayOfMonth != 1) {
                calendar.add(Calendar.DAY_OF_YEAR, -(dayOfMonth-1));
            }

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek > 1) {
                calendar.add(Calendar.DAY_OF_YEAR, -(dayOfWeek-1));
            }

            for (int j = 0; j < 7; j++) {
                for (int k = 0; k < 7; k++) {
                    result[i][j][k] = (Calendar) calendar.clone();
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
            }
        }

        return result;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        calendarDays = createCalendarList();
        plottedShifts = ShiftCalendar.loadFromDate(this, calendarDays[0][0][0]);

        mMonthCollectionPagerAdapter = new MonthCollectionPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mMonthCollectionPagerAdapter);
        mViewPager.setCurrentItem(6);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = new Intent(this, HomeActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this)
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class MonthCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public MonthCollectionPagerAdapter(FragmentManager fm) {
            super(fm);

            currentFragments.clear();
            for (int i = 0; i < 12; i++) {
                currentFragments.put(i, createMonthFragment(i));
            }
        }

        @Override
        public Fragment getItem(int i) {

            if (currentFragments.containsKey(i)) {
                return currentFragments.get(i);
            } else {
                Fragment fragment = createMonthFragment(i);

                currentFragments.put(i, fragment);

                return fragment;
            }
        }

        @Override
        public int getCount() {
            return 12;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            calendar.add(Calendar.MONTH, position - 6);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
            return dateFormat.format(calendar.getTime());
        }
    }

    private Fragment createMonthFragment(int i) {
        Fragment fragment = new MonthFragment();
        Bundle args = new Bundle();

        args.putInt(MonthFragment.MONTH, i);
        fragment.setArguments(args);
        return fragment;
    }

    public class MonthFragment extends Fragment {

        public MonthFragment() {
        }

        public static final String MONTH = "month";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            final Integer month = (Integer) args.get(MONTH);
            long todayLong = roundMillisToDate(Calendar.getInstance().getTimeInMillis());
            int thisMonth = calendarDays[month][2][0].get(Calendar.MONTH);

            TableLayout tableLayout = (TableLayout) inflater.inflate(R.layout.fragment_calendar, container, false);
            TableRow tableRow = null;
            for (int i = 0; i < 49; i++) {

                //Create a separate table row for every 7 days (a week)
                if (i % 7 == 0) {
                    if (i > 0) {
                        tableLayout.addView(tableRow);
                    }
                    tableRow = new TableRow(HomeActivity.this);
                    tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }

                Calendar date = calendarDays[month][i / 7][i % 7];
                long dayMillis = roundMillisToDate(date.getTimeInMillis());

                //Create the date box.
                LinearLayout linearLayout = new LinearLayout(HomeActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                //Create the date text.
                TextView dayOfMonthText = new TextView(HomeActivity.this);
                dayOfMonthText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                dayOfMonthText.setGravity(Gravity.TOP);
                dayOfMonthText.setText(Long.toString(date.get(Calendar.DAY_OF_MONTH)));
                linearLayout.addView(dayOfMonthText);

                //Create the shift text layout
                TextView shiftText = new TextView(HomeActivity.this);
                shiftText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                shiftText.setGravity(Gravity.CENTER_HORIZONTAL);
                shiftText.setPadding(0, 25, 0, 0);
                shiftText.setTextSize(15);

                //Check whether a shift is plotted for this day. If true, create the Shift text.
                if (plottedShifts.containsKey(dayMillis)) {
                    ShiftCalendar shiftCalendar = plottedShifts.get(dayMillis);
                    shiftText.setTextColor(shiftCalendar.getShift().getColour());
                    shiftText.setText(shiftCalendar.getShift().getName(), TextView.BufferType.SPANNABLE);
                }
                linearLayout.addView(shiftText);

                //Colour the background depending on the whether the day is of the current month.
                linearLayout.setPadding(5, 5, 5, 5);
                if (dayMillis == todayLong) {
                    linearLayout.setBackgroundResource(R.drawable.calendar_cell_today);
                } else if (date.get(Calendar.MONTH) == thisMonth) {
                    linearLayout.setBackgroundResource(R.drawable.calendar_cell_dim);
                } else {
                    linearLayout.setBackgroundResource(R.drawable.calendar_cell_dark);
                }

                //On long press, show the shift input screen start from the date pressed.
                linearLayout.setTag(R.id.table_cell_position_tag, i);
                linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent shiftInput = new Intent(getApplicationContext(), ShiftInputActivity.class);
                        Integer position = (Integer) v.getTag(R.id.table_cell_position_tag);
                        shiftInput.putExtra(ShiftInputActivity.ARG_STARTING_DATE, calendarDays[month][position / 7][position % 7]);
                        startActivity(shiftInput);
                        return true;
                    }
                });

                //Add the date box to the table row.
                if (tableRow != null) {
                    tableRow.addView(linearLayout);
                }
            }

            return tableLayout;
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

    public void refresh() {
        if (mMonthCollectionPagerAdapter != null) {
            mMonthCollectionPagerAdapter.notifyDataSetChanged();
        }
    }
}
