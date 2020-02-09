package com.example.kyrznermanager.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kyrznermanager.R;
import com.example.kyrznermanager.UserActivity1;
import com.example.kyrznermanager.adapter.AdapterRecyclerGroupWorker;
import com.example.kyrznermanager.clases.Lote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentLoteDetalle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentLoteDetalle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentLoteDetalle extends Fragment implements Response.Listener<JSONArray>, Response.ErrorListener, View.OnClickListener {
    TextView txttitulo;
    ProgressBar bardetalle, infoLoad;
    Button btndetallese0, btndetallese1;
    TextView txtgavetas, txtidproceso, txtidlote,
            txtinicio, txtfin, txttiempo, txtproceso;

    LinearLayout head, containerAlmacenamiento, containerPelado;
    ScrollView info;
    TextView txtFecha, txtPeso, txtGavetas, txtProov,txtetapa;
    ArrayList<String> loteActual;
    ImageView procesoe0, procesoe1, procesoe2, procesoe3, procesoe4, procesoe5, imgError;
    RecyclerView rvgroup1;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue request;
    Lote lote;
    NavController navController;

    Bundle objetoLote;

    int PESO_APROXIMADO = 25;

    private static final String TAG = "FragmentLoteDetalle";
    ArrayList<ArrayList<String>> responseG01;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentLoteDetalle() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentLoteDetalle.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentLoteDetalle newInstance(String param1, String param2) {
        FragmentLoteDetalle fragment = new FragmentLoteDetalle();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lote_detalle, container, false);
        request = Volley.newRequestQueue(getContext());
        loteActual = new ArrayList<>();


        txttitulo = view.findViewById(R.id.txtdetalle);
        bardetalle = view.findViewById(R.id.progressLoteDetalle);

        prepararEntorno(view);

        objetoLote = getArguments();

        if(objetoLote!=null){
            lote= (Lote) objetoLote.getSerializable("lote");
            obtenerLoteActualInfo();
            txttitulo.setText(lote.getId_lote().trim());


        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void pintarCabeceraProceso() {
        int a =lote.getEtapa().intValue();
        actualizarEstadoHeader(a);
        int porcentaje=a*50/3;
        String estado = "Error";
        switch (a){
            case 0: estado = "Almacenamiento de Lote"; break;
            case 1: estado = "Fase de Pelado";break;
            case 2: estado = "Fase de Deshidratación";break;
            case 3: estado = "Fase de Separación";break;
            case 4: estado = "Fase de Corte";break;
            case 5: estado = "Fase de Empaque";break;
            case 6: estado = "Finalizado"; break;
        }
        txtetapa.setText(estado);
        bardetalle.setProgress(porcentaje);
        if(porcentaje<30){
            bardetalle.getProgressDrawable().setColorFilter(
                    Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        }
        else{
            if(porcentaje<60){
                bardetalle.getProgressDrawable().setColorFilter(
                        Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
            }
            else{
                bardetalle.getProgressDrawable().setColorFilter(
                        Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void actualizarEstadoHeader(int a) {
        int disable = R.drawable.shape_circle_disabled;
        int shape = 0;
        if(a<2){
            shape = R.drawable.shape_circle_inicial;
        }
        else{
            if(a<4){
                shape = R.drawable.shape_circle_intermedio;
            }
            else{
                shape = R.drawable.shape_circle_final;
            }
        }

        switch (a){
            case 0 :
                procesoe0.setBackground(getResources().getDrawable(shape));
                procesoe0.setImageDrawable(getResources().getDrawable(R.drawable.ic_e0_wh));
                break;
            case 1: procesoe1.setBackground(getResources().getDrawable(shape));
                procesoe0.setBackground(getResources().getDrawable(disable));
                procesoe1.setImageDrawable(getResources().getDrawable(R.drawable.ic_e1_wh));break;
            case 2: procesoe2.setBackground(getResources().getDrawable(shape));
                procesoe2.setImageDrawable(getResources().getDrawable(R.drawable.ic_e2_wh));
                procesoe0.setBackground(getResources().getDrawable(disable));
                procesoe1.setBackground(getResources().getDrawable(disable));break;
            case 3: procesoe3.setBackground(getResources().getDrawable(shape));
                procesoe3.setImageDrawable(getResources().getDrawable(R.drawable.ic_e3_wh));
                procesoe0.setBackground(getResources().getDrawable(disable));
                procesoe1.setBackground(getResources().getDrawable(disable));
                procesoe2.setBackground(getResources().getDrawable(disable));break;
            case 4: procesoe4.setBackground(getResources().getDrawable(shape));
            procesoe4.setImageDrawable(getResources().getDrawable(R.drawable.ic_e4_wh));
                procesoe0.setBackground(getResources().getDrawable(disable));
                procesoe1.setBackground(getResources().getDrawable(disable));
                procesoe2.setBackground(getResources().getDrawable(disable));
                procesoe3.setBackground(getResources().getDrawable(disable));break;
            case 5: procesoe5.setBackground(getResources().getDrawable(shape));
            procesoe5.setImageDrawable(getResources().getDrawable(R.drawable.ic_e5_wh));
                procesoe0.setBackground(getResources().getDrawable(disable));
                procesoe1.setBackground(getResources().getDrawable(disable));
                procesoe2.setBackground(getResources().getDrawable(disable));
                procesoe3.setBackground(getResources().getDrawable(disable));
                procesoe4.setBackground(getResources().getDrawable(disable));break;
            default: break;
        }
    }

    private void obtenerLoteActualInfo() {
        String url = "http://34.95.141.34/kyrznerApp/consultaLoteGeneral.php?idlote="+lote.getId_lote().trim();
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonArrayRequest);
    }

    private void prepararEntorno(View view) {
        //header proceso
        info = view.findViewById(R.id.info);
        //head = view.findViewById(R.id.head);
        imgError = view.findViewById(R.id.imgError);
        infoLoad = view.findViewById(R.id.loadInfo);
        imgError.setVisibility(View.GONE);
        infoLoad.setVisibility(View.VISIBLE);
        info.setVisibility(View.GONE);
        //head.setVisibility(View.GONE);


        procesoe0 = view.findViewById(R.id.ive0);
        procesoe1 = view.findViewById(R.id.ive1);
        procesoe2 = view.findViewById(R.id.ive2);
        procesoe3 = view.findViewById(R.id.ive3);
        procesoe4 = view.findViewById(R.id.ive4);
        procesoe5 = view.findViewById(R.id.ive5);
        //
        containerPelado = view.findViewById(R.id.containerPelado);
        containerAlmacenamiento = view.findViewById(R.id.containerAlmacenamiento);
        containerPelado.setVisibility(View.GONE);
        containerAlmacenamiento.setVisibility(View.VISIBLE);
        txtFecha = view.findViewById(R.id.txtfechaDash);
        txtetapa = view.findViewById(R.id.txtEtapainfo);
        txtGavetas = view.findViewById(R.id.txtGavetasDash);
        txtProov = view.findViewById(R.id.txtProovDash);
        txtPeso = view.findViewById(R.id.txtPesoDash);
        btndetallese0 = view.findViewById(R.id.btnDetallesE0);
        btndetallese1 = view.findViewById(R.id.btnDetallesE1);
        btndetallese1.setOnClickListener(this);
        btndetallese0.setOnClickListener(this);

        rvgroup1 = view.findViewById(R.id.grupos01Recycler);





    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        infoLoad.setVisibility(View.GONE);
        info.setVisibility(View.GONE);
        //head.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        imgError.startAnimation(anim);
        imgError.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), "Error en la respuesta del servidor, intente más tarde.", Toast.LENGTH_LONG).show();
        containerPelado.setVisibility(View.GONE);
        containerAlmacenamiento.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onResponse(JSONArray response) {

        try {
            JSONArray response1 = response.getJSONArray(0);
            lote.setEtapa(response1.getJSONObject(0).optInt("etapa"));
            lote.setEstado(response1.getJSONObject(0).optString("estado"));
            String fecha = response1.getJSONObject(0).optString("fecha_ingreso");
            int gave = response1.getJSONObject(0).optInt("cantidad");
            int pesoTotal = gave*PESO_APROXIMADO;
            try {
                txtFecha.setText(UserActivity1.toStringDate(fecha.trim()));
            }catch (ParseException e) {
                e.printStackTrace();
                txtFecha.setText(fecha.trim());
            }
            txtGavetas.setText(String.valueOf(gave));
            txtProov.setText(response1.getJSONObject(0).optString("descripcion").trim());
            txtPeso.setText(String.valueOf(pesoTotal)+" Kg");
            btndetallese0.setEnabled(true);

        } catch (JSONException e) {
            e.printStackTrace();
            txtGavetas.setText(getResources().getString(R.string.txtNohayInfo));
            txtProov.setText(getResources().getString(R.string.txtNohayInfo));
            txtPeso.setText(getResources().getString(R.string.txtNohayInfo));
            txtFecha.setText(getResources().getString(R.string.txtNohayInfo));

        }

        try {
            JSONArray response2 = response.getJSONArray(1);
            if(response2.length() != 0){
                containerPelado.setVisibility(View.VISIBLE);
                responseG01 = new ArrayList<>();
                for(int i = 0; i < response2.length() ; i++){
                    JSONObject r = response2.getJSONObject(i);
                    ArrayList<String> consulta = new ArrayList<>();
                    consulta.add(r.optString("id_grupo"));
                    consulta.add(r.optString("count"));
                    consulta.add(r.optString("id_tag"));
                    responseG01.add(consulta);
                }
                iniciarRecyclerViewGrupo1();
                btndetallese1.setEnabled(true);
                //preguntar aqui por el siguiente response de pelado. Afuera del if no tiene sentido
            }
            else{
                containerPelado.setVisibility(View.GONE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            containerPelado.setVisibility(View.GONE);

        }
        pintarCabeceraProceso();
        infoLoad.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        //head.startAnimation(anim);
        info.startAnimation(anim);
        //head.setVisibility(View.VISIBLE);
        info.setVisibility(View.VISIBLE);

    }

    private void iniciarRecyclerViewGrupo1() {
        Log.d(TAG, "iniciarRecyclerViewGrupo1: init recycler view.");
        AdapterRecyclerGroupWorker adapter = new AdapterRecyclerGroupWorker(getContext(), responseG01);
        rvgroup1.setAdapter(adapter);
        rvgroup1.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL, false));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDetallesE0:
                Bundle b = new Bundle();
                b.putSerializable("lote",lote);
                navController.navigate(R.id.action_nav_lote_detalles_to_detalleAlmacenamiento, b);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.action_exit).setVisible(false);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_reloadFragment:
                navController.navigate(R.id.action_nav_lote_detalles_self, objetoLote);
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
