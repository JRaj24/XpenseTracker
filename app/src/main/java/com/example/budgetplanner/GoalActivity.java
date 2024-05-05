package com.example.budgetplanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.example.budgetplanner.databinding.ActivityGoalBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class GoalActivity extends AppCompatActivity {



    @NonNull ActivityGoalBinding binding;
    String gName, gAmount;
    String gDate,gSaving;
    FirebaseDatabase db;
    DatabaseReference reference;

    private TableLayout tableLayout;
    private Button pickDateBtn;
    private TextView selectedDateTV;

    ImageButton backButton;

    Button showButton,deleteButton,addbutton,computeButton;

    EditText savingResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showButton = findViewById(R.id.showDetails);
        deleteButton = findViewById(R.id.buttonDelete);
        computeButton=findViewById(R.id.goalCompute);
        savingResult=findViewById(R.id.savingResult);
        addbutton=findViewById(R.id.button);
        backButton = findViewById(R.id.backButton);

        deleteButton.setEnabled(false);
        addbutton.setEnabled(false);


        // on below line we are initializing our variables.
        pickDateBtn = findViewById(R.id.idBtnPickDate);
        selectedDateTV = findViewById(R.id.goalDuration);

        // on below line we are adding click listener for our pick date button
        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        GoalActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                if(monthOfYear<10 && dayOfMonth<10 ) {
                                    selectedDateTV.setText("0"+dayOfMonth + "-0" + (monthOfYear + 1) + "-" + year);
                                }
                                else if(dayOfMonth<10) {
                                    selectedDateTV.setText("0"+dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }else if(monthOfYear<9) {
                                    selectedDateTV.setText(dayOfMonth + "-0" + (monthOfYear + 1) + "-" + year);
                                }
                                else {
                                    selectedDateTV.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                }


                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        computeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gDate = binding.goalDuration.getText().toString();
                gAmount = binding.editTextNumberDecimal.getText().toString();

                if (!gDate.isEmpty() && !gAmount.isEmpty() ) {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    LocalDate endDate = LocalDate.parse(gDate, formatter);
                    LocalDate startDateWithoutFormat = LocalDate.now();
                    String startDateString = startDateWithoutFormat.format(formatter);
                    LocalDate startDate = LocalDate.parse(startDateString, formatter);

                    Period period = Period.between(startDate, endDate);

                    long months = period.toTotalMonths();

                    Log.d("months", String.valueOf(months));


                    long tempGoalAmt = Long.parseLong(gAmount);
                    double inflation = 0.05;
                    int n = 12;

                    double compoundedprincipal = tempGoalAmt * Math.pow(1 + (inflation / n), months);


                    Log.d("principal", String.valueOf(compoundedprincipal));

                    double amounthPermonth = compoundedprincipal / months;

                    Log.d("per month", String.valueOf(amounthPermonth));

                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String formattedNumber = decimalFormat.format(amounthPermonth);

                    savingResult.setText(formattedNumber);
                    addbutton.setEnabled(true);


                    Toast.makeText(GoalActivity.this, "Computed the savings", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(GoalActivity.this, "Enter the details correctly", Toast.LENGTH_LONG).show();
                }



            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gName = binding.editTextTextPersonName.getText().toString();
                gAmount = binding.editTextNumberDecimal.getText().toString();
                gDate = binding.goalDuration.getText().toString();
                gSaving=binding.savingResult.getText().toString();


                if (!gName.isEmpty() && !gAmount.isEmpty() && !gDate.isEmpty()&&!gSaving.isEmpty()) {
                    Goal goal = new Goal(gName, gAmount, gDate,gSaving);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    reference.child("users").child(uid).child("Goals").child(gName).setValue(goal).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            binding.editTextTextPersonName.setText("");
                            binding.editTextNumberDecimal.setText("");
                            binding.goalDuration.setText("");
                            binding.savingResult.setText("");

                            Toast.makeText(GoalActivity.this, "Pushed the data", Toast.LENGTH_LONG).show();

                        }
                    });
                }

            }
        });

        showButton.setOnClickListener(view -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            reference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Goals");
            Log.d("reference",reference.toString());
            tableLayout = findViewById(R.id.expenseTable);

            populateTable();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoalActivity.this,DashboardActivity.class);
                startActivity(intent);
            }
        });

    }



    private void populateTable() {

        // Attach a ValueEventListener to the databaseReference to read the data

        String key;
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the TableLayout before populating it with data


                // Iterate through each child node in the Realtime Database
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    // Get the values of each column from the child node

                    String column1Value = childSnapshot.child("gName").getValue(String.class);
                    String column2Value = childSnapshot.child("gAmount").getValue(String.class);
                    String column3Value = childSnapshot.child("gDate").getValue(String.class);
                    String column4Value = childSnapshot.child("gSaving").getValue(String.class);

                    // Create a new row in the TableLayout
                    TableRow tableRow = new TableRow(GoalActivity.this);

                    // Create TextViews for each column and add them to the TableRow
                    TextView column1TextView = new TextView(GoalActivity.this);
                    column1TextView.setText(column1Value);
                    tableRow.addView(column1TextView);

                    TextView column2TextView = new TextView(GoalActivity.this);
                    column2TextView.setText(column2Value);
                    tableRow.addView(column2TextView);

                    TextView column3TextView = new TextView(GoalActivity.this);
                    column3TextView.setText(column3Value);
                    tableRow.addView(column3TextView);

                    TextView column4TextView = new TextView(GoalActivity.this);
                    column4TextView.setText(column4Value);
                    tableRow.addView(column4TextView);


                    tableRow.setTag(childSnapshot.getKey());
                    tableRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteButton.setEnabled(true);
                            String key = (String) v.getTag();
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override

                                public void onClick(View view) {
                                    reference.child(key).removeValue();
                                    Toast.makeText(GoalActivity.this,"Deleted the data",Toast.LENGTH_LONG).show();
                                    int index = getIndexFromKey(key);
                                    if (index >= 1) {
                                        tableLayout.removeViews(index,1);
                                    }
                                }
                            });
                        }
                    });
                    // Add the TableRow to the TableLayout
                    tableLayout.addView(tableRow);
                }


            }


            private int getIndexFromKey(String key) {
                int index = -1;
                for (int i = 1; i < tableLayout.getChildCount(); i++) {
                    View row = tableLayout.getChildAt(i);
                    if (key.equals(row.getTag())) {
                        index = i;
                        break;
                    }
                }
                return index;
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
