package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> taskList;
    private OnTaskChangeListener listener;

    public interface OnTaskChangeListener {
        void onTaskChanged();
        void onTaskDeleted(int position);
    }

    public TaskAdapter(ArrayList<Task> taskList, OnTaskChangeListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textTask.setText(task.getTitle());
        holder.checkCompleted.setChecked(task.isCompleted());

        holder.checkCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            listener.onTaskChanged();
        });

        holder.btnDelete.setOnClickListener(v -> {
            listener.onTaskDeleted(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkCompleted;
        TextView textTask;
        MaterialButton btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkCompleted = itemView.findViewById(R.id.checkCompleted);
            textTask = itemView.findViewById(R.id.textTask);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
