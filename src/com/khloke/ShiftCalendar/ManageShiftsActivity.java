package com.khloke.ShiftCalendar;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
    ArrayAdapter<Shift> mShiftArrayAdapter;
    ListView mShiftListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Manage Shifts");
        setContentView(R.layout.manage_shifts);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // TODO: Load up all current shifts
        mShifts = Shift.loadAll(this.getApplicationContext());

        mShiftListView = (ListView) findViewById(R.id.shiftListView);
        mShiftArrayAdapter = new ArrayAdapter<Shift>(this, android.R.layout.simple_list_item_1, mShifts) {
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
        mShiftListView.setAdapter(mShiftArrayAdapter);

        mShiftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Shift shift = mShifts.get(position);
                Bundle shiftDetails = shift.toBundle();

                NewShiftDialogFragment newShiftDialogFragment = new NewShiftDialogFragment();
                newShiftDialogFragment.setArguments(shiftDetails);
                newShiftDialogFragment.show(getSupportFragmentManager(), "addShift");
            }
        });

        mShiftListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Shift shift = mShifts.get(position);
                DialogFragment dialogFragment = new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setItems(R.array.dialog_shift_actions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        shift.delete(getActivity());
                                        mShifts.remove(shift);
                                        break;
                                }
                            }
                        });

                        return builder.create();
                    }

                    @Override
                    public void onDetach() {
                        super.onDetach();
                        ((ManageShiftsActivity) getActivity()).refresh();
                    }
                };

                dialogFragment.show(getSupportFragmentManager(), "shiftActions");
                return true;
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

    public List<Shift> getShifts() {
        return mShifts;
    }

    public void refresh() {
        mShiftArrayAdapter.notifyDataSetChanged();
    }
}
