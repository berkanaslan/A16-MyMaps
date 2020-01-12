package com.berkanaslan.mymaps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener { /*GoogleMap metodu import ederek
                                                                                                                        * tıklandığında adres gösterebiliriz.
                                                                                                                        * */

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

               /** mMap.clear(); //Harita her güncellendiğinde Marker oluşturmasını engelliyoruz.

                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15)); */

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        /* Eğer ContextCompat yazmasaydık API23 öncesi telefonlarda çalışmayacaktı. ContexCompat API23
         * sonrası gelen izin verme ile sormamayı birlikte ele almaya yarıyor.
         *
         * Aşağıda if ile sdk kontorlü de yaptık. İkisi de kullanılabilir.*/

    /**   if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Kullanıcıdan lokasyona ulaşmak için izin al, eğer kontrol olumsuz sonuclanırsa
            ActivityCompat.requestPermissions(this, new  String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500,0,locationListener);

        } */

        //0, 0 verince her saniye güncellenen bir konum elde ederiz. 500, 0 verdik.

     /** İkinci versiyon: */

        if (Build.VERSION.SDK_INT >=23) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500,0,locationListener);

                lastLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER); //Son bilinen lokasyonu al.

                System.out.println("Last Location:"+lastLocation);
                LatLng lastUserLoc = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().title("Last Known Location").position(lastUserLoc));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLoc, 15));
            }

        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500,0,locationListener);
            lastLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER); //Son bilinen lokasyonu al.

            System.out.println("Last Location:"+lastLocation);

            LatLng lastUserLoc = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().title("Last Known Location").position(lastUserLoc));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLoc, 15));

        } //Eğer SDK Kontrolü yaparsak da üstteki açıklama satırı gibi çalışır.

        mMap.setOnMapLongClickListener(this); //onMapReady olduğunda uzun click metodumuzu çağırıyoruz.

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //İzin sonuçları metodu
    //Kullanıcı ilk defa izin veriyorsa onRequestPer.. metodu kullanıyoruz.

        if (grantResults.length > 0) { //Bir sonuç geldiyse
            if (requestCode == 1) { //1 değerini ActivityCompat içerisinde verdik.
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,0,locationListener);
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        mMap.clear();

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if (addressList != null && addressList.size() > 0) { //addressList değişkeni boş değilse
                if(addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare();
                }
                    if(addressList.get(0).getSubThoroughfare() != null) {
                        address += addressList.get(0).getSubThoroughfare();
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (address.matches("")) {
            address = "No address"; //addres değişkeni boş ise "no address yazacak.
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

    }
}
