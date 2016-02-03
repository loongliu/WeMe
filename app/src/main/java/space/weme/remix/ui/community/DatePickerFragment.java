package space.weme.remix.ui.community;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by Liujilong on 16/2/3.
 * liujilong.me@gmail.com
 */
public class DatePickerFragment extends DialogFragment {

    DatePickerDialog.OnDateSetListener dateSetListener;
    private int year = 1990;
    private int month = 0;
    private int day = 1;


    public void setDateSetListener(DatePickerDialog.OnDateSetListener dateSetListener) {
        this.dateSetListener = dateSetListener;
    }

    public DatePickerFragment(){
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new DatePickerDialog(getActivity(), dateSetListener, year,
                month, day);
    }
}