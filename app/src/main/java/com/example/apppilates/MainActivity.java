package com.example.apppilates;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.apppilates.Fragments.AgregarClienteFragment;
import com.example.apppilates.Fragments.AgregarEjercicioFragment;
import com.example.apppilates.Fragments.BuscarClienteFragment;
import com.example.apppilates.Fragments.pagosFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        replaceFragment(new pagosFragment());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.pagos) {
                replaceFragment(new pagosFragment());
            } else if (item.getItemId() == R.id.ejercicios) {
                replaceFragment(new AgregarEjercicioFragment());
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