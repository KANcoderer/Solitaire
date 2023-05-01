package edu.psu.solitaire;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DisplayNewGameDialog extends DialogFragment {
    public interface onDialogListener{
        GameActivity getActivity();
    }
    GameActivity ga;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        ga=((onDialogListener) context).getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Game")
                .setMessage("Do you want to lose your progress?")
                .setPositiveButton("Yes", (dialog, id) -> {
                    ga.reset();
                })

                .setNegativeButton("No", (dialog, id) -> {

                });


        return builder.create();
    }
}
