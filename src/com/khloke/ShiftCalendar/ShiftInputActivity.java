package com.khloke.ShiftCalendar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.khloke.ShiftCalendar.objects.Shift;
import com.khloke.ShiftCalendar.objects.ShiftCalendar;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 1:57 PM
 */
public class ShiftInputActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shift_input);

        // Set up action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        ScrollView scrollView = (ScrollView) findViewById(R.id.shiftInputScroll);
//        scrollView.setListener

        List<Shift> shifts = Shift.loadAll(this.getApplicationContext());
        ViewGroup layout = (ViewGroup) findViewById(R.id.shiftInputLayout);
        ShiftCalendar shiftsFromNow = ShiftCalendar.load(this.getApplicationContext());
        Calendar now = Calendar.getInstance();
//        for (String date:shiftsFromNow.getShiftMap().keySet()) {
        for (int i=0; i<30; i++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView dateText = new TextView(this);
            String date = ShiftCalendar.DATE_FORMAT.format(now.getTime());
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
            SecureRandom secureRandom = new SecureRandom();

            for (Shift shift:shifts) {
                ToggleButton button = new ToggleButton(this);
                button.setId(secureRandom.nextInt());
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

            layout.addView(linearLayout);
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
}
