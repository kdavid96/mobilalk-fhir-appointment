package com.example.fhirappointment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppointmentsActivity extends AppCompatActivity {
    private static final String LOG_TAG = AppointmentsActivity.class.getName();
    private FirebaseAuth mAuth;
    private User currentUser = new User();
    List<Participant> practitionersList = new ArrayList();
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);
        FirebaseUser user;
        AppointmentsActivity activity = this;
        user = FirebaseAuth.getInstance().getCurrentUser();
        /*mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d(LOG_TAG, String.valueOf(location));
                            }
                        }
                    });
            return;
        }*/

        if(user != null){
            Log.d(LOG_TAG, "Authenticated user!");
            mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore mDB = FirebaseFirestore.getInstance();
            mDB.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        if(doc.exists()){
                            currentUser.set(doc.getString("email"),doc.getString("accountType"),doc.getString("uid"),doc.getString("name"));
                            if(currentUser != null) {
                                TextView textViewWelcome = (TextView) findViewById(R.id.textViewWelcome);
                                Log.d(LOG_TAG,currentUser.getName());
                                textViewWelcome.setText("Welcome " + currentUser.getName() + "!");
                                TableLayout table = (TableLayout) findViewById(R.id.doctorsTable);
                                getPractitioners(table, activity);
                                if(currentUser.getAccountType().equals("Practitioner")){
                                    Button addAppointmentButton = (Button) findViewById(R.id.addAppointmentButton);
                                    addAppointmentButton.setVisibility(View.VISIBLE);
                                }
                            }else{
                                Log.d("current user null", "idk why");
                            }
                        }else{
                            Log.d("Document", "No data");
                        }
                    }else{
                        Log.d("Error", "Getting user error");
                    }
                }
            });
        }else{
            Log.d(LOG_TAG, "Not authenticated user!");
            finish();
        }

    }

    private void getPractitioners(TableLayout table, AppointmentsActivity context) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        CollectionReference usersRef = mDB.collection("users");
        Query query = usersRef.whereEqualTo("accountType", "Practitioner");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(!Objects.equals(document.get("uid"), currentUser.uid)){
                            practitionersList.add(new Participant(Objects.requireNonNull(document.get("email")).toString(), Objects.requireNonNull(document.get("uid")).toString()));
                            TableRow tableRow = new TableRow(context);
                            tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                            tableRow.setMinimumHeight(150);
                            TextView tw1 = new TextView(context);
                            TextView tw2 = new TextView(context);
                            tw1.setText(Objects.requireNonNull(document.get("name")).toString());
                            tw2.setText(Objects.requireNonNull(document.get("accountType")).toString());
                            tw1.setPaddingRelative(40, 5, 40, 5);
                            tw2.setPaddingRelative(40, 5, 40, 5);
                            Button b = new Button(context);
                            b.setText("Get appointment");
                            b.setBackgroundResource(R.drawable.roundedbutton);
                            b.setPaddingRelative(20, 5, 20, 5);
                            b.setHeight(20);
                            b.setOnClickListener(new View.OnClickListener(){
                                public void onClick(View v){
                                    Intent selectAppointmentIntent = new Intent(getApplicationContext(), SelectAppointmentActivity.class);
                                    Bundle b = new Bundle();
                                    b.putString("uid", Objects.requireNonNull(document.get("uid")).toString());
                                    b.putString("name", currentUser.getName());
                                    selectAppointmentIntent.putExtras(b);
                                    startActivity(selectAppointmentIntent);
                                    finish();
                                }
                            });
                            tableRow.addView(tw1);
                            tableRow.addView(tw2);
                            tableRow.addView(b);
                            table.addView(tableRow);
                        }
                    }
                } else {
                    Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void logout(View view){
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void myAppointments(View view){
        Intent myAppointmentsIntent = new Intent(getApplicationContext(), MyAppointmentsActivity.class);
        Bundle b = new Bundle();
        b.putString("uid", currentUser.getUid());
        myAppointmentsIntent.putExtras(b);
        startActivity(myAppointmentsIntent);
        overridePendingTransition(R.anim.slide_down, 0);
        finish();
    }

    public void addAppointment(View view) {
        Intent AddAppointmentIntent = new Intent(getApplicationContext(), AddAppointmentActivity.class);
        Bundle b = new Bundle();
        b.putString("uid", currentUser.getUid());
        b.putString("name", currentUser.getName());
        AddAppointmentIntent.putExtras(b);
        startActivity(AddAppointmentIntent);
        finish();
    }
}