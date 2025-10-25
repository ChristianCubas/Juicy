package com.example.juicy;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

        // El id debe coincidir con el XML: nav_host_fragment
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            throw new IllegalStateException("No se encontró nav_host_fragment en activity_main.xml");
        }

        navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) return true;

        if (id == R.id.action_mpago)            { navController.navigate(R.id.paymentMethodFragment);  return true; }
        if (id == R.id.action_billetera)        { navController.navigate(R.id.paymentWalletFragment);   return true; }
        if (id == R.id.action_agregardirecciones){ navController.navigate(R.id.agregarDirecciones);     return true; }
        if (id == R.id.action_direcciones)      { navController.navigate(R.id.direccionesFragment);     return true; }
        if (id == R.id.action_buscarmapa)       { navController.navigate(R.id.buscarMapa);              return true; }
        if (id == R.id.action_perfilUsuario)    { navController.navigate(R.id.perfilUsuario);           return true; }
        if (id == R.id.action_editorPerfil)     { navController.navigate(R.id.editarPerfil);            return true; }

        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
