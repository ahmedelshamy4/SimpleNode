package com.example.ahmed.simplenode;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ahmed on 4/5/18.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.myViewHolder> {
    List<Note> noteList;
    Context context;

    public NoteAdapter(List<Note> noteList, Context context) {
        this.noteList = noteList;
        this.context = context;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_note, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        Note currentNote = noteList.get(position);

        holder.note.setText(currentNote.getNote());
        holder.timestamp.setText(formatDate(currentNote.getTimestamp()));
        holder.dot.setText(Html.fromHtml("&#8226;")); // Displaying dot from HTML character code


    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public String formatDate(String date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = simpleDateFormat.parse(date);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView note;
        TextView dot;
        TextView timestamp;

        public myViewHolder(View view) {
            super(view);

            note = view.findViewById(R.id.note);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }
}

