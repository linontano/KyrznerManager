package com.example.kyrznermanager.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kyrznermanager.R;
import com.example.kyrznermanager.clases.Lote;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdapterLote extends RecyclerView.Adapter<AdapterLote.ViewHolderLotes> implements View.OnClickListener {
    ArrayList<Lote> lotes;
    private View.OnClickListener listener;

    public AdapterLote() {
        this.lotes = new ArrayList<>();
    }

    public ArrayList<Lote> getLotes() {
        return lotes;
    }

    public void setLotes(ArrayList<Lote> lotes) {
        this.lotes = lotes;
    }

    public AdapterLote(ArrayList<Lote> lotes) {
        this.lotes = lotes;
    }

    @NonNull
    @Override
    public ViewHolderLotes onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lote,null,false);
        view.setOnClickListener(this);
        return new ViewHolderLotes(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderLotes holder, int position) {
        holder.numeroLote.setText(lotes.get(position).getId_lote());
        String estado = "Error";
        int a = lotes.get(position).getEtapa().intValue();
        int porcentaje=a*50/3;
        switch (a){
            case 0: estado = "Almacenamiento de Lote"; break;
            case 1: estado = "Fase de Pelado";break;
            case 2: estado = "Fase de Deshidratación";break;
            case 3: estado = "Fase de Separación";break;
            case 4: estado = "Fase de Corte";break;
            case 5: estado = "Fase de Empaque";break;
            case 6: estado = "Finalizado"; break;
        }
        holder.estadoLote.setText(estado);
        holder.progresoLote.setProgress(porcentaje);
        if(porcentaje<30){
            holder.progresoLote.getProgressDrawable().setColorFilter(
                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else{
            if(porcentaje<60){
                holder.progresoLote.getProgressDrawable().setColorFilter(
                        Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
            }
            else{
                holder.progresoLote.getProgressDrawable().setColorFilter(
                        Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }


    }

    @Override
    public int getItemCount() {
        return lotes.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    public void addAll(ArrayList<Lote> lotes){
        lotes.addAll(lotes);
        notifyDataSetChanged();


    }

    public void clear() {
        lotes.clear();
        notifyDataSetChanged();
    }

    public class ViewHolderLotes extends RecyclerView.ViewHolder {
        TextView numeroLote;
        TextView estadoLote;
        ProgressBar progresoLote;
        Button btnDetalles;

        public ViewHolderLotes(@NonNull View itemView) {
            super(itemView);
            numeroLote = itemView.findViewById(R.id.txtlote);
            estadoLote = itemView.findViewById(R.id.txtestado);
            progresoLote = itemView.findViewById(R.id.barestado);
            btnDetalles = itemView.findViewById(R.id.btndetalle);

        }
    }
}
