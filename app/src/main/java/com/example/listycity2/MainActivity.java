package com.example.listycity2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ListView cityList;
    private ArrayAdapter<String> cityAdapter;
    private ArrayList<String> dataList;

    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        cityList = findViewById(R.id.city_list);
        Button addBtn = findViewById(R.id.btn_add);
        Button deleteBtn = findViewById(R.id.btn_delete);

        String[] cities = {"Edmonton", "Vancouver", "Moscow", "Sydney", "Berlin", "Vienna", "Tokyo", "Beijing", "Osaka", "New Delhi"};
        dataList = new ArrayList<>();
        dataList.addAll(Arrays.asList(cities));

        // Keep your row layout (content.xml)
        cityAdapter = new ArrayAdapter<>(this, R.layout.content, dataList);
        cityList.setAdapter(cityAdapter);

        // Tap a city to "select" it for deletion
        cityList.setOnItemClickListener((parent, view, position, id) -> selectedPosition = position);

        // ADD CITY
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

                        dataList.add(newCity);
                        cityAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // DELETE CITY (delete selected)
        deleteBtn.setOnClickListener(v -> {
            if (selectedPosition < 0 || selectedPosition >= dataList.size()) {
                Toast.makeText(this, "Tap a city to select it first.", Toast.LENGTH_SHORT).show();
                return;
            }

            dataList.remove(selectedPosition);
            cityAdapter.notifyDataSetChanged();
            selectedPosition = -1;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
