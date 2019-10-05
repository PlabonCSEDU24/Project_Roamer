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

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private static clickListener clickListener;
    private Context mContext;
    private Cursor mCursor;
    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener onItemClickListener;
    Bus bus;
    ArrayList<Bus> busArrayList=new ArrayList<>();

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
        bus=new Bus(Integer.parseInt(mCursor.getString(0)),mCursor.getString(1),Integer.parseInt(mCursor.getString(2)));
        busArrayList.add(bus);
        holder.busName.setText(bus.getBusName());
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView busName;
        public ViewHolder(View itemView) {
            super(itemView);
            busName=itemView.findViewById(R.id.textItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            clickListener.onItemClick(getLayoutPosition(),v,busArrayList.get(getAdapterPosition()).getRoadId(),busArrayList.get(getAdapterPosition()).getBusName());
        }
    }
    public interface clickListener{
        void onItemClick(int position,View view,int roadId,String busName);
    }
    public void setOnItemClickListener(clickListener clickListener){
        Adapter.clickListener=clickListener;
    }
}
