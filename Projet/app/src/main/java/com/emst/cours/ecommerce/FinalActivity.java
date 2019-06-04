package com.emst.cours.ecommerce;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.emst.cours.ecommerce.prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class FinalActivity extends AppCompatActivity {

    private EditText nameTxt , phoneTxt , addressTxt , cityTxt;
    private Button btn_confirm;
    private String totalPrice = "";
    private ImageView btn_retour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        totalPrice = getIntent().getStringExtra("Total price");

        btn_confirm = (Button) findViewById(R.id.btn_final);
        btn_retour = (ImageView) findViewById(R.id.retour1);
        nameTxt = (EditText) findViewById(R.id.final_name);
        phoneTxt = (EditText) findViewById(R.id.final_number);
        cityTxt = (EditText) findViewById(R.id.final_city);
        addressTxt = (EditText) findViewById(R.id.final_address);

        nameTxt.setText(Prevalent.currentOnlineUser.getName());
        phoneTxt.setText(Prevalent.currentOnlineUser.getPhone());

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        btn_retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinalActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void verify() {
        if (TextUtils.isEmpty(nameTxt.getText().toString())){
            Toast.makeText(this, "Saisissez votre nom SVP", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phoneTxt.getText().toString())){
            Toast.makeText(this, "Saisissez votre numéro de téléphone SVP", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(addressTxt.getText().toString())){
            Toast.makeText(this, "Saisissez votre adresse SVP", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(cityTxt.getText().toString())){
            Toast.makeText(this, "Saisissez votre ville SVP",  Toast.LENGTH_SHORT).show();
        }
        else{
            confirm();
        }
    }

    private void confirm() {

        final String saveCurrentDate , saveCurrentTIme ;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd,yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTIme = currentTime.format(calendar.getTime());

        final DatabaseReference commandRef = FirebaseDatabase.getInstance().getReference().child("Command")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("totalPrice",totalPrice);
        hashMap.put("name",nameTxt.getText().toString());
        hashMap.put("phone",phoneTxt.getText().toString());
        hashMap.put("address",addressTxt.getText().toString());
        hashMap.put("city",cityTxt.getText().toString());
        hashMap.put("date",saveCurrentDate);
        hashMap.put("time",saveCurrentTIme);
        hashMap.put("state","non livre");

        commandRef.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Card List")
                            .child("User View").child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(FinalActivity.this, "Commande Valider", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(FinalActivity.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

}
