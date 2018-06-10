package test.com.potherdabai.potherdabi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.rom4ek.arcnavigationview.ArcNavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    ArcNavigationView arcNavigationView;
    Intent intent;
    ImageView toilet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer);
        setUpUIMain();
        setSupportActionBar(toolbar);



        toggle = new ActionBarDrawerToggle(HomeActivity.this,drawerLayout,
                toolbar,R.string.open_drawer,R.string.close_drawer);
        //navigationView.setNavigationItemSelectedListener(HomeActivity.this);
        arcNavigationView.setNavigationItemSelectedListener(HomeActivity.this);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

    }

    public void setUpUIMain() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //navigationView = (NavigationView)findViewById(R.id.navigation_view);
        arcNavigationView =(ArcNavigationView)findViewById(R.id.nav_view);
        toilet = (ImageView)findViewById(R.id.img_toilet);
    }

    public void goMapActivity(View v){
        if(v.getId()== R.id.img_toilet){
            intent = new Intent(HomeActivity.this,MapsActivity.class);
            startActivity(intent);
        }
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){
            case R.id.home:
                intent = new Intent(HomeActivity.this,HomeActivity.class);
                startActivity(intent);

                break;

            case R.id.history:
//                intent = new Intent(MainActivity.this,OverViewActivity.class);
//                startActivity(intent);
                Toast.makeText(HomeActivity.this, "History", Toast.LENGTH_LONG).show();
                break;

            case R.id.contribution:
//                intent = new Intent(MainActivity.this,BRAPrimarySchoolActivity.class);
//                startActivity(intent);
                Toast.makeText(HomeActivity.this, "Contribution", Toast.LENGTH_LONG).show();
                break;

            case R.id.aboutus:
//                intent = new Intent(MainActivity.this,BRAcPreprimaryActivity.class);
//                startActivity(intent);
                Toast.makeText(HomeActivity.this, "About us", Toast.LENGTH_LONG).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;


    }
}
