package alarm.ls.developer.com.ls_alarm;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by "Lakshmisagar" on 9/5/2016.
 */
public class AlarmTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    //Creating an interface to communicate from ListFrag to Preview Frag via Settings Activity
    AlTimePkrFragInterface mAlTimePkrFragInterface;
    public interface AlTimePkrFragInterface{
        public void sendAlarmTIme(String al_time);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //This is just to make sure the Settings Activty has implemented the interface
        try {
            mAlTimePkrFragInterface = (AlTimePkrFragInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TextClicked");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mAlTimePkrFragInterface = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Current time as default value
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("TimePickerValue", getActivity().MODE_PRIVATE).edit();
        editor.putInt("hour_value", hour);
        editor.putInt("minute_value", minute);
        editor.commit();

        TextView tv = (TextView) getActivity().findViewById(R.id.alarmtime_tv);
        if (hour > 12) {
            tv.setText(String.format("%02d", (hour - 12)) + ":" + String.format("%02d", (minute)) + " pm");
            mAlTimePkrFragInterface.sendAlarmTIme(String.format("%02d", (hour - 12)) + ":" + String.format("%02d", (minute)) + " pm");
        } else {
            tv.setText(String.format("%02d", (hour)) + ":" + String.format("%02d", (minute)) + " am");
            mAlTimePkrFragInterface.sendAlarmTIme(String.format("%02d", (hour)) + ":" + String.format("%02d", (minute)) + " am");
        }

    }
}