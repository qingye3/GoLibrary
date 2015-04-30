package org.qing.golibrary.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import org.qing.golibrary.app.fragments.ViewAlarmsFragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";
    private LoginManager loginManager;
    private CallbackManager callbackManager;
    private final int appVersion = 2;
    private final int welcomeRequestCode = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logKeyHash();
        showSplashIfFirstBoot();
        prepareFacebookSDK(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void showSplashIfFirstBoot() {
        if (getSavedAppVersion() != appVersion) {
            saveCurrentAppVersion();
            showSplash();
        }
    }

    private void showSplash() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivityForResult(intent, welcomeRequestCode);
    }

    private int getSavedAppVersion(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(getString(R.string.APPLICATION_VERSION), 0);
    }

    private void saveCurrentAppVersion(){
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.APPLICATION_VERSION), appVersion);
        editor.apply();
    }


    /**
     * Prepare loginManager and callbackManager to handle Facebook login
     */
    private void prepareFacebookSDK(final Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //render the view if login succeeded
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.container, new ViewAlarmsFragment())
                            .commit();
                }
            }

            //If the user cancelled the login or login failed the show a useful dialog
            @Override
            public void onCancel() {
                showLoginFailedDialog();
            }

            @Override
            public void onError(FacebookException e) {
                showLoginFailedDialog();
            }
        });

    }


    private void showLoginFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cannot Log into Facebook");
        builder.setMessage("You need to log into Facebook to use the App");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void logout() {
        loginManager.logOut();
    }


    private void login() {
        loginManager.logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }


    /**
     *  Method found on facebook developer site to log a key hash in case I need to change signature of the app
     */
    private void logKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "org.qing.golibrary.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        } catch (NoSuchAlgorithmException ignored) {
        }
    }


    /**
     * AppEventsLogger creates helpful messages on developer dashboard
     */
    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }


    /**
     * AppEventsLogger creates helpful messages on developer dashboard
     */
    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }


    /**
     *  Create the option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Adding the login and logout function
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_logout:
                logout();
                break;
            case R.id.action_login:
                login();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == welcomeRequestCode){
            //View is not rendered until login succeeded
            login();
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
