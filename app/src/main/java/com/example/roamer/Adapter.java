package com.example.roamer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener onItemClickListener;

    public Adapter(Context context,Cursor cursor){
        mContext=context;
        mCursor=cursor;
        mInflater=LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=mInflater.inflate(R.layout.custom_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(!mCursor.moveToPosition(position)){
            return;
        }
        String name=mCursor.getString(1);
        holder.busName.setText(name);
    }

    @Override
    public int getItemCount() {

        return mCursor.getCount();
    }
    public void swapCursor(Cursor newCursor){
        if(mCursor!=null){
            mCursor.close();
        }
        mCursor=newCursor;
        if(newCursor!=null){
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView busName;
        public ViewHolder(View itemView) {
            super(itemView);
            busName=itemView.findViewById(R.id.textItem);
        }
    }
}
