package com.khloke.ShiftCalendar;

import android.app.*;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.khloke.ShiftCalendar.database.ShiftCalendarDbOpenHelper;
import com.khloke.ShiftCalendar.objects.Shift;
import com.khloke.ShiftCalendar.utils.ColourUtils;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 19/01/13
 * Time: 1:28 AM
 */
public class AddShiftActivity extends Activity {

    private boolean isNew = true;
    private Shift mShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Manage Shifts");
        setContentView(R.layout.add_shift);

        // Set up action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Spinner spinner = (Spinner) findViewById(R.id.colourSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.colours_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Bundle shiftBundle = getIntent().getExtras();
        if (shiftBundle != null) {
            isNew = false;
            mShift = Shift.fromBundle(shiftBundle);

            EditText nameText = (EditText) findViewById(R.id.nameText);
            nameText.setText(shiftBundle.getString(Shift.NAME_COLUMN));

            //TODO: Set to the right colour
            Spinner colourSpinner = (Spinner) findViewById(R.id.colourSpinner);
            colourSpinner.setSelection(1);

            EditText fromText = (EditText) findViewById(R.id.fromText);
            fromText.setText(shiftBundle.getString(Shift.TIME_FROM_COLUMN));

            EditText toText = (EditText) findViewById(R.id.toText);
            toText.setText(shiftBundle.getString(Shift.TIME_TO_COLUMN));
        } else {
            mShift = new Shift();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_shift, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addShiftDone:
                setShiftValues();
                if (isNew) {
                    boolean success = saveNewShift();
                    if (!success) {
                        return false;
                    }
                } else {
                    ShiftCalendarDbOpenHelper dbOpenHelper = new ShiftCalendarDbOpenHelper(this);
                    SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
                    ContentValues contentValues = mShift.toContentValues();
                    db.update(Shift.TABLE_NAME, contentValues, "id="+String.valueOf(mShift.getId()), null);
                }

            case android.R.id.home:
            case R.id.addShiftCancel:
                Intent intent = new Intent(this, ManageShiftsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;

            default:
                return false;
        }
    }

    public void showTimePicker(View aView) {
        if (aView instanceof EditText) {
            TimePickerFragment timePickerFragment = new TimePickerFragment((EditText) aView);
            timePickerFragment.show(getFragmentManager(), "timepicker");
        }

    }

    public void setShiftValues() {
        EditText nameText = (EditText) findViewById(R.id.nameText);
        String name = nameText.getText().toString();

        Spinner colourSpinner = (Spinner) findViewById(R.id.colourSpinner);
        String color = (String) colourSpinner.getSelectedItem();

        EditText fromText = (EditText) findViewById(R.id.fromText);
        String from = fromText.getText().toString();

        EditText toText = (EditText) findViewById(R.id.toText);
        String to = toText.getText().toString();

        mShift.setName(name);
        mShift.setColour(ColourUtils.getColourInt(color));
        mShift.setTimeFrom(from);
        mShift.setTimeTo(to);

    }

    public boolean saveNewShift() {
        if (mShift.isValid()) {
            mShift.save(this);
            Toast.makeText(this, "Shift saved", Toast.LENGTH_SHORT).show();

            return true;
        } else {

            return false;
        }
    }

    public class TimePickerFragment extends DialogFragment
                                implements TimePickerDialog.OnTimeSetListener {

        private final EditText mEditText;

        public TimePickerFragment(EditText aEditText) {
            mEditText = aEditText;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hourString = String.valueOf(view.getCurrentHour());
            String minuteString = String.valueOf(view.getCurrentMinute());
            mEditText.setText(hourString + ":" + minuteString);
        }
    }
}
