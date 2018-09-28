package me.itsdavis.apps.uber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class DriverLoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;
    private CircularProgressButton mLogin, mRegistration;

    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user!= null){
                    Intent intent = new Intent(DriverLoginActivity.this, DriverMapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        mLogin = findViewById(R.id.login);
        mRegistration = findViewById(R.id.registration);

        mRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegistration.startAnimation();
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if (email.equals("")|| password.equals("")){
                    Toast.makeText(DriverLoginActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    mEmail.setText("");
                    mPassword.setText("");
                    mRegistration.revertAnimation();
                }else
                {
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(DriverLoginActivity.this, "Registration Error", Toast.LENGTH_LONG).show();

                                mRegistration.revertAnimation();
                                mEmail.setText("");
                                mPassword.setText("");

                            }else{
                                String userid = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentuserdb = FirebaseDatabase.getInstance().getReference().child("Users").child("Riders").child(userid).child("name");
                                currentuserdb.setValue(email);
                                mRegistration.revertAnimation();
                            }
                        }
                    });
                }

            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLogin.startAnimation();
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if (email.equals("")|| password.equals("")){
                    Toast.makeText(DriverLoginActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
                    mEmail.setText("");
                    mPassword.setText("");
                    mLogin.revertAnimation();
                }

                else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()){
                                Toast.makeText(DriverLoginActivity.this, "There was a problem signing in", Toast.LENGTH_LONG).show();

                                mLogin.revertAnimation();
                                mEmail.setText("");
                                mPassword.setText("");
                            }
                            mLogin.revertAnimation();
                        }
                    });
                }


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
