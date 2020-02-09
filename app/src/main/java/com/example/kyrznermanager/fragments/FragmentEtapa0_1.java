package com.example.kyrznermanager.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kyrznermanager.R;
import com.example.kyrznermanager.UserActivity1;
import com.example.kyrznermanager.UserActivity2;
import com.example.kyrznermanager.clases.Proovedor;
import com.google.android.material.snackbar.Snackbar;
import com.example.kyrznermanager.clases.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentEtapa0_1 extends Fragment implements View.OnClickListener, Response.Listener<JSONArray>, Response.ErrorListener {
    NavController navController = null;
    TextView proovedor, idproovedor, cantidadGavetas;
    Button btncancelar, btnleertags, btnguardar, btnfinalizar;
    Proovedor seleccion;
    ListView listtags;
    ArrayList<Tag> tags;
    CardView cancelarcv;

    StringRequest stringRequest;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue request;
    RequestQueue request2;
    String lote;
    public FragmentEtapa0_1() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        request = Volley.newRequestQueue(getContext());
        prepararEntorno(view);
        seleccion = getArguments().getParcelable("proovedor");
        lote = "LOT"+getArguments().getString("lotenum");
        proovedor.setText(seleccion.getDescripcion().trim());
        idproovedor.setText(seleccion.getIdProovedor().trim());


    }

    private void prepararEntorno(View view) {
        proovedor = view.findViewById(R.id.txtproovedor);
        idproovedor = view.findViewById(R.id.txtidproovedor);

        cancelarcv = view.findViewById(R.id.cv_cancelar);
        cancelarcv.setEnabled(true);
        cantidadGavetas = view.findViewById(R.id.txtnumerotags);
        btncancelar = view.findViewById(R.id.btnCancelar);
        btnleertags = view.findViewById(R.id.btntags);
        btnguardar = view.findViewById(R.id.btnguardar);
        btnfinalizar = view.findViewById(R.id.btnfinalizar);
        btnfinalizar.setVisibility(View.GONE);
        btnguardar.setVisibility(View.GONE);

        listtags = view.findViewById(R.id.lvtags);

        cancelarcv.setOnClickListener(this);
        btncancelar.setOnClickListener(this);
        btnleertags.setOnClickListener(this);
        btnfinalizar.setOnClickListener(this);
        btnguardar.setOnClickListener(this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_etapa0_1, container, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_cancelar:
                confirmarEliminar();
                break;

            case R.id.btntags:
                cantidadGavetas.setText("Leyendo...");
                leerTags();
                break;
            case R.id.btnguardar:
                cargarTags();
                break;
            case R.id.btnfinalizar:
                mostrarResumen();
                break;
        }
    }

    private void mostrarResumen() {
        AlertDialog.Builder builder = new  AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View popup = inflater.inflate(R.layout.popup_finalizare0, null);
        builder.setView(popup);
        builder.setNeutralButton(
                "FINALIZAR",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        navController.navigate(R.id.action_fragmentEtapa0_1_to_fragmentHome22);
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        final LinearLayout containerMessage = popup.findViewById(R.id.containerInfo);
        final LinearLayout containerError = popup.findViewById(R.id.containerError);
        containerError.setVisibility(View.GONE);
        containerMessage.setVisibility(View.VISIBLE);
        final TextView txtLote = popup.findViewById(R.id.txtLote);
        final TextView txtidproov = popup.findViewById(R.id.txtidproov);
        final TextView txtnogavetas = popup.findViewById(R.id.txtnogavetas);
        final ProgressBar progressFin = popup.findViewById(R.id.progressFinalizar);
        containerMessage.setVisibility(View.GONE);
        containerError.setVisibility(View.GONE);
        progressFin.setVisibility(View.VISIBLE);
        String url = "http://34.95.141.34/kyrznerApp/consultaLoteActual.php?idlote="+lote.trim()+"&gavetas="+tags.size();
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject object = response.getJSONObject(0);
                            txtLote.setText(object.optString("id_lote").trim());
                            txtidproov.setText(object.optString("descripcion").trim()+" ("+object.optString("id_proveedor").trim()+")");
                            txtnogavetas.setText(object.optString("cantidad").trim());
                            containerMessage.setVisibility(View.VISIBLE);
                            containerError.setVisibility(View.GONE);
                            progressFin.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressFin.setVisibility(View.GONE);
                            containerMessage.setVisibility(View.GONE);
                            containerError.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressFin.setVisibility(View.GONE);
                containerMessage.setVisibility(View.GONE);
                containerError.setVisibility(View.VISIBLE);
            }
        });
        request.add(jsonArrayRequest);
        alert.show();



    }

    private void cargarTags() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle("Almacenamiento");
        builder1.setMessage("¿Desea cargar los siguientes "+tags.size()+" tags para el presente lote?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "SI",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        cargarTagsxLote();
                        btnguardar.setEnabled(false);
                        btnfinalizar.setEnabled(true);
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

    private void cargarTagsxLote() {
        request2 = Volley.newRequestQueue(getContext());
        for (Tag tag : tags){
            String url = "http://34.95.141.34/kyrznerApp/insertTagxLote.php?idtag="+tag.getId().trim()+"&idlote="+lote.trim();

            stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                }
            });
            request2.add(stringRequest);

        }
        Snackbar.make(getView(), "Se han insertado con éxito "+ tags.size()+" tags del Lote.", Snackbar.LENGTH_LONG).show();
        btnleertags.setEnabled(false);
        actualizarModoLectura2("A","N");



    }


    private void confirmarEliminar() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle("Almacenamiento");
        builder1.setMessage("¿Desea cancelar el registro del lote actual?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "SI",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        actualizarModoLectura("A","N");
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

    private void leerTags() {
        tags = new ArrayList<>();
        request = Volley.newRequestQueue(getContext());
        String url = "http://34.95.141.34/kyrznerApp/consultaTagsRecientes.php";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonArrayRequest);



    }

    private void ejecutarDeleteLote() {
        String url = "http://34.95.141.34/kyrznerApp/deleteLoteProov.php";
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Snackbar.make(getView(), "Se ha eliminado con éxito el Lote.", Snackbar.LENGTH_LONG).show();

                navController.navigate(R.id.action_fragmentEtapa0_1_to_fragmentHome22);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getView(),"No se ha podido eliminar el Lote, error."+error.toString(), Snackbar.LENGTH_LONG).show();

            }
        });
        request.add(stringRequest);
    }

    private void actualizarModoLectura(String lector, String modo) {
        String url = "http://34.95.141.34/kyrznerApp/cambiarLectorMode.php?idlector="+lector+"&idmodo="+modo.trim();
        StringRequest req = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ejecutarDeleteLote();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getView(), "Error para actualizar lectura de tags, vuelva a intentarlo.", Snackbar.LENGTH_SHORT).show();
            }
        });
        request.add(req);
    }

    private void actualizarModoLectura2(String lector, String modo) {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmarEliminar();


            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        cantidadGavetas.setText(getResources().getString(R.string.InfocantidadGavetas));
        btnguardar.setVisibility(View.GONE);
        btnfinalizar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Error con el servidor, intente nuevamente.", Toast.LENGTH_SHORT).show();
        ArrayAdapter<Tag> adaptertags = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,tags);
        listtags.setAdapter(adaptertags);

    }

    @Override
    public void onResponse(JSONArray response) {
        try {
            if(response.length()<=0){
                cantidadGavetas.setText(getResources().getString(R.string.InfocantidadGavetas));
                btnguardar.setVisibility(View.GONE);
                btnfinalizar.setVisibility(View.GONE);

            }
            else{

                btnguardar.setVisibility(View.VISIBLE);
                btnfinalizar.setVisibility(View.VISIBLE);
                btnfinalizar.setEnabled(false);

                for (int i=0; i < response.length(); i++) {
                    JSONObject r = response.getJSONObject(i);
                    Tag t = crearTag(r);
                    tags.add(t);

                }
                cantidadGavetas.setText(tags.size()+" gaveta(s) encontrada(s)");
                /*ArrayAdapter<Tag> adaptertags = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,tags);
                listtags.setAdapter(adaptertags);
                listtags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Tag selectedItem = (Tag) parent.getItemAtPosition(position);
                        try {
                            Toast.makeText(getContext(),"Hora Vista: "+ UserActivity1.toStringDate(selectedItem.getSeen()),Toast.LENGTH_LONG).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(),"Hora Vista: "+ selectedItem.getSeen(),Toast.LENGTH_LONG).show();
                        }
                    }
                });*/

            }
            ArrayAdapter<Tag> adaptertags = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,tags);
            listtags.setAdapter(adaptertags);
            listtags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Tag selectedItem = (Tag) parent.getItemAtPosition(position);
                    try {
                        Toast.makeText(getContext(),"Hora Vista: "+ UserActivity1.toStringDate(selectedItem.getSeen()),Toast.LENGTH_LONG).show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(),"Hora Vista: "+ selectedItem.getSeen(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            cantidadGavetas.setText(getResources().getString(R.string.InfocantidadGavetas));
            btnguardar.setVisibility(View.GONE);
            btnfinalizar.setVisibility(View.GONE);
        }






    }

    private Tag crearTag(JSONObject r) {
        Tag t = new Tag(r.optString("id_tag").trim(), r.optString("seen_time"));
        return t;
    }
}
