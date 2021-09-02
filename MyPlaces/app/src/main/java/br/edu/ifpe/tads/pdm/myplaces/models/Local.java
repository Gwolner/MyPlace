package br.edu.ifpe.tads.pdm.myplaces.models;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import br.edu.ifpe.tads.pdm.myplaces.MapsActivity;

import static android.content.ContentValues.TAG;

@IgnoreExtraProperties
public class Local {
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference myRef  = database.getReference("locais");

    private String nome;
    private String lat;
    private String lng;
    private String cidade;
    private String estado;
    private float avaliacao;
    private String observacao;
    private CategoriasLocais categoria;
    private String diretorioFotos; //atributo que armazena o caminho do diretório das fotos do local.

    public Local(){}
    public Local(String nome, String lat, String lng, String cidade, String estado,
                 float avaliacao, String observacao, CategoriasLocais categoria){
        this.nome = nome;
        this.lat = lat;
        this.lng = lng;
        this.cidade = cidade;
        this.estado = estado;
        this.avaliacao = avaliacao;
        this.observacao = observacao;
        this.categoria = categoria;
        //comando para criar diretório para fotos com o nome do local
    }

    public static void adicionarLocal(String idLocal, Local local){
        myRef.child(idLocal.replace(".", "")).setValue(local);
    }

    public static void listarLocaisNoMapa(GoogleMap mMap){
        /* Registrar evento para listagem de locais (os locais são listados no momento em que
        o evento é registrado e sempre que um local novo for persistido. */
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
                            icon(BitmapDescriptorFactory.defaultMarker(MapsActivity.corMarcador(local))));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRef.addValueEventListener(locaisListener);
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public float getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(float avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getDiretorioFotos() {
        return diretorioFotos;
    }

    public void setDiretorioFotos(String diretorioFotos) {
        this.diretorioFotos = diretorioFotos;
    }

    public CategoriasLocais getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriasLocais categoria) {
        this.categoria = categoria;
    }
}
