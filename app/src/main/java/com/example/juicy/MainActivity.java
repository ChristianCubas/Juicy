package com.example.juicy;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.juicy.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        // Navegar usando la instancia guardada de navController
        if (id == R.id.action_mpago) {
            navController.navigate(R.id.paymentMethodFragment);
            return true;
        }
        if (id == R.id.action_billetera) {
            navController.navigate(R.id.paymentWalletFragment);
            return true;
        }
        if (id == R.id.action_agregardirecciones) {
            // Abrir AgregarDirecciones (ID en nav_graph: agregarDirecciones)
            navController.navigate(R.id.agregarDirecciones);
            return true;
        }

        if (id == R.id.action_direcciones) {
            // Abrir lista de direcciones (ID en nav_graph: direccionesFragment)
            navController.navigate(R.id.direccionesFragment);
            return true;
        }

        if (id == R.id.action_buscarmapa) {
            // Ir al mapa (ID en nav_graph: buscarMapa)
            navController.navigate(R.id.buscarMapa);
            return true;
        }
        if (id == R.id.action_perfilUsuario) {
            // Ir al mapa (ID en nav_graph: buscarMapa)
            navController.navigate(R.id.perfilUsuario);
            return true;
        }
        if (id == R.id.action_editorPerfil) {
            // Ir al mapa (ID en nav_graph: buscarMapa)
            navController.navigate(R.id.editarPerfil);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Usar la instancia guardada de navController para manejar el botón de "atrás"
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}