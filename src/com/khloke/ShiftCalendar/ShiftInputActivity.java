package com.khloke.ShiftCalendar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.khloke.ShiftCalendar.objects.Shift;
import com.khloke.ShiftCalendar.objects.ShiftCalendar;

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

        List<Shift> shifts = Shift.loadAll(this.getApplicationContext());
        ViewGroup layout = (ViewGroup) findViewById(R.id.shiftInputLayout);
        ShiftCalendar shiftsFromNow = ShiftCalendar.load(this.getApplicationContext());
        for (String date:shiftsFromNow.getShiftMap().keySet()) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView dateText = new TextView(this);
            dateText.setText(date);
            linearLayout.addView(dateText);

            for (Shift shift:shifts) {
                Button button = new Button(this);
                button.setText(shift.getName());
                linearLayout.addView(button);
            }

            layout.addView(linearLayout);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent home = new Intent(this, HomeActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                return true;

            default:
                return false;
        }
    }
}
