package me.itsdavis.apps.uber;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail, mPassword;

    private String type;
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

// this is a listener that listens if there is auser currently loggged in

        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user!= null){
                    String userid = user.getUid();


                    DatabaseReference usertype = FirebaseDatabase.getInstance().getReference().child("usertype").child(userid);

//        if there is, , it checks for the user type of the user in the database then redirects to the respective activities
                    usertype.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            type = dataSnapshot.getValue().toString();
                            if (type.equals("driver")){
                                Intent intent = new Intent(LoginActivity.this, DriverMapsActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                            else {
                                Intent intent = new Intent(LoginActivity.this, CustomerMapsActivity.class);
                                startActivity(intent);
                                finish();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    }

            }
        };
//initialize the  rgister floating button
        FloatingActionButton reg = findViewById(R.id.reg);

//        on click listener redirects to the register activity when the floating button is clicked
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

//        submits the form
        Button login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                login.startAnimation();

//                validates the input to check for empty fields
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                if (email.equals("") || password.equals("")) {
                    mEmail.setText("");
                    mPassword.setText("");
                    Toast.makeText(LoginActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
//                    mLogin.setBackgroundResource(R.drawable.buttonstyle);
//                    mLogin.revertAnimation();

                } else {
//                    firebase function to sign in with firebase auth
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "There was a problem signing in", Toast.LENGTH_LONG).show();

//                                login.revertAnimation();
                                mEmail.setText("");
                                mPassword.setText("");
                            }

//                            mLogin.revertAnimation();

                        }
                    });


                }
            }
        });
    }
    @Override
    protected void onStart() {

//        start the auth listener when the app starts
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stop the listener when the app stops
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}