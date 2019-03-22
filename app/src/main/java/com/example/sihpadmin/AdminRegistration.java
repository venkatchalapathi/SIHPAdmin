package com.example.sihpadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminRegistration extends AppCompatActivity {
    ProgressDialog progressDialog;
    EditText email, password, re_enterPassword, mobile, u_name, emp_id;
    EditText lat,lng;
    private FirebaseAuth mAuth;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_registration);database = FirebaseDatabase.getInstance();
        reference = database.getReference("SIHP");
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.adreg_email);
        mobile = findViewById(R.id.adreg_phone);
        u_name = findViewById(R.id.adreg_name);
        emp_id = findViewById(R.id.adreg_empid);
        password = findViewById(R.id.adreg_password);
        re_enterPassword = findViewById(R.id.adreenter_password);
        lat= findViewById(R.id.lat);
        lng= findViewById(R.id.longi);

    }
    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registering User...");
        progressDialog.setMessage("Please wait ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
    public void adregisterButton(View view) {
        if (Utils.isNetworkAvailable(this)) {
            showProgress();
            if (!TextUtils.isEmpty(u_name.getText().toString())) {
                if (!TextUtils.isEmpty(email.getText().toString())) {
                    if (!TextUtils.isEmpty(mobile.getText().toString())) {
                        if (!TextUtils.isEmpty(emp_id.getText().toString())) {
                            if (password.getText().toString().equals(re_enterPassword.getText().toString())) {
                                registerAdmin(u_name.getText().toString(),
                                        email.getText().toString(), mobile.getText().toString(),
                                        emp_id.getText().toString(), password.getText().toString(),
                                        lat.getText().toString(),lng.getText().toString());
                            } else {
                                password.setError("Password Doesn't match");
                                Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            emp_id.setError("Enter EMP ID");
                        }
                    } else {
                        mobile.setError("Enter Mobile number ");
                    }
                } else {
                    email.setError("Enter email id");
                }
            } else {
                u_name.setError("Enter ur name");
            }
        } else {
            showAlert();
        }
    }

    private void registerAdmin(final String u_name, final String email, final String mobile, final String
            emp_id, final String password,final String lat,final String longi) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            saveUserDataToDB(u_name,email,mobile,emp_id,lat,longi);
                            FirebaseUser user = mAuth.getCurrentUser();
                            // updateUI(user);


                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AdminRegistration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });

    }

    private void saveUserDataToDB(String u_name, String email, String mobile, String emp_id, String lat,String longitude) {

        Map<String,Object> user = new HashMap<>();
        user.put("name",u_name);
        user.put("email",email);
        user.put("mobile",mobile);
        user.put("emp_id",emp_id);
        user.put("Lat",lat);
        user.put("Longi",longitude);

        reference.child("Admins").push().setValue(user);
        Toast.makeText(this, "Data Saved!", Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        finish();
    }

    public void showAlert() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Please check your Internet connection...");
        builder.setTitle("No internet Access,Connection Failed");
        builder.setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(
                        Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        setContentView(R.layout.error_loading_screen);
        ImageView imageView = findViewById(R.id.errorimg);
        Glide.with(AdminRegistration.this)
                .load(R.drawable.anim).into(imageView);
        builder.show();
    }
}

