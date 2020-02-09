package com.example.kyrznermanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kyrznermanager.clases.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements Response.Listener<JSONObject>,Response.ErrorListener {

    private EditText user;
    private EditText password;
    private TextView message;
    Button botonLogin;
    private ProgressBar proceso;
    Boolean pruebas = false;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlogin);

        //vinculacion xml id con objetos
        user = (EditText) findViewById(R.id.user);
        password = (EditText) findViewById(R.id.password);
        message = (TextView) findViewById(R.id.message);
        botonLogin = (Button) findViewById(R.id.login);
        request = Volley.newRequestQueue(this);
        proceso = (ProgressBar)findViewById(R.id.progressBar);

    }
    public void validarLogin(View view){
        message.setTextColor(Color.rgb(220,50,50));
        String usuario = user.getText().toString();
        String pass = password.getText().toString();
        if(pruebas){
            usuario = "lontano";
            pass = "lontano1234";
            /*usuario = "mirandajp";
            pass = "mirandajp1234";*/
        }
        Boolean bandera = true;
        message.setVisibility(View.GONE);
        if (usuario.isEmpty()){
            message.setText("Error: Ingrese un usuario");
            message.setVisibility(View.VISIBLE);
            bandera = false;
        }
        else{
            if(pass.isEmpty()){
                message.setText("Error: Ingrese contraseña");
                message.setVisibility(View.VISIBLE);
                bandera = false;
            }
        }
        if (bandera){
            consultarLogin(usuario, pass);
        }

    }

    private void consultarLogin(String usuario, String password) {
        message.setVisibility(View.GONE);
        proceso.setVisibility(View.VISIBLE);

        String url = "http://34.95.141.34/kyrznerApp/consultaUsuario.php?user="+usuario+"&password="+password;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url, null, this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        proceso.setVisibility(View.GONE);
        Toast.makeText(this,"Error"+error.toString(), Toast.LENGTH_SHORT).show();
        message.setText("Error: "+error.toString());
        message.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResponse(JSONObject response) {
        proceso.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        JSONArray json = response.optJSONArray("response");
        try {
            JSONObject object = json.getJSONObject(0);
            String usuario = object.optString("user");
            message.setText(usuario);
            if(usuario.equals("null")){
                message.setText("Usuario y contraseña incorrectos");
            }
            else{
                Usuario u = crearUsuario(object);
                message.setText("Bienvenido: "+u.getNombres().split(" ")[0]);
                message.setTextColor(Color.rgb(50,180,50));
                if (u.getRol().equals(0)){
                    Intent intent = new Intent(this, UserActivity1.class);
                    intent.putExtra("Usuario",u);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(this, UserActivity2.class);
                    intent.putExtra("Usuario",u);
                    startActivity(intent);
                    finish();
                }
                //intent.putExtra;




            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private Usuario crearUsuario(JSONObject object)  {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date nacimiento = null;
        Date contrato = null;
        Integer rol = Integer.parseInt(object.optString("rol"));
        try {
            nacimiento = dateformat.parse(object.optString("fecha_nacimiento"));
            contrato = dateformat.parse(object.optString("fecha_contrato"));
        } catch (ParseException e) {
            e.printStackTrace();
            nacimiento = Calendar.getInstance().getTime();
            contrato = Calendar.getInstance().getTime();
        }

        String [] contactos = new String[3];
        contactos[2]=object.optString("numero_conven");
        contactos[1]=object.optString("numero_celular");
        contactos[0]=object.optString("email");
        Usuario u = new Usuario(object.optString("id_empleado"),
                                object.optString("user"),
                                object.optString("nombres"),
                                object.optString("apellidos"),
                                object.optString("tipo_id"),
                                object.optString("id"),
                                object.optString("ecivil"),
                                nacimiento,
                                contactos,
                                contrato,
                                object.optString("estado"),
                                object.optString("accountapp"),
                                rol);

        return u;
    }
}
