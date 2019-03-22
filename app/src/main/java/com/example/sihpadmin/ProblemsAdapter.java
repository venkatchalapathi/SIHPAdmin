package com.example.sihpadmin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class ProblemsAdapter extends RecyclerView.Adapter<ProblemsAdapter.ViewHolder> {
    Context context;
    ArrayList<ComPojo> list;
    public ProblemsAdapter(AdminDrawerActivity drawer2Activity, ArrayList<ComPojo> tempList) {
        this.context = drawer2Activity;
        list = tempList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ComPojo pojo = list.get(i);
        viewHolder.pro.setText(pojo.getProblem());
        viewHolder.loca.setText(pojo.getLattitude()+":"+pojo.getLongitude());
    }

    @Override
    public int getItemCount() {
        if (list!=null){
            return list.size();
        }else {
            return list.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pro,loca,postedby;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pro = itemView.findViewById(R.id.problem);
            loca = itemView.findViewById(R.id.latlong);
        }
    }
}
