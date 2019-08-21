package com.xuxi.lbstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient;

    private TextView positionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerNotifyLocationListener(new MyLocationListener());

        positionText = findViewById(R.id.position_text_view);
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);

        }else {
            requestLocation();
        }

    }

    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;
                default:
                    break;
        }
    }

    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    StringBuilder currentPostion = new StringBuilder();
                    currentPostion.append("纬度:").append(bdLocation.getLatitude()).append("\n");
                    currentPostion.append("经线:").append(bdLocation.getLongitude()).append("\n");
                    currentPostion.append("定位方式:");
                    if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                        currentPostion.append("GPS");
                    }else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
                        currentPostion.append("网络");
                    }
                    currentPostion.append("国家:").append(bdLocation.getCountry()).append("\n");
                    currentPostion.append("省:").append(bdLocation.getProvince()).append("\n");
                    currentPostion.append("市:").append(bdLocation.getCity()).append("\n");
                    currentPostion.append("区:").append(bdLocation.getDistrict()).append("\n");
                    currentPostion.append("街道:").append(bdLocation.getStreet()).append("\n");
                    positionText.setText(currentPostion);

                }
            });
        }


    }

}
