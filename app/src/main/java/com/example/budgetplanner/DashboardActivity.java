package com.example.budgetplanner;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;



import com.example.budgetplanner.util.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;




public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{


    private String TAG = DashboardActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private ProgressBar dataAnalysis;
    private Runnable updateRatingRunnable;
    private Handler handler;
    private Context context;
    private DatabaseReference reference;

    private DatabaseReference reference2;

    Button btLogout;
    private TextView user_name, user_email;

    FirebaseAuth firebaseAuth;

    private FirebaseUser currentUser;
    private Dialog dialog;
    GoogleSignInClient googleSignInClient;

    public int getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(int authProvider) {
        this.authProvider = authProvider;
    }

    private int authProvider;

    private Map<String, Integer> budgetMap = new HashMap<String, Integer>();
    private Map<String, Integer> expenseMap = new HashMap<String, Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        init();
        setClickListeners();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        setUserData();

        getAuthenticationProvider();

        ImageView dBudget = findViewById(R.id.budget);

        ImageView dExpense=findViewById(R.id.expense);

        ImageView dGoal=findViewById(R.id.goal);

        ImageView dActivity = findViewById(R.id.activity);

        btLogout = findViewById(R.id.BtnLogout);

        // Initialize firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        dExpense.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, ExpenseActivity.class);
                startActivity(intent);
            }
        });

        dBudget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, BudgetActivity.class);
                startActivity(intent);
            }
        });

        dGoal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, GoalActivity.class);
                startActivity(intent);
            }
        });
        dActivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(DashboardActivity.this, ActivityActivity.class);
                String email = firebaseUser.getEmail();
                String displayName = firebaseUser.getDisplayName();
                Intent intent = new Intent(context, ActivityActivity.class);
                intent.putExtra(Constants.KEY_USER_EMAIL, email);
                intent.putExtra(Constants.KEY_USER_DISPLAY_NAME, displayName);
                startActivity(intent);
            }
        });





    }

    private void getAuthenticationProvider() {
        List<? extends UserInfo> providerData = null;
        if (currentUser != null) {
            providerData = currentUser.getProviderData();
        }
        if (providerData != null) {
            for (UserInfo userInfo : providerData) {
                switch (userInfo.getProviderId()) {

                    case Constants.GOOGLE_PROVIDER:
                        Log.e(TAG, "User is signed in with Google");
                        disableAccountSettings();
                        setAuthProvider(2);
                        break;
                    case Constants.PASSWORD_PROVIDER:
                        Log.e(TAG, "User is signed in with Password");
                        setAuthProvider(1);
                        break;
                }

            }
        }
    }

    private void disableAccountSettings() {
      /*  btn_change_email.setEnabled(false);
        btn_change_email.setAlpha(0.3f);

        btn_change_password.setEnabled(false);
        btn_change_password.setAlpha(0.3f);

*/
    }

    private void setUserData() {
        String userEmail = getIntent().getStringExtra(Constants.KEY_USER_EMAIL);
        String userName = getIntent().getStringExtra(Constants.KEY_USER_DISPLAY_NAME);

/*        Log.d("username",userName);
        Log.d("userEmail",userEmail);*/

       // user_name.setText(userName);
       // user_email.setText(userEmail);
    }

    private void setClickListeners() {
        btLogout.setOnClickListener((View.OnClickListener) this);
        //btn_change_email.setOnClickListener(this);
       // btn_change_password.setOnClickListener(this);
        //btn_remove_user.setOnClickListener(this);

    }

    private void signOut() {
        progressBar.setVisibility(View.VISIBLE);

        int authProvider = getAuthProvider();
        if (authProvider == 1) {          // password
            FirebaseAuth.getInstance().signOut();
        } else if (authProvider == 2) {     // google
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions);
            googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(context, "Log out successfully", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "Log out failed :" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        startActivity(new Intent(context, LoginActivity.class));
        finish();
    }


    private void init() {
        context = this;
        progressBar = findViewById(R.id.progressBar);
        //tv_user_name = findViewById(R.id.tv_user_name);
        //tv_user_email = findViewById(R.id.tv_user_email);
        btLogout = findViewById(R.id.BtnLogout);
        computeBudget();

        //btn_change_email = findViewById(R.id.btn_change_email);
        //btn_change_password = findViewById(R.id.btn_change_password);
      //  btn_remove_user = findViewById(R.id.btn_remove_user);


    }

    public void displayGraph(){
        // Create BarChart object
        BarChart barChart = findViewById(R.id.chart);
        System.out.println("Inside display graph");
// Disable some features of the chart
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.getDescription().setEnabled(false);

// Create ArrayLists to store the data for the chart
        ArrayList<BarEntry> budgetEntries = new ArrayList<>();
        ArrayList<BarEntry> expenseEntries = new ArrayList<>();

// Iterate through the budgetMap and expenseMap to populate the data ArrayLists
        int i = 0;
        for (String key : budgetMap.keySet()) {
            int budgetValue ,expenseValue;
            if( budgetMap.get(key)!=null && expenseMap.get(key)!=null) {

                budgetValue = budgetMap.get(key);
                expenseValue = expenseMap.get(key);

                // Add entries to the ArrayLists
                budgetEntries.add(new BarEntry(i, budgetValue));
                expenseEntries.add(new BarEntry(i, expenseValue));

                i++;
            } else  {
                if ( budgetMap.get(key)==null && expenseMap.get(key)!=null ) {
                    Toast.makeText(DashboardActivity.this, "Budget unavailable " + key, Toast.LENGTH_SHORT).show();
                    budgetValue = 0;
                    expenseValue = expenseMap.get(key);
                    budgetEntries.add(new BarEntry(i, budgetValue));
                    expenseEntries.add(new BarEntry(i, expenseValue));

                    i++;
                }
                if( expenseMap.get(key)==null && budgetMap.get(key)!=null ) {
                    Toast.makeText(DashboardActivity.this, "Budget unavailable " + key, Toast.LENGTH_SHORT).show();
                    expenseValue = 0;
                    budgetValue = budgetMap.get(key);
                    budgetEntries.add(new BarEntry(i, budgetValue));
                    expenseEntries.add(new BarEntry(i, expenseValue));
                    i++;
                }


                }

            }
        System.out.println(budgetEntries);
        System.out.println(expenseEntries);



// Create BarDataSet objects for the data
        BarDataSet budgetDataSet = new BarDataSet(budgetEntries, "Budget");
        budgetDataSet.setColor(Color.LTGRAY);
        Log.d("Budget_graph",budgetDataSet.toString());

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(Color.BLACK);
        Log.d("Budget_graph",expenseDataSet.toString());
        expenseDataSet.setDrawValues(false);

// Create BarData object and add the data sets
        BarData barData = new BarData(budgetDataSet, expenseDataSet);
        barData.setBarWidth(0.45f);

// Set the data for the chart
        barChart.setData(barData);

// Configure the X-axis
        XAxis xAxis = barChart.getXAxis();
        String s= Arrays.toString(budgetMap.keySet().toArray());
        xAxis.setValueFormatter(new IndexAxisValueFormatter(Collections.singleton(s)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(barData.getXMax() + 0.5f);

// Configure the legend
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);

        barChart.animate();
        barChart.invalidate();


    }



    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.BtnLogout:
                signOut();
                break;

        }
    }

    public void computeBudget() {

        //final String budgetString = new String[]{new String()};
        final String[] expenseString = {new String()};
        ObjectMapper objectMapper = new ObjectMapper();
        final Expense[] expObj = new Expense[1];
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        LocalDate currentDate = LocalDate.now();

        // Format the date to extract month and year
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMMyyyy");
        String currentMonth = currentDate.format(formatter);
        Double budgetForTheMonth;


        reference2 = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("budget");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String budgetTemp=new String();
                try {
                    budgetTemp = dataSnapshot.getValue().toString();

                }catch (NullPointerException ex){
                    Log.d("error",ex.getMessage());
                    Toast.makeText( DashboardActivity.this,"No Budget details", Toast.LENGTH_SHORT).show();
                }

                Pattern pattern = Pattern.compile("\\{([^{}]+)\\}");
                Matcher matcher = pattern.matcher(budgetTemp);

                while (matcher.find()) {
                    String[] keyValuePairs = matcher.group(1).split(",\\s+");
                    Map<String, String> map = new HashMap<>();
                    for (String keyValuePair : keyValuePairs) {
                        String[] tokens = keyValuePair.split("=");
                        if (tokens.length == 2) {
                            map.put(tokens[0].trim(), tokens[1].trim());
                        }
                    }
                    String amount = map.get("amount");
                    String month = map.get("month");
                    String year = map.get("year");
                    if (amount != null && month != null && year != null) {
                        budgetMap.put(month + year, budgetMap.getOrDefault(month + year, 0) + Integer.parseInt(amount));
                    }
                }

                System.out.println(budgetMap);
                Log.d("budget amount", budgetTemp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("expense");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method will be called every time data at the specified database reference changes.
                // Use the DataSnapshot parameter to access the data.

                //Budget budget = dataSnapshot.getValue(Budget.class);


                String expenseTemp=new String();
                try {
                    expenseTemp = dataSnapshot.getValue().toString();

                }catch (NullPointerException ex){
                    Log.d("error",ex.getMessage());
                    Toast.makeText( DashboardActivity.this,"No Expense details", Toast.LENGTH_SHORT).show();
                }

                //Gson gson = new Gson();
                Pattern pattern = Pattern.compile("\\{([^{}]+)\\}");
                Matcher matcher = pattern.matcher(expenseTemp);


                while (matcher.find()) {
                    String[] keyValuePairs = matcher.group(1).split(",\\s+");
                    Map<String, String> map = new HashMap<>();
                    for (String keyValuePair : keyValuePairs) {
                        String[] tokens = keyValuePair.split("=");
                        if (tokens.length == 2) {
                            map.put(tokens[0].trim(), tokens[1].trim());
                        }
                    }
                    String eName = map.get("eName");
                    String eAmount = map.get("eAmount");
                    String eMonthyear = map.get("eMonthyear");
                    if (eName != null && eAmount != null && eMonthyear != null) {
                        expenseMap.put(eMonthyear, expenseMap.getOrDefault(eMonthyear, 0) + Integer.parseInt(eAmount));

                    }
                }

                System.out.println(expenseMap);
                //BudgetDetails bd= (BudgetDetails) budget.getBudgetDetailsMap();
                //String reqJson = gson.toJson(bd.toString());
               // Log.d("budget string", budgetTemp);
                // budgetString[0] = dataSnapshot.getValue().toString();
                //Log.d("budget data",reqJson);
                //Log.d("object  data",budget.getMonthYear());

                //   Log.d(TAG, "Value is: " + budgetString[0]);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // This method will be called if the listener is cancelled or if there is an error
                Log.w(TAG, "Listener cancelled or error occurred", databaseError.toException());
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Call your function here
                displayGraph();
            }
        }, 8000); // Delay in milliseconds (8 seconds in this example)


    }


    @Override
    protected void onResume() {
        super.onResume();
        if (progressBar.getVisibility() == View.VISIBLE)
            progressBar.setVisibility(View.INVISIBLE);

    }

}


