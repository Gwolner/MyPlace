package br.edu.ifpe.tads.pdm.myplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import br.edu.ifpe.tads.pdm.myplaces.models.Local;
import br.edu.ifpe.tads.pdm.myplaces.models.CategoriasLocais;


public class FormLocalActivity extends AppCompatActivity {
    private String lat;
    private String lng;
    private static final int PICK_IMAGE = 100;
    private ArrayList<ImageView> fotos = new ArrayList<ImageView>();
    private View formLocal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_local);
        formLocal = findViewById(R.id.formLocal);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String cidade = extras.getString("cidade");
            String estado = extras.getString("estado");
            this.lat = extras.getString("lat");
            this.lng = extras.getString("lng");

            EditText editTextCidade = findViewById(R.id.editTextCidade);
            editTextCidade.setText(cidade);
            EditText editTextEstado = findViewById(R.id.editTextEstado);
            editTextEstado.setText(estado);
        }

        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinnerCategorias);
        //create a list of items for the spinner.
        ArrayList<String> items = new ArrayList<String>();
        for(CategoriasLocais c: CategoriasLocais.values()){
            items.add(c.toString());
        }
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void abrirGaleria(View view){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            LinearLayout layout = (LinearLayout) findViewById(R.id.fotos);
            ImageView imageView = new ImageView(this);
            imageView.setImageURI(data.getData());

            int length = data.getData().getPath().split("/").length;
            String nomeFoto = data.getData().getPath().split("/")[length-1];
            imageView.setTag(nomeFoto);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
            params.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(params);

            fotos.add(imageView);

            layout.addView(imageView);
        }
    }

    public void salvarLocal(View view){
        String latlng = lat.replace(".", "")+this.lng.replace(".", "");

        //Salvando imagem no storage do FireBase
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference("fotos");
        // Create a reference to "imagens/latlng/"

        for(ImageView imageView: fotos) {
            StorageReference fotosRef = storageRef.child("local"+latlng + "/" + String.valueOf(imageView.getTag()));

            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = fotosRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                }
            });
        }

        EditText editTextNomeLocal = findViewById(R.id.editTextNomeLocal);
        EditText editTextCidade = findViewById(R.id.editTextCidade);
        EditText editTextEstado = findViewById(R.id.editTextEstado);
        EditText editTextObservacao = findViewById(R.id.editTextObservacao);
        Spinner spinnerCategorias = findViewById(R.id.spinnerCategorias);
        RatingBar ratingBarAvaliacao = findViewById(R.id.ratingBarAvaliacao);

        CategoriasLocais categoria = CategoriasLocais.valueOf(spinnerCategorias.getSelectedItem().toString());

        Local local = new Local(editTextNomeLocal.getText().toString(), this.lat, this.lng,
                                editTextCidade.getText().toString(), editTextEstado.getText().toString(),
                                ratingBarAvaliacao.getRating(), editTextObservacao.getText().toString(), categoria);


        String idLocal = "local"+latlng;
        Local.adicionarLocal(idLocal, local);

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);

    }

}