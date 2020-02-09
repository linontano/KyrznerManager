package com.example.kyrznermanager.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.example.kyrznermanager.clases.Lote;
import com.example.kyrznermanager.clases.Tag;
import com.example.kyrznermanager.clases.Worker;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */

public class FragmentEtapa1_2 extends Fragment implements View.OnClickListener, Response.ErrorListener, Response.Listener<JSONArray> {
    NavController navController=null;
    ArrayList<Worker> seleccionGroup;
    ArrayAdapter<Worker> adapterSeleccion;
    ArrayList<Tag> rtags;
    Worker userActual;
    Lote seleccionLote;

    CardView containerError;
    CardView cv_back, cv_save;
    LinearLayout containerProgress, containerInfo;
    TextView idgrupo, cantidadGavetas;
    Button btnback, btnguardar, btntags;
    ListView listSeleccion, listTags;

    String id_grupo;

    JsonArrayRequest jsonArrayRequest;
    RequestQueue request;
    RequestQueue request2;
    StringRequest stringRequest;

    public FragmentEtapa1_2() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        seleccionGroup = getArguments().getParcelableArrayList("group");
        userActual = getArguments().getParcelable("userActual");
        id_grupo = getArguments().getString("grupoId");
        seleccionLote = (Lote) getArguments().getSerializable("lote");
        idgrupo.setText(id_grupo);
        //seleccionGroup.add(userActual);
        pintarSeleccionGroup();

        btnback.setEnabled(true); btnguardar.setEnabled(false);
        cv_back.setEnabled(true);
        cv_back.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
        cv_save.setEnabled(false);


        btnback.setOnClickListener(this); btnguardar.setOnClickListener(this);
        btntags.setEnabled(true); btntags.setOnClickListener(this);
        cv_back.setOnClickListener(this); cv_save.setOnClickListener(this);

        containerError.setVisibility(View.GONE);
        containerInfo.setVisibility(View.VISIBLE);
        containerProgress.setVisibility(View.GONE);


    }

    private void pintarSeleccionGroup() {
        adapterSeleccion = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,seleccionGroup);
        listSeleccion.setAdapter(adapterSeleccion);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_etapa1_2, container, false);
        prepararEntorno(view);
        request = Volley.newRequestQueue(getContext());

        return view;
    }

    private void prepararEntorno(View view) {
        containerError = view.findViewById(R.id.containerError);
        containerInfo = view.findViewById(R.id.containerInfo);
        containerProgress = view.findViewById(R.id.containerProgress);

        containerProgress.setVisibility(View.VISIBLE);
        containerInfo.setVisibility(View.GONE);
        containerError.setVisibility(View.GONE);

        listSeleccion = view.findViewById(R.id.listGroup);
        listTags = view.findViewById(R.id.listTags);
        btnback = view.findViewById(R.id.btnBackE13);
        btnguardar = view.findViewById(R.id.btnguardarE12);
        cv_save = view.findViewById(R.id.cv_save);
        cv_back = view.findViewById(R.id.cv_back);
        btntags = view.findViewById(R.id.btnLeerTagsE12);
        idgrupo = view.findViewById(R.id.txtidgrupo);

        cantidadGavetas = view.findViewById(R.id.txtnumerotags);


        btntags.setEnabled(false);
        btnback.setEnabled(false);
        btnguardar.setEnabled(false);
        cv_back.setEnabled(false);
        cv_save.setEnabled(false);



    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cv_back:
                //correrReverso();
                getActivity().onBackPressed();
                break;
            case R.id.btnLeerTagsE12:
                leerTags();
                break;
            case R.id.cv_save:
                popupConfirmacion();
                break;

        }
    }

    private void popupConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pelado");
        Drawable warning = getResources().getDrawable(R.drawable.ic_warning_black_24dp);
        builder.setIcon(warning);
        builder.setMessage("¿Desea ingresar los siguientes "+rtags.size()+" procesados para el grupo "+id_grupo.trim()+"?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "SI",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        btnguardar.setEnabled(false);
                        containerError.setVisibility(View.GONE); containerInfo.setVisibility(View.GONE);
                        containerProgress.setVisibility(View.VISIBLE);
                        dialog.cancel();
                        insertTagPeladoBD();

                        //navController.
                    }
                });

        builder.setNegativeButton(
                "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    private void insertTagPeladoBD() {
        request2 = Volley.newRequestQueue(getContext());
        for(Tag t : rtags){
            String url = "http://34.95.141.34/kyrznerApp/insertFinalPelado.php?idlote=" + seleccionLote.getId_lote().trim() + "&idgrupo=" + id_grupo.trim()+"&idtag="+t.getId().trim();
            stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pelado");
        Drawable ok = getResources().getDrawable(R.drawable.ic_done_black_128dp);
        builder.setIcon(ok);
        builder.setMessage("Datos ingresado con éxito.");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        actualizarModoLectura("A","N");
                        navController.navigate(R.id.action_fragmentEtapa1_2_to_fragmentHome2);
                        dialog.cancel();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

    private void leerTags() {
        rtags = new ArrayList<>();
        request = Volley.newRequestQueue(getContext());
        String url = "http://34.95.141.34/kyrznerApp/consultaTagsxLote.php?idlote="+seleccionLote.getId_lote().trim();
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this , this);
        request.add(jsonArrayRequest);




    }

    private ArrayAdapter<Tag> getAdapterListViewPersonalizado(final ArrayList<Tag> tags ){
        return new ArrayAdapter<Tag>(getContext(),android.R.layout.simple_list_item_1,tags){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView txt = view.findViewById(android.R.id.text1);
                txt.setTextColor(Color.BLACK);
                if(tags.get(position).getOk().equalsIgnoreCase("N")) {

                    txt.setText(tags.get(position).getId()+" (ERROR)");
                    txt.setTextColor(Color.RED);
                }
                return view;

            }
        };
    }

    private void correrReverso() {

        String url = "http://34.95.141.34/kyrznerApp/reversoGrupoPelado.php?idlote=" + seleccionLote.getId_lote().trim() + "&idgrupo=" + id_grupo.trim();
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Toast.makeText(getContext(), "Atras xd", Toast.LENGTH_SHORT).show();
                correrReverso();
                actualizarModoLectura("A","N");
                getFragmentManager().popBackStack();


            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this,callback);

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        cantidadGavetas.setText(getResources().getString(R.string.InfocantidadGavetas));
        cv_save.setEnabled(false);
        cv_save.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundDisabled));
        ArrayAdapter<Tag> adaptertags = getAdapterListViewPersonalizado(rtags);
        listTags.setAdapter(adaptertags);
        Toast.makeText(getContext(), "Error en el servidor, intente nuevamente.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResponse(JSONArray response) {
        Boolean error = true;
        try {
            if(response.length()<=0){
                cantidadGavetas.setText(getResources().getString(R.string.InfocantidadGavetas));
                cv_save.setEnabled(false);
                cv_save.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundDisabled));
                ArrayAdapter<Tag> adaptertags = getAdapterListViewPersonalizado(rtags);
                listTags.setAdapter(adaptertags);

            }
            else{



                for (int i=0; i < response.length(); i++) {
                    JSONObject r = response.getJSONObject(i);
                    Tag t = crearTag(r);
                    rtags.add(t);

                }
                /*
                Tag test = new Tag("TAGPRUEBA-0000001", "05-05-2020 12:12:12.121-05");
                test.setOk("N");
                rtags.add(test);*/
                String mssge = rtags.size()+" gaveta(s) encontrada(s)";


                error = confirmarErrores(rtags);
                if(error){
                    cantidadGavetas.setText(mssge+" (retirar gavetas erróneas)");
                    cantidadGavetas.setTextColor(Color.RED);
                }
                else{
                    cantidadGavetas.setText(mssge);
                    cantidadGavetas.setTextColor(Color.BLACK);
                }


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
            ArrayAdapter<Tag> adaptertags = getAdapterListViewPersonalizado(rtags);
            listTags.setAdapter(adaptertags);
            listTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            if(!error) {
                btnguardar.setEnabled(true);
                cv_save.setEnabled(true);
                cv_save.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            else{
                cv_save.setEnabled(false);
                cv_save.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundDisabled));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            cantidadGavetas.setText(getResources().getString(R.string.InfocantidadGavetas));
            cv_save.setEnabled(false);
            cv_save.setCardBackgroundColor(getResources().getColor(R.color.colorBackgroundDisabled));


        }

    }

    private Boolean confirmarErrores(ArrayList<Tag> rtags) {

        for(Tag t : rtags){
            if (t.getOk().equalsIgnoreCase("N")){
                return true;
            }
        }
        return false;
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

    private Tag crearTag(JSONObject r) {
        Tag t = new Tag(r.optString("id_tag").trim(), r.optString("seen_time").trim());
        t.setOk(r.optString("encontrado").trim());
        return t;
    }
}
