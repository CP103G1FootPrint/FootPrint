package com.example.molder.footprint;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;

public class ScheduleCreateActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener{
    private EditText shEtTripName,shEtDay ;
    private ImageButton shBtAddFriend ;
    private Button shBtCancel,shBtSave;
    private TextView shTvGroupM,shTvDatePicker;
    private int year, month, day ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_create);
        handleViews();
        showNow();
    }

    private void showNow() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        updateDisplay();
    }

    private void updateDisplay() {
        shTvDatePicker.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day)));

    }

    private String pad(int number) {
        if (number >= 10) {
            return String.valueOf(number);
        }
        else {
            return "0" + String.valueOf(number);
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int  year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        updateDisplay();

    }

    public static class DatePickerDialogFragment extends DialogFragment {
        /* 覆寫onCreateDialog()以提供想要顯示的日期挑選器 */
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            /* 呼叫getActivity()會取得此DialogFragment所依附的MainActivity物件 */
            ScheduleCreateActivity activity = (ScheduleCreateActivity) getActivity();
            /* DatePickerDialog建構式第2個參數為OnDateSetListener物件。
               因為MainActivity有實作OnDateSetListener的onDateSet方法，
               所以MainActivity物件亦為OnDateSetListener物件。
               year、month、day會成為日期挑選器預選的年月日 */
            //DatePickerDialong.getDatePicker()->DatePicker
            //DatePick.setMaxDate()/setMinDate()
            //設定最大最小可選取日期
            return new DatePickerDialog(
                    activity, activity,
                    activity.year, activity.month, activity.day);
        }
    }

    public void onDateClick(View view) {
        DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
        FragmentManager fm = getSupportFragmentManager();
        datePickerFragment.show(fm, "datePicker");
    }


    private void handleViews(){
        shEtTripName = findViewById(R.id.shEtTripName);
        shTvDatePicker = findViewById(R.id.shTvDatePicker);
        shEtDay = findViewById(R.id.shEtDay);
        shBtAddFriend = findViewById(R.id.shBtAddFriend);
        shBtCancel = findViewById(R.id.shBtCancel);
        shBtSave = findViewById(R.id.shBtSave);
        shTvGroupM = findViewById(R.id.shTvGroupM);


    }
}
