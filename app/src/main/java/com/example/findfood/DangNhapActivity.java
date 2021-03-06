package com.example.findfood;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findfood.CallBack.UserCallBack;
import com.example.findfood.Databases.DatabaseUser;
import com.example.findfood.View.CheckRoleActivity;
import com.example.findfood.View.User.QuenMatKhau;
import com.example.findfood.model.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;


import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DangNhapActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener,  FirebaseAuth.AuthStateListener {
    Button mloginBtn,btnLoginPhone,btnLoginGoogle,btnLoginFacebok;
    TextView btndangky,btnquenmatkhau;
    EditText mEmail,mPassword;
    ProgressBar progressBar;
    DatabaseUser databaseUser;
    ArrayList<User> datastore;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private DatabaseUser DatabaseUser;


    /*-------------------- Google --------------------*/
    private FirebaseAuth firebaseAuth;
    public static int KEY_LOGIN_GOOGLE = 99;
    public static int KIEMTRA_PROVIDER_DANGNHAP = 0;
    GoogleApiClient apiClient;
    /*-------------------- END google --------------------*/

    /*-------------------- Facebook --------------------*/
    CallbackManager callbackManager;
    LoginManager loginManager;
    List<String> permissionsFacebook = Arrays.asList("email", "public_profile");
    /*-------------------- END facebook --------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_dang_nhap);
        /*-------------------- G??n gi?? tr??? --------------------*/
        btndangky = findViewById(R.id.btndangky);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mloginBtn = findViewById(R.id.loginBtn);
        btnLoginPhone = findViewById(R.id.btnLoginPhone);
        btnquenmatkhau = findViewById(R.id.btnquenmatkhau);
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnLoginFacebok = findViewById(R.id.btnLoginFacebook);
        progressBar = findViewById(R.id.progressBar);
        /*-------------------- END g??n gi?? tr??? --------------------*/

        /*-------------------- Kh???i t???o ????ng nh???p b???ng Facebook --------------------*/
        callbackManager = CallbackManager.Factory.create();
        loginManager = loginManager.getInstance();
        /*-------------------- End kh???i t???o ????ng nh???p b???ng Facebook --------------------*/

        firebaseAuth = FirebaseAuth.getInstance();

        mloginBtn.setOnClickListener(this);
        btnLoginGoogle.setOnClickListener(this);
        btnLoginFacebok.setOnClickListener(this);
        btndangky.setOnClickListener(this);
        btnLoginPhone.setOnClickListener(this);
        btnquenmatkhau.setOnClickListener(this);

        createClientSigninGoogle();
    }

    /*-------------------- Kh???i t???o client cho ????ng nh??p Google --------------------*/

    private void createClientSigninGoogle() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();
    }

    /*--------------------END kh???i t???o client cho ????ng nh??p Google --------------------*/

    /*-------------------- M??? form ????ng nh??p b???ng Google --------------------*/

    private void loginGoogle(GoogleApiClient apiClient) {
        KIEMTRA_PROVIDER_DANGNHAP = 1;
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(intent,KEY_LOGIN_GOOGLE);
    }

    /*-------------------- END m??? form ????ng nh??p b???ng Google --------------------*/

    /*-------------------- M??? form ????ng nh??p b???ng Facebook --------------------*/

    private void loginFacebook() {
//        loginManager.logInWithReadPermissions(this,permissionsFacebook);
//        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                KIEMTRA_PROVIDER_DANGNHAP = 2;
//                String tokenID = loginResult.getAccessToken().getToken();
//                chungThucDangNhapFirebase(tokenID);
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });
        btnLoginFacebok.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), "??ang ph??t tri???n, d??? ki???n kh??? d???ng : CDIO 4", Toast.LENGTH_SHORT).show();
    }

    /*-------------------- END m??? form ????ng nh??p b???ng Facebook --------------------*/

    /*-------------------- L???y tokenID ???? ????ng nh??p b???ng Google ????? ????ng nh???p tr??n Firebase --------------------*/

    private void chungThucDangNhapFirebase(String tokenID) {
        if (KIEMTRA_PROVIDER_DANGNHAP == 1) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(tokenID, null);
            firebaseAuth.signInWithCredential(authCredential);
        }else if (KIEMTRA_PROVIDER_DANGNHAP == 2) {
            AuthCredential authCredential = FacebookAuthProvider.getCredential(tokenID);
            firebaseAuth.signInWithCredential(authCredential);
        }
    }

    /*-------------------- END l???y tokenID ???? ????ng nh??p b???ng Google ????? ????ng nh???p tr??n Firebase --------------------*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == KEY_LOGIN_GOOGLE) {
            if (resultCode == RESULT_OK){
                GoogleSignInResult signInResult =Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = signInResult.getSignInAccount();

                DatabaseUser = new DatabaseUser(getApplicationContext());
                User user = new User(account.getEmail(),null,account.getDisplayName(),null,null,null,null,null,firebaseAuth.getUid(), "true");
                DatabaseUser.insert(user);

                String tokenID =account.getIdToken();
                chungThucDangNhapFirebase(tokenID);
            }
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /*-------------------- ????ng nh???p b???ng email, password --------------------*/

    private void loginbymailPassword() {
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            mEmail.setError("B???t bu???c");
            mPassword.setError("B???t bu???c");
            Toast.makeText(getApplicationContext(), "Vui L??ng Nh???p ?????y ????? 2 Tr?????ng", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 6) {
            mPassword.setError("M???t kh???u ph???i l???n h??n 6 k?? t???");
            return;
        }else if (!email.matches("^[a-zA-Z][a-z0-9_\\.]{4,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
            mEmail.setError("Email kh??ng h???p l???");
            return;
        }else {
            databaseUser = new DatabaseUser(getApplicationContext());
            datastore = new ArrayList<>();
            databaseUser.getAll(new UserCallBack() {
                @Override
                public void onSuccess(ArrayList<User> lists) {
                    datastore.clear();
                    datastore.addAll(lists);
                }

                @Override
                public void onError(String message) {

                }
            });
        }
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i < datastore.size(); i++) {
                        if (datastore.get(i).getEmail().equalsIgnoreCase(email.toString()) && datastore.get(i).getPassword().equalsIgnoreCase(password.toString()) && datastore.get(i).getTrangThai().equalsIgnoreCase("true")) {
//                            Toast.makeText(getApplicationContext(), "????ng Nh???p Th??nh C??ng", Toast.LENGTH_SHORT).show();
//                            Intent is = new Intent(getApplicationContext(),MainActivity.class);
//                            startActivity(is);
                            Intent intent = new Intent(getApplicationContext(), CheckRoleActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.from_right_in,R.anim.from_left_out);
                            break;
                        }
                        else {
//                            Toast.makeText(getApplicationContext(), "Login Th???t B???i", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
//                    Toast.makeText(getApplicationContext(), "Login Th???t B???i", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    /*-------------------- END ????ng nh???p b???ng email, password --------------------*/


    /*-------------------- L???ng nghe s??? ki???n ng?????i d??ng click v??o button ????ng nh???p b???ng google, facebook, email, phone --------------------*/

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnLoginGoogle:
                loginGoogle(apiClient);
                break;
            case R.id.btnLoginFacebook:
                loginFacebook();
                break;
            case R.id.btndangky:
                Intent iDangKy = new Intent(DangNhapActivity.this, DangKyActivity.class);
                startActivity(iDangKy);
                break;
            case R.id.btnLoginPhone:
                Intent iDangKyPhone = new Intent(DangNhapActivity.this, DangKyWithPhone.class);
                startActivity(iDangKyPhone);
                break;
            case R.id.loginBtn:
                loginbymailPassword();
                break;
            case R.id.btnquenmatkhau:
                Intent iKhoiPhuc = new Intent(DangNhapActivity.this, QuenMatKhau.class);
                startActivity(iKhoiPhuc);
        }
    }

    /*-------------------- END l???ng nghe s??? ki???n ng?????i d??ng click v??o button ????ng nh???p b???ng google, facebook, email, phone --------------------*/

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(this);
    }
    /*-------------------- Ki???m tra s??? ki???n ng?????i d??ng ????ng nh???p, ????ng xu???t --------------------*/
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user !=null){
            Intent intent = new Intent(DangNhapActivity.this, CheckRoleActivity.class);
            startActivity(intent);
            Log.d("thien","da ch???y dang nhap");
        }
    }

    /*-------------------- END ki???m tra s??? ki???n ng?????i d??ng ????ng nh???p, ????ng xu???t --------------------*/
}