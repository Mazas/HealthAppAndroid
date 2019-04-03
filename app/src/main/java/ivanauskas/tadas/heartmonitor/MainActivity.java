package ivanauskas.tadas.heartmonitor;

import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.HashMap;

import ivanauskas.tadas.heartmonitor.Model.BackgroundService;
import ivanauskas.tadas.heartmonitor.Model.FirestoreConnector;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    private DrawerLayout drawerLayout;
    private LinearLayout container;
    private Fragment homeFragment, settingsPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        container = findViewById(R.id.content_frame);

        homeFragment = new HomeFragment();
        settingsPage = new SettingsPage();
        getFragmentManager().beginTransaction()
                .replace(container.getId(), homeFragment)
                .addToBackStack(null)
                .commit();

        // start service
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        switch (item.getItemId()){
                            case R.id.nav_home:

                                getFragmentManager().beginTransaction()
                                        .replace(container.getId(), homeFragment)
                                        .addToBackStack(null)
                                        .commit();

                                return true;
                            case R.id.nav_settings:

                                getFragmentManager().beginTransaction()
                                        .replace(container.getId(), settingsPage)
                                        .addToBackStack(null)
                                        .commit();
                                return true;
                            case R.id.nav_logout:
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                return true;
                        }
                        return true;
                    }
                }
        );

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            ActionBar actionbar = getSupportActionBar();
            if (actionbar != null) {
                actionbar.setDisplayHomeAsUpEnabled(true);
            }
            if (actionbar != null) {
                actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String action, HashMap data) {
        String user = getIntent().getStringExtra("user");
        FirestoreConnector connector = new FirestoreConnector((String)data.get("email"));
        connector.updateUser(user,data);

    }
}
