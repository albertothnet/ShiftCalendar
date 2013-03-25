package com.khloke.ShiftCalendar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.khloke.ShiftCalendar.objects.Shift;
import com.khloke.ShiftCalendar.objects.ShiftCalendar;
import com.khloke.ShiftCalendar.utils.CalendarUtil;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 1:57 PM
 */
public class ShiftInputActivity extends Activity {

    public static final String ARG_STARTING_DATE = "startDate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_input);

        // Set up action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Calendar now = Calendar.getInstance();

        Bundle arguments = getIntent().getExtras();
        if (arguments != null && arguments.containsKey(ARG_STARTING_DATE)) {
            Calendar startingDate = (Calendar) arguments.get(ARG_STARTING_DATE);
            now = (Calendar) startingDate.clone();
        }

        final ArrayList<View> views = new ArrayList<View>();
        List<Shift> shifts = Shift.loadAll(this.getApplicationContext());
        ListView layout = (ListView) findViewById(R.id.shiftInputScroll);
        layout.setAdapter(new ArrayAdapter<View>(this, android.R.layout.simple_list_item_1, views) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return views.get(position);
            }
        });
//        layout.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });

        HashMap<Long, ShiftCalendar> allShiftsFromDate = ShiftCalendar.loadFromDate(getApplicationContext(), now);

        LinearLayout shiftInputLabels = (LinearLayout) findViewById(R.id.shiftInputLabels);
        TextView dateLabel = new TextView(this);
        dateLabel.setText("Date");
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        dateLabel.setMinWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, displayMetrics));
        dateLabel.setGravity(Gravity.CENTER_HORIZONTAL);
        shiftInputLabels.addView(dateLabel);
//        shiftInputLabels.setBackgroundColor(Color.BLUE);
        for (Shift shift:shifts) {
            TextView shiftLabel = new TextView(this);
            shiftLabel.setText(shift.getName());
            shiftLabel.setGravity(Gravity.CENTER_HORIZONTAL);
            shiftLabel.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, displayMetrics));
//            shiftLabel.setBackgroundColor(Color.YELLOW);
            shiftInputLabels.addView(shiftLabel);
        }

        ArrayList<Integer> checkIds = new ArrayList<Integer>();
        for (int i=0; i<30; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView dateText = new TextView(this);
            String date = ShiftCalendar.DATE_FORMAT.format(now.getTime());

            dateText.setText(date);
            dateText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dateText.setGravity(Gravity.CENTER_VERTICAL);
            dateText.setMinWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, displayMetrics));
            linearLayout.addView(dateText);

            RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setOrientation(RadioGroup.HORIZONTAL);

            SecureRandom secureRandom = new SecureRandom();

            int nextId = 0;

            for (Shift shift:shifts) {

                RadioButton button = new RadioButton(this);
                button.setMinWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, displayMetrics));
                button.setGravity(Gravity.CENTER_HORIZONTAL);
//                button.setBackgroundColor(Color.YELLOW);

                button.setTag(R.id.shift_calendar_tag, allShiftsFromDate.get(CalendarUtil.roundMillisToDate(now.getTimeInMillis())));
                button.setTag(R.id.shift_input_radio_tag, shift);

                if (allShiftsFromDate.containsKey(CalendarUtil.roundMillisToDate(now.getTimeInMillis()))) {

                    ShiftCalendar shiftCal = allShiftsFromDate.get(CalendarUtil.roundMillisToDate(now.getTimeInMillis()));
                    if (shiftCal.getShift().getId() == shift.getId()) {

                        nextId = secureRandom.nextInt();
                        while (checkIds.contains(nextId) || nextId <= 0) {
                            nextId = secureRandom.nextInt();
                        }

                        button.setId(nextId);
                        checkIds.add(nextId);

                    }
                } else {
                    button.setTag(R.id.shift_calendar_tag, new ShiftCalendar(CalendarUtil.roundMillisToDate(now.getTimeInMillis()), shift));
                }

                radioGroup.addView(button);
            }

            if (nextId != 0) {
                radioGroup.check(nextId);
            }
            linearLayout.addView(radioGroup);

            views.add(linearLayout);
            now.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_shift_input, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saveShiftInput:
                save();

            case android.R.id.home:
            case R.id.cancelShiftInput:
                Intent home = new Intent(this, HomeActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                return true;


            default:
                return false;
        }
    }

    public boolean save() {
        ListView list = (ListView) findViewById(R.id.shiftInputScroll);
        int childCount = list.getChildCount();

        for (int i = 0; i < childCount; i++) {
            LinearLayout linearLayout = (LinearLayout) list.getChildAt(i);
            RadioGroup radioGroup = (RadioGroup) linearLayout.getChildAt(1);
            int shiftCount = radioGroup.getChildCount();
            for (int j = 0; j < shiftCount; j++) {
                RadioButton button = (RadioButton) radioGroup.getChildAt(j);
                if (button.isChecked()) {
                    ShiftCalendar shiftCalendar = (ShiftCalendar) button.getTag(R.id.shift_calendar_tag);
                    Shift shift = (Shift) button.getTag(R.id.shift_input_radio_tag);
                    shiftCalendar.setShift(shift);
                    if (shiftCalendar.isDirty()) {
                        shiftCalendar.save(this);
                    }
                }
            }
        }

        return true;
    }
}
