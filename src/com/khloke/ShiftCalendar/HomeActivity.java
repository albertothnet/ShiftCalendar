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
import com.khloke.ShiftCalendar.utils.CalendarUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class HomeActivity extends FragmentActivity {

    MonthCollectionPagerAdapter mMonthCollectionPagerAdapter;
    ArrayList<ArrayList<Calendar>> calendarDays;
//    HashMap<Integer, GridView> currentGridViews = new HashMap<Integer, GridView>();
    HashMap<Long, ShiftCalendar> plottedShifts = new HashMap<Long, ShiftCalendar>();
    HashMap<Integer, Fragment> currentFragments = new HashMap<Integer, Fragment>();

    ViewPager mViewPager;

    public static ArrayList<ArrayList<Calendar>> createCalendarList() {
        ArrayList<ArrayList<Calendar>> fullYearCal = new ArrayList<ArrayList<Calendar>>();

        for (int i = 0; i < 12; i++) {
            ArrayList<Calendar> month = new ArrayList<Calendar>();

            Calendar calendar = Calendar.getInstance();
            //Set the calendar to i - 6 months ago
            calendar.add(Calendar.MONTH, (i-6));

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

            while (calendar.get(Calendar.DAY_OF_MONTH) != calendar.getActualMinimum(Calendar.DAY_OF_MONTH)) {
                //Do nothing
            }

            calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));

            for (int j = 0; j < 49; j++) {

                month.add((Calendar) calendar.clone());
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
        plottedShifts = ShiftCalendar.loadFromDate(this, calendarDays.get(0).get(0));

        mMonthCollectionPagerAdapter = new MonthCollectionPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mMonthCollectionPagerAdapter);
        mViewPager.setCurrentItem(6);
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
            long todayLong = CalendarUtil.roundMillisToDate(Calendar.getInstance().getTimeInMillis());
            int thisMonth = calendarDays.get(month).get(15).get(Calendar.MONTH);

            TableLayout tableLayout = (TableLayout) inflater.inflate(R.layout.fragment_calendar, container, false);
            TableRow tableRow = null;
            for (int i = 0; i < 49; i++) {
                if (i % 7 == 0) {
                    if (i > 0) {
                        tableLayout.addView(tableRow);
                    }
                    tableRow = new TableRow(HomeActivity.this);
                    tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }

                Calendar date = calendarDays.get(month).get(i);
                LinearLayout linearLayout = new LinearLayout(HomeActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);

                TextView dayOfMonthText = new TextView(HomeActivity.this);
                dayOfMonthText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                dayOfMonthText.setGravity(Gravity.TOP);
                dayOfMonthText.setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));
                linearLayout.addView(dayOfMonthText);

                TextView shiftText = new TextView(HomeActivity.this);
                shiftText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                shiftText.setGravity(Gravity.CENTER_HORIZONTAL);
                shiftText.setPadding(0, 25, 0, 0);
                shiftText.setTextSize(15);
                long dayMillis = CalendarUtil.roundMillisToDate(date.getTimeInMillis());
                if (plottedShifts.containsKey(dayMillis)) {
                    ShiftCalendar shiftCalendar = plottedShifts.get(dayMillis);
                    shiftText.setTextColor(shiftCalendar.getShift().getColour());
                    shiftText.setText(shiftCalendar.getShift().getName(), TextView.BufferType.SPANNABLE);
                }
                linearLayout.setPadding(5, 5, 5, 5);
                if (dayMillis == todayLong) {
                    linearLayout.setBackgroundResource(R.drawable.calendar_cell_today);
                } else if (date.get(Calendar.MONTH) == thisMonth) {
                    linearLayout.setBackgroundResource(R.drawable.calendar_cell_dim);
                } else {
                    linearLayout.setBackgroundResource(R.drawable.calendar_cell_dark);
                }
                linearLayout.addView(shiftText);
                linearLayout.setTag(R.id.table_cell_position_tag, i);
                linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent shiftInput = new Intent(getApplicationContext(), ShiftInputActivity.class);
                        shiftInput.putExtra(ShiftInputActivity.ARG_STARTING_DATE, calendarDays.get(month).get((Integer) v.getTag(R.id.table_cell_position_tag)));
                        startActivity(shiftInput);
                        return true;
                    }
                });
                tableRow.addView(linearLayout);
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
}
