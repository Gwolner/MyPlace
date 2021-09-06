package br.edu.ifpe.tads.pdm.myplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.edu.ifpe.tads.pdm.myplaces.databinding.ActivityMapsBinding;
import br.edu.ifpe.tads.pdm.myplaces.models.Local;
import br.edu.ifpe.tads.pdm.myplaces.models.CategoriasLocais;

import static android.graphics.BitmapFactory.decodeByteArray;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int FINE_LOCATION_REQUEST = 23;
    private GoogleMap mMap = null;
    private ActivityMapsBinding binding;
    private boolean fine_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Solicitando permissão do usuário
        requestPermission();
    }

    //Solicita permissão do usuario
    private void requestPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        this.fine_location = (permissionCheck == PackageManager.PERMISSION_GRANTED);
        if (this.fine_location) return;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_REQUEST);
    }

    //Chamado após o usuario conceder ou negar a permissão
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = (grantResults.length > 0) &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED);
        this.fine_location = (requestCode == FINE_LOCATION_REQUEST) && granted;

        //Essa linha habilita a camada “My Location”, com o botão que leva a posição atual.
        if (mMap != null) {
            mMap.setMyLocationEnabled(this.fine_location);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
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
        Local.listarLocaisNoMapa(mMap);

        //Rferência inicial
        LatLng recife = new LatLng(-8.059830483094396, -34.882885962724686);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(recife));

        //Toast (mensagem na tela) quando o marcador é tocado.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String lat = String.valueOf(marker.getPosition().latitude);
                String lng = String.valueOf(marker.getPosition().longitude);

                Intent intent = new Intent(getApplicationContext(), ExibeLocalActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);
                /*Toast.makeText(MapsActivity.this,
                        "Você clicou em " + latlng,
                        Toast.LENGTH_SHORT).show();*/
                return false;
            }
        });

        //Adiciona marcadores com o toque do usuário no mapa
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Double lat = latLng.latitude;
                Double lng = latLng.longitude;

                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> addresses = gcd.getFromLocation(lat, lng, 1);

                    String nomeEstado = addresses.get(0).getAdminArea();
                    String nomeCidade = addresses.get(0).getSubAdminArea();

                    Intent intent = new Intent(getApplicationContext(), FormLocalActivity.class);
                    intent.putExtra("cidade", nomeCidade);
                    intent.putExtra("estado", nomeEstado);
                    intent.putExtra("lat", lat.toString());
                    intent.putExtra("lng", lng.toString());

                    startActivity(intent);

                    /*
                    mMap.addMarker(new MarkerOptions().
                            position(latLng).
                            title("Adicionado em " + addresses.get(0).getAdminArea() + " "+addresses.get(0).getSubAdminArea()).
                            icon(BitmapDescriptorFactory.defaultMarker(0))); */
                } catch (IOException e) {
                    e.printStackTrace();
                }




            }
        });

        //Localização atual (“alvo” no canto superior direito)
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Toast.makeText(MapsActivity.this, "Indo para a sua localização.", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //Tratador de evento para clique na localização atual (círculo azul)
        mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {
                Toast.makeText(MapsActivity.this, "Você está aqui!", Toast.LENGTH_SHORT).show();
            }
        });

        //Essa linha habilita a camada “My Location”, com o botão que leva a posição atual.
        mMap.setMyLocationEnabled(this.fine_location);
        
    }

    public static int corMarcador(Local local){
        //PARQUE, RESTAURANTE, BAR, MUSEU, PRAIA, LOJA, JOGO
        CategoriasLocais categoria = local.getCategoria();
        if(categoria.equals(CategoriasLocais.PARQUE)){
            return 90;
        }else if(categoria.equals(CategoriasLocais.RESTAURANTE)){
            return 330;
        }else if(categoria.equals(CategoriasLocais.BAR)){
            return 30;
        }else if(categoria.equals(CategoriasLocais.MUSEU)){
            return 300;
        }else if(categoria.equals(CategoriasLocais.PRAIA)){
            return 60;
        }else if(categoria.equals(CategoriasLocais.LOJA)){
            return 210;
        }else if(categoria.equals(CategoriasLocais.JOGO)){
            return 0;
        }else{
            return 0;
        }
    }


    //Tratador de clique do botão button_location
    public void currentLocation(View view) {
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null) {
                    Toast.makeText(MapsActivity.this, "Localização atual: \n" +
                            "Lat: " + location.getLatitude() + " " +
                            "Long: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void buttonSignOutClick(MenuItem view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mAuth.signOut();
            this.finish();
        } else {
            Toast.makeText(MapsActivity.this, "Erro!", Toast.LENGTH_SHORT).show();
        }
    }
}