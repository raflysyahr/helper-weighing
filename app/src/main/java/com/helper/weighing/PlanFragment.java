package com.helper.weighing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PlanFragment extends Fragment {
    private EditText etTotal;
    private EditText etCap;
    private TextView tvA;
    private TextView tvB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        View root = inflater.inflate(R.layout.fragment_plan, container, false);
        etTotal = root.findViewById(R.id.et_total);
        etCap = root.findViewById(R.id.et_cap);
        tvA = root.findViewById(R.id.tv_plan_a);
        tvB = root.findViewById(R.id.tv_plan_b);
        Ui.watch(this::calc, etTotal, etCap);
        calc();
        return root;
    }

    private void calc() {
        double total = Calc.parse(etTotal.getText());
        double cap = Calc.parse(etCap.getText());
        if (total <= 0 || cap <= 0) {
            tvA.setText("—");
            tvB.setText("—");
            return;
        }
        int n = Calc.batchCount(total, cap);
        tvA.setText(getString(R.string.fmt_plan_n, n, Calc.fmt(total / n, 2)));

        int full = (int) Math.floor(total / cap + 1e-9);
        double rest = total - full * cap;
        if (full == 0) {
            tvB.setText(getString(R.string.fmt_plan_n, 1, Calc.fmt(total, 2)));
        } else {
            String b = getString(R.string.fmt_plan_n, full, Calc.fmt(cap, 2));
            if (rest > 0.005) b += "\n" + getString(R.string.fmt_plan_rest, Calc.fmt(rest, 2));
            tvB.setText(b);
        }
    }
}
