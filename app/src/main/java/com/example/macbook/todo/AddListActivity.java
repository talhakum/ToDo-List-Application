package com.example.macbook.todo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macbook.todo.database.TodoDatabase;
import com.example.macbook.todo.database.entity.Todo;

import java.util.Calendar;

public class AddListActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Calendar calendar;
    private EditText editTxtDate;
    private EditText editTxtTask;
    private Spinner spnPriority;
    private CheckBox checkboxReminder;
    private int year, month, day;

    Todo todo;

    private PendingIntent pendingIntent;

    private static final String DATABASE_NAME = "TodoDatabase";

    private TodoDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        editTxtDate = (EditText) findViewById(R.id.editTxtDate);
        editTxtTask = (EditText) findViewById(R.id.editTxtTask);
        spnPriority = (Spinner) findViewById(R.id.spnPriority);
        checkboxReminder = (CheckBox) findViewById(R.id.checkboxReminder);

        // create database
        database = Room.databaseBuilder(getApplicationContext(), TodoDatabase.class, DATABASE_NAME)
                .build();

        todo = new Todo();
    }

    public TodoDatabase getDB() {
        return database;
    }

    public void saveToDo(View v) {

        long calID = 1;
        ContentResolver cr = AddListActivity.this.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, calendar.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE, editTxtTask.getText().toString());
        values.put(CalendarContract.Events.DESCRIPTION, Integer.valueOf(spnPriority.getSelectedItem().toString()));
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().getID());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        Log.i("test", String.valueOf(eventID));

        todo.setEventId(eventID);
        todo.setDate(calendar.getTimeInMillis());
        todo.setTask(editTxtTask.getText().toString());
        todo.setPriority(Integer.valueOf(spnPriority.getSelectedItem().toString()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDB().todoDao().insert(todo);
            }
        }).start();

        if (checkboxReminder.isChecked()) {
            cr = getContentResolver();
            values = new ContentValues();
            values.put(CalendarContract.Reminders.MINUTES, 15);
            values.put(CalendarContract.Reminders.EVENT_ID, eventID);
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
        }

        Intent myIntent = new Intent(AddListActivity.this, MyReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AddListActivity.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(getApplicationContext(), "Task saved!", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(AddListActivity.this, ListActivity.class);
        startActivity(i);
    }

    public void cancel(View v) {
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int selectedYear, int selectedMonth, int selectedDay) {
                    // TODO Auto-generated method stub
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    editTxtDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                }
            };
}
