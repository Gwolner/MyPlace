package br.edu.ifpe.tads.pdm.myplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText edEmail;
    private EditText edPassword;
    private EditText edName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edEmail = (EditText) findViewById(R.id.edit_email);
        edPassword = (EditText) findViewById(R.id.edit_password);
        edName = findViewById(R.id.edit_name);
    }

    public void buttonSignUpClick(View view) {
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String msg = task.isSuccessful() ? "SIGN UP OK!": "SIGN UP ERROR!";
                        Toast.makeText(SignUpActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}