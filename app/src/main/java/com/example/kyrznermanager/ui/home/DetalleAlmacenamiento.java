package com.example.kyrznermanager.ui.home;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kyrznermanager.R;
import com.example.kyrznermanager.UserActivity2;
import com.example.kyrznermanager.clases.Lote;
import com.example.kyrznermanager.clases.Worker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalleAlmacenamiento extends Fragment implements Response.ErrorListener, Response.Listener<JSONArray> {
    Lote lote;
    TextView txtlote;
    TextView idencargado, nomencargado, fecha, hora, restante, nomprov, idprov, cantgav, peso;
    ImageView error;
    ListView tagslv;
    ScrollView info;
    ProgressBar loadInfo;
    RequestQueue request;
    JsonArrayRequest jsonArrayRequest;
    ArrayList<String> tags;
    ArrayAdapter<String> adapterTags;

    public DetalleAlmacenamiento() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalle_almacenamiento, container, false);
        recibirBundle();
        prepararEntorno(view);

        return view;
    }

    private void prepararEntorno(View v) {
        loadInfo = v.findViewById(R.id.loadInfo);
        info = v.findViewById(R.id.containerInfo);
        error = v.findViewById(R.id.imgError);
        loadInfo.setVisibility(View.VISIBLE); info.setVisibility(View.GONE); error.setVisibility(View.GONE);
        tagslv = v.findViewById(R.id.det04lvtags);
        txtlote = v.findViewById(R.id.txtLote);
        txtlote.setText(lote.getId_lote().trim());
        idencargado = v.findViewById(R.id.det01idEmp);
        nomencargado = v.findViewById(R.id.det01Nombre);
        fecha = v.findViewById(R.id.det02fecha);
        hora = v.findViewById(R.id.det02hora);
        restante = v.findViewById(R.id.det02left);
        nomprov = v.findViewById(R.id.det03proov);
        idprov = v.findViewById(R.id.det03idproov);
        cantgav = v.findViewById(R.id.det04cangav);
        peso = v.findViewById(R.id.det04peso);
        cargarInformacion();


    }

    private void cargarInformacion() {
        request = Volley.newRequestQueue(getContext());
        String url = "http://34.95.141.34/kyrznerApp/consultaDetalleAlmacenamiento.php?idlote="+lote.getId_lote().trim();
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonArrayRequest);

    }

    private void recibirBundle() {
        Bundle b = getArguments();
        lote = (Lote) b.getSerializable("lote");
    }


    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONArray response) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        try {
            JSONArray responseInfo = response.getJSONArray(0);
            boolean e = pintarInfo(responseInfo);
            if(e){
                loadInfo.setVisibility(View.GONE); info.setVisibility(View.GONE);
                error.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Error procesando los datos, intente dentro de un momento", Toast.LENGTH_SHORT).show();

            }
            else{
                info.startAnimation(anim);
                loadInfo.setVisibility(View.GONE); info.setVisibility(View.VISIBLE);
                error.setVisibility(View.GONE);

            }

            JSONArray responseTags = response.getJSONArray(1);
            tags = new ArrayList<>();
            for (int i=0; i < responseTags.length(); i++) {
                JSONObject r = responseTags.getJSONObject(i);
                tags.add(r.optString("id_tag"));
            }
            adapterTags = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,tags);
            tagslv.setAdapter(adapterTags);



        } catch (JSONException e) {
            e.printStackTrace();
            loadInfo.setVisibility(View.GONE); info.setVisibility(View.GONE);
            error.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Error procesando los datos, intente dentro de un momento", Toast.LENGTH_SHORT).show();

        }


    }

    private boolean pintarInfo(JSONArray responseInfo) {
        try {
            idencargado.setText(responseInfo.getJSONObject(0).optString("id_worker").trim());
            nomencargado.setText(responseInfo.getJSONObject(0).optString("nombre").trim());
            fecha.setText(responseInfo.getJSONObject(0).optString("fecha_ingreso").trim());
            hora.setText(responseInfo.getJSONObject(0).optString("hora_ingreso").trim());
            String rest = calcularRestante(responseInfo.getJSONObject(0).optString("diferencia").trim());
            restante.setText(rest);
            nomprov.setText(responseInfo.getJSONObject(0).optString("descripcion").trim());
            idprov.setText(responseInfo.getJSONObject(0).optString("id_proveedor").trim());
            cantgav.setText(responseInfo.getJSONObject(0).optString("cantidad").trim());
            peso.setText(responseInfo.getJSONObject(0).optString("peso").trim());
            return false;

        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }

    }

    private String calcularRestante(String diferencia) {
        String[] s = diferencia.split(" ");
        String mensaje = "";
        int dia = Integer.parseInt(s[0]);
        int hora = Integer.parseInt(s[1]);
        int minuto = Integer.parseInt(s[2]);

        if(dia>0){
            if(dia>1){
                mensaje = dia +" días";
            }
            else{
                mensaje = dia +" día";

            }
        }

        if(hora>0){
            if(hora>1){
                mensaje = mensaje + " "+hora+" horas";
            }
            else{
                mensaje = mensaje + " "+hora+" hora";
            }
        }
        if (minuto>0){
            if(minuto>1){
                mensaje = mensaje + " "+minuto+" minutos";
            }
            else{
                mensaje = mensaje + " "+minuto+" minuto";
            }

        }
        return mensaje.trim();

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.action_exit).setVisible(false);
        menu.findItem(R.id.action_reloadFragment).setVisible(false);


        super.onCreateOptionsMenu(menu, inflater);
    }
}
