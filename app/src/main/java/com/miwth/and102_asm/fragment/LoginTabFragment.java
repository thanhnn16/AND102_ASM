package com.miwth.and102_asm.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.miwth.and102_asm.R;
import com.miwth.and102_asm.users.PhoneLogin;
import com.miwth.and102_asm.users.UserAuth;

import java.util.Arrays;

public class LoginTabFragment extends Fragment implements UserAuth {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    LottieAnimationView buttonAnimation;
    LinearLayout btnLogin;
    TextView tvLogin;
    TextView tvFacebook;
    TextView tvGoogle;
    TextView tvOTP;
    CallbackManager callbackManager;
    private OnLoginListener onLoginListener;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
//    private SignInClient oneTapClient;
//    private BeginSignInRequest signInRequest;
//    private static final int REQ_ONE_TAP = 999;  // Can be any integer unique to the Activity.
//    private boolean showOneTapUI = true;

    ActivityResultLauncher<Intent> googleLogin = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    try {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> googleTask = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = googleTask.getResult(ApiException.class);
                        Toast.makeText(getActivity(), "Logging...", Toast.LENGTH_SHORT).show();
                        if (googleTask.isSuccessful()) {
                            Log.d("TAG", "signInWithCredential:success");

                            // Lấy IdToken của tài khoản Google
                            String idToken = account.getIdToken();

                            if (idToken != null && !idToken.isEmpty()) {
                                // Tạo AuthCredential từ IdToken
                                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

                                // Đăng nhập vào Firebase với AuthCredential
                                mAuth.signInWithCredential(credential)
                                        .addOnCompleteListener(firebaseTask -> {
                                            if (firebaseTask.isSuccessful()) {
                                                // Đăng nhập thành công
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    mAuth.updateCurrentUser(user);
                                                } else {
                                                    Log.e("TAG", "User is null");
                                                }
                                                onLoginListener.onLoginSuccess(account.getEmail());
                                            } else {
                                                // Đăng nhập thất bại
                                                Log.w("TAG", "signInWithCredential:failure", firebaseTask.getException());
                                            }
                                        });
                            } else {
                                Log.e("TAG", "IdToken is null or empty");
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", googleTask.getException());
                        }
                    } catch (ApiException e) {
                        Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
                    }
                }
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View loginTabFragment = inflater.inflate(R.layout.login_tab_fragment, container, true);

        TextInputEditText etEmail = loginTabFragment.findViewById(R.id.edtEmail);
        TextInputEditText etPassword = loginTabFragment.findViewById(R.id.edtPassword);
        CheckBox cbRememberMe = loginTabFragment.findViewById(R.id.cbRememberMe);
        TextView tvForgotPassword = loginTabFragment.findViewById(R.id.tvForgotPassword);
        btnLogin = loginTabFragment.findViewById(R.id.btnLogin);
        tvLogin = loginTabFragment.findViewById(R.id.tvLogin);
        buttonAnimation = loginTabFragment.findViewById(R.id.button_animation);

        tvFacebook = loginTabFragment.findViewById(R.id.tvFacebook);

        tvGoogle = loginTabFragment.findViewById(R.id.tvGoogle);
        tvOTP = loginTabFragment.findViewById(R.id.tvPhoneOTP);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
//        oneTapClient = Identity.getSignInClient(getActivity());
//        signInRequest = BeginSignInRequest.builder()
//                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
//                        .setSupported(true)
//                        .build())
//                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                        .setSupported(true)
//                        // Your server's client ID, not your Android client ID.
//                        .setServerClientId(getString(R.string.default_web_client_id))
//                        // Only show accounts previously used to sign in.
//                        .setFilterByAuthorizedAccounts(true)
//                        .build())
//                // Automatically sign in when exactly one credential is retrieved.
//                .setAutoSelectEnabled(false)
//                .build();
//
//        oneTapClient.beginSignIn(signInRequest)
//                .addOnSuccessListener(getActivity(), new OnSuccessListener<BeginSignInResult>() {
//                    @Override
//                    public void onSuccess(BeginSignInResult result) {
//                        try {
//                            startIntentSenderForResult(
//                                    result.getPendingIntent().getIntentSender(),
//                                    REQ_ONE_TAP,
//                                    null,
//                                    0,
//                                    0,
//                                    0,
//                                    null
//                            );
//                        } catch (IntentSender.SendIntentException e) {
//                            Log.e("TAG", "Couldn't start One Tap UI: " + e.getLocalizedMessage());
//                        }
//                    }
//                })
//                .addOnFailureListener(getActivity(), new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        // No saved credentials found. Launch the One Tap sign-up flow, or
//                        // do nothing and continue presenting the signed-out UI.
//                        Log.d("TAG", e.getLocalizedMessage());
//                    }
//                });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                boolean rememberMe = cbRememberMe.isChecked();

                startButtonAnimation();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetButton();
                    }
                }, 2000);

                if (email.isEmpty()) {
                    etEmail.setError("Email is empty");
                    etEmail.requestFocus();
                } else if (password.isEmpty()) {
                    etPassword.setError("Password is empty");
                    etPassword.requestFocus();
                } else {
                    if (rememberMe) {
                        sharedPreferences = getActivity().getSharedPreferences("login_state", getActivity().MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.putBoolean("is_logged_in", true);
                        editor.apply();
                    }
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    onLoginListener.onLoginSuccess(email);
                                } else {
                                    onLoginListener.onLoginFailure("Wrong email or password");
                                    etEmail.requestFocus();
                                }
                            });
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                        onLoginListener.onLoginSuccess(loginResult.getAccessToken().getUserId());
                    }

                    @Override
                    public void onCancel() {
                        onLoginListener.onLoginFailure("Facebook login is cancelled");
                    }

                    @Override
                    public void onError(@NonNull FacebookException e) {
                        onLoginListener.onLoginFailure("Facebook login is failed: " + e.getMessage());
                    }
                }
        );


        tvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
            }
        });

        tvGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                googleLogin.launch(signInIntent);
            }
        });


        tvOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhoneLogin.class);
                startActivity(intent);
            }
        });


        return loginTabFragment;
    }

    private FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void setOnLoginListener(OnLoginListener listener) {
        this.onLoginListener = listener;
    }

    public void resetButton() {
        buttonAnimation.setVisibility(View.GONE);
        buttonAnimation.cancelAnimation();
        tvLogin.setVisibility(View.VISIBLE);
    }

    public void startButtonAnimation() {
        buttonAnimation.setVisibility(View.VISIBLE);
        buttonAnimation.playAnimation();
        tvLogin.setVisibility(View.GONE);
    }

    private void handleFacebookAccessToken(@NonNull AccessToken token) {
        Log.d("handleFacebookAccessToken", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}

