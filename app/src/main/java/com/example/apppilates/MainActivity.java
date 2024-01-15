package com.example.apppilates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.apppilates.Fragments.AgregarClienteFragment;
import com.example.apppilates.Fragments.BuscarClienteFragment;
import com.example.apppilates.Fragments.InicioFragment;
import com.example.apppilates.Fragments.pagosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    InicioFragment inicio = new InicioFragment();
    AgregarClienteFragment agregar = new AgregarClienteFragment();
    BuscarClienteFragment buscar = new BuscarClienteFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new InicioFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.inicio) {
                replaceFragment(new pagosFragment());
            } else if (item.getItemId() == R.id.agregar) {
                replaceFragment(new AgregarClienteFragment());
            } else if (item.getItemId() == R.id.buscar) {
                replaceFragment(new BuscarClienteFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}