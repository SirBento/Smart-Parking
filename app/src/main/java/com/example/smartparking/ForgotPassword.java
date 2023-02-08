package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText emailReset;
    private Button btnresetPass;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        emailReset =(EditText) findViewById(R.id.passReset);
        btnresetPass =(Button) findViewById(R.id.btnReset);
        mAuth = FirebaseAuth.getInstance();

        btnresetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                resetPassword();
            }


        });

    }




    private void resetPassword() {

        String e_mail = emailReset.getText().toString().trim();

         //checking if the email text field is empty or not
        if(e_mail.isEmpty()){
            emailReset.setError("Email is required");
            emailReset.requestFocus();
            return;
        }
        // checking if the email entered is valid
        if(!Patterns.EMAIL_ADDRESS.matcher(e_mail).matches()){
            emailReset.setError("Please provide a email ");
            emailReset.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(e_mail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(ForgotPassword.this, "Check your email to reset your password", Toast.LENGTH_LONG).show();

                }else {

                    Toast.makeText(ForgotPassword.this, "Failed to reset your password. PLEASE TRY AGAIN!!", Toast.LENGTH_LONG).show();

                }


            }
        });
    }
}