package com.example.fhirappointment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class MyAppointmentsActivity extends AppCompatActivity {
    private static final String LOG_TAG = MyAppointmentsActivity.class.getName();
    private FirebaseAuth mAuth;
    private User currentUser = new User();
    private Participant currentPractitioner = new Participant();
    private Participant currentRecipient = new Participant();
    List<Appointment> appointmentsList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);
        FirebaseUser user;
        MyAppointmentsActivity activity = this;
        user = FirebaseAuth.getInstance().getCurrentUser();
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
                                TextView textViewWelcome = (TextView) findViewById(R.id.textView4);
                                TableLayout table = (TableLayout) findViewById(R.id.doctorsTable);
                                if(currentUser.getAccountType().equals("Practitioner")){
                                    textViewWelcome.setText(currentUser.getName() + "'s listed appointments");
                                    getMyAppointments(table, activity);
                                }else{
                                    textViewWelcome.setText("Appointments of " + currentUser.getName() + ".");
                                    getAppointments(table, activity);
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

    private void getAppointments(TableLayout table, MyAppointmentsActivity activity) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        CollectionReference currentUserAppointments = mDB.collection("appointments");
        Query query = currentUserAppointments.whereEqualTo("patient", currentUser.name);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Timestamp start = (Timestamp) document.get("start");
                        Timestamp end = (Timestamp) document.get("end");
                        assert start != null;
                        Date startDate = start.toDate();
                        assert end != null;
                        Date endDate = end.toDate();
                        appointmentsList.add(new Appointment(Objects.requireNonNull(document.get("id")).toString(), Objects.requireNonNull(document.get("title")).toString(), document.get("patient").toString(), startDate, endDate, Objects.requireNonNull(document.get("patient")).toString()));
                        TableRow tableRow = new TableRow(activity);
                        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tableRow.setMinimumHeight(100);
                        TableRow tableRow2 = new TableRow(activity);
                        tableRow2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tableRow2.setMinimumHeight(100);
                        TableRow tableRow3 = new TableRow(activity);
                        tableRow3.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tableRow3.setMinimumHeight(100);
                        TextView tw1 = new TextView(activity);
                        TextView tw2 = new TextView(activity);
                        TextView tw4 = new TextView(activity);
                        TextView tw5 = new TextView(activity);
                        TextView tw6 = new TextView(activity);
                        Timestamp tsStart = (Timestamp) document.get("start");
                        Timestamp tsEnd = (Timestamp) document.get("end");
                        Calendar cal = Calendar.getInstance();
                        TimeZone tz = cal.getTimeZone();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                        sdf.setTimeZone(tz);
                        String localTimeStart = sdf.format(new Date(tsStart.getSeconds() * 1000));
                        String localTimeEnd = sdf.format(new Date(tsEnd.getSeconds() * 1000));
                        tw1.setText(localTimeStart);
                        tw2.setText(localTimeEnd);
                        tw4.setText("Practitioner: " + Objects.requireNonNull(document.get("practitioner")).toString());
                        tw5.setText("Title: " + Objects.requireNonNull(document.get("title")).toString());
                        tw1.setPaddingRelative(10, 5, 10, 5);
                        tw2.setPaddingRelative(10, 5, 10, 5);
                        tw4.setPaddingRelative(10, 5, 10, 5);
                        tw5.setPaddingRelative(10, 5, 10, 5);
                        tw6.setPaddingRelative(10, 5, 10, 5);
                        Button b = new Button(activity);
                        b.setText("Delete");
                        b.setBackgroundResource(R.drawable.roundedbutton);
                        b.setPaddingRelative(20, 5, 20, 5);
                        b.setHeight(20);
                        b.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v){
                                currentUserAppointments.document(Objects.requireNonNull(document.get("id")).toString()).update("patient", "", "status", "cancelled" );
                                Toast.makeText(MyAppointmentsActivity.this, "Deleted.", Toast.LENGTH_LONG).show();
                                recreate();
                            }
                        });
                        tableRow.addView(tw1);
                        tableRow.addView(tw2);
                        tableRow2.addView(tw4);
                        tableRow3.addView(tw5);
                        tableRow2.addView(b);
                        table.addView(tableRow);
                        table.addView(tableRow2);
                        table.addView(tableRow3);
                    }
                } else {
                    Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public Participant setUserRecord(String uid, Participant participant, String role){
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        mDB.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if(doc.exists()){
                        participant.Set(doc.getString("email"),doc.getString("uid"), "participation", doc.getString("accountType"), "required", "booked");
                    }else{
                        Log.d("Document", "No data");
                    }
                }else{
                    Log.d("Error", "Getting user error");
                }
            }
        });
        return participant;
    }

    private void getMyAppointments(TableLayout table, MyAppointmentsActivity activity) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        CollectionReference currentUserAppointments = mDB.collection("appointments");
        Query query = currentUserAppointments.whereEqualTo("practitioner", currentUser.name);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Timestamp start = (Timestamp) document.get("start");
                        Timestamp end = (Timestamp) document.get("end");
                        assert start != null;
                        Date startDate = start.toDate();
                        assert end != null;
                        Date endDate = end.toDate();
                        appointmentsList.add(new Appointment(Objects.requireNonNull(document.get("id")).toString(), Objects.requireNonNull(document.get("title")).toString(), Objects.requireNonNull(document.get("patient")).toString(), startDate, endDate, Objects.requireNonNull(document.get("patient")).toString()));
                        TableRow tableRow = new TableRow(activity);
                        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tableRow.setMinimumHeight(100);
                        TableRow tableRow2 = new TableRow(activity);
                        tableRow2.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tableRow2.setMinimumHeight(100);
                        TableRow tableRow3 = new TableRow(activity);
                        tableRow3.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tableRow3.setMinimumHeight(100);
                        TextView tw1 = new TextView(activity);
                        TextView tw2 = new TextView(activity);
                        TextView tw4 = new TextView(activity);
                        TextView tw5 = new TextView(activity);
                        TextView tw6 = new TextView(activity);
                        Timestamp tsStart = (Timestamp) document.get("start");
                        Timestamp tsEnd = (Timestamp) document.get("end");
                        Calendar cal = Calendar.getInstance();
                        TimeZone tz = cal.getTimeZone();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                        sdf.setTimeZone(tz);
                        String localTimeStart = sdf.format(new Date(tsStart.getSeconds() * 1000));
                        String localTimeEnd = sdf.format(new Date(tsEnd.getSeconds() * 1000));
                        tw1.setText(localTimeStart);
                        tw2.setText(localTimeEnd);
                        String patient = Objects.requireNonNull(document.get("patient")).toString().isEmpty() ? "Not reserved" : Objects.requireNonNull(document.get("patient")).toString();
                        tw4.setText("Patient: " + patient);
                        tw5.setText("Title: " + Objects.requireNonNull(document.get("title")).toString());
                        tw1.setPaddingRelative(10, 5, 10, 5);
                        tw2.setPaddingRelative(10, 5, 10, 5);
                        tw4.setPaddingRelative(10, 5, 10, 5);
                        tw5.setPaddingRelative(10, 5, 10, 5);
                        tw6.setPaddingRelative(10, 5, 10, 5);
                        Button b = new Button(activity);
                        b.setText("Delete");
                        b.setBackgroundResource(R.drawable.roundedbutton);
                        b.setPaddingRelative(20, 5, 20, 5);
                        b.setHeight(20);
                        b.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v){
                                currentUserAppointments.document(Objects.requireNonNull(document.get("id")).toString()).delete();
                            }
                        });
                        tableRow.addView(tw1);
                        tableRow.addView(tw2);
                        tableRow2.addView(tw4);
                        tableRow3.addView(tw5);
                        tableRow3.addView(b);
                        table.addView(tableRow);
                        table.addView(tableRow2);
                        table.addView(tableRow3);
                    }
                } else {
                    Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void back(View view) {
        Intent selectAppointmentIntent = new Intent(getApplicationContext(), AppointmentsActivity.class);
        startActivity(selectAppointmentIntent);
        finish();
    }

}
