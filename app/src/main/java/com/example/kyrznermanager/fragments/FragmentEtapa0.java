package com.example.kyrznermanager.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kyrznermanager.R;
import com.example.kyrznermanager.UserActivity2;
import com.example.kyrznermanager.clases.Proovedor;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentEtapa0 extends Fragment implements Response.Listener<JSONArray>,
        Response.ErrorListener, View.OnClickListener {

    ArrayList<Proovedor> proovedores;
    ArrayList<String> stringproov, bindsql, tags;
    Button back, save;
    CardView backcv;
    View vista;
    LinearLayout containerproovedor;
    TextView idseleccion, error, seleccion;
    LinearLayout response01;
    ProgressBar progressGrabarLote;
    Spinner sproovedor;
    ListView listtags;
    String id_proov, id_lote;
    NavController navController = null;
    Proovedor select;
    RequestQueue request;
    JsonArrayRequest jsonArrayRequest;
    ArrayAdapter<Proovedor> adapter;
    ArrayAdapter<String> adaptertags;
    private OnFragmentInteractionListener mListener;

    public FragmentEtapa0() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Toast.makeText(getContext(), "Quisiste retroceder xd", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStack();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        proovedores = new ArrayList<>();
        stringproov = new ArrayList<>();
        bindsql = new ArrayList<>();
        tags= new ArrayList<>();
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_fragment_etapa0, container, false);
        //listtags = vista.findViewById(R.id.listview);
        back = vista.findViewById(R.id.btnback);
        back.setEnabled(false);
        backcv = vista.findViewById(R.id.cv_back);
        backcv.setEnabled(false);


        progressGrabarLote = vista.findViewById(R.id.progressGrabarLote);
        progressGrabarLote.setVisibility(View.GONE);
        containerproovedor = vista.findViewById(R.id.containerproovedor);
        containerproovedor.setVisibility(View.VISIBLE);
//        nombre = vista.findViewById(R.id.nombreUser);
        sproovedor = vista.findViewById(R.id.spinnerProov);
        save = vista.findViewById(R.id.btnsave);
        //tag = vista.findViewById(R.id.btntags);
        //tag.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
//        nombre.setText(UserActivity2.user.getNombres());
        error = vista.findViewById(R.id.errormessage);
        seleccion = vista.findViewById(R.id.txtSeleccion);
        idseleccion = vista.findViewById(R.id.txtidseleccion);
        error.setVisibility(View.GONE);
        response01 = vista.findViewById(R.id.response01);
        response01.setVisibility(View.VISIBLE);

        request = Volley.newRequestQueue(getContext());
        String url ="http://34.95.141.34/kyrznerApp/consultaProovedores.php";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonArrayRequest);

        save.setOnClickListener(this);
        back.setOnClickListener(this);
        backcv.setOnClickListener(this);

        return vista;
    }

    @Override
    public void onResponse(JSONArray response) {
        obtenerProovedores(response);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, proovedores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sproovedor.setAdapter(adapter);
        sproovedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select = (Proovedor) sproovedor.getSelectedItem();
                seleccion.setText(select.getDescripcion().trim());
                idseleccion.setText(select.getIdProovedor().trim());
                save.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        back.setEnabled(true);
        backcv.setEnabled(true);
        backcv.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Snackbar.make(getView(),"No hay conexión al Servidor, revise su conexión a Internet.", Snackbar.LENGTH_LONG).show();
        back.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_back:
                getActivity().onBackPressed();
                break;
            case R.id.btnsave:
                confirmarSave(select);
                break;

        }
    }

    private void confirmarSave(Proovedor p) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle("Almacenamiento");
        builder1.setMessage(p.getDescripcion()+ " escogido como proovedor.\nLa acción no se podrá deshacer.");
        builder1.setIcon(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "SI",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        containerproovedor.setVisibility(View.GONE);
                        progressGrabarLote.setVisibility(View.VISIBLE);
                        actualizarModoLectura("A","S");
                        dialog.cancel();

                    }
                });

        builder1.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void insertarLoteProov() {
        String url2 = "http://34.95.141.34/kyrznerApp/insertLoteProov.php?idproov=" + select.getIdProovedor().trim() + "&username=" + UserActivity2.user.getId_empleado();
        JsonObjectRequest obj = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                id_lote = response.optString("nextval");
                Snackbar.make(getView(), "Se ha insertado con éxito el Lote.", Snackbar.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putParcelable("proovedor", select);
                bundle.putString("lotenum",id_lote);
                navController.navigate(R.id.action_fragmentEtapa0_to_fragmentEtapa0_1, bundle);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getView(), "No se ha insertado el Lote, error." + error.toString(), Snackbar.LENGTH_LONG).show();
            }
        }) ;
                /*
                new JsonArrayRequest(Request.Method.GET, url2, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject r = response.getJSONObject(0);
                    id_lote = r.optString("nextval");
                    Snackbar.make(getView(), "Se ha insertado con éxito el Lote.", Snackbar.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("proovedor", select);
                    bundle.putString("lotenum",id_lote);
                    navController.navigate(R.id.action_fragmentEtapa0_to_fragmentEtapa0_1, bundle);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Snackbar.make(getView(), "No se ha insertado el Lote, error." + e.toString(), Snackbar.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getView(), "No se ha insertado el Lote, error." + error.toString(), Snackbar.LENGTH_LONG).show();

            }
        });*/

        /*StringRequest rsave = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Snackbar.make(getView(), "Se ha insertado con éxito el Lote.", Snackbar.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                bundle.putParcelable("proovedor", select);
                navController.navigate(R.id.action_fragmentEtapa0_to_fragmentEtapa0_1, bundle);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getView(), "No se ha insertado el Lote, error." + error.toString(), Snackbar.LENGTH_LONG).show();

            }
        });*/
        request.add(obj);
    }

    private void actualizarModoLectura(String lector, String modo) {
        String url = "http://34.95.141.34/kyrznerApp/cambiarLectorMode.php?idlector="+lector+"&idmodo="+modo.trim();
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                insertarLoteProov();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getView(), "Error para lectura de tags, vuelva a intentarlo.", Snackbar.LENGTH_SHORT).show();
            }
        });
        request.add(req);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void obtenerProovedores(JSONArray response){
        try{
            Proovedor proovedor;
            // Loop through the array elements
            if(response.length()<=0){
                error.setVisibility(View.VISIBLE);
                error.setText("No se encontraron proovedores");
                response01.setVisibility(View.GONE);


            }
            else{
                for (int i=0; i < response.length(); i++) {
                    JSONObject r = response.getJSONObject(i);
                    proovedor = crearProovedor(r);
                    proovedores.add(proovedor);
                    stringproov.add(proovedor.getIdProovedor());

                }
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }


    }

    private Proovedor crearProovedor(JSONObject r) {
        Proovedor p = new Proovedor(r.optString("id_proovedor"),
                r.optString("descripcion"));
        return p;

    }




}
