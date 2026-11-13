package com.helper.weighing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class TallyFragment extends Fragment {
    private final ArrayList<Double> vals = new ArrayList<>();
    private SharedPreferences prefs;
    private EditText etWeight;
    private EditText etTarget;
    private TextView tvCount;
    private TextView tvTotal;
    private TextView tvAvg;
    private TextView tvLeft;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        View root = inflater.inflate(R.layout.fragment_tally, container, false);
        prefs = requireContext().getSharedPreferences("tally", Context.MODE_PRIVATE);

        etWeight = root.findViewById(R.id.et_weight);
        etTarget = root.findViewById(R.id.et_target);
        tvCount = root.findViewById(R.id.tv_count);
        tvTotal = root.findViewById(R.id.tv_total);
        tvAvg = root.findViewById(R.id.tv_avg);
        tvLeft = root.findViewById(R.id.tv_left);

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        ((ListView) root.findViewById(R.id.list)).setAdapter(adapter);

        loadSaved();
        etTarget.setText(prefs.getString("target", ""));
        Ui.watch(this::refresh, etTarget);

        root.findViewById(R.id.btn_add).setOnClickListener(x -> add());
        etWeight.setOnEditorActionListener((t, actionId, ev) -> {
            add();
            return true;
        });
        root.findViewById(R.id.btn_undo).setOnClickListener(x -> {
            if (!vals.isEmpty()) {
                vals.remove(vals.size() - 1);
                saveVals();
                refresh();
            }
        });
        root.findViewById(R.id.btn_reset).setOnClickListener(x -> {
            if (vals.isEmpty()) return;
            new MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.confirm_reset)
                    .setPositiveButton(R.string.btn_reset, (d, w) -> {
                        vals.clear();
                        saveVals();
                        refresh();
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        });

        refresh();
        return root;
    }

    private void add() {
        double w = Calc.parse(etWeight.getText());
        if (w <= 0) return;
        vals.add(w);
        etWeight.setText("");
        saveVals();
        refresh();
    }

    private void loadSaved() {
        String s = prefs.getString("vals", "");
        if (s.isEmpty()) return;
        for (String p : s.split(",")) {
            try {
                vals.add(Double.parseDouble(p));
            } catch (NumberFormatException ignored) {
                // lewati data rusak
            }
        }
    }

    private void saveVals() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.size(); i++) {
            if (i > 0) sb.append(',');
            sb.append(vals.get(i));
        }
        prefs.edit().putString("vals", sb.toString()).apply();
    }

    private void refresh() {
        prefs.edit().putString("target", etTarget.getText().toString()).apply();

        double total = 0;
        for (double d : vals) total += d;
        int n = vals.size();
        tvCount.setText(getString(R.string.fmt_count, n));
        tvTotal.setText(Calc.fmt(total));
        tvAvg.setText(n == 0 ? "—" : Calc.fmt(total / n));

        double target = Calc.parse(etTarget.getText());
        if (target > 0) {
            double left = target - total;
            tvLeft.setText(left >= 0
                    ? getString(R.string.fmt_left, Calc.fmt(left))
                    : getString(R.string.fmt_over, Calc.fmt(-left)));
            tvLeft.setVisibility(View.VISIBLE);
        } else {
            tvLeft.setVisibility(View.GONE);
        }

        List<String> rows = new ArrayList<>();
        for (int i = n - 1; i >= 0; i--) {
            rows.add(getString(R.string.fmt_tally_row, i + 1, Calc.fmt(vals.get(i))));
        }
        adapter.clear();
        adapter.addAll(rows);
    }
}
