package com.tmsfalcon.device.tmsfalcon.widgets;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.widget.NumberPicker;

import com.tmsfalcon.device.tmsfalcon.R;

/**
 * Created by Dell on 4/9/2019.
 */
public class RCStatusPicker extends DialogFragment {

    private NumberPicker.OnValueChangeListener valueChangeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final NumberPicker numberPicker = new NumberPicker(getActivity());

        final String[] values= getActivity().getResources().getStringArray(R.array.rc_status_options);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(values.length - 1);
        numberPicker.setDisplayedValues(values);
        //numberPicker.setWrapSelectorWheel(true);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       // builder.setTitle("Choose Value");
        builder.setMessage("Status");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                /*valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());*/
            }
        });

        builder.setView(numberPicker);
        return builder.create();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}