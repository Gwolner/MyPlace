package br.edu.ifpe.tads.pdm.myplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;

import br.edu.ifpe.tads.pdm.myplaces.models.Local;

import static android.graphics.BitmapFactory.decodeByteArray;

public class ExibeLocalActivity extends AppCompatActivity {
    private static ArrayList<Bitmap> fotosBM = new ArrayList<Bitmap>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibe_local);

        Bundle extras = getIntent().getExtras();
        String lat = extras.getString("lat");
        String lng = extras.getString("lng");

        String latlng = lat.replace(".", "")+lng.replace(".", "");

        /* Download das imagens associadas ao local clicado */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("fotos/"+latlng);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference foto : listResult.getItems()) {
                            // All the items under listRef.
                            final long FIVE_MEGABYTES = 1024 * 1024 * 5;
                            foto.getBytes(FIVE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Data for "images/island.jpg" is returns, use this as needed
                                    Bitmap fotoBM = decodeByteArray(bytes,0,bytes.length);

                                    ViewFlipper vf = findViewById(R.id.fotos);

                                    ImageView imageView = new ImageView(getApplicationContext());
                                    imageView.setImageBitmap(fotoBM);

                                    vf.addView(imageView);
                                    vf.setFlipInterval(4000);//4 segundos
                                    vf.setAutoStart(true);

                                    //vf.setAnimation(getApplicationContext(), android.R.anim.slide_in_left);
                                    //vf.setAnimation(getApplicationContext(), android.R.anim.slide_out_right);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("locais");

        mDatabase.child("local"+latlng).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    String json = String.valueOf(task.getResult().getValue());
                    Gson gson = new Gson();
                    Local local = gson.fromJson(json,Local.class);
                    Log.e("AQUI:", local.getNome());

                    TextView textViewNomeLocal = findViewById(R.id.textViewNomeLocal);
                    textViewNomeLocal.setText(local.getNome());
                    TextView textViewCategoria = findViewById(R.id.textViewCategoria);
                    textViewCategoria.setText(local.getCategoria().toString());
                    TextView textViewCidade = findViewById(R.id.textViewCidade);
                    textViewCidade.setText(local.getCidade());
                    TextView textViewEstado = findViewById(R.id.textViewEstado);
                    textViewEstado.setText(local.getEstado());
                    TextView textViewObservacao = findViewById(R.id.textViewObservacao);
                    textViewObservacao.setText(local.getObservacao());
                    TextView textViewAvaliacao = findViewById(R.id.textViewAvaliacao);
                    textViewAvaliacao.setText(String.valueOf(local.getAvaliacao()));
                }
            }
        });

    }
}