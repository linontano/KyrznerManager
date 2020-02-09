package com.example.kyrznermanager.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kyrznermanager.MainActivity;
import com.example.kyrznermanager.R;
import com.example.kyrznermanager.UserActivity2;
import com.example.kyrznermanager.clases.Lote;
import com.example.kyrznermanager.clases.Worker;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEtapa1_1 extends Fragment implements  Response.Listener<JSONArray>, Response.ErrorListener, View.OnClickListener {
    NavController navController = null;
    Lote loteSeleccion;
    ArrayList<Worker> workers;
    ArrayList<Worker> grupoWorkers;
    ArrayAdapter<Worker> adapterworkers;
    Worker userActual;
    String id_grupo;

    ProgressBar cargando;
    CardView containerInfo, containerError;
    TextView bundleLote, bundleProov, txterror;
    ListView listWorkers;
    Button btnback, btnsave;
    CardView backcv;


    JsonArrayRequest jsonArrayRequest;
    RequestQueue request;
    StringRequest stringRequest;
    SparseBooleanArray checked;

    public FragmentEtapa1_1() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        loteSeleccion = (Lote) getArguments().getSerializable("lote");
        prepararArgumentos(view);
        consultarWebService();

    }

    private void prepararArgumentos(View view) {
        bundleLote = view.findViewById(R.id.bundleLote);
        bundleProov = view.findViewById(R.id.bundleProov);
        bundleLote.setText(loteSeleccion.getId_lote());
        bundleProov.setText(loteSeleccion.getDescripcionProov()+" ("+loteSeleccion.getId_proveedor()+")");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_etapa1_1, container, false);
        prepararEntorno(view);

        return view;
    }

    private void prepararEntorno(View view) {
        checked = new SparseBooleanArray();
        workers = new ArrayList<>();

        cargando = view.findViewById(R.id.progressPeladoE1_1);

        btnback = view.findViewById(R.id.btnback2);
        btnsave = view.findViewById(R.id.btnsave);
        backcv = view.findViewById(R.id.cv_back);
        backcv.setEnabled(false);
        btnback.setEnabled(false);
        btnsave.setEnabled(false);
        listWorkers = view.findViewById(R.id.listWorkers);
        containerInfo = view.findViewById(R.id.containerInfo);
        containerError = view.findViewById(R.id.containerError);
        cargando.setVisibility(View.VISIBLE);
        containerInfo.setVisibility(View.GONE);
        containerError.setVisibility(View.GONE);

        txterror = view.findViewById(R.id.txtErrorE11);

        btnsave.setOnClickListener(this);
        btnback.setOnClickListener(this);
        backcv.setOnClickListener(this);
    }

    private void consultarWebService() {
        request = Volley.newRequestQueue(getContext());
        String url = "http://34.95.141.34/kyrznerApp/consultaWorkers.php?idlote="+loteSeleccion.getId_lote().trim();
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonArrayRequest);
        //grabar id_grupo
        String grupo = "http://34.95.141.34/kyrznerApp/consultaGrupoWorker.php?idlote="+loteSeleccion.getId_lote().trim();
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, grupo, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject r = response.getJSONObject(0);
                    id_grupo = r.optString("id_grupo").trim();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        request.add(jsonArrayRequest);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_back:
                getActivity().onBackPressed();
                break;
            case R.id.btnsave:
                if(guardarSeleccion()){;
                    grupoWorkers.add(userActual);
                    Bundle b = new Bundle();
                    b.putSerializable("lote",loteSeleccion);
                    b.putParcelable("userActual", userActual);
                    b.putParcelableArrayList("group",grupoWorkers);
                    insertarGrupoBD();
                    b.putString("grupoId",id_grupo);
                    actualizarModoLectura("A","S");
                    navController.navigate(R.id.action_fragmentEtapa1_1_to_fragmentEtapa1_2,b);

                }
                else{
                    Toast.makeText(getContext(), "Usted debe seleccionar al menos un compañero", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    private void insertarGrupoBD() {

        for (final Worker w : grupoWorkers){
            String url = "http://34.95.141.34/kyrznerApp/insertWorkerxGrupoLote.php?idlote="+loteSeleccion.getId_lote().trim()+"&idworker="+w.getId_empleado().trim()+"&idgrupo="+id_grupo.trim();

            stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(),error.toString(), Toast.LENGTH_LONG).show();
                }
            });
            request.add(stringRequest);

        }

        Snackbar.make(getView(), "Se han insertado con éxito el grupo de trabajo", Snackbar.LENGTH_LONG).show();
        //UPDATE ID GRUPO PARA UN SIGUIENTE GRUPO DE TRABAJO
        String url2 = "http://34.95.141.34/kyrznerApp/updateIdGrupo.php?idlote="+loteSeleccion.getId_lote().trim();
        stringRequest = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        request.add(stringRequest);

    }


    private boolean guardarSeleccion() {
        grupoWorkers = new ArrayList<>();
        boolean bandera = false;
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                Worker w = adapterworkers.getItem(checked.keyAt(i));
                bandera = true;
                grupoWorkers.add(w);
            }
        }
        return bandera;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        cargando.setVisibility(View.GONE);
        containerInfo.setVisibility(View.GONE);
        containerError.setVisibility(View.VISIBLE);
        txterror.setText(getResources().getString(R.string.errorFinalizar));
        btnback.setEnabled(true);
        backcv.setEnabled(true);
        backcv.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    @Override
    public void onResponse(JSONArray response) {
        Boolean repetido = false;

        try {
            if(response.length()<=1){
                cargando.setVisibility(View.GONE);
                containerInfo.setVisibility(View.GONE);
                containerError.setVisibility(View.VISIBLE);
                txterror.setText(getResources().getString(R.string.errorNoWorkers));

            }
            else{

                btnsave.setEnabled(true);

                for (int i=0; i < response.length(); i++) {
                    JSONObject r = response.getJSONObject(i);
                    Worker w = crearWorker(r);
                    if(w.getId_empleado().equalsIgnoreCase(UserActivity2.user.getId_empleado().trim())){
                        repetido = true;
                        userActual = w;
                    }
                    else {
                        workers.add(w);
                    }
                }
                if(repetido){
                    cargando.setVisibility(View.GONE);
                    containerInfo.setVisibility(View.VISIBLE);
                    containerError.setVisibility(View.GONE);
                    //mostrarListViewNombreUser();

                    setearListView();

                }
                else{

                    cargando.setVisibility(View.GONE);
                    containerInfo.setVisibility(View.GONE);
                    containerError.setVisibility(View.VISIBLE);
                    txterror.setText(getResources().getString(R.string.errorRepetidoWorker));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            btnback.setEnabled(true);
            backcv.setEnabled(true);
        }
        btnback.setEnabled(true);
        backcv.setEnabled(true);
        backcv.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));


    }
    //TODO XML(ListView) android:choiceMode="multipleChoice"
    private void setearListView() {
        adapterworkers = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_multiple_choice,workers);
        //ArrayAdapter<Worker > adapterworkers = getAdapterListViewPersonalizado(workers);
        listWorkers.setAdapter(adapterworkers);
        listWorkers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int count = listWorkers.getCheckedItemCount();
                checked = listWorkers.getCheckedItemPositions();

            }
        });

    }
    //PRUEBAS///////////////////////////////////////////////////////////////////
    /*TUTORIAL LISTVIEW PERSONALIZABLE
    private ArrayAdapter<Worker> getAdapterListViewPersonalizado(ArrayList<Worker> workers ){
        return new ArrayAdapter<Worker>(getContext(),android.R.layout.simple_list_item_1,workers){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView txt = view.findViewById(android.R.id.text1);
                txt.setTextColor(Color.RED);
                return view;

            }
        };
    }

    private void mostrarListViewNombreUser(){
        ArrayList<Map<String, Object>> listaUsuarios = new ArrayList<>();
        int usuarios = listaUsuarios.size();
        for (Worker w: workers){
            Map<String,Object> listItem = new HashMap<>();
            listItem.put("Nombre",w.getNombre_completo());
            listItem.put("Usuario",w.getUser());
            listaUsuarios.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(),listaUsuarios,android.R.layout.simple_list_item_2,
                new String[]{"Nombre","Usuario"}, new int[]{android.R.id.text1, android.R.id.text2});
        listWorkers.setAdapter(simpleAdapter);


    }
       */
    ////////////////////////////////////////////////////////////////////////////
    private Worker crearWorker(JSONObject r) {
        Worker w = new Worker(r.optString("user").trim(),
                              r.optString("nombre").trim(),
                              r.optString("id_empleado").trim());
        return w;
    }
    private void actualizarModoLectura(String lector, String modo) {
        String url = "http://34.95.141.34/kyrznerApp/cambiarLectorMode.php?idlector="+lector+"&idmodo="+modo.trim();
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getView(), "Error no se ha actualizado la lectura de tags, vuelva a intentarlo.", Snackbar.LENGTH_SHORT).show();
            }
        });
        request.add(req);
    }
}
