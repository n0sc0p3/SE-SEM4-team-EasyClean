package com.lithium.easyclean.mainPackage.dashboard.admin;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.lithium.easyclean.R;
import com.lithium.easyclean.mainPackage.dashboard.ToiletsAdapter;
import com.lithium.easyclean.mainPackage.dashboard.ViewToiletActivity;
import com.lithium.easyclean.mainPackage.start.Toilet;

import java.util.ArrayList;

public class ToiletListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toilet_list);
        Button backButton = findViewById(R.id.back);
        backButton.setOnClickListener(v -> finish());

//        Button addToiletButton = findViewById(R.id.add_toilet_button);
//        addToiletButton.setOnClickListener(v -> {
//            Intent intent = new Intent(ToiletListActivity.this, NewToiletActivity.class);
//            startActivity(intent);
//            finish();
//        });
    setListView();

    }

    private void setListView(){
        ArrayList<Toilet> list = new ArrayList<>();

        ToiletsAdapter adapter = new ToiletsAdapter(this, list);
        ListView listView = findViewById(R.id.toilet_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((arg0, view, position, id) -> {
            Toilet toilet = (Toilet) arg0.getItemAtPosition(position);
                Toast.makeText(ToiletListActivity.this, toilet.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ToiletListActivity.this, ViewToiletActivity.class);
            intent.putExtra("toilet", toilet);
            startActivity(intent);
            finish();

        });
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference scoreRef = rootRef.child("toilets");
        ChildEventListener mChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Toilet toilet = snapshot.getValue(Toilet.class);
                assert toilet != null;
                toilet.setId(snapshot.getKey());
                list.add(toilet);
                adapter.notifyDataSetChanged();
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Toilet toilet = snapshot.getValue(Toilet.class);
                assert toilet != null;
                toilet.setId(snapshot.getKey());

                String toiletId = snapshot.getKey();
                list.removeIf(n -> (n.getId().equals(toiletId)));


                list.add(toilet);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Toilet toilet = snapshot.getValue(Toilet.class);
                assert toilet != null;
                toilet.setId(snapshot.getKey());
                list.remove(toilet);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        scoreRef.addChildEventListener(mChildEventListener);
//        scoreRef.addListenerForSingleValueEvent(eventListener);
    }
}