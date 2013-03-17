package com.khloke.ShiftCalendar;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.khloke.ShiftCalendar.objects.Shift;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 18/01/13
 * Time: 9:42 PM
 */
public class ManageShiftsActivity extends FragmentActivity {

    List<Shift> mShifts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Manage Shifts");
        setContentView(R.layout.manage_shifts);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // TODO: Load up all current shifts
        mShifts = Shift.loadAll(this.getApplicationContext());

        ListView shiftListView = (ListView) findViewById(R.id.shiftListView);
        ArrayAdapter<Shift> shiftArrayAdapter = new ArrayAdapter<Shift>(this, android.R.layout.simple_list_item_1, mShifts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                TextView nameView = new TextView(ManageShiftsActivity.this);

                Shift item = getItem(position);
                nameView.setText(item.getName() + " " + item.getTimeFrom() + "~" + item.getTimeTo());
                nameView.setTextColor(item.getColour());
                nameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
                nameView.setHeight(80);
                nameView.setPadding(30, 0, 30, 0);
                nameView.setGravity(Gravity.CENTER_VERTICAL);

                return nameView;
            }
        };
        shiftListView.setAdapter(shiftArrayAdapter);

        shiftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Shift shift = mShifts.get(position);
                Bundle shiftDetails = shift.toBundle();

                NewShiftDialogFragment newShiftDialogFragment = new NewShiftDialogFragment();
                newShiftDialogFragment.setArguments(shiftDetails);
                newShiftDialogFragment.show(getSupportFragmentManager(), "addShift");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_manage_shifts, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent home = new Intent(this, HomeActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                return true;

            case R.id.addShiftAction:
                new NewShiftDialogFragment().show(getSupportFragmentManager(), "addShift");
                return true;

            default:
                return false;
        }
    }
}
