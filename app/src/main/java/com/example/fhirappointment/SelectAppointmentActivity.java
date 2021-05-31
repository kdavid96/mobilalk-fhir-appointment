package com.example.fhirappointment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SelectAppointmentActivity extends AppCompatActivity {
    private static final String LOG_TAG = SelectAppointmentActivity.class.getName();
    private FirebaseAuth mAuth;
    private final User currentUser = new User();
    private final User currentPractitioner = new User();
    List<Appointment> appointmentsList = new ArrayList();
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_appointment);
        Bundle b = getIntent().getExtras();
        String uid = null;
        name = null;
        if( b != null ){
            uid = b.getString("uid");
            name = b.getString("name");
        }
        FirebaseUser user;
        SelectAppointmentActivity activity = this;
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Authenticated user!");
            mAuth = FirebaseAuth.getInstance();
            FirebaseFirestore mDB = FirebaseFirestore.getInstance();
            assert uid != null;
            mDB.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot doc = task.getResult();
                        assert doc != null;
                        if(doc.exists()){
                            currentPractitioner.set(doc.getString("email"),doc.getString("accountType"),doc.getString("uid"),doc.getString("name"));
                            if(currentPractitioner.getName() != null) {
                                TextView textViewAppointemnsOf = (TextView) findViewById(R.id.textViewAppointemnsOf);
                                textViewAppointemnsOf.setText("Select appointment from " + currentPractitioner.getName() + ".");
                                TableLayout table = (TableLayout) findViewById(R.id.appointmentsTable);
                                getAppointments(table, activity);
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

    private void getAppointments(TableLayout table, SelectAppointmentActivity activity) {
        FirebaseFirestore mDB = FirebaseFirestore.getInstance();
        CollectionReference appRef = mDB.collection("appointments");
        Query query = appRef.whereEqualTo("practitioner", currentPractitioner.getName()).whereEqualTo("patient", "");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task){
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Timestamp start = (Timestamp) document.get("start");
                        Timestamp end = (Timestamp) document.get("end");
                        assert start != null;
                        Date startDate = start.toDate();
                        assert end != null;
                        Date endDate = end.toDate();
                        appointmentsList.add(new Appointment(Objects.requireNonNull(document.get("id")).toString(), Objects.requireNonNull(document.get("title")).toString(), document.get("patient").toString(), startDate, endDate, Objects.requireNonNull(document.get("patient")).toString()));
                        TableRow tableRow = new TableRow(activity);
                        TableRow tableRow2 = new TableRow(activity);
                        TableRow tableRow3 = new TableRow(activity);
                        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tableRow.setMinimumHeight(60);
                        TextView tw1 = new TextView(activity);
                        TextView tw2 = new TextView(activity);
                        tw1.setText("Practitioner: " + Objects.requireNonNull(Objects.requireNonNull(document.get("practitioner")).toString()));
                        tw2.setText("Title: " + Objects.requireNonNull(document.get("title")).toString());
                        tw1.setPaddingRelative(40, 0, 40, 0);
                        tw2.setPaddingRelative(40, 0, 40, 0);
                        Button b = new Button(activity);
                        b.setText(R.string.book);
                        b.setBackgroundResource(R.drawable.roundedbutton);
                        b.setPaddingRelative(20, 0, 20, 0);
                        b.setHeight(20);
                        b.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v){
                                appRef.document(Objects.requireNonNull(document.get("id")).toString()).update("patient", name);
                                Toast.makeText(SelectAppointmentActivity.this, "Successful booking!", Toast.LENGTH_LONG).show();
                                recreate();
                            }
                        });
                        tableRow.addView(tw1);
                        tableRow2.addView(tw2);
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
