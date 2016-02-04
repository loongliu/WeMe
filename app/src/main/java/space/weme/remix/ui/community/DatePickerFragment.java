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


    public void setDateSetListener(DatePickerDialog.OnDateSetListener dateSetListener) {
        this.dateSetListener = dateSetListener;
    }

    public DatePickerFragment(){
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = 1990;
        int month = 0;
        int day = 1;
        return new DatePickerDialog(getActivity(), dateSetListener, year,
                month, day);
    }
}