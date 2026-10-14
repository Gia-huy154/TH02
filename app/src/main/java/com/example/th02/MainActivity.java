package com.example.th02;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalAmount;
    private List<Expense> expenseList;
    private ExpenseAdapter adapter;

    private final ActivityResultLauncher<Intent> addExpenseLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Expense newExpense;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        newExpense = result.getData().getSerializableExtra("EXTRA_EXPENSE", Expense.class);
                    } else {
                        @SuppressWarnings("deprecation")
                        Expense exp = (Expense) result.getData().getSerializableExtra("EXTRA_EXPENSE");
                        newExpense = exp;
                    }
                    if (newExpense != null) {
                        expenseList.add(newExpense);
                        adapter.notifyItemInserted(expenseList.size() - 1);
                        updateTotalAmount();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> detailExpenseLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean isDeleted = result.getData().getBooleanExtra("EXTRA_IS_DELETED", false);
                    int position = result.getData().getIntExtra("EXTRA_POSITION", -1);
                    if (isDeleted && position >= 0 && position < expenseList.size()) {
                        expenseList.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, expenseList.size() - position);
                        updateTotalAmount();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        RecyclerView rvExpenseList = findViewById(R.id.rvExpenseList);
        Button btnAddExpense = findViewById(R.id.btnAddExpense);

        initSampleData();

        adapter = new ExpenseAdapter(expenseList, (expense, position) -> {
            Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
            intent.putExtra("EXTRA_EXPENSE", expense);
            intent.putExtra("EXTRA_POSITION", position);
            detailExpenseLauncher.launch(intent);
        });

        rvExpenseList.setLayoutManager(new LinearLayoutManager(this));
        rvExpenseList.setAdapter(adapter);

        updateTotalAmount();

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            addExpenseLauncher.launch(intent);
        });
    }

    private void initSampleData() {
        expenseList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String today = sdf.format(new Date());

        expenseList.add(new Expense("1", "Ăn sáng", 30000, "Ăn uống", today, "Ăn phở bò"));
        expenseList.add(new Expense("2", "Đổ xăng", 50000, "Đi lại", today, "Xăng A95"));
        expenseList.add(new Expense("3", "Mua sách", 120000, "Học tập", today, "Sách Lập trình Android"));
    }

    private void updateTotalAmount() {
        double total = 0;
        for (Expense expense : expenseList) {
            total += expense.getAmount();
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,### đ", symbols);

        tvTotalAmount.setText(formatter.format(total));
    }
}