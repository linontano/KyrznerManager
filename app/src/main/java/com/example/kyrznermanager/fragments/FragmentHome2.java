package com.example.kyrznermanager.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.kyrznermanager.MainActivity;
import com.example.kyrznermanager.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentHome2 extends Fragment implements View.OnClickListener, Response.Listener<JSONArray>, Response.ErrorListener {

    Button etapa0, etapa1, etapa2, etapa3, etapa4, etapa5;
    FragmentEtapa0 fragmente0;
    NavController navController = null;
    JsonArrayRequest jsonArrayRequest;
    RequestQueue request;
    CardView exitcv;
    ArrayList<Boolean> habilitadores;
    CardView cardviewE0,cardviewE1,cardviewE2,cardviewE3,cardviewE4,cardviewE5;

    private OnFragmentInteractionListener mListener;

    public FragmentHome2() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        habilitadores = new ArrayList<>();
        cardviewE0 = view.findViewById(R.id.cardviewe0);
        cardviewE1 = view.findViewById(R.id.cardviewe1);
        cardviewE2 = view.findViewById(R.id.cardviewe2);
        cardviewE3 = view.findViewById(R.id.cardviewe3);
        cardviewE4 = view.findViewById(R.id.cardviewe4);
        cardviewE5 = view.findViewById(R.id.cardviewe5);
        exitcv = view.findViewById(R.id.cv_exit);
        exitcv.setEnabled(true);
        exitcv.setOnClickListener(this);


        cargarEnableProcesos(view);



        cardviewE0.setOnClickListener(this);
        cardviewE1.setOnClickListener(this);
        cardviewE2.setOnClickListener(this);
        cardviewE3.setOnClickListener(this);
        cardviewE4.setOnClickListener(this);
        cardviewE5.setOnClickListener(this);
//        etapa5.setOnClickListener(this);


    }

    private void cargarEnableProcesos(View view) {
        request = Volley.newRequestQueue(getContext());
        String url ="http://34.95.141.34/kyrznerApp/consultaLoteProceso.php";
        jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonArrayRequest);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home2, container, false);


        fragmente0 = new FragmentEtapa0();




        return root;
    }

    private void validarProcesos() {

        cardviewE1.setEnabled(habilitadores.get(0));
        if(!habilitadores.get(0)) {
            cardviewE1.setCardBackgroundColor(getResources().getColor(R.color.colorCardNeutral));
        }
        cardviewE2.setEnabled(habilitadores.get(1));
        if(!habilitadores.get(1)) {
            cardviewE2.setCardBackgroundColor(getResources().getColor(R.color.colorCardNeutral));
        }
        cardviewE3.setEnabled(habilitadores.get(2));
        if(!habilitadores.get(2)) {
            cardviewE3.setCardBackgroundColor(getResources().getColor(R.color.colorCardNeutral));
        }
        cardviewE4.setEnabled(habilitadores.get(3));
        if(!habilitadores.get(3)) {
            cardviewE4.setCardBackgroundColor(getResources().getColor(R.color.colorCardNeutral));
        }
        cardviewE5.setEnabled(habilitadores.get(4));
        if(!habilitadores.get(4)) {
            cardviewE5.setCardBackgroundColor(getResources().getColor(R.color.colorCardNeutral));
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cv_exit:
                confirmarSalirApp();
                break;

            case R.id.cardviewe0:
                navController.navigate(R.id.action_fragmentHome2_to_fragmentEtapa0);
                break;
            case R.id.cardviewe1:
                navController.navigate(R.id.action_fragmentHome2_to_fragmentEtapa1);
                break;

            default:
                break;

        }


    }

    @Override
    public void onResponse(JSONArray response) {
        try {
            JSONArray response1 = response.getJSONArray(0);
            JSONArray response2 = response.getJSONArray(1);
            for (int i=0; i < response1.length()-1; i++) {
                Boolean bp = response1.getBoolean(i);
                Boolean bi = response2.getBoolean(i+1);
                habilitadores.add(bp||bi);
            }
            habilitadores.add(response1.getBoolean(response1.length()-1));
        } catch (JSONException e) {
            e.printStackTrace();
        }




        validarProcesos();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG);

    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
}
