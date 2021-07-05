package br.edu.ifpe.tads.pdm.myplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

import br.edu.ifpe.tads.pdm.myplaces.entities.CategoriasLocal;
import br.edu.ifpe.tads.pdm.myplaces.entities.Local;
import br.edu.ifpe.tads.pdm.myplaces.repositories.LocalRepository;

public class FormLocalActivity extends AppCompatActivity {
    private String lat;
    private String lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_local);

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
        for(CategoriasLocal c: CategoriasLocal.values()){
            items.add(c.toString());
        }
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
    }

    public void salvarLocal(View view){

        EditText editTextNomeLocal = findViewById(R.id.editTextNomeLocal);
        EditText editTextCidade = findViewById(R.id.editTextCidade);
        EditText editTextEstado = findViewById(R.id.editTextEstado);
        EditText editTextObservacao = findViewById(R.id.editTextObservacao);
        Spinner spinnerCategorias = findViewById(R.id.spinnerCategorias);
        RatingBar ratingBarAvaliacao = findViewById(R.id.ratingBarAvaliacao);

        CategoriasLocal categoria = CategoriasLocal.valueOf(spinnerCategorias.getSelectedItem().toString());

        LocalRepository rep = LocalRepository.getInstance();
        Local local = new Local(editTextNomeLocal.getText().toString(), this.lat, this.lng,
                                editTextCidade.getText().toString(), editTextEstado.getText().toString(),
                                ratingBarAvaliacao.getRating(), editTextObservacao.getText().toString(), categoria);

        rep.adicionarLocal(local);

        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);

    }

}