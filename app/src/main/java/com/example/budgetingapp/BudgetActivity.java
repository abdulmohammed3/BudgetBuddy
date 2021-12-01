package com.example.budgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BudgetActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private TextView totalBudgetAmountTV;
    private RecyclerView recyclerView;

    private DatabaseReference budgetRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loader;

    private  String post_key = "";
    private String item ="";
    private int amount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        mAuth = FirebaseAuth.getInstance();
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(mAuth.getCurrentUser().getUid());
        loader = new ProgressDialog(this);

        totalBudgetAmountTV = findViewById(R.id.totalBudgetAmountTextView);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;

                for(DataSnapshot snap: snapshot.getChildren()){
                    Data data = snap.getValue(Data.class);
                    totalAmount += data.getAmount();
                    String sTotal = String.valueOf("Month Budget: $"+ totalAmount);
                    totalBudgetAmountTV.setText(sTotal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

    }

    private void addItem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout,null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.itemsspinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText no = myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budgetAmount = amount.getText().toString();
                String budgetItem = itemSpinner.getSelectedItem().toString();
                String notes = no.getText().toString();

                if(TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Amount is requiered");
                }

                if(budgetItem.equals("Select Item")){
                    Toast.makeText(BudgetActivity.this, "Select A valid Item", Toast.LENGTH_SHORT).show();
                }

                else{
                    loader.setMessage("Adding budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = budgetRef.push().getKey();
                    DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = df.format(cal.getTime());

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks = Weeks.weeksBetween(epoch,now);
                    Months month = Months.monthsBetween(epoch,now);

                    Data data = new Data(budgetItem,date,id,notes,Integer.parseInt(budgetAmount),month.getMonths(),weeks.getWeeks());
                    budgetRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BudgetActivity.this, "Item Added Successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });


                }
                dialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(budgetRef,Data.class)
                .build();

        FirebaseRecyclerAdapter<Data,myViewHolder> adapter = new FirebaseRecyclerAdapter<Data, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder myViewHolder, int i, @NonNull Data data) {

                myViewHolder.setItemAmount("Allocated amount: $"+data.getAmount());
                myViewHolder.setDate("On: "+data.getDate());
                myViewHolder.setNotes("Notes: "+data.getNotes());
                myViewHolder.setItemName("Budget Item: "+data.getItem());

               // myViewHolder.notes.setText(View.GONE);

                switch (data.getItem()){
                    case "Transport":
                        myViewHolder.imageView.setImageResource(R.drawable.ic_transport);
                        break;
                    case "Food":
                        myViewHolder.imageView.setImageResource(R.drawable.ic_food);
                        break;
                    case "House":
                        myViewHolder.imageView.setImageResource(R.drawable.ic_house);
                        break;
                    case "Entertainment":
                        myViewHolder.imageView.setImageResource(R.drawable.ic_entertainment);
                        break;
                    case "Education":
                        myViewHolder.imageView.setImageResource(R.drawable.ic_education);
                        break;
                    case "Charity":
                        myViewHolder.imageView.setImageResource(R.drawable.ic_consultancy);
                        break;
                    case "Apparel":
                        myViewHolder.imageView.setImageResource(R.drawable.ic_shirt);
                        break;
                    case "Health":
                        myViewHolder.imageView.setImageResource(R.drawable.ic_health);
                        break;
                    case "Personal":
                       myViewHolder.imageView.setImageResource(R.drawable.ic_personalcare);
                        break;
                    case "Other":
                        myViewHolder.imageView.setImageResource(R.drawable.ic_other);
                        break;
                }

                myViewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        post_key = getRef(i).getKey();
                        item = data.getItem();
                        amount = data.getAmount();
                        updateData();
                    }
                });

            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrive_layout,parent,false);
                return new myViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }

    public class myViewHolder extends RecyclerView.ViewHolder{
    View myView;
    public ImageView imageView;
    public TextView notes,date;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
            imageView = itemView.findViewById(R.id.imageView);
            notes = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);
        }
        public void setItemName (String itemName){
            TextView item = myView.findViewById(R.id.item);
            item.setText(itemName);
        }

        public void setItemAmount (String itemAmount){
            TextView item = myView.findViewById(R.id.amount);
            item.setText(itemAmount);
        }
        public void setNotes (String itemNotes){
            TextView item = myView.findViewById(R.id.note);
            item.setText(itemNotes);
        }

        public void setDate (String itemDate){
            TextView date = myView.findViewById(R.id.date);
            date.setText(itemDate);
        }
    }
    private void updateData(){
        AlertDialog.Builder myDia = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout, null);

        myDia.setView(mView);
        final AlertDialog dialog = myDia.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNotes = mView.findViewById(R.id.note);

        mItem.setText(item);



        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button delBut = mView.findViewById(R.id.btnDelete);
        Button btnUpdate = mView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount = Integer.parseInt(mAmount.getText().toString());

                DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = df.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch,now);
                Months month = Months.monthsBetween(epoch,now);

                Data data = new Data(item,date,post_key,null,amount,month.getMonths(),weeks.getWeeks());
                budgetRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();

            }
        });

        dialog.show();


    }
}