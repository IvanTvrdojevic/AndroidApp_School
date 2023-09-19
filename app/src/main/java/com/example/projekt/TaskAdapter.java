package com.example.projekt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    private List<TaskData> tasklist;

    public TaskAdapter(List<TaskData> tasklist) {
        this.tasklist = tasklist;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView image;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.tvTitle);
            this.description = (TextView) itemView.findViewById(R.id.tvDescription);
            this.image = (ImageView) itemView.findViewById(R.id.ivImage);

            this.relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rlItem);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View task = layoutInflater.inflate(R.layout.rv_item_task, parent, false);
        return new ViewHolder(task);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TaskData task = tasklist.get(position);
        holder.title.setText(task.title);
        holder.description.setText(task.description);

        Glide.with(holder.itemView).load(task.image).into(holder.image);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position, task);
                }
            }
        });
    }

    public interface OnClickListener {
        void onClick(int position, TaskData task);
    }

    private OnClickListener onClickListener;
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return tasklist.size();
    }
}













