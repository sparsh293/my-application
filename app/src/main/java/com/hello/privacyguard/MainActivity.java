package com.hello.privacyguard;

import static android.app.admin.DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN;
import static android.app.admin.DevicePolicyManager.EXTRA_DEVICE_ADMIN;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final int DPM_ACTIVATION_REQUEST_CODE = 100;
    private DevicePolicyManager policyManager;
    private ComponentName componentName;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    Switch switch1;
    Switch switch2;
    Switch switch3;
    Switch switch4;
    Switch switch5;
    Context context;
    Intent intent1;
    Button logout;
    Button resetPassLocal;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        switch1=findViewById(R.id.switch1);
        switch2=findViewById(R.id.switch2);
        switch3=findViewById(R.id.switch3);
        switch4=findViewById(R.id.switch4);
        switch5=findViewById(R.id.switch5);
        logout=findViewById(R.id.logout);
        user=fAuth.getCurrentUser();
        resetPassLocal = findViewById(R.id.resetPasswordLocal);
        context=getApplicationContext();
        componentName = new ComponentName(this, CameraPolicyReceiver.class);
        policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);

        requestAdminPermissionsIfNecessary();
        final AudioManager audioManager = (AudioManager)getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    audioManager.setMicrophoneMute(false);

                    Toast.makeText(context, "Microphone enabled", Toast.LENGTH_SHORT).show();
                } else {
                    audioManager.setMicrophoneMute(true);
                    Toast.makeText(context, "Microphone disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                policyManager.setCameraDisabled(componentName, !isChecked);
                if(isChecked){
                    Toast.makeText(context, "Camera enabled", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Camera disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent1);
                if(isChecked){
                    Toast.makeText(context, "GPS enabled", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "GPS disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent("android.settings.panel.action.INTERNET_CONNECTIVITY");
                startActivity(intent);
                if(isChecked){
                    Toast.makeText(context, "Mobile Data enabled", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Mobile Data disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
        switch5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent("android.settings.panel.action.INTERNET_CONNECTIVITY");
                startActivity(intent);
                if(isChecked){
                    Toast.makeText(context, "Wifi enabled", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Wifi disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });
        resetPassLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText resetPassword = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String newPassword = resetPassword.getText().toString();
                        user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });

                passwordResetDialog.create().show();

            }
        });
    }







    private void requestAdminPermissionsIfNecessary() {
        if (!policyManager.isAdminActive(componentName)) {
            Intent activateDeviceAdmin = new Intent(ACTION_ADD_DEVICE_ADMIN);
            activateDeviceAdmin.putExtra(EXTRA_DEVICE_ADMIN, componentName);
            startActivityForResult(activateDeviceAdmin, DPM_ACTIVATION_REQUEST_CODE);
        }
    }









}