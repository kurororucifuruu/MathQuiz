package com.rucifuruu.mathquiz;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(12, 18, 34);
    private final int panel = Color.rgb(25, 34, 57);
    private final int accent = Color.rgb(99, 102, 241);
    private final int white = Color.WHITE;
    private final int muted = Color.rgb(177, 188, 211);
    private LinearLayout root, content, options;
    private TextView progress, question, feedback;
    private Button[] answerButtons = new Button[4];
    private Button mainButton;
    private int questionNumber = 0, score = 0, correctAnswer = 0;
    private boolean answered = false;
    private final Random random = new Random();

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showHome();
    }

    private GradientDrawable rounded(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radius);
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private Button button(String label, int color) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(16);
        b.setTextColor(white);
        b.setAllCaps(false);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(rounded(color, 24));
        b.setPadding(12, 14, 12, 14);
        return b;
    }

    private void setup(String subtitle) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(bg);

        TextView brand = text("MATHQUIZ", 14, Color.rgb(165, 180, 252), true);
        brand.setLetterSpacing(0.15f);
        brand.setGravity(Gravity.CENTER);
        root.addView(brand, new LinearLayout.LayoutParams(-1, -2));

        TextView sub = text(subtitle, 14, muted, false);
        sub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, -2);
        sp.topMargin = 8;
        root.addView(sub, sp);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(20, 24, 20, 24);
        content.setBackground(rounded(panel, 28));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.topMargin = 28;
        root.addView(content, cp);
        setContentView(root);
    }

    private void showHome() {
        setup("Latihan matematika singkat");
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView emoji = text("＋  −  ×  ÷", 34, white, true);
        emoji.setGravity(Gravity.CENTER);
        content.addView(emoji, new LinearLayout.LayoutParams(-1, -2));

        TextView title = text("Asah logikamu!", 27, white, true);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-1, -2);
        tp.topMargin = 18;
        content.addView(title, tp);

        TextView desc = text("10 soal pilihan ganda dengan operasi\npenjumlahan, pengurangan, perkalian,\ndan pembagian.", 15, muted, false);
        desc.setGravity(Gravity.CENTER);
        desc.setLineSpacing(5, 1f);
        LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(-1, -2);
        dp.topMargin = 12;
        content.addView(desc, dp);

        mainButton = button("Mulai Kuis", accent);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2);
        bp.topMargin = 26;
        content.addView(mainButton, bp);
        mainButton.setOnClickListener(v -> startQuiz());
        TextView foot = text("Dibuat untuk belajar • MathQuiz v1.0", 12, muted, false);
        foot.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(-1, -2);
        fp.topMargin = 20;
        root.addView(foot, fp);
    }

    private void startQuiz() {
        questionNumber = 0;
        score = 0;
        showQuestion();
    }

    private void showQuestion() {
        answered = false;
        setup("Pilih jawaban yang benar");
        progress = text("SOAL " + (questionNumber + 1) + " DARI 10", 13, Color.rgb(165,180,252), true);
        content.addView(progress);

        question = text("", 30, white, true);
        question.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams qp = new LinearLayout.LayoutParams(-1, -2);
        qp.topMargin = 26;
        qp.bottomMargin = 24;
        content.addView(question, qp);

        options = new LinearLayout(this);
        options.setOrientation(LinearLayout.VERTICAL);
        content.addView(options, new LinearLayout.LayoutParams(-1, -2));
        for (int i = 0; i < 4; i++) {
            final int index = i;
            Button b = button("", Color.rgb(43, 54, 82));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
            p.topMargin = 8;
            options.addView(b, p);
            answerButtons[i] = b;
            b.setOnClickListener(v -> choose(index));
        }

        feedback = text("", 14, muted, true);
        feedback.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams fbp = new LinearLayout.LayoutParams(-1, -2);
        fbp.topMargin = 16;
        content.addView(feedback, fbp);

        mainButton = button("Lanjut", accent);
        mainButton.setVisibility(View.GONE);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2);
        bp.topMargin = 16;
        content.addView(mainButton, bp);
        mainButton.setOnClickListener(v -> {
            questionNumber++;
            if (questionNumber >= 10) showResult();
            else showQuestion();
        });
        generateQuestion();
    }

    private void generateQuestion() {
        int a, b, op;
        op = random.nextInt(4);
        if (op == 0) { a = random.nextInt(50) + 1; b = random.nextInt(50) + 1; correctAnswer = a + b; }
        else if (op == 1) { a = random.nextInt(50) + 10; b = random.nextInt(a) + 1; correctAnswer = a - b; }
        else if (op == 2) { a = random.nextInt(12) + 2; b = random.nextInt(12) + 2; correctAnswer = a * b; }
        else { b = random.nextInt(11) + 2; correctAnswer = random.nextInt(12) + 2; a = b * correctAnswer; }
        String symbol = new String[]{"+", "−", "×", "÷"}[op];
        question.setText(a + " " + symbol + " " + b + " = ?");
        ArrayList<Integer> answers = new ArrayList<>();
        answers.add(correctAnswer);
        while (answers.size() < 4) {
            int candidate = correctAnswer + random.nextInt(19) - 9;
            if (candidate >= 0 && !answers.contains(candidate)) answers.add(candidate);
        }
        Collections.shuffle(answers);
        for (int i = 0; i < 4; i++) answerButtons[i].setText(String.valueOf(answers.get(i)));
    }

    private void choose(int index) {
        if (answered) return;
        answered = true;
        int selected = Integer.parseInt(answerButtons[index].getText().toString());
        if (selected == correctAnswer) {
            score++;
            feedback.setText("Benar! +1 poin");
            feedback.setTextColor(Color.rgb(110, 231, 183));
        } else {
            feedback.setText("Belum tepat. Jawaban: " + correctAnswer);
            feedback.setTextColor(Color.rgb(253, 164, 175));
        }
        for (Button b : answerButtons) b.setEnabled(false);
        mainButton.setText(questionNumber == 9 ? "Lihat Hasil" : "Soal Berikutnya");
        mainButton.setVisibility(View.VISIBLE);
    }

    private void showResult() {
        setup("Kuis selesai");
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView trophy = text("SELESAI!", 15, Color.rgb(165,180,252), true);
        trophy.setGravity(Gravity.CENTER);
        content.addView(trophy);

        TextView scoreText = text(score + " / 10", 46, white, true);
        scoreText.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams scp = new LinearLayout.LayoutParams(-1, -2);
        scp.topMargin = 18;
        content.addView(scoreText, scp);

        String message = score >= 9 ? "Luar biasa! Kamu menguasai soal ini." :
                         score >= 7 ? "Bagus! Terus tingkatkan kemampuanmu." :
                         score >= 5 ? "Lumayan! Coba lagi untuk hasil lebih baik." :
                         "Jangan menyerah. Latihan membuatmu makin jago!";
        TextView result = text(message, 16, muted, false);
        result.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
        rp.topMargin = 14;
        content.addView(result, rp);

        mainButton = button("Main Lagi", accent);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2);
        bp.topMargin = 28;
        content.addView(mainButton, bp);
        mainButton.setOnClickListener(v -> showHome());

        Button home = button("Menu Utama", Color.rgb(43,54,82));
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2);
        hp.topMargin = 10;
        content.addView(home, hp);
        home.setOnClickListener(v -> showHome());
    }
}
