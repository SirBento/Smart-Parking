package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    // declaring variables of the widget type
    private EditText firstName, surName, email, phone, passWord1, passWord2;
    private Spinner accountType;
    private Button signUp;
    private String role;

    DatabaseReference AccountRole = FirebaseDatabase.getInstance().getReference("Roles");


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // accessing elements from a registration form  to send to the database
        signUp = findViewById(R.id.btnRegister);
        firstName = findViewById(R.id.signup_first_Name);
        surName = findViewById(R.id.signup_sur_name);
        email = findViewById(R.id.signup_email_Add);
        phone = findViewById(R.id.signup_Pnumber);
        passWord1 = findViewById(R.id.signup_Pass);
        passWord2 = findViewById(R.id.signup_Pass2);
        accountType = findViewById(R.id.accountType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.usertype, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        accountType.setAdapter(adapter);

// creating a variable for accessing the database authentication
        mAuth = FirebaseAuth.getInstance();


        signUp.setOnClickListener(view -> {

            if(!validateSecondPassword() | !validateFirstPassword() | !validateFirstName() |  !validatePhoneNumber() | !validateSurname() | !validateEmail() ){

                return;
            }

            registerUser();

        });


    }

    //if checks if there is a user already logged in, if so it opens the homepage
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser LoggediNuser = mAuth.getCurrentUser();
        if (LoggediNuser != null) {

            startActivity(new Intent(SignUp.this, Home.class));
            finish();
        }
    }


    private void registerUser() {

        String fname = firstName.getText().toString().trim();
        String sname = surName.getText().toString().trim();
        String e_mail = email.getText().toString().trim();
        String pass1 = passWord1.getText().toString().trim();
        String pass2 = passWord2.getText().toString().trim();
        String pnum = phone.getText().toString().trim();
        role = accountType.getSelectedItem().toString();

        //entering data into the firebase database during signup
        mAuth.createUserWithEmailAndPassword(e_mail, pass1)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {  //for if the user was successfully registered

                        User user = new User(fname, sname, e_mail, pnum, pass1);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {

                                    if (task1.isSuccessful()) {

                                        createUserRole();

                                        //checking if the account is verified , if not the user has to first verify the account before logging in

                                            //show a notification if the account was created successfully
                                            Toast.makeText(SignUp.this, "Account created successfully.Please check your email to verify then LogIn with your account", Toast.LENGTH_LONG).show();
                                            // Go to login page
                                            startActivity(new Intent(SignUp.this, Log_In.class));
                                            finish();

                                    } else {

                                        //show a notification if the account failed to create an account successfully
                                        Toast.makeText(SignUp.this, "Account creation failed. Please check your internet connection and try again!", Toast.LENGTH_LONG).show();

                                    }

                                });


                    }
                });


    }


    private boolean validateFirstName() {

        String fname = firstName.getText().toString().trim();


        if (fname.isEmpty()) {
            firstName.setError("First name is required");
            firstName.requestFocus();
            return false;

        } else if (fname.length() <= 3) {
            firstName.setError("Your name is invalid");
            firstName.requestFocus();
            return false;

        } else {
            firstName.setError(null);
            return true;

        }

    }

    private boolean validateSurname() {

        String sname = surName.getText().toString().trim();

        if (sname.isEmpty()) {
            surName.setError("Surname is required");
            surName.requestFocus();
            return false;

        } else if (sname.length() <= 3) {
            surName.setError("Your Surname is invalid");
            surName.requestFocus();
            return false;

        } else {
            surName.setError(null);
            return true;

        }
    }

    private boolean validateEmail() {

        String e_mail = email.getText().toString().trim();

        if (e_mail.isEmpty()) {
            email.setError("Email is required");
            email.requestFocus();
            return false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(e_mail).matches()) {
            email.setError("Please enter a valid email ");
            email.requestFocus();
            return false;

        } else {

            email.setError(null);
            return true;

        }
    }

    private boolean validateFirstPassword() {

        String pass1 = passWord1.getText().toString().trim();
        String pass2 = passWord2.getText().toString().trim();

        if (pass1.isEmpty()) {
            passWord1.setError("First password is required");
            passWord1.requestFocus();
            return false;

        } else if (!pass1.equals(pass2)) {
            passWord1.setError("Password not matching");
            passWord1.requestFocus();
            return false;

        } else if (pass1.length() < 6) {
            passWord1.setError("Password is too short");
            passWord1.requestFocus();
            return false;

        } else {

            passWord1.setError(null);
            return true;

        }
    }

    private boolean validateSecondPassword() {


        String pword2 = passWord2.getText().toString().trim();
        String pass1 = passWord1.getText().toString().trim();

        if (pword2.isEmpty()) {
            passWord2.setError("Surname password is required");
            passWord2.requestFocus();
            return false;

        } else if (!pword2.equals(pass1)) {
            passWord2.setError("Password not matching");
            passWord2.requestFocus();
            return false;

        } else if (pword2.length() < 6) {
            passWord2.setError("Password is too short");
            passWord2.requestFocus();
            return false;

        } else {

            passWord2.setError(null);
            return true;

        }
    }

    private boolean validatePhoneNumber(){

        // validating the form
        String pnum = phone.getText().toString().trim();

        if (pnum.isEmpty()) {
            phone.setError("Phone number is required");
            phone.requestFocus();
            return false;

        } else if (pnum.length() > 10 || pnum.length() < 10) {
            phone.setError("Enter a valid number");
            phone.requestFocus();
            return false ;

        } else {
            phone.setError(null);
            return true;

        }


    }


    private void createUserRole(){

        HashMap userRoles = new HashMap<>();
        userRoles.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), role );
        AccountRole.updateChildren(userRoles);

    }
}