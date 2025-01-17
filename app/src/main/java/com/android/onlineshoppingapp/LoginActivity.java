package com.android.onlineshoppingapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.onlineshoppingapp.models.UserInformation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private TextInputEditText editTextEmail, editTextPassword;
    private TextInputLayout layoutEmail, layoutPassword;
    private TextView textViewForgotPass, textViewRegister;
    private ImageView imageViewGoogle, imageViewFacebook;

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private ArrayList<UserInformation> userList = new ArrayList<UserInformation>();
    public ArrayList<String> userEmailList = new ArrayList<String>();

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if (currentUser != null) {
            finishAffinity();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // init
        btnLogin = findViewById(R.id.btnLogin);
        editTextEmail = findViewById(R.id.etEmailLogin);
        editTextPassword = findViewById(R.id.etPassLogin);
        layoutEmail = findViewById(R.id.tfEmailLogin);
        layoutPassword = findViewById(R.id.tfPassLogin);
        textViewForgotPass = findViewById(R.id.tvForgotPassword);
        textViewRegister = findViewById(R.id.txtRegister);
        imageViewGoogle = findViewById(R.id.ivGoogle);
        imageViewFacebook = findViewById(R.id.ivFacebook);

        fAuth = FirebaseAuth.getInstance();
        requestGoogleSignIn();

//        // check email
//        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean onFocus) {
//                if (!onFocus) {
//                    // get user
//                    userList = new ArrayList<UserInformation>();
//                    db = FirebaseFirestore.getInstance();
//
//                    db.collection("Users").orderBy("email", Query.Direction.ASCENDING)
//                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                @Override
//                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//                                    if (error != null) {
//                                        Log.e("Firestore error ", error.getMessage());
//                                    }
//
//                                    for (DocumentChange doc : value.getDocumentChanges()) {
//                                        if (doc.getType() == DocumentChange.Type.ADDED) {
//                                            userList.add(doc.getDocument().toObject(UserInformation.class));
//                                        }
//                                    }
//
//                                    userList.forEach(user -> {
//                                        userEmailList.add(user.getEmail().toString());
//                                    });
//
//                                    boolean check = false;
//                                    for (int i = 0; i < userEmailList.size(); i++) {
//                                        if (editTextEmail.getText().toString().trim().equals(userEmailList.get(i))) {
//                                            check = true;
//                                        }
//                                    }
//                                    if (!check) {
//                                        layoutEmail.setHelperText("Email chưa được đăng ký");
//                                    }
//                                }
//                            });
//                } else {
//                    layoutEmail.setHelperTextEnabled(false);
//                }
//            }
//        });

        // check password: null
        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean onFocus) {
                if (!onFocus) {
                    if (editTextPassword.getText().toString().equals("")) {
                        layoutPassword.setHelperText("Mật khẩu không được để trống");
                    }
                } else {
                    layoutPassword.setHelperTextEnabled(false);
                }
            }
        });

        // Click on LOGIN button
        btnLogin.setOnClickListener(view -> {

            if (!checkNullInputData()) {
                fAuth.signInWithEmailAndPassword(editTextEmail.getText().toString().trim(),
                        editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            layoutPassword.setHelperTextEnabled(false);
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }, 1000);
                        } else {
                            layoutPassword.setHelperText("Mật khẩu bạn nhập không đúng");
                        }
                    }
                });
            }

        });


        // Click on Forgot password
        textViewForgotPass.setOnClickListener(view -> {
            // navigate to Forgot password activity
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        // Click on Register
        textViewRegister.setOnClickListener(view -> {
            //navigate to register activity
            finish();
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Click on Google
        imageViewGoogle.setOnClickListener(view -> {
            googleSignIn();
        });

        // CLick on Facebook
        imageViewFacebook.setOnClickListener(view -> Toast.makeText(LoginActivity.this, "Facebook", Toast.LENGTH_SHORT).show());

    }

    // -------------- Function -----------------

    private boolean checkNullInputData() {
        return editTextEmail.getText().toString().equals("") || editTextPassword.getText().toString().equals("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("GoogleSignIn", "firebaseAuthWithGoogle: " + account.getEmail());
                firebaseAuthWithGoogle(account.getIdToken());
                SharedPreferences.Editor editor = getApplicationContext()
                        .getSharedPreferences("google_sign_in_info", MODE_PRIVATE)
                        .edit();
                editor.putString("google_name", account.getDisplayName());
                editor.putString("google_email", account.getEmail());
                editor.apply();

            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Failed: " + e.getMessage());
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Log.d("GoogleSignIn", "firebaseAuthWithGoogle: success");

                    // create a user profile on database
                    SharedPreferences preferences = getSharedPreferences("google_sign_in_info", MODE_PRIVATE);
                    String displayName = preferences.getString("google_name", "");
                    String googleEmail = preferences.getString("google_email", "");

                    String lastName = "";
                    String firstName = "";
                    if (displayName.split("\\w+").length > 1) {

                        lastName = displayName.substring(displayName.lastIndexOf(" ") + 1);
                        firstName = displayName.substring(0, displayName.lastIndexOf(" "));
                    } else {
                        firstName = displayName;
                    }

                    // create user profile on fire store
                    Log.d("google_info", displayName + " | " + firstName + " | " + lastName + " | " + googleEmail);
                    try {
                        UserInformation userInformation = new UserInformation(firstName,
                                lastName, googleEmail,
                                "0", "Nam",
                                new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000"),
                                "Mua hàng");

                        db.collection("Users").document(Objects.requireNonNull(task.getResult().getUser()).getUid())
                                .set(userInformation).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    } catch (Exception ex) {
                        Log.e("Error: ", ex.getMessage());
                    }

                    // navigate to main activity
                    finishAffinity();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Log.d("GoogleSignIn", "firebaseAuthWithGoogle: failed");
                    Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void requestGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
}