package com.example.listycity2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView cityList;
    private CityAdapter cityAdapter;

    private ArrayList<String> dataList;
    private ArrayList<String> docIdList;

    private FirebaseFirestore db;
    private CollectionReference citiesRef;

    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        cityList = findViewById(R.id.city_list);
        Button addBtn = findViewById(R.id.btn_add);
        Button deleteBtn = findViewById(R.id.btn_delete);

        dataList = new ArrayList<>();
        docIdList = new ArrayList<>();

        // RecyclerView setup
        cityAdapter = new CityAdapter(dataList, pos -> {
            selectedPosition = pos;
            cityAdapter.setSelectedPos(pos);
        });

        cityList.setLayoutManager(new LinearLayoutManager(this));
        cityList.setAdapter(cityAdapter);

        // Firestore listener (keeps UI synced)
        citiesRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", error.toString());
                return;
            }

            if (value != null) {
                dataList.clear();
                docIdList.clear();

                for (QueryDocumentSnapshot doc : value) {
                    String name = doc.getString("name");
                    if (name != null) {
                        dataList.add(name);
                        docIdList.add(doc.getId());
                    }
                }

                selectedPosition = -1;
                cityAdapter.setSelectedPos(RecyclerView.NO_POSITION);
                cityAdapter.notifyDataSetChanged();
            }
        });

        // Swipe to delete (REQUIRED)
        ItemTouchHelper.SimpleCallback swipeCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int pos = viewHolder.getAdapterPosition();
                        if (pos < 0 || pos >= docIdList.size()) return;

                        String docId = docIdList.get(pos);

                        citiesRef.document(docId)
                                .delete()
                                .addOnFailureListener(e -> {
                                    Toast.makeText(MainActivity.this,
                                            "Delete failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                        // Snapshot listener will refresh the list
                    }
                };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(cityList);

        // Add City (writes to Firestore)
        addBtn.setOnClickListener(v -> {
            EditText input = new EditText(this);
            input.setHint("Enter city name");

            new AlertDialog.Builder(this)
                    .setTitle("Add City")
                    .setView(input)
                    .setPositiveButton("Add", (dialog, which) -> {

                        String newCity = input.getText().toString().trim();

                        if (newCity.isEmpty()) {
                            Toast.makeText(this, "City name can't be empty.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        HashMap<String, Object> city = new HashMap<>();
                        city.put("name", newCity);

                        citiesRef.add(city)
                                .addOnSuccessListener(docRef -> {
                                    Log.d("Firestore", "Added doc id: " + docRef.getId());
                                    Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Add failed", e);
                                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Optional: keep delete button working too (not required if swipe is required)
        deleteBtn.setOnClickListener(v -> {
            if (selectedPosition < 0 || selectedPosition >= docIdList.size()) {
                Toast.makeText(this, "Tap a city to select it first.", Toast.LENGTH_SHORT).show();
                return;
            }

            String docId = docIdList.get(selectedPosition);

            citiesRef.document(docId)
                    .delete()
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}