package com.emst.cours.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.emst.cours.ecommerce.interfaces.ItemClickListner;
import com.emst.cours.ecommerce.model.Command;
import com.emst.cours.ecommerce.view.CommandView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminCommandActivity extends AppCompatActivity {

    private RecyclerView commandList;
    private DatabaseReference commandRef;
    private ImageView fer_com;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_command);

        commandRef = FirebaseDatabase.getInstance().getReference().child("Command");
        commandList = findViewById(R.id.command_list);
        commandList.setLayoutManager(new LinearLayoutManager(this));
        fer_com = (ImageView) findViewById(R.id.fer_com);
        fer_com.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminCommandActivity.this, AdminHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Command> options = new FirebaseRecyclerOptions.Builder<Command>().setQuery(commandRef, Command.class)
                .build();

        FirebaseRecyclerAdapter<Command, CommandView> adapter = new FirebaseRecyclerAdapter<Command, CommandView>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommandView holder, final int position, @NonNull final Command model) {
                holder.userName.setText(model.getName());
                holder.userPhone.setText(model.getPhone());
                holder.userCity.setText(model.getAddress() + " " + model.getCity());
                holder.userDateTime.setText(model.getDate() + " " + model.getTime());
                holder.userPrice.setText(model.getTotalPrice() + " £");


                holder.btn_all_products.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pid = getRef(position).getKey();
                        Intent intent = new Intent(AdminCommandActivity.this, AdminDisplayActivity.class);
                        intent.putExtra("pid", pid);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence sequence[] = new CharSequence[]{
                                "Oui", "Non"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminCommandActivity.this);
                        builder.setTitle("Commande livré ou non:");
                        builder.setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    String pid = getRef(position).getKey();
                                    removeCommand(pid);
                                } else {
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CommandView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.command_layout, viewGroup, false);
                return new CommandView(view);
            }
        };

        commandList.setAdapter(adapter);
        adapter.startListening();
    }

    private void removeCommand(String pid) {
        commandRef.child(pid).removeValue();
    }
}


