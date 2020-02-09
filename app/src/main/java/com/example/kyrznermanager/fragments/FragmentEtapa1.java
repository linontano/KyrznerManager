package com.example.kyrznermanager.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kyrznermanager.R;
import com.example.kyrznermanager.UserActivity1;
import com.example.kyrznermanager.clases.Lote;
import com.example.kyrznermanager.clases.Proovedor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEtapa1 extends Fragment implements Response.Listener<JSONArray>, Response.ErrorListener, View.OnClickListener {
    ArrayList<Lote> lotes;
    Lote seleccion;

    NavController navController = null;

    CardView containerInfo, containerError;
    CardView backcv;
    ProgressBar progressPeladoE1;
    Button btnback, btnnext;
    Spinner spinnerLote;
    TextView detallesLote, detallesProov, detallesGav, detallesIngreso, txterror;

    ArrayAdapter<Lote> adapter;

    JsonArrayRequest jsonArrayRequest;
    RequestQueue request;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);



    }

    public FragmentEtapa1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_etapa1, container, false);
        prepararEntorno(view);
        consultarWebService();


        return view;
    }

    private void consultarWebService() {
        request = Volley.newRequestQueue(getContext());
        String url = "http://34.95.141.34/kyrznerApp/consultaLotexProceso.php?etapa=0";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonArrayRequest);

    }

    private void prepararEntorno(View view) {
        lotes = new ArrayList<>();

        spinnerLote = view.findViewById(R.id.spinnerLote);
        detallesLote = view.findViewById(R.id.detallesIdLote);
        detallesProov = view.findViewById(R.id.detalleProov);
        detallesGav = view.findViewById(R.id.detalleGav);
        detallesIngreso = view.findViewById(R.id.detalleIngreso);
        txterror = view.findViewById(R.id.txterrore1);

        progressPeladoE1 = view.findViewById(R.id.progressPeladoE1);
        containerInfo = view.findViewById(R.id.containerInfo);
        containerError = view.findViewById(R.id.containerError);
        btnnext = view.findViewById(R.id.btnSiguiente01);
        btnback = view.findViewById(R.id.btnback);
        btnback.setEnabled(false); btnnext.setEnabled(false);
        backcv = view.findViewById(R.id.cv_back);
        backcv.setEnabled(false);
        progressPeladoE1.setVisibility(View.VISIBLE);
        containerInfo.setVisibility(View.GONE);
        containerError.setVisibility(View.GONE);
        btnback.setOnClickListener(this); btnnext.setOnClickListener(this);
        backcv.setOnClickListener(this);
    }

    @Override
    public void onResponse(JSONArray response) {
        try {
            if(response.length()<=0){
                progressPeladoE1.setVisibility(View.GONE);
                containerInfo.setVisibility(View.GONE);
                containerError.setVisibility(View.VISIBLE);
                txterror.setText(getResources().getString(R.string.txtNohayInfo));

                btnback.setEnabled(true);
            }
            else{
                for (int i=0; i < response.length(); i++) {
                    JSONObject r = response.getJSONObject(i);
                    Lote l = crearLote(r);
                    l.setCantidadPelado(r.optInt("tagpelado"));
                    lotes.add(l);
                }
                progressPeladoE1.setVisibility(View.GONE);
                containerInfo.setVisibility(View.VISIBLE);
                containerError.setVisibility(View.GONE);
                btnback.setEnabled(true);
                setearSpinner();
            }
            backcv.setEnabled(true);
            backcv.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressPeladoE1.setVisibility(View.GONE);
        containerInfo.setVisibility(View.GONE);
        containerError.setVisibility(View.VISIBLE);

        backcv.setEnabled(true);
        backcv.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btnback.setEnabled(true);
    }

    private void setearSpinner() {
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lotes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLote.setAdapter(adapter);
        btnnext.setEnabled(true);
        spinnerLote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seleccion = (Lote) spinnerLote.getSelectedItem();
                detallesGav.setText(seleccion.getCantidadPelado().toString()+"/"+seleccion.getCantidad().toString());
                detallesLote.setText(seleccion.getId_lote());
                detallesProov.setText(seleccion.getDescripcionProov()+" ("+seleccion.getId_proveedor()+")");
                String fechaingreso;
                fechaingreso = convertirDateString(seleccion.getFecha_ingreso());
                detallesIngreso.setText(fechaingreso);

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
    }

    private String convertirDateString(Date fecha_ingreso) {
        DateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        String fechaFormateada = formatoFecha.format(fecha_ingreso) + " " + formatoHora.format(fecha_ingreso);
        return fechaFormateada;
    }

    private Lote crearLote(JSONObject object)  {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

        Date ingreso;
        Date salida = null;
        Integer etapa = Integer.parseInt(object.optString("etapa").trim());
        try {
            ingreso = formatter.parse(object.optString("fecha_ingreso").trim());
            String ssalida = object.optString("fecha_salida").trim();
            if (!ssalida.equalsIgnoreCase("null")){
                salida = formatter.parse(ssalida);
            }


        } catch (ParseException e) {
            e.printStackTrace();
            ingreso = Calendar.getInstance().getTime();

        }

        Lote l = new Lote(object.optString("estado").trim(),
                object.optString("id_proveedor").trim(),
                object.optString("id_lote").trim(),
                etapa,
                ingreso,
                salida,
                object.optString("descripcion").trim(),
                Integer.parseInt(object.optString("cantidad").trim()));


        return l;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_back:
                getActivity().onBackPressed();
                break;
            case R.id.btnSiguiente01:

                Bundle b = new Bundle();
                b.putSerializable("lote",seleccion);

                navController.navigate(R.id.action_fragmentEtapa1_to_fragmentEtapa1_1,b);
        }

    }

}
