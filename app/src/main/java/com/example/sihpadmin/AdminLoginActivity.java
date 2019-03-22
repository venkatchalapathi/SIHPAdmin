package com.example.sihpadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
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

public class AdminLoginActivity extends AppCompatActivity {
    EditText email, password;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    AlertDialog.Builder a_dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        mAuth = FirebaseAuth.getInstance();
        a_dialog = new AlertDialog.Builder(this);

        email = findViewById(R.id.ademail_edittext);
        password = findViewById(R.id.adpassword_edittext);
    }

    public void adminForgotPassword(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.forgot_screen, null);
        final EditText mail = v.findViewById(R.id.resetmail);
        a_dialog.setView(v);
        a_dialog.setTitle("Forgot Password!!!");
        a_dialog.setPositiveButton("Send Recovery Mail", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                if (Utils.isNetworkAvailable(AdminLoginActivity.this)) {
                    showProgress();
                    Utils.showProgress(AdminLoginActivity.this);
                    String sMail = mail.getText().toString();
                    FirebaseAuth.getInstance().sendPasswordResetEmail(sMail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(AdminLoginActivity.this, "Recovery Mail sent Successfully check your mail.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AdminLoginActivity.this, "Recovery Failed", Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                } else {
                    showAlert();
                }
            }
        });
        a_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        a_dialog.show();

    }
    public void navigateToAdminRegister(View view) {
        startActivity(new Intent(this, AdminRegistration.class));

    }

    public void adminlogIn(View view) {
        if (!TextUtils.isEmpty(email.getText().toString())){
            if (!TextUtils.isEmpty(password.getText().toString())) {
                loginToAdmin();
            }else {
                password.setError("Enter valid password...");
            }
        }
        else {
            email.setError("Enter email id");
        }
    }

    private void loginToAdmin() {

        if (Utils.isNetworkAvailable(this)) {
            showProgress();
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent intent = new Intent(AdminLoginActivity.this, AdminDrawerActivity.class);
                                startActivity(intent);
                                progressDialog.dismiss();
                                // updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(AdminLoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                // updateUI(null);
                            }

                            // ...
                        }
                    });
        } else {
            showAlert();
        }
    }

    private void showAlert() {
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
        Glide.with(AdminLoginActivity.this)
                .load(R.drawable.anim).into(imageView);
        builder.show();
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging In...");
        progressDialog.setMessage("Please wait while we check the Credentials...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }
}
