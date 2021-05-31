package com.example.fhirappointment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddAppointmentActivity extends AppCompatActivity {
    private static final String LOG_TAG = AddAppointmentActivity.class.getName();
    private FirebaseAuth mAuth;
    private Appointment appointment;

    EditText editTextTitle;
    EditText editTextStartDate;
    EditText editTextEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
    }

    public void save(View view) throws ParseException {
        FirebaseUser user;
        AddAppointmentActivity activity = this;
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
            mAuth = FirebaseAuth.getInstance();
            editTextTitle = findViewById(R.id.editTextTitle);
            editTextStartDate = findViewById(R.id.editTextStartDate);
            Log.d("EDITTEXTSTARTDATE", editTextStartDate.getText().toString());
            editTextEndDate = findViewById(R.id.editTextEndDate);
            FirebaseFirestore mDB = FirebaseFirestore.getInstance();
            DocumentReference docRef = mDB.collection("appointments").document();
            SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy HH:mm:ss a", Locale.US);
            Date startDate = format.parse(editTextStartDate.getText().toString());
            Date endDate = format.parse(editTextEndDate.getText().toString());
            Bundle b = getIntent().getExtras();
            String practitionerName = null;
            if( b != null ){
                practitionerName = b.getString("name");
            }
            appointment = new Appointment(docRef.getId(), editTextTitle.getText().toString(), "", startDate, endDate, practitionerName);
            mDB.collection("appointments").document(docRef.getId())
                    .set(appointment)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(LOG_TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(LOG_TAG, "Error writing document", e);
                        }
                    });
            Intent selectAppointmentIntent = new Intent(getApplicationContext(), AppointmentsActivity.class);
            startActivity(selectAppointmentIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    public void back(View view) {
        Intent selectAppointmentIntent = new Intent(getApplicationContext(), AppointmentsActivity.class);
        startActivity(selectAppointmentIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
