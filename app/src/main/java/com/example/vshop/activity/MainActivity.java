package com.example.vshop.activity;

import com.example.vshop.R;
import com.example.vshop.model.Giohang;
import com.example.vshop.ultil.CheckConnection;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final Fragment fragmentHome = new TrangchuFragment();
    final Fragment fragmentDienthoai = new DienthoaiFragment();
    final Fragment fragmentLaptop = new LaptopFragment();
    final Fragment fragmentThongtin = new ThongtinFragment();
    final Fragment fragmentCheckInternet = new CheckInternetFragment();

    Toolbar toolbar;
    public static ArrayList<Giohang> manggiohang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manggiohang = new ArrayList<>();
        shoppingCartAction();

        if (CheckConnection.haveNetworkConnection(getApplicationContext())){
            BottomNavigationAction();
        }else{
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragmentCheckInternet).commit();
            CheckConnection.showToast_Short(getApplicationContext(),"Không có kết nối internet");
        }
    }

    private void BottomNavigationAction() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentHome).commit(); //start app with home fragment

    }

    private void shoppingCartAction() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // tạo icon menu giỏ hàng trên thanh toolbar
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //xét sự kiện click vào icon giỏ hàng
        switch (item.getItemId()){
            case R.id.menugiohang:
                Intent intent = new Intent(getApplicationContext(), com.example.vshop.activity.Giohang.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //tạo biến bắt sự kiện trên thanh bottom navigation
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectFragment = new Fragment();

                    switch (item.getItemId()){
                        case R.id.navigation_home:
                            selectFragment = fragmentHome;
                            break;
                        case R.id.navigation_smartphone:
                            selectFragment = fragmentDienthoai;
                            break;
                        case R.id.navigation_laptop:
                            selectFragment = fragmentLaptop;

                            break;
                        case R.id.navigation_information:
                            selectFragment = fragmentThongtin;
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectFragment).commit();
                    return true;
                }
            };
}
