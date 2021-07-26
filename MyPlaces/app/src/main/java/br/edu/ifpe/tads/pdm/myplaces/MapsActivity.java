package br.edu.ifpe.tads.pdm.myplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.edu.ifpe.tads.pdm.myplaces.databinding.ActivityMapsBinding;
import br.edu.ifpe.tads.pdm.myplaces.entities.CategoriasLocal;
import br.edu.ifpe.tads.pdm.myplaces.entities.Local;
import br.edu.ifpe.tads.pdm.myplaces.repositories.LocalRepository;

import static android.content.ContentValues.TAG;

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

        //Essa linha habilita o botão caso o usuário tenha dado a permissão
        findViewById(R.id.button_location).setEnabled(this.fine_location);
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


        //Rferência inicial
        LatLng gramado = new LatLng(-29.36, -50.87);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gramado));


        /* Registrar evento para listagem de locais (os locais são listados no momento em que
        o evento é registrado e sempre que um local novo for persistido. */
        FirebaseDatabase database =  FirebaseDatabase.getInstance();
        DatabaseReference locaisRef = database.getReference("locais");

        ValueEventListener locaisListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Local object and use the values to update the UI
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Local local = ds.getValue(Local.class);
                    LatLng coordenadas = new LatLng(Double.parseDouble(local.getLat()), Double.parseDouble(local.getLng()));
                    mMap.addMarker(new MarkerOptions().
                            position(coordenadas).
                            title(local.getNome()).
                            icon(BitmapDescriptorFactory.defaultMarker(240)));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        locaisRef.addValueEventListener(locaisListener);

        //Toast (mensagem na tela) quando o marcador é tocado.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this,
                        "Você clicou em " + marker.getTitle(),
                        Toast.LENGTH_SHORT).show();
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

        //Essa linha habilita o botão caso o usuário tenha dado a permissão
        findViewById(R.id.button_location).setEnabled(this.fine_location);
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
}