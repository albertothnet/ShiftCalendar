package com.khloke.ShiftCalendar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.khloke.ShiftCalendar.database.ShiftCalendarDbOpenHelper;
import com.khloke.ShiftCalendar.objects.Shift;
import com.khloke.ShiftCalendar.utils.ColourUtils;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 18/01/13
 * Time: 9:42 PM
 */
public class ManageShiftsActivity extends Activity {

    List<Shift> mShifts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Manage Shifts");
        setContentView(R.layout.manage_shifts);

        // Set up action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        actionBar.setDisplayHomeAsUpEnabled(true);

        // TODO: Load up all current shifts
        mShifts = Shift.loadAll(this.getApplicationContext());

        ListView shiftListView = (ListView) findViewById(R.id.shiftListView);
        shiftListView.setAdapter(new ArrayAdapter<Shift>(this, android.R.layout.simple_list_item_1, mShifts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

//                TextView nameView = (TextView) getLayoutInflater().inflate(R.id.shiftTextView, parent);
//                TextView nameView = (TextView) findViewById(R.id.shiftTextView);
                TextView nameView = new TextView(ManageShiftsActivity.this);

                Shift item = getItem(position);
                String text = "<font color='" + ColourUtils.colourIntToHex(item.getColour()) + "'>" + item.getName() + "</font> " + item.getTimeFrom() + "~" + item.getTimeTo();
                nameView.setHeight(70);
                nameView.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
                nameView.setGravity(Gravity.CENTER_VERTICAL);

                return nameView;
            }
        });
        shiftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent addShift = new Intent(ManageShiftsActivity.this, AddShiftActivity.class);
                Shift shift = mShifts.get(position);
                Bundle shiftDetails = shift.toBundle();
                addShift.putExtras(shiftDetails);

                startActivity(addShift);
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
                Intent addShift = new Intent(this, AddShiftActivity.class);
                startActivity(addShift);
                return true;

            default:
                return false;
        }
    }
}
