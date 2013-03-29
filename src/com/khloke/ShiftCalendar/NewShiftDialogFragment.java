package com.khloke.ShiftCalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.khloke.ShiftCalendar.objects.Shift;
import com.khloke.ShiftCalendar.utils.ColourUtils;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: khloke
 * Date: 17/03/13
 * Time: 3:08 PM
 */
public class NewShiftDialogFragment extends DialogFragment {

    private Shift mShift;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogLayout = inflater.inflate(R.layout.dialog_new_shift, null);

        final EditText editTextName = (EditText) dialogLayout.findViewById(R.id.editTextName);
        final Spinner spinnerColour = (Spinner) dialogLayout.findViewById(R.id.spinnerColour);
        final EditText editTextTimeFrom = (EditText) dialogLayout.findViewById(R.id.editTextTimeFrom);
        final EditText editTextTimeTo = (EditText)dialogLayout.findViewById(R.id.editTextTimeTo);

        //Populate the colours in the dropdown
        initColourSpinner(dialogLayout);

        editTextTimeFrom.setOnFocusChangeListener(new TimeEditTextOnFocusListener(editTextTimeFrom));
        editTextTimeTo.setOnFocusChangeListener(new TimeEditTextOnFocusListener(editTextTimeTo));

        //If not a new shift, fill in the existing shift's info
        final Bundle shiftBundle = getArguments();
        if (shiftBundle != null) {

            mShift = Shift.fromBundle(shiftBundle);

            builder.setTitle(R.string.edit_shift_title);
            editTextName.setText(shiftBundle.getString(Shift.NAME_COLUMN));

            spinnerColour.setSelection(ColourUtils.getColourOrdinal(shiftBundle.getInt(Shift.COLOUR_COLUMN)) - 1);

            editTextTimeFrom.setText(shiftBundle.getString(Shift.TIME_FROM_COLUMN));
            editTextTimeTo.setText(shiftBundle.getString(Shift.TIME_TO_COLUMN));
        } else {
            builder.setTitle(R.string.new_shift_title);
        }

        builder
                .setPositiveButton(R.string.new_shift_button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String toastMessage;

                        if (mShift == null) {
                            mShift = new Shift(
                                    editTextName.getText().toString(),
                                    ColourUtils.getColourInt((String) spinnerColour.getSelectedItem()),
                                    editTextTimeFrom.getText().toString(),
                                    editTextTimeTo.getText().toString());
                            ((ManageShiftsActivity)getActivity()).getShifts().add(mShift);
                            toastMessage = "New Shift Saved";
                        } else {
                            ((ManageShiftsActivity)getActivity()).getShifts().remove(mShift);
                            mShift.setName(editTextName.getText().toString());
                            mShift.setColour(ColourUtils.getColourInt((String) spinnerColour.getSelectedItem()));
                            mShift.setTimeFrom(editTextTimeFrom.getText().toString());
                            mShift.setTimeTo(editTextTimeTo.getText().toString());
                            ((ManageShiftsActivity)getActivity()).getShifts().add(mShift);
                            toastMessage = "Shift Saved";
                        }

                        mShift.save(getActivity());
                        ((ManageShiftsActivity)getActivity()).refresh();
                        Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.new_shift_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(getActivity(), "Don't save it!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setView(dialogLayout);

        return builder.create();
    }

    private void initColourSpinner(View aDialogLayout) {
        Spinner spinner = (Spinner) aDialogLayout.findViewById(R.id.spinnerColour);

        CharSequence[] strings = getActivity().getResources().getTextArray(R.array.colours_array);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item, strings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(ColourUtils.getColourInt(textView.getText().toString()));
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                CheckedTextView dropDownView = (CheckedTextView) super.getDropDownView(position, convertView, parent);
                dropDownView.setTextColor(ColourUtils.getColourInt(dropDownView.getText().toString()));
                return dropDownView;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    class TimeEditTextOnFocusListener implements View.OnFocusChangeListener {

        final EditText mEditText;

        TimeEditTextOnFocusListener(EditText aEditText) {
            mEditText = aEditText;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Calendar now = Calendar.getInstance();
                new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mEditText.setText(hourOfDay + ":" + minute);
                            }
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                ).show();
            }
        }
    }
}
