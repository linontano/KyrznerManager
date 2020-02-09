package com.example.kyrznermanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.kyrznermanager.clases.Usuario;
import com.example.kyrznermanager.fragments.FragmentEtapa0;
import com.example.kyrznermanager.fragments.FragmentHome2;

public class UserActivity2 extends AppCompatActivity implements FragmentHome2.OnFragmentInteractionListener, FragmentEtapa0.OnFragmentInteractionListener {

    public static Usuario user;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();

        user = (Usuario)i.getSerializableExtra("Usuario");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);
        //Toolbar toolbar = findViewById(R.id.toolbar2);

        //setSupportActionBar(toolbar);

        //final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //NavigationUI.setupActionBarWithNavController(this, navController,appBarConfiguration);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //MANTENER PANTALLA ACTIVA. SOLO EN LA ACTIVIDAD
    }

    @Override
    public void onFragmentInteraction(Uri uri) {



    }




}
