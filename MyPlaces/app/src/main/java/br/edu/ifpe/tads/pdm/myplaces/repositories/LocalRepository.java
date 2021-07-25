package br.edu.ifpe.tads.pdm.myplaces.repositories;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import br.edu.ifpe.tads.pdm.myplaces.entities.CategoriasLocal;
import br.edu.ifpe.tads.pdm.myplaces.entities.Local;

public class LocalRepository {
    private static LocalRepository instanceLocalRepository = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private LocalRepository(){
        this.database = FirebaseDatabase.getInstance();
        this.myRef = database.getReference("locais");
    }

    public static LocalRepository getInstance() {
        if (instanceLocalRepository == null) {
            instanceLocalRepository = new LocalRepository();
        }
        return instanceLocalRepository;
    }

    public void adicionarLocal(String idLocal,Local local){
        this.myRef.child(idLocal.replace(".", "")).setValue(local);
    }

    public Local pegarLocal(String lat, String lng){ return null; }

    public void deletarLocal(String lat, String lng){ }

    public void atualizarLocal(Local novosDadosLocal, String lat, String lng){ }


}
