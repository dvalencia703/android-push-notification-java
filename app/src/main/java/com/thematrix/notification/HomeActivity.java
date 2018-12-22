package com.thematrix.notification;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.thematrix.notification.services.FirebaseRestService;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    EditText editName;
    Button btnEnter;
    EditText editData;

    EditText editToken;
    Button btnSend;

    EditText editMessage;

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        editName = findViewById(R.id.editName);
        btnEnter = findViewById(R.id.btnEnter);
        editData = findViewById(R.id.editData);

        editToken = findViewById(R.id.editToken);
        btnSend = findViewById(R.id.btnSend);

        editMessage = findViewById(R.id.editMessage);

        editToken.setEnabled(false);
        btnSend.setEnabled(false);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                Utilitario.pushToken = instanceIdResult.getToken();
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilitario.pushToken == null || Utilitario.pushToken.isEmpty()) {
                    editData.setText("Token no fue inicializado aún");
                }

                /* Registrando mi push token en alguna base de datos */

                if (editName.getText() != null) {
                    name = editName.getText().toString();
                    if (!name.isEmpty()) {
                        Utilitario.registeringData(getApplicationContext(), name);
                        btnEnter.setEnabled(false);
                        fillMultiLineEditor();

                        editToken.setEnabled(true);
                        btnSend.setEnabled(true);
                    }
                }
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilitario.pushToken == null || Utilitario.pushToken.isEmpty()) {
                    editData.setText("Token no fue inicializado aún");
                }

                /* Registrando mi push token en alguna base de datos */

                if (editToken.getText() != null && editMessage.getText() != null) {
                    String token = editToken.getText().toString();
                    String message = editMessage.getText().toString();
                    if (!token.isEmpty() & !message.isEmpty()) {
                        Map<String, Object> info = new HashMap<>();
                        info.put("title", name + " dice");
                        info.put("content", message);
                        FirebaseRestService.sendNotification(HomeActivity.this, token, info);
                    }
                }
            }
        });
    }

    private void fillMultiLineEditor() {

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference()
                .child("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                editData.setText("");
                StringBuilder result = new StringBuilder();
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    result.append(child.child("name").getValue(String.class))
                            .append(" - ")
                            .append(child.child("push-token").getValue(String.class))
                            .append("\n\n");
                }

                editData.setText(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
