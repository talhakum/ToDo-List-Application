package com.example.macbook.todo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.macbook.todo.database.entity.Todo;

import java.util.List;

public class ListActivity extends AppCompatActivity {

    private RecyclerView rcylerViewList;

    List<Todo> todoList;
    private Context mCtx;

    private Toolbar mTopToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Set custom toolbar with sort option
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);

        rcylerViewList = (RecyclerView) findViewById(R.id.rcylerViewList);

        mCtx = this;

        rcylerViewList.addOnItemTouchListener(new RecyclerItemClickListener(rcylerViewList.getContext(), rcylerViewList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent i = new Intent(mCtx, EditActivity.class);
                        i.putExtra("id", todoList.get(position).getId());
                        i.putExtra("task", todoList.get(position).getTask());
                        i.putExtra("date", todoList.get(position).getDate());
                        i.putExtra("priority", todoList.get(position).getPriority());
                        i.putExtra("eventId", todoList.get(position).getEventId());
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        // run the sentence in a new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Todo> todos = App.get().getDB().todoDao().getAll();
                populateTodos(todos);
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // action_favorite is sort icon
        // When sort icon is clicked, open pop-up menu
        switch (id) {
            case R.id.sortByPriority:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Todo> todos = App.get().getDB().todoDao().getAllByPrioritySorting();
                        populateTodos(todos);
                    }
                }).start();
                return true;
            case R.id.sortByDueDate:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<Todo> todos = App.get().getDB().todoDao().getAllByDateSorting();
                        populateTodos(todos);
                    }
                }).start();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void populateTodos(final List<Todo> todos) {
        todoList = todos;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                rcylerViewList.addItemDecoration(new DividerItemDecoration(rcylerViewList.getContext(), DividerItemDecoration.VERTICAL));

                rcylerViewList.setAdapter(new TodoAdapter(todos));
            }
        });
    }

    public void createTask(View v) {
        Intent i = new Intent(this, AddListActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update recyclerview when back to activity
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Todo> todos = App.get().getDB().todoDao().getAll();
                populateTodos(todos);
            }
        }).start();
    }
}
