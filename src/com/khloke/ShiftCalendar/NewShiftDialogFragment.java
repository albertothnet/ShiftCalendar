package com.khloke.ShiftCalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.khloke.ShiftCalendar.objects.Shift;
import com.khloke.ShiftCalendar.utils.ColourUtils;

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

        //Populate the colours in the dropdown
        Spinner spinner = (Spinner) dialogLayout.findViewById(R.id.spinnerColour);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.colours_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //If not a new shift, fill in the existing shift's info
        final Bundle shiftBundle = getArguments();
        if (shiftBundle != null) {

            mShift = Shift.fromBundle(shiftBundle);

            EditText nameText = (EditText) dialogLayout.findViewById(R.id.editTextName);
            nameText.setText(shiftBundle.getString(Shift.NAME_COLUMN));

            //TODO: Set to the right colour
            Spinner colourSpinner = (Spinner) dialogLayout.findViewById(R.id.spinnerColour);
            colourSpinner.setSelection(1);

            EditText fromText = (EditText) dialogLayout.findViewById(R.id.editTextTimeFrom);
            fromText.setText(shiftBundle.getString(Shift.TIME_FROM_COLUMN));

            EditText toText = (EditText) dialogLayout.findViewById(R.id.editTextTimeTo);
            toText.setText(shiftBundle.getString(Shift.TIME_TO_COLUMN));
        }

        builder
                .setTitle(R.string.new_shift_title)
                .setPositiveButton(R.string.new_shift_button_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText nameInput = (EditText) dialogLayout.findViewById(R.id.editTextName);
                        Spinner colourSpinner = (Spinner) dialogLayout.findViewById(R.id.spinnerColour);
                        EditText timeFromInput = (EditText) dialogLayout.findViewById(R.id.editTextTimeFrom);
                        EditText timeToInput = (EditText) dialogLayout.findViewById(R.id.editTextTimeTo);

                        if (shiftBundle == null) {
                            mShift = new Shift(
                                    nameInput.getText().toString(),
                                    ColourUtils.getColourInt((String) colourSpinner.getSelectedItem()),
                                    timeFromInput.getText().toString(),
                                    timeToInput.getText().toString());
                        } else {
                            mShift.setName(nameInput.getText().toString());
                            mShift.setColour(ColourUtils.getColourInt((String) colourSpinner.getSelectedItem()));
                            mShift.setTimeFrom(timeFromInput.getText().toString());
                            mShift.setTimeTo(timeToInput.getText().toString());
                        }

                        mShift.save(getActivity());
                        Toast.makeText(getActivity(), "New Shift Saved", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().recreate();
    }
}
