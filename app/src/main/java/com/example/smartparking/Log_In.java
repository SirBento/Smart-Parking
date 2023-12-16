package com.example.smartparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Log_In extends AppCompatActivity {

    //declaring variables to access components on the Login form
    private EditText log_Email, logPass;
    // creating a variable for accessing the database authentication
    private FirebaseAuth mAuth;
    private String AccessLevel;
    LoadingDialog loadingDialog = new LoadingDialog(Log_In.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        TextView forgotPassword = findViewById(R.id.forgotPass);
        Button btn_login = findViewById(R.id.btnLogin);

        // initializing the database authentication
        mAuth = FirebaseAuth.getInstance();

        btn_login.setOnClickListener(view -> {

            log_Email = findViewById(R.id.logEmail);
            logPass = findViewById(R.id.logPassword);


            if(!validatePassword() |  !validateEmail() ){
                // If the functions continue to return false repeat validation until true
                return;
            }

            if(!haveNetworkConnection()){

                Toast.makeText(Log_In.this, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();

            }else{
                loadingDialog.startLoadingDialog();
                userLogin();

            }

        });

         //sign up activity calling
        Button btn_Sign_Up = findViewById(R.id.btnSignUp);
        btn_Sign_Up.setOnClickListener(view -> startActivity(new Intent( Log_In.this,SignUp.class) ));

        forgotPassword.setOnClickListener(view -> startActivity(new Intent( Log_In.this,ForgotPassword.class) ));

    }

    private void userLogin(){

        String LogIne_mail = log_Email.getText().toString().trim();
        String LogInpass = logPass.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(LogIne_mail,LogInpass).addOnCompleteListener(task -> {

            if(task.isSuccessful()){

                //checking if the account is verified , if not the user has to first verify the account before logging in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user.isEmailVerified()){

                    userType(user.getUid());
                    loadingDialog.dismissDialog();

                }else{

                    user.sendEmailVerification();
                    loadingDialog.dismissDialog();
                    Toast.makeText(Log_In.this,
                            "Account not verified. Please check your email to verify your account the Login",
                            Toast.LENGTH_LONG).show();
                }


            }else {
                loadingDialog.dismissDialog();
                Toast.makeText(Log_In.this, "LogIn Failed!!! Please check your credentials and your internet connection", Toast.LENGTH_LONG).show();

            }

        });


    }


    private boolean validateEmail(){
        String e_mail = log_Email.getText().toString().trim();

        //checking if the email text box is filled or not
        if(e_mail.isEmpty()){
            log_Email.setError("Email is required");
            log_Email.requestFocus();
            return false;

        //checking if the email entered is valid
        }else if(!Patterns.EMAIL_ADDRESS.matcher(e_mail).matches()){
            log_Email.setError("Please enter a email ");
            log_Email.requestFocus();
            return false;

        } else {
            log_Email.setError(null);
            return true;

        }

    }

    private boolean validatePassword(){

        String pass = logPass.getText().toString().trim();

        // checking if the password text box is filled or not

        if(pass.isEmpty()){
            logPass.setError("Password is required");
            logPass.requestFocus();
            return false;

        // checking the password length due to firebase restrictions
        } else if (pass.length()< 6) {
            logPass.setError("Your password is too short");
            logPass.requestFocus();
            return false;

        } else {
            log_Email.setError(null);
            return true;

        }


    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }

        return haveConnectedWifi || haveConnectedMobile;
    }


    //Determine the user type during login
    private void userType(String uid){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Roles").child(uid);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //store the access level of the user trying to login
                AccessLevel = snapshot.getValue(String.class);

                if( AccessLevel.equals("Marshal")){

                    Toast.makeText(Log_In.this, "Welcome Marshal....", Toast.LENGTH_LONG).show();
                    //if login is successful go to the Marshal's homepage
                    startActivity(new Intent( Log_In.this,MarshalHomeActivity.class) );
                    finish();

                }else if(AccessLevel.equals("Driver")){

                    Toast.makeText(Log_In.this, "Welcome Driver....", Toast.LENGTH_LONG).show();
                    //if login is successful go to the Driver's homepage
                    startActivity(new Intent( Log_In.this,Home.class) );
                    finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(Log_In.this, "Something Wrong Happened, Please check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });



    }


}