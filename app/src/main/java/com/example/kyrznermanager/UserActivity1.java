package com.example.kyrznermanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.kyrznermanager.clases.Lote;
import com.example.kyrznermanager.clases.Usuario;
import com.example.kyrznermanager.interfaces.IComunicaFragments;
import com.example.kyrznermanager.ui.gallery.GalleryFragment;
import com.example.kyrznermanager.ui.home.FragmentLoteDetalle;
import com.example.kyrznermanager.ui.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserActivity1 extends AppCompatActivity implements IComunicaFragments, FragmentLoteDetalle.OnFragmentInteractionListener {



    public Usuario user;
    public TextView nav_name;
    public TextView nav_correo;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        user = (Usuario)i.getSerializableExtra("Usuario");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user1);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_dashboard, R.id.nav_logs, R.id.nav_tools)
                .setDrawerLayout(drawer)
                .build();


        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);


        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //FloatingActionButton fab = findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
                startActivity(getIntent());
                navController.navigate(R.id.nav_dashboard);


                navController.popBackStack();


                NavOptions.Builder nav  = new NavOptions.Builder();



            }
        });*/



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_activity1, menu);

        nav_name = (TextView)findViewById(R.id.nav_name);
        nav_correo = (TextView)findViewById(R.id.nav_correo);
        String mail = user.getContactos()[0];
        nav_name.setText(user.getNombres());
        if(!mail.isEmpty()){
        nav_correo.setText(mail);}
        else{
            nav_correo.setText("");
        }
        return true;
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

  /*
*/

    @Override
    public void enviarLote(Lote lote) {
        Bundle bundleenvio = new Bundle();
        bundleenvio.putSerializable("lote",lote);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.action_nav_dashboard_to_nav_lote_detalles,bundleenvio);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static String toStringDate(String sdate) throws ParseException {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
        Date date = formatter.parse(sdate);
        DateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        String fechaFormateada = formatoFecha.format(date) + " " + formatoHora.format(date);
        return fechaFormateada;

    }

}
