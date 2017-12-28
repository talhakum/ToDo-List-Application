package com.example.macbook.todo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.macbook.todo.database.entity.Todo;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by macbook on 17/12/2017.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private final List<Todo> list;

    public TodoAdapter(List<Todo> list) {
        this.list = list;
    }

    @Override
    public TodoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_list_item, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {

        private TextView task;
        private TextView date;
        private TextView priority;

        public TodoViewHolder(View itemView) {
            super(itemView);
            task = itemView.findViewById(R.id.txtTask);
            date = itemView.findViewById(R.id.txtDate);
            priority = itemView.findViewById(R.id.txtPriority);

        }

        public void bind(Todo todo) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            calendar.setTimeInMillis(todo.getDate());

            task.setText(todo.getTask());
            try {
                date.setText(formateddate(sdf.format(calendar.getTime())));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HATA", "Date Parse Exception Error!");
            }
            priority.setText(String.valueOf(todo.getPriority()));

        }

        /**
         * It is used for showing date pretty
         */
        public static String formateddate(String date) {
            DateTime dateTime = DateTimeFormat.forPattern("dd-MMM-yyyy").parseDateTime(date);
            DateTime today = new DateTime();
            DateTime yesterday = today.minusDays(1);
            DateTime twodaysago = today.minusDays(2);
            DateTime tomorrow = today.minusDays(-1);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yy");
            date = sdf.format(dateTime.toDate());

            if (dateTime.toLocalDate().equals(today.toLocalDate())) {
                return "Today ";
            } else if (dateTime.toLocalDate().equals(yesterday.toLocalDate())) {
                return "Yesterday ";
            } else if (dateTime.toLocalDate().equals(twodaysago.toLocalDate())) {
                return "2 days ago ";
            } else if (dateTime.toLocalDate().equals(tomorrow.toLocalDate())) {
                return "Tomorrow ";
            } else {
                return date;
            }
        }
    }
}
