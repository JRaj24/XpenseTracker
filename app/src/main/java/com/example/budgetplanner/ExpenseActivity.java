package com.example.budgetplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.budgetplanner.databinding.ActivityExpenseBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class ExpenseActivity extends AppCompatActivity {

    ActivityExpenseBinding binding;
    String eName,eAmount;
    String eDate;
    FirebaseDatabase db;
    DatabaseReference reference;

    private TableLayout tableLayout;

    Button showButton,deleteButton;


    ImageButton backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showButton = findViewById(R.id.showDetails);
        deleteButton = findViewById(R.id.buttonDelete);
        deleteButton.setEnabled(false);
        backButton = findViewById(R.id.backButton);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eName = binding.editTextTextPersonName.getText().toString();
                eAmount = binding.editTextNumberDecimal.getText().toString();
                eDate = binding.editTextDate.getText().toString();


                if(!eName.isEmpty() && !eAmount.isEmpty() && !eDate.isEmpty()){
                    String dateString = eDate;
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = null;
                    try {
                        date = dateFormat.parse(dateString);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    dateFormat = new SimpleDateFormat("MMMMyyyy");
                    String monthYearString = dateFormat.format(date);
                    Log.d("date in exp",monthYearString);
                    Expense expense = new Expense(eName,eAmount,eDate,monthYearString);
                    db = FirebaseDatabase.getInstance();
                    reference = db.getReference();
                    String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

                    reference.child("users").child(uid).child("expense").child(eName).setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            binding.editTextTextPersonName.setText("");
                            binding.editTextNumberDecimal.setText("");
                            binding.editTextDate.setText("");

                            Toast.makeText(ExpenseActivity.this,"Pushed the data",Toast.LENGTH_LONG).show();

                        }
                    });
                }

            }
        });

        showButton.setOnClickListener(view -> {
            String uid= FirebaseAuth.getInstance().getCurrentUser().getUid();
            reference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("expense");
            tableLayout = findViewById(R.id.expenseTable);

            populateTable();
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpenseActivity.this,DashboardActivity.class);
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

                    String column1Value = childSnapshot.child("eName").getValue(String.class);
                    String column2Value = childSnapshot.child("eAmount").getValue(String.class);
                    String column3Value = childSnapshot.child("eDate").getValue(String.class);
                    //String column4Value = childSnapshot.child("column4").getValue(String.class);

                    // Create a new row in the TableLayout
                    TableRow tableRow = new TableRow(ExpenseActivity.this);

                    // Create TextViews for each column and add them to the TableRow
                    TextView column1TextView = new TextView(ExpenseActivity.this);
                    column1TextView.setText(column1Value);
                    tableRow.addView(column1TextView);

                    TextView column2TextView = new TextView(ExpenseActivity.this);
                    column2TextView.setText(column2Value);
                    tableRow.addView(column2TextView);

                    TextView column3TextView = new TextView(ExpenseActivity.this);
                    column3TextView.setText(column3Value);
                    tableRow.addView(column3TextView);


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
                                    Toast.makeText(ExpenseActivity.this,"Deleted the data",Toast.LENGTH_LONG).show();
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
