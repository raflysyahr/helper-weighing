package com.helper.weighing;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

final class Ui {
    private Ui() {}

    static void watch(final Runnable r, EditText... fields) {
        TextWatcher w = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable e) { r.run(); }
        };
        for (EditText f : fields) {
            if (f != null) f.addTextChangedListener(w);
        }
    }

    static int dp(View v, int dp) {
        return Math.round(dp * v.getResources().getDisplayMetrics().density);
    }
}
