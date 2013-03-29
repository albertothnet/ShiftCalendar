package com.khloke.ShiftCalendar;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.khloke.ShiftCalendar.objects.Shift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.khloke.ShiftCalendar.utils.StringUtils.notEmptyOrNull;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 18/01/13
 * Time: 9:42 PM
 */
public class ManageShiftsActivity extends FragmentActivity {

    List<Shift> mShifts;
    SimpleAdapter mShiftArrayAdapter;
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

        List<HashMap<String, Shift>> shiftTexts = new ArrayList<HashMap<String, Shift>>();
        for (Shift shift:mShifts) {
            HashMap<String, Shift> stringShiftHashMap = new HashMap<String, Shift>();
            stringShiftHashMap.put("shift", shift);
            shiftTexts.add(stringShiftHashMap);
        }

        mShiftArrayAdapter = new SimpleAdapter(this, shiftTexts, R.layout.shift_item, new String[]{"shift"}, new int[] {R.id.shiftNameView}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View layout = super.getView(position, convertView, parent);
                TextView nameView = (TextView) layout.findViewById(R.id.shiftNameView);

                Shift item = ((HashMap<String, Shift>)getItem(position)).get("shift");

                StringBuilder titleBuilder = new StringBuilder(item.getName());
                if (notEmptyOrNull(item.getTimeFrom()) && notEmptyOrNull(item.getTimeTo())) {
                    titleBuilder.append(" ").append(item.getTimeFrom()).append("~").append(item.getTimeTo());
                }
                nameView.setText(titleBuilder.toString());
                nameView.setTextColor(item.getColour());

                return layout;
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
                        builder.setTitle("Actions");
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
        Collections.sort(mShifts, new Shift.ShiftComparator());
        mShiftArrayAdapter.notifyDataSetChanged();
    }
}
