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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEmail, mPassword, mPhone, cPassword, mName;
    private Button register;
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//assign  the variables for the data input
        mEmail = findViewById(R.id.email);
        mName = findViewById(R.id.name);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.phone);
        cPassword = findViewById(R.id.cpassword);
// get instance of firebase auth
        mAuth = FirebaseAuth.getInstance();
// initialize the auth listener
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user!= null){
                    Intent intent = new Intent(RegisterActivity.this, CustomerMapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        register = findViewById(R.id.register);
//        runs when the register button is clicked
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                get values from the input fields
                final String email = mEmail.getText().toString();
                final String name = mName.getText().toString();
                final String password = mPassword.getText().toString();
                final String cpassword = cPassword.getText().toString();
                final String phone = mPhone.getText().toString();
//validate the input
                if (email.equals("") || password.equals("")){
                    mEmail.setText("");
                    mPassword.setText("");
                    Toast.makeText(RegisterActivity.this, "All fields must be filled", Toast.LENGTH_SHORT).show();
//                    mRegistration.setBackgroundResource(R.drawable.buttonstyle);
//                    mRegistration.revertAnimation();
                }else
                {
                    if (password.equals(cpassword)){
//                        register a new firebase auth user
                        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_LONG).show();

                                    mEmail.setText("");
                                    mPassword.setText("");
//                                mRegistration.revertAnimation();
//                                mRegistration.setBackgroundResource(R.drawable.buttonstyle);

                                }else{
//                                    if successful? store name and phone number in the database
                                    String userid = mAuth.getCurrentUser().getUid();
                                    DatabaseReference currentuserdb = FirebaseDatabase.getInstance().getReference().child("usertype").child(userid);
                                    currentuserdb.setValue("customer");

                                    DatabaseReference userinfo = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userid);


                                    Map userInfo = new HashMap();
                                    userInfo.put("name", name);
                                    userInfo.put("phone", phone);
//                                userInfo.put("phone", mPhone);


                                    userinfo.updateChildren(userInfo);
//                                mRegistration.setBackgroundResource(R.drawable.buttonstyle);
//                                mRegistration.revertAnimation();

                                }
                            }
                        });
                    }else {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
//        start the auth listener when the app starts
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stop the auth listener when the app stops
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }
}
