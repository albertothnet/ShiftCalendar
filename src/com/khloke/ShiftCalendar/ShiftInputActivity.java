package com.khloke.ShiftCalendar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    HashMap<Integer, Shift> idShiftMap = new HashMap<Integer, Shift>();
    HashMap<Integer, Integer> idDateMap = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_input);

        // Set up action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        ScrollView scrollView = (ScrollView) findViewById(R.id.shiftInputScroll);
//        scrollView.setListener

        final ArrayList<View> views = new ArrayList<View>();
        List<Shift> shifts = Shift.loadAll(this.getApplicationContext());
        ListView layout = (ListView) findViewById(R.id.shiftInputScroll);
        layout.setAdapter(new ArrayAdapter<View>(this, android.R.layout.simple_list_item_1, views) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return views.get(position);
            }
        });
        layout.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                view.getScrol
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });

//        ShiftCalendar shiftsFromNow = ShiftCalendar.load(this.getApplicationContext());
        Calendar now = Calendar.getInstance();
        SecureRandom secureRandom = new SecureRandom();
//        for (String date:shiftsFromNow.getShiftMap().keySet()) {
        for (int i=0; i<30; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView dateText = new TextView(this);
            String date = ShiftCalendar.DATE_FORMAT.format(now.getTime());
            int dateId = secureRandom.nextInt();
            while (idDateMap.containsKey(dateId)) {
                dateId = secureRandom.nextInt();
            }
            dateText.setId(dateId);
            idDateMap.put(dateId, CalendarUtil.roundMillisToDate((int) now.getTimeInMillis()));
            dateText.setText(date);
            dateText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
            dateText.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.addView(dateText);

            RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setOrientation(RadioGroup.HORIZONTAL);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    for (int j = 0; j < group.getChildCount(); j++) {
                        final ToggleButton view = (ToggleButton) group.getChildAt(j);
                        view.setChecked(view.getId() == checkedId);
                    }
                }
            });

            for (Shift shift:shifts) {
                ToggleButton button = new ToggleButton(this);
                int id = secureRandom.nextInt();
                while (idShiftMap.containsKey(id)) {
                    id = secureRandom.nextInt();
                }
                button.setId(id);
                idShiftMap.put(id, shift);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((RadioGroup)v.getParent()).check(v.getId());
                    }
                });
                button.setText(shift.getName());
                button.setTextOn(shift.getName());
                button.setTextOff(shift.getName());
//                button.setTextColor(shift.getColour());

                radioGroup.addView(button);
            }
            linearLayout.addView(radioGroup);

//            layout.addView(linearLayout);
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
            TextView dateText = (TextView) linearLayout.getChildAt(0);
            RadioGroup radioGroup = (RadioGroup) linearLayout.getChildAt(1);
            int shiftCount = radioGroup.getChildCount();
            for (int j = 0; j < shiftCount; j++) {
                ToggleButton button = (ToggleButton) radioGroup.getChildAt(j);
                if (button.isChecked()) {
                    ShiftCalendar.save(this, idDateMap.get(dateText.getId()), idShiftMap.get(button.getId()).getId());
                }
            }
        }

        return true;
    }
}
