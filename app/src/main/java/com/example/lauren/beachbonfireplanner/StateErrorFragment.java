package com.example.lauren.beachbonfireplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Lauren on 12/17/2015.
 */
public class StateErrorFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Error")
                .setMessage("This city/state combination is incorrect. Please re-enter city and state.")
                .setPositiveButton(context.getString(R.string.error_ok_button_text), null);

        AlertDialog dialog = builder.create();
        return dialog;

    }
}
