package com.emst.cours.ecommerce;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.snackbar.ContentViewCallback;
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
import android.widget.Toast;

import com.emst.cours.ecommerce.model.Card;
import com.emst.cours.ecommerce.prevalent.Prevalent;
import com.emst.cours.ecommerce.view.CardView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button btn_next;
    private TextView total_price,msg;
    private TextView total;
    private int totalPrice = 0;
    private ImageView btn_retour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        recyclerView = (RecyclerView) findViewById(R.id.card_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btn_next = (Button) findViewById(R.id.next_card);
        btn_retour = (ImageView) findViewById(R.id.retour);
        total_price = (TextView) findViewById(R.id.total_price);
        total = (TextView) findViewById(R.id.total);
        msg = (TextView) findViewById(R.id.msg);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CardActivity.this,FinalActivity.class);
                intent.putExtra("Total price" , String.valueOf(totalPrice));
                startActivity(intent);
                finish();
            }
        });

        btn_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {

        super.onStart();
        myCommand();

        final DatabaseReference cardListRef = FirebaseDatabase.getInstance().getReference().child("Card List");

        FirebaseRecyclerOptions<Card> options = new FirebaseRecyclerOptions.Builder<Card>().setQuery(cardListRef.child("User View")
        .child(Prevalent.currentOnlineUser.getPhone()).child("Products") , Card.class).build();

        FirebaseRecyclerAdapter<Card, CardView> adapter = new FirebaseRecyclerAdapter<Card, CardView>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CardView holder, int position, @NonNull final Card model) {
                holder.txtProductQuantity.setText(model.getQuantity());
                holder.txtProductPrice.setText(model.getPrice());
                holder.txtProductName.setText(model.getPname());

                int totalPriceForOne = ((Integer.valueOf(model.getPrice()))) * Integer.valueOf(model.getQuantity());
                totalPrice = totalPrice + totalPriceForOne ;

                total.setText(String.valueOf(totalPrice));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence charSequence[] = new CharSequence[]{
                                "Modifier","Supprimer"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CardActivity.this);
                        builder.setTitle("Options");
                        builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    Intent intent = new Intent(CardActivity.this,ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if (which == 1){
                                    cardListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone())
                                            .child("Products").child(model.getPid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(CardActivity.this, "Produit supprimé", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CardActivity.this,HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CardView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_items_layout , viewGroup ,false);
                CardView cardView = new CardView(view);
                return cardView;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void myCommand(){
        DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference().child("Command").child(Prevalent.currentOnlineUser.getPhone());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String state = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                    if (state.equals("livre")){
                        recyclerView.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);
                        msg.setText("Merci , Votre commande a été livré");
                        btn_next.setVisibility(View.GONE);

                    }else if (state.equals("non livre")){
                        recyclerView.setVisibility(View.GONE);
                        msg.setVisibility(View.VISIBLE);
                        btn_next.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
