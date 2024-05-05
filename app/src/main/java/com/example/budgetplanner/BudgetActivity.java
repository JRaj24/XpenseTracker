package com.example.budgetplanner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BudgetActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner monthSpinner,yearSpinner;
    private TextView amountTextView;
    private Button pushButton,showBudgetButton,deleteBudgetButton;
    private DatabaseReference reference;

    private TableLayout tableLayout;

    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Initialize Firebase
        reference = FirebaseDatabase.getInstance().getReference();

        // Get references to UI views
        monthSpinner = findViewById(R.id.monthSpinner);
        amountTextView = findViewById(R.id.amountEditText);
        pushButton = findViewById(R.id.pushButton);
        showBudgetButton = findViewById(R.id.showBudget);
        deleteBudgetButton = findViewById(R.id.deleteBudget);
        yearSpinner = findViewById(R.id.yearSpinner);
        backButton = findViewById(R.id.backButton);

        // Set up spinner with month names
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter);
        monthSpinner.setOnItemSelectedListener(this);




// Get the integer array from strings.xml
        int[] myIntegers = getResources().getIntArray(R.array.years);

// Convert the integer array to an array of Strings
        String[] stringArray = new String[myIntegers.length];
        for (int i = 0; i < myIntegers.length; i++) {
            stringArray[i] = String.valueOf(myIntegers[i]);
        }

// Create an ArrayAdapter with the string array
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stringArray);

// Set the drop-down layout style
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Set the adapter on the spinner
        yearSpinner.setAdapter(adapter2);

        // Set click listener for push button
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushToFirebase();
            }
        });

        showBudgetButton.setOnClickListener(view -> {
            String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("budget");
            tableLayout = findViewById(R.id.budgetTable);

            populateTable();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BudgetActivity.this,DashboardActivity.class);
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

                    String column1Value = childSnapshot.child("month").getValue(String.class);
                    String column2Value = childSnapshot.child("year").getValue(String.class);
                    String column3Value = childSnapshot.child("amount").getValue(String.class);



                    // Create a new row in the TableLayout
                    TableRow tableRow = new TableRow(BudgetActivity.this);

                    // Create TextViews for each column and add them to the TableRow
                    TextView column1TextView = new TextView(BudgetActivity.this);
                    column1TextView.setText(column1Value);
                    tableRow.addView(column1TextView);

                    TextView column2TextView = new TextView(BudgetActivity.this);
                    column2TextView.setText(column2Value);
                    tableRow.addView(column2TextView);

                    TextView column3TextView = new TextView(BudgetActivity.this);
                    column3TextView.setText(column3Value);
                    tableRow.addView(column3TextView);


                    tableRow.setTag(childSnapshot.getKey());
                    tableRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteBudgetButton.setEnabled(true);
                            String key = (String) v.getTag();
                            deleteBudgetButton.setOnClickListener(new View.OnClickListener() {
                                @Override

                                public void onClick(View view) {
                                    reference.child(key).removeValue();
                                    Toast.makeText(BudgetActivity.this, "Deleted the data", Toast.LENGTH_LONG).show();
                                    int index = getIndexFromKey(key);
                                    if (index >= 1) {
                                        tableLayout.removeViews(index, 1);
                                    }
                                }
                            });
                        }
                    });
                    // Add the TableRow to the TableLayout
                    tableLayout.addView(tableRow);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        });
    }

    private void pushToFirebase() {
        String month = monthSpinner.getSelectedItem().toString();
        String amount = amountTextView.getText().toString();
        String year = yearSpinner.getSelectedItem().toString();

        // Push data to Firebase Realtime Database
        String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference dataRef = reference.child("users").child(uid).child("budget").child(month+year);


       // reference.child("users").child(uid).child("expense").child(eName).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {

          //  String key = dataRef.push().getKey();
        dataRef.child("month").setValue(month);
        dataRef.child("year").setValue(year);
        dataRef.child("amount").setValue(amount);

        // Clear input fields
        monthSpinner.setSelection(0);
        amountTextView.setText("");

        // Display success message
        Toast.makeText(this, "Data pushed to Firebase", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Do nothing
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Do nothing
    }
}

