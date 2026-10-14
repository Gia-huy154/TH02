package com.example.th02;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ExpenseDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvAmount;
    private TextView tvCategory;
    private TextView tvDate;
    private TextView tvNote;

    private Expense currentExpense;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvAmount = findViewById(R.id.tvDetailAmount);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDate = findViewById(R.id.tvDetailDate);
        tvNote = findViewById(R.id.tvDetailNote);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        if (intent != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                currentExpense = intent.getSerializableExtra("EXTRA_EXPENSE", Expense.class);
            } else {
                @SuppressWarnings("deprecation")
                Expense exp = (Expense) intent.getSerializableExtra("EXTRA_EXPENSE");
                currentExpense = exp;
            }
            position = intent.getIntExtra("EXTRA_POSITION", -1);
        }

        if (currentExpense != null) {
            displayExpenseDetails(currentExpense);
        }

        btnBack.setOnClickListener(v -> finish());

        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void displayExpenseDetails(Expense expense) {
        tvTitle.setText(expense.getTitle());
        tvAmount.setText(expense.getFormattedAmount());
        tvCategory.setText(expense.getCategory());
        tvDate.setText(expense.getDate());
        if (expense.getNote() != null && !expense.getNote().isEmpty()) {
            tvNote.setText(expense.getNote());
        } else {
            tvNote.setText(R.string.no_note);
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa khoản chi này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteExpense())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteExpense() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("EXTRA_IS_DELETED", true);
        resultIntent.putExtra("EXTRA_POSITION", position);
        setResult(RESULT_OK, resultIntent);

        Toast.makeText(this, "Đã xóa khoản chi!", Toast.LENGTH_SHORT).show();
        finish();
    }
}