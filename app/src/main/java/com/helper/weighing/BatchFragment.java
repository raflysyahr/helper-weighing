package com.helper.weighing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class BatchFragment extends Fragment {
    private static final int N = 5;
    private static final int[] NAMES = {
            R.string.mat_cement, R.string.mat_sand, R.string.mat_stone, R.string.mat_water, R.string.mat_admix
    };

    private final EditText[] design = new EditText[N];
    private final EditText[] moist = new EditText[N]; // hanya indeks 1 (pasir) dan 2 (batu)
    private EditText etVol;
    private EditText etTol;
    private LinearLayout llResults;
    private TextView tvTotal;
    private TextView tvNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        View root = inflater.inflate(R.layout.fragment_batch, container, false);
        etVol = root.findViewById(R.id.et_volume);
        etTol = root.findViewById(R.id.et_tol);
        llResults = root.findViewById(R.id.ll_results);
        tvTotal = root.findViewById(R.id.tv_total);
        tvNote = root.findViewById(R.id.tv_note);

        LinearLayout llInputs = root.findViewById(R.id.ll_inputs);
        for (int i = 0; i < N; i++) {
            View row = inflater.inflate(R.layout.item_mat_input, llInputs, false);
            ((TextView) row.findViewById(R.id.mat_name)).setText(NAMES[i]);
            design[i] = row.findViewById(R.id.et_design);
            if (i == 1 || i == 2) {
                moist[i] = row.findViewById(R.id.et_moist);
            } else {
                row.findViewById(R.id.til_moist).setVisibility(View.INVISIBLE);
            }
            llInputs.addView(row);
        }
        etTol.setText("2");

        Ui.watch(this::calc, etVol, etTol);
        Ui.watch(this::calc, design);
        Ui.watch(this::calc, moist);

        root.findViewById(R.id.btn_load).setOnClickListener(x -> loadPreset());
        root.findViewById(R.id.btn_save).setOnClickListener(x -> savePreset());
        root.findViewById(R.id.btn_delete).setOnClickListener(x -> deletePreset());
        root.findViewById(R.id.btn_reset).setOnClickListener(x -> reset());

        calc();
        return root;
    }

    private void calc() {
        double vol = Calc.parse(etVol.getText());
        double tol = Calc.parse(etTol.getText());
        double[] d = new double[N];
        for (int i = 0; i < N; i++) d[i] = Calc.parse(design[i].getText());
        double sm = moist[1] == null ? 0 : Calc.parse(moist[1].getText());
        double tm = moist[2] == null ? 0 : Calc.parse(moist[2].getText());

        double[] t = Calc.batch(vol, d, sm, tm);
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        llResults.removeAllViews();
        double total = 0;
        for (int i = 0; i < N; i++) {
            total += t[i];
            int dec = (i == 4) ? 2 : 1;
            View row = inflater.inflate(R.layout.item_result, llResults, false);
            ((TextView) row.findViewById(R.id.r_name)).setText(NAMES[i]);
            ((TextView) row.findViewById(R.id.r_target)).setText(getString(R.string.fmt_kg, Calc.fmt(t[i], dec)));
            ((TextView) row.findViewById(R.id.r_range)).setText(getString(R.string.fmt_range,
                    Calc.fmt(Calc.lower(t[i], tol), dec), Calc.fmt(Calc.upper(t[i], tol), dec)));
            llResults.addView(row);
        }
        tvTotal.setText(getString(R.string.fmt_total, Calc.fmt(total)));

        double adj = Calc.waterAdjust(vol, d, sm, tm);
        if (Math.abs(adj) > 0.0001) {
            tvNote.setText(getString(R.string.note_water, Calc.fmt(adj)));
            tvNote.setVisibility(View.VISIBLE);
        } else {
            tvNote.setVisibility(View.GONE);
        }
    }

    private void reset() {
        etVol.setText("");
        for (int i = 0; i < N; i++) {
            design[i].setText("");
            if (moist[i] != null) moist[i].setText("");
        }
        etTol.setText("2");
    }

    // Urutan preset: desain x5, kadar air pasir, kadar air batu, toleransi.
    private String[] collect() {
        return new String[]{
                design[0].getText().toString(), design[1].getText().toString(),
                design[2].getText().toString(), design[3].getText().toString(),
                design[4].getText().toString(),
                moist[1].getText().toString(), moist[2].getText().toString(),
                etTol.getText().toString()
        };
    }

    private void apply(String[] v) {
        if (v == null || v.length < 8) return;
        for (int i = 0; i < N; i++) design[i].setText(v[i]);
        moist[1].setText(v[5]);
        moist[2].setText(v[6]);
        etTol.setText(v[7]);
    }

    private void savePreset() {
        final EditText input = new EditText(requireContext());
        input.setHint(R.string.preset_name);
        input.setSingleLine(true);
        FrameLayout box = new FrameLayout(requireContext());
        int p = Ui.dp(box, 20);
        box.setPadding(p, p / 2, p, 0);
        box.addView(input);
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.title_save)
                .setView(box)
                .setPositiveButton(R.string.btn_save, (dlg, w) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) return;
                    Presets.put(requireContext(), name, collect());
                    Toast.makeText(requireContext(), getString(R.string.msg_saved, name), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void loadPreset() {
        final List<String> names = Presets.names(requireContext());
        if (names.isEmpty()) {
            Toast.makeText(requireContext(), R.string.msg_no_preset, Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.title_load)
                .setItems(names.toArray(new String[0]),
                        (dlg, i) -> apply(Presets.get(requireContext(), names.get(i))))
                .show();
    }

    private void deletePreset() {
        final List<String> names = Presets.names(requireContext());
        if (names.isEmpty()) {
            Toast.makeText(requireContext(), R.string.msg_no_preset, Toast.LENGTH_SHORT).show();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.title_delete)
                .setItems(names.toArray(new String[0]), (dlg, i) -> {
                    final String name = names.get(i);
                    new MaterialAlertDialogBuilder(requireContext())
                            .setMessage(getString(R.string.confirm_delete, name))
                            .setPositiveButton(R.string.btn_delete, (d2, w2) -> {
                                Presets.remove(requireContext(), name);
                                Toast.makeText(requireContext(), getString(R.string.msg_deleted, name),
                                        Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                })
                .show();
    }
}
