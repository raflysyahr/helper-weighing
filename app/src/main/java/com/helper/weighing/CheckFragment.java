package com.helper.weighing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

public class CheckFragment extends Fragment {
    private EditText etTarget;
    private EditText etTol;
    private EditText etRead;
    private EditText etTare;
    private MaterialCardView card;
    private TextView tvStatus;
    private TextView tvNet;
    private TextView tvLeft;
    private TextView tvRange;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        View root = inflater.inflate(R.layout.fragment_check, container, false);
        etTarget = root.findViewById(R.id.et_target);
        etTol = root.findViewById(R.id.et_tol);
        etRead = root.findViewById(R.id.et_read);
        etTare = root.findViewById(R.id.et_tare);
        card = root.findViewById(R.id.card_status);
        tvStatus = root.findViewById(R.id.tv_status);
        tvNet = root.findViewById(R.id.tv_net);
        tvLeft = root.findViewById(R.id.tv_left);
        tvRange = root.findViewById(R.id.tv_range);

        etTol.setText("2");
        Ui.watch(this::calc, etTarget, etTol, etRead, etTare);
        root.findViewById(R.id.btn_reset).setOnClickListener(x -> {
            etTarget.setText("");
            etRead.setText("");
            etTare.setText("");
            etTol.setText("2");
        });
        calc();
        return root;
    }

    private void setCard(int colorRes) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), colorRes));
    }

    private void calc() {
        double target = Calc.parse(etTarget.getText());
        double tol = Calc.parse(etTol.getText());
        double net = Calc.parse(etRead.getText()) - Calc.parse(etTare.getText());
        tvNet.setText(getString(R.string.fmt_net, Calc.fmt(net)));

        if (target <= 0) {
            setCard(R.color.status_idle_bg);
            tvStatus.setText(R.string.status_idle);
            tvLeft.setText("");
            tvRange.setText("");
            return;
        }

        switch (Calc.status(net, target, tol)) {
            case Calc.LOW:
                setCard(R.color.status_low_bg);
                tvStatus.setText(R.string.status_low);
                break;
            case Calc.HIGH:
                setCard(R.color.status_high_bg);
                tvStatus.setText(R.string.status_high);
                break;
            default:
                setCard(R.color.status_ok_bg);
                tvStatus.setText(R.string.status_ok);
                break;
        }

        double left = target - net;
        tvLeft.setText(left >= 0
                ? getString(R.string.fmt_left, Calc.fmt(left))
                : getString(R.string.fmt_over, Calc.fmt(-left)));
        tvRange.setText(getString(R.string.fmt_range,
                Calc.fmt(Calc.lower(target, tol)), Calc.fmt(Calc.upper(target, tol))));
    }
}
