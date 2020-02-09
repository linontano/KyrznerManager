package com.example.kyrznermanager.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kyrznermanager.MainActivity;
import com.example.kyrznermanager.R;
import com.example.kyrznermanager.UserActivity1;
import com.example.kyrznermanager.adapter.AdapterLote;
import com.example.kyrznermanager.clases.Lote;
import com.example.kyrznermanager.clases.Usuario;
import com.example.kyrznermanager.interfaces.IComunicaFragments;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.transform.sax.SAXSource;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    UserActivity1 userActivity;
    Lote lote;
    public TextView textoBienvenida;
    public TextView textoMensaje;
    public ProgressBar barload;
    AdapterLote adapter;
    private FragmentLoteDetalle.OnFragmentInteractionListener mListener;

    SwipeRefreshLayout refresh;
    RequestQueue request;
    JsonArrayRequest jsonArrayRequest;
    private HomeViewModel homeViewModel;
    RecyclerView recyclerLotes;
    ArrayList<Lote> lotes;
    Activity activity;
    IComunicaFragments interfaceComunicaFragments;
    NavController navController = null;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        refresh = root.findViewById(R.id.swiperefresh);
        refresh.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);
        refresh.setOnRefreshListener(this);

        userActivity= (UserActivity1) getActivity();
        Usuario u = userActivity.user;
        barload = root.findViewById(R.id.barload);
        barload.setVisibility(View.VISIBLE);
        textoBienvenida = root.findViewById(R.id.textwelcome);
        textoMensaje = root.findViewById(R.id.txtMensaje);
        textoMensaje.setVisibility(View.GONE);
        textoBienvenida.setText("Bienvenido "+u.getNombres().split(" ")[0]);
        recyclerLotes = root.findViewById(R.id.recyclerlotes);
        recyclerLotes.setLayoutManager(new GridLayoutManager(getContext(),1));
        adapter = new AdapterLote();
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interfaceComunicaFragments.enviarLote(lotes.get(recyclerLotes.getChildAdapterPosition(v)));

            }
        });
        recyclerLotes.setAdapter(adapter);
        //Volley REQUEST////////////////////////////////////////////////////////////////////////////////
        request = Volley.newRequestQueue(getContext());
        String url = "http://34.95.141.34/kyrznerApp/consultaLote.php";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url, null, new Response.Listener<JSONArray>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(JSONArray response) {
                //Response del Servidor / Se crean los lotes de respuesta ////////////////////////////////
                obtenerLotes(response);
                adapter.setLotes(lotes);
                barload.setVisibility(View.GONE);
                Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
                recyclerLotes.startAnimation(anim);
                recyclerLotes.setAdapter(adapter);
                }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                // Do something when error occurred
                barload.setVisibility(View.GONE);
                Snackbar.make(getView(), "No hay conexión al Servidor, revise su conexión a Internet.", Snackbar.LENGTH_INDEFINITE).show();
                //Toast.makeText(getContext(),"Error"+error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        request.add(jsonArrayRequest);


        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void obtenerLotes(JSONArray response) {
        lotes = new ArrayList<>();

        try{
            // Loop through the array elements
            if(response.length()<=0){
                barload.setVisibility(View.GONE);
                textoMensaje.setText("No hay lotes procesándose actualmente");
                textoMensaje.setTextColor(Color.RED);
                textoMensaje.setVisibility(View.VISIBLE);
                recyclerLotes.setVisibility(View.GONE);

            }
            else{
                for (int i=0; i < response.length(); i++) {
                    JSONObject r = response.getJSONObject(i);
                    System.out.printf(r.toString());
                    lote = crearLote(r);
                    System.out.printf(lote.toString());
                    lotes.add(lote);

                }
            }

        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }

    private Lote crearLote(JSONObject object)  {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date ingreso = null;
        Date salida = null;
        Integer etapa = Integer.parseInt(object.optString("etapa"));
        try {
            ingreso = dateformat.parse(object.optString("fecha_ingreso"));
            String ssalida = object.optString("fecha_contrato");
            if (!ssalida.isEmpty()){
            salida = dateformat.parse(object.optString("fecha_contrato"));
            }

        } catch (ParseException e) {
            e.printStackTrace();
            ingreso = Calendar.getInstance().getTime();

        }

        Lote l = new Lote(object.optString("estado"),
                            object.optString("id_proveedor"),
                            object.optString("id_lote"),
                            etapa,
                            ingreso,
                            salida);


        return l;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_reloadFragment:
                navController.navigate(R.id.action_nav_dashboard_self);
                break;
            case R.id.action_exit:

                confirmarSalirApp();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmarSalirApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Salir");
        builder.setMessage("¿Desea cerrar sesión?");
        builder.setCancelable(true);
        builder.setIcon(getResources().getDrawable(R.drawable.ic_warning_black_24dp));

        builder.setPositiveButton(
                "SI",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        dialog.cancel();
                        getActivity().finish();

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof  Activity){
            this.activity = (Activity) context;
            interfaceComunicaFragments = (IComunicaFragments) this.activity;
        }
        if (context instanceof FragmentLoteDetalle.OnFragmentInteractionListener) {
            mListener = (FragmentLoteDetalle.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onRefresh() {
        //navController.navigate(R.id.action_nav_dashboard_self);

        recyclerLotes.setVisibility(View.GONE);
        new HackingBackgroundTask().execute();

    }

    private class HackingBackgroundTask extends AsyncTask<Void, Void, ArrayList<Lote>> {
        static final int DURACION = 1 * 1000;



        @Override
        protected ArrayList<Lote> doInBackground(Void... voids) {
            request = Volley.newRequestQueue(getContext());
            String url = "http://34.95.141.34/kyrznerApp/consultaLote.php";
            jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url, null, new Response.Listener<JSONArray>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(JSONArray response) {
                    //Response del Servidor / Se crean los lotes de respuesta ////////////////////////////////
                    obtenerLotes(response);
                    adapter.clear();
                    adapter.setLotes(lotes);
                    refresh.setRefreshing(false);
                    Animation anim = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
                    recyclerLotes.startAnimation(anim);
                    recyclerLotes.setVisibility(View.VISIBLE);

                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error){
                    // Do something when error occurred
                    Snackbar.make(getView(), "No hay conexión al Servidor, revise su conexión a Internet.", Snackbar.LENGTH_INDEFINITE).show();
                    //Toast.makeText(getContext(),"Error"+error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
            request.add(jsonArrayRequest);
            return lotes;

        }


    }



    /*@Override
    public void onBackPressed() {
        NavController nav = Navigation.findNavController(this,R.id.nav_host_fragment);
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            getActivity().onBackPressed();
            return;
        } else {
            Toast.makeText(getContext(), "Presione nuevamente para cerrar la aplicación",    Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }*/
}