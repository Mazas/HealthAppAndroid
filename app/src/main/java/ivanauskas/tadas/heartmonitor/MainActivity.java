package ivanauskas.tadas.heartmonitor;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    private DrawerLayout drawerLayout;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        container = findViewById(R.id.content_frame);

        Fragment homeFragment = new HomeFragment();
        getFragmentManager().beginTransaction()
                .replace(container.getId(), homeFragment)
                .addToBackStack(null)
                .commit();

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        switch (item.getItemId()){
                            case R.id.nav_home:

                                Fragment homeFragment = new HomeFragment();
                                getFragmentManager().beginTransaction()
                                        .replace(container.getId(), homeFragment)
                                        .addToBackStack(null)
                                        .commit();

                                return true;
                            case R.id.nav_settings:

                                Fragment settingsPage = new SettingsPage();
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
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
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
    public void onFragmentInteraction(Uri uri) {

    }
}
