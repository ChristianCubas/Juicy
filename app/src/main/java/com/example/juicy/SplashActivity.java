package com.example.juicy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long DELAY_MS = 900L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Aplicamos tema principal para una transición suave
        setTheme(R.style.Theme_Juicy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView logo = findViewById(R.id.splashLogo);
        if (logo != null) {
            logo.setScaleX(0.7f);
            logo.setScaleY(0.7f);
            logo.setAlpha(0f);
            logo.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(500L)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .start();
        }

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, DELAY_MS);
    }
}
