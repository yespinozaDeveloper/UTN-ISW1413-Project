package test.yespinoza.androidproject.View.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import org.json.JSONObject;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.internal.WebDialog;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;

import io.reactivex.annotations.NonNull;
import test.yespinoza.androidproject.Model.Request.RegisterDeviceRequest;
import test.yespinoza.androidproject.Model.Response.UserResponse;
import test.yespinoza.androidproject.Model.Entity.User;
import test.yespinoza.androidproject.Model.Utils.Helper;
import test.yespinoza.androidproject.Model.Utils.HttpApiResponse;
import test.yespinoza.androidproject.Model.Utils.HttpClientManager;
import test.yespinoza.androidproject.Project;
import test.yespinoza.androidproject.R;

public class Login extends AppCompatActivity {
    public static final String ACTIVITY_CODE = "100";
    private final int RC_SIGN_IN = 10;
    private SharedPreferences myPreferences;
    private User oUser;
    private Response.Listener<JSONObject> callBack_OK;
    private Response.ErrorListener callBack_ERROR;
    private ProgressDialog progress;
    private HttpClientManager proxy;
    private FirebaseAuth mAuth;
    private boolean isBKNAuthentication = true;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Project.getInstance().setCurrentUser(null);
            Project.getInstance().setCurrentActivity(this);
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
            proxy = new HttpClientManager(this);
            setContentView(R.layout.activity_login);
            getSupportActionBar().hide();
            //Se obtienen los valores Guardados
            progress  = new ProgressDialog(this);
            myPreferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
            String userName = myPreferences.getString("UserName", "");
            String password = myPreferences.getString("Password", "");
            ((EditText) findViewById(R.id.txtUserName)).setText(userName);
            ((EditText) findViewById(R.id.txtPassword)).setText(password);
            if(!userName.equals("") && !password.equals(""))
                Login(null);
            mAuth = FirebaseAuth.getInstance();
            callbackManager = CallbackManager.Factory.create();

            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            LoginButton loginButton = findViewById(R.id.login_button);
            loginButton.setReadPermissions("email", "public_profile");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                    validarUsuario();
                }

                @Override
                public void onCancel() {
                    //Log.d(, "facebook:onCancel");
                    // ...
                }

                @Override
                public void onError(FacebookException error) {
                    //Log.d(TAG, "facebook:onError", error);
                    // ...
                }
            });
            validarUsuario();
            //Helper.getProjectHash(getPackageManager(), getPackageName());
        } catch (Exception ex) {


        }
    }

    public void Login(View view) {
        String email = ((EditText) findViewById(R.id.txtUserName)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.txtPassword)).getText().toString().trim();
        isBKNAuthentication = true;
        if (email.equals("")) {
            Toast.makeText(this, getString(R.string.LoginTstNoUsername), Toast.LENGTH_LONG).show();
            return;
        }
        if (password.equals("")) {
            Toast.makeText(this, getString(R.string.LoginTstNoPassword), Toast.LENGTH_LONG).show();
            return;
        }

        oUser = new User();
        oUser.setUserName(email);
        oUser.setEmail(email);
        oUser.setPassword(password);
        userAuthentication(oUser);
    }

    private void userAuthentication(User user) {
        try {
            ShowProgressDialog(getString(R.string.user_validation_title), getString(R.string.user_validation_description));
            callBack_OK = new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        UserResponse oResponse = new Gson().fromJson(response.toString(), UserResponse.class);
                        if (Integer.parseInt(oResponse.getCode()) == HttpApiResponse.SUCCES_CODE) {
                            myPreferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
                            SharedPreferences.Editor myEditor = myPreferences.edit();
                            oUser = oResponse.getData();
                            if (oUser != null) {
                                myEditor.putString("UserName", user.getUserName());
                                if (isBKNAuthentication)
                                    myEditor.putString("Password", user.getPassword());
                                myEditor.commit();
                                if (!oUser.isRegistered()) {
                                    RegisterDeviceRequest oRegisterDeviceRequest = new RegisterDeviceRequest();
                                    oRegisterDeviceRequest.setSo("ANDROID");
                                    oRegisterDeviceRequest.setDeviceId(Helper.getDeviceId());
                                    oRegisterDeviceRequest.setUser(oUser.getUserName());
                                    oRegisterDeviceRequest.setDeviceToken(FirebaseInstanceId.getInstance().getToken());

                                    callBack_OK = new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            IndexRedirection();
                                            DismissProgressDialog();
                                        }
                                    };

                                    callBack_ERROR = new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            IndexRedirection();
                                            DismissProgressDialog();
                                        }
                                    };

                                    proxy.BACKEND_API_POST(HttpClientManager.BKN_REGISTER_DEVICE, new JSONObject(new Gson().toJson(oRegisterDeviceRequest)), callBack_OK, callBack_ERROR);
                                } else
                                    IndexRedirection();
                            } else {
                                LoginFailed();
                            }
                        } else
                            LoginFailed();
                    } catch (Exception oException) {
                        LoginFailed();
                        DismissProgressDialog();
                    }
                }
            };

            callBack_ERROR = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LoginFailed();
                    DismissProgressDialog();
                }
            };

            proxy.BACKEND_API_POST(HttpClientManager.BKN_GET_USER, new JSONObject(new Gson().toJson(user)), callBack_OK, callBack_ERROR);
        } catch (Exception oException) {
            LoginFailed();
            DismissProgressDialog();
        }
    }

    public void Facebook_Login(View view){
        Toast.makeText(this,getString(R.string.OptionNoImplemented), Toast.LENGTH_SHORT).show();
    }

    public void Google_Login(View view){
        isBKNAuthentication = false;

        ShowProgressDialog(getString(R.string.user_validation_title), getString(R.string.user_validation_description));
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        //Toast.makeText(this,getString(R.string.OptionNoImplemented), Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                mGoogleSignInClient.signOut();
                User userToAuthenticate = new User();
                userToAuthenticate.setEmail(account.getEmail());
                userToAuthenticate.setUserName(account.getEmail());
                userToAuthenticate.setName(account.getGivenName());
                userToAuthenticate.setLastName(account.getFamilyName());
                if(account.getPhotoUrl() != null)
                    userToAuthenticate.setPicture(Helper.fromUriToBase64(account.getPhotoUrl().toString()));
                userAuthentication(userToAuthenticate);
            } catch (ApiException e) {
                LoginFailed();
                DismissProgressDialog();
            }
        }
        else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
            validarUsuario();
        }
    }

    private void IndexRedirection() {
        Intent oIntent = new Intent(this, Index.class);
        Project.getInstance().setCurrentUser(oUser);
        oIntent.putExtra("ACTIVITY_CODE", ACTIVITY_CODE);
        startActivity(oIntent);
        finish();
    }

    public void CreateUserRedirection(View pView) {
        Intent oIntent = new Intent(this, PersonalInfo.class);
        oIntent.putExtra("ACTIVITY_CODE", ACTIVITY_CODE);
        startActivity(oIntent);
    }


    private void LoginFailed(){
        ((EditText) Project.getInstance().getCurrentActivity().findViewById(R.id.txtPassword)).setText("");
        Toast.makeText(Project.getInstance().getCurrentActivity(), getString(R.string.LoginTstFailed), Toast.LENGTH_LONG).show();
        if(oUser != null)
            oUser.setPassword("");
    }

    private void ShowProgressDialog(String tittle, String message){
        progress.setTitle(tittle);
        progress.setMessage(message);
        progress.setCancelable(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        progress.show();
    }

    private void DismissProgressDialog(){
        progress.dismiss();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public void FirebaseSignIn(View view){
        isBKNAuthentication = false;
        String email = ((EditText) findViewById(R.id.txtUserName)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.txtPassword)).getText().toString().trim();
        if (email.equals("")) {
            Toast.makeText(this, getString(R.string.LoginTstNoUsername), Toast.LENGTH_LONG).show();
            return;
        }
        if (password.equals("")) {
            Toast.makeText(this, getString(R.string.LoginTstNoPassword), Toast.LENGTH_LONG).show();
            return;
        }

        try {

            ShowProgressDialog(getString(R.string.user_validation_title), getString(R.string.user_validation_description));
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(mAuth.getCurrentUser() != null) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                User userToAuthenticate = new User();
                                String firebaseEmail = firebaseUser.getEmail();
                                if(firebaseEmail == null || firebaseEmail.isEmpty())
                                    firebaseEmail = email;
                                userToAuthenticate.setEmail(firebaseEmail);
                                userToAuthenticate.setName(firebaseUser.getDisplayName() != null ?
                                                            firebaseUser.getDisplayName() :
                                                            (firebaseEmail.split("@"))[0]);
                                userToAuthenticate.setPhone(firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : "");
                                userAuthentication(userToAuthenticate);
                            }else {
                                LoginFailed();
                                DismissProgressDialog();
                            }
                        } else {
                            LoginFailed();
                            DismissProgressDialog();
                        }
                    }
                });
        } catch (Exception oException) {
            LoginFailed();
            DismissProgressDialog();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void validarUsuario(){
        FirebaseUser usuario = mAuth.getCurrentUser();
        cerrarSesion(null);
    }
    public void login(View view){
        FirebaseUser usuario = mAuth.getCurrentUser();
        if(usuario != null){
            String name = usuario.getDisplayName();
            String email = usuario.getEmail();
            SharedPreferences pref = getSharedPreferences("TEST",MODE_PRIVATE);
            pref.edit().putString("name",name);
            if(email!=null){
                pref.edit().putString("email",email);
            }
            pref.edit().commit();
/*

            Intent oIntent = new Intent(this, lec08b.class);
            oIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(oIntent);
 */
        }else{
            //startActivityForResult(auth.getInstanc);
        }
    }
    public void fbLogin(View view){

    }

    public void cerrarSesion(View view){
        try {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
        }catch(Exception ex){

        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            User userToAuthenticate = new User();
                            userToAuthenticate.setEmail(firebaseUser.getEmail());
                            userToAuthenticate.setName(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "");
                            userToAuthenticate.setPhone(firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : "");
                            userAuthentication(userToAuthenticate);
                            validarUsuario();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


}
