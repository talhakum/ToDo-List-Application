package com.example.macbook.todo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.macbook.todo.database.TodoDatabase;
import com.example.macbook.todo.database.entity.Todo;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity {

    private Calendar calendar;
    private EditText editTxtDate;
    private EditText editTxtTask;
    private Spinner spnPriority;

    private int year, month, day, id;
    private long eventId;
    Todo todo;

    private static final String DATABASE_NAME = "TodoDatabase";

    private TodoDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        calendar = Calendar.getInstance();

        Bundle extras = getIntent().getExtras();
        Long dateExtra = extras.getLong("date");
        if (dateExtra != null) {
            calendar.setTimeInMillis(dateExtra);
        }

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        editTxtDate = (EditText) findViewById(R.id.editTxtDateEdit);
        editTxtTask = (EditText) findViewById(R.id.editTxtTaskEdit);
        spnPriority = (Spinner) findViewById(R.id.spnPriorityEdit);

        editTxtDate.setText(day + "/" + (month + 1) + "/" + year);
        editTxtTask.setText(extras.getString("task"));
        spnPriority.setSelection(extras.getInt("priority") - 1);
        id = extras.getInt("id");
        eventId = extras.getLong("eventId");

        // create database
        database = Room.databaseBuilder(getApplicationContext(), TodoDatabase.class, DATABASE_NAME)
                .build();

        todo = new Todo();
    }

    public TodoDatabase getDB() {
        return database;
    }

    public void updateToDo(View v) {

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;
        values.put(CalendarContract.Events.TITLE, editTxtTask.getText().toString());
        values.put(CalendarContract.Events.DESCRIPTION, Integer.valueOf(spnPriority.getSelectedItem().toString()));
        values.put(CalendarContract.Events.DTSTART, calendar.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, calendar.getTimeInMillis());
        values.put(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        // Get eventId from ListActivity's extras
        // It is needed to update Calendar
        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = getContentResolver().update(updateUri, values, null, null);
        Log.i("test", String.valueOf(eventId));

        todo.setEventId(eventId);
        todo.setId(id);
        todo.setDate(calendar.getTimeInMillis());
        todo.setTask(editTxtTask.getText().toString());
        todo.setPriority(Integer.valueOf(spnPriority.getSelectedItem().toString()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDB().todoDao().update(todo);
            }
        }).start();
        Toast.makeText(getApplicationContext(), "Task updated!", Toast.LENGTH_SHORT).show();
    }

    public void sendSms(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(promptsView);

        final EditText phoneNumber = (EditText) promptsView
                .findViewById(R.id.editTxtSms);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SmsManager smsManager = SmsManager.getDefault();
                                String msgBody = "Task: " + editTxtTask.getText().toString() + "\n" + "Date: " + editTxtDate.getText().toString() + "\n" + "Priority: " + spnPriority.getSelectedItem().toString();
                                Log.i("test", msgBody);
                                smsManager.sendTextMessage(phoneNumber.getText().toString(), null, ": " + msgBody, null, null);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void sendMail(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompts_mail, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(promptsView);

        final EditText mailAddress = (EditText) promptsView
                .findViewById(R.id.editTxtMail);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String msgBody = "Task: " + editTxtTask.getText().toString() + "\n" + "Date: " + editTxtDate.getText().toString() + "\n" + "Priority: " + spnPriority.getSelectedItem().toString();

                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setData(Uri.parse("mailto:"));
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{mailAddress.getText().toString()});
                                i.putExtra(Intent.EXTRA_SUBJECT, "ToDo");
                                i.putExtra(Intent.EXTRA_TEXT, msgBody);
                                try {
                                    startActivity(Intent.createChooser(i, "Send mail..."));
                                    finish();
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(EditActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void deleteToDo(View v) {

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        int rows = getContentResolver().delete(deleteUri, null, null);

        todo.setId(id);
        todo.setDate(calendar.getTimeInMillis());
        todo.setTask(editTxtTask.getText().toString());
        todo.setPriority(Integer.valueOf(spnPriority.getSelectedItem().toString()));
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDB().todoDao().delete(todo);
            }
        }).start();
        Toast.makeText(getApplicationContext(), "Task deleted!", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
    }

    public void cancel(View v) {
        Intent i = new Intent(this, ListActivity.class);
        startActivity(i);
    }

    public void addReminder(View v) {

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, 15);
        values.put(CalendarContract.Reminders.EVENT_ID, eventId);
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
        Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);

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
