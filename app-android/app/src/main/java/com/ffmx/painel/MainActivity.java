package com.ffmx.painel;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    LinearLayout root;
    final int red = Color.rgb(255, 32, 32);
    final int REQUEST_NOTIFICATIONS = 77;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        solicitarNotificacao();
        montar();
    }

    void solicitarNotificacao() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATIONS);
        }
    }

    TextView texto(String t, int size) {
        TextView v = new TextView(this);
        v.setText(t);
        v.setTextColor(Color.WHITE);
        v.setTextSize(size);
        v.setPadding(24, 18, 24, 18);
        return v;
    }

    Button botao(String texto) {
        Button b = new Button(this);
        b.setText(texto);
        return b;
    }

    void montar() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 40, 24, 24);
        root.setBackgroundColor(Color.rgb(8, 8, 8));

        TextView title = texto("FFMX", 34);
        title.setTextColor(red);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));
        root.addView(texto("Painel Android + botão flutuante", 18));
        root.addView(texto("O botão FFMX é uma interface independente e não modifica nem injeta código no Free Fire.", 15));

        Button perm = botao("⚙️ Permitir sobrepor a outros apps");
        perm.setOnClickListener(v -> abrirPermissao());
        root.addView(perm, new LinearLayout.LayoutParams(-1, -2));

        Button start = botao("🔴 Ativar botão flutuante FFMX");
        start.setOnClickListener(v -> iniciar());
        root.addView(start, new LinearLayout.LayoutParams(-1, -2));

        Button game = botao("🎮 Abrir Free Fire");
        game.setOnClickListener(v -> abrirJogo());
        root.addView(game, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
    }

    void abrirPermissao() {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(i);
        }
    }

    void iniciar() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Conceda primeiro a permissão de sobreposição.", Toast.LENGTH_LONG).show();
            abrirPermissao();
            return;
        }
        Intent i = new Intent(this, OverlayService.class);
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
        Toast.makeText(this, "Botão FFMX ativado.", Toast.LENGTH_SHORT).show();
    }

    void abrirJogo() {
        try {
            Intent i = getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
            if (i != null) startActivity(i);
            else Toast.makeText(this, "Free Fire não encontrado.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir o jogo.", Toast.LENGTH_SHORT).show();
        }
    }
}
