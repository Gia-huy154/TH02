package com.example.th02;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etAmount;
    private Spinner spCategory;
    private DatePicker dpDate;
    private EditText etNote;
    private Button btnSave;

    private static final String[] CATEGORIES = {"Ăn uống", "Đi lại", "Học tập", "Giải trí", "Khác"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        spCategory = findViewById(R.id.spCategory);
        dpDate = findViewById(R.id.dpDate);
        etNote = findViewById(R.id.etNote);
        btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CATEGORIES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();
        String note = etNote.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("Tên khoản chi không được để trống!");
            etTitle.requestFocus();
            return;
        }

        if (amountStr.isEmpty()) {
            etAmount.setError("Số tiền không được để trống!");
            etAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                etAmount.setError("Số tiền phải lớn hơn 0!");
                etAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("Số tiền không hợp lệ!");
            etAmount.requestFocus();
            return;
        }

        int day = dpDate.getDayOfMonth();
        int month = dpDate.getMonth() + 1;
        int year = dpDate.getYear();
        String date = String.format(Locale.getDefault(), "%02d/%02d/%d", day, month, year);

        String id = UUID.randomUUID().toString();
        Expense expense = new Expense(id, title, amount, category, date, note);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("EXTRA_EXPENSE", expense);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Thêm khoản chi thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }
}