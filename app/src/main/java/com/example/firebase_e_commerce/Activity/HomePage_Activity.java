package com.example.firebase_e_commerce.Activity;

import static com.example.firebase_e_commerce.Activity.MainActivity.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.example.firebase_e_commerce.Fragment.Add_Product_Fragment;
import com.example.firebase_e_commerce.Fragment.Show_AllProducts_Fragment;
import com.example.firebase_e_commerce.Fragment.View_Product_Fragment;
import com.example.firebase_e_commerce.R;
import com.google.android.material.navigation.NavigationView;

public class HomePage_Activity extends AppCompatActivity {

    NavigationView navigationView;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    AppCompatTextView header_email;
    AppCompatImageView header_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);

        View view = navigationView.getHeaderView(0);
        header_image = view.findViewById(R.id.header_image);
        header_email = view.findViewById(R.id.header_email);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.Open_Drawer,R.string.Close_Drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        header_email.setText(""+ preferences.getString("email",null));

        addFragment(new View_Product_Fragment());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.add_product)
                {
                    replaceFragment(new Add_Product_Fragment());
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                if (id == R.id.view_product)
                {
                    replaceFragment(new View_Product_Fragment());
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                if (id == R.id.show_all_products)
                {
                    replaceFragment(new Show_AllProducts_Fragment());
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
                if (id == R.id.logout)
                {
                    editor.putBoolean("LoginStatus",false);
                    editor.commit();
                    Intent intent = new Intent(HomePage_Activity.this, MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_main,fragment);
        transaction.commit();
    }

    private void addFragment(Fragment fragment)
    {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_main,fragment);
        transaction.commit();
    }
}