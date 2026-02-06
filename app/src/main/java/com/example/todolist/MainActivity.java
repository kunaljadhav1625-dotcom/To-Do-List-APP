package com.example.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskChangeListener {

    EditText editTask;
    Button btnAdd;
    RecyclerView recyclerView;

    ArrayList<Task> taskList;
    TaskAdapter adapter;
    SharedPreferences preferences;
    static final String KEY_TASKS = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTask = findViewById(R.id.editTask);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerView);

        taskList = new ArrayList<>();
        adapter = new TaskAdapter(taskList, this);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        preferences = getSharedPreferences("todo", MODE_PRIVATE);
        loadTasks();

        // Add task button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTask.getText().toString().trim();

                if (!title.isEmpty()) {
                    taskList.add(new Task(title, false));
                    adapter.notifyItemInserted(taskList.size() - 1);
                    saveTasks();
                    editTask.setText("");
                } else {
                    Toast.makeText(MainActivity.this,
                            "Enter a task",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onTaskChanged() {
        saveTasks();
    }

    @Override
    public void onTaskDeleted(int position) {
        if (position >= 0 && position < taskList.size()) {
            taskList.remove(position);
            adapter.notifyItemRemoved(position);
            saveTasks();
        }
    }

    private void saveTasks() {
        JSONArray jsonArray = new JSONArray();
        for (Task task : taskList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("title", task.getTitle());
                jsonObject.put("completed", task.isCompleted());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        preferences.edit().putString(KEY_TASKS, jsonArray.toString()).apply();
    }

    private void loadTasks() {
        String json = preferences.getString(KEY_TASKS, null);
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                taskList.clear();
                for (int i = 0; i < array.length(); i++) {
                    Object item = array.get(i);
                    if (item instanceof JSONObject) {
                        JSONObject obj = (JSONObject) item;
                        taskList.add(new Task(
                                obj.getString("title"),
                                obj.optBoolean("completed", false)
                        ));
                    } else if (item instanceof String) {
                        // Migrate old String-only format
                        taskList.add(new Task((String) item, false));
                    }
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
