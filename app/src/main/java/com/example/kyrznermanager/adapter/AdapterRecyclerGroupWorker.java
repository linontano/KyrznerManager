package com.example.kyrznermanager.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyrznermanager.R;

import java.util.ArrayList;

public class AdapterRecyclerGroupWorker extends  RecyclerView.Adapter<AdapterRecyclerGroupWorker.ViewHolder>{
    private static final String TAG = "AdapterRecyclerGroupWor";
    private ArrayList<ArrayList<String>> responseWorkers  = new ArrayList<>();
    private Context mContext;


    public AdapterRecyclerGroupWorker(Context mContext, ArrayList<ArrayList<String>> responseWorkers) {
        this.responseWorkers = responseWorkers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grupoitem, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder: called");
        holder.txtgrupo.setText(responseWorkers.get(position).get(0));
        holder.txtintegrantes.setText(responseWorkers.get(position).get(1));
        holder.txtgavetas.setText(responseWorkers.get(position).get(2));
    }

    @Override
    public int getItemCount() {
        return responseWorkers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtgavetas, txtintegrantes, txtgrupo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtgrupo = itemView.findViewById(R.id.txtNumeroGrupo);
            txtgavetas = itemView.findViewById(R.id.cantidadGavetas);
            txtintegrantes = itemView.findViewById(R.id.cantidadWorkers);

        }
    }
}
