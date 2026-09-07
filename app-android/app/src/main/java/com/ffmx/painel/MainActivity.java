package com.ffmx.painel;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private LinearLayout root;
    private final int red = Color.rgb(255, 32, 32);
    private final int REQUEST_NOTIFICATIONS = 77;
    private SharedPreferences prefs;

    private CheckBox aimbot;
    private CheckBox esp;
    private CheckBox balaMagica;
    private CheckBox segundoPlano;
    private TextView status;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("ffmx", MODE_PRIVATE);
        solicitarNotificacao();
        mostrarLogin();
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

    void prepararRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 28, 24, 24);
        root.setBackgroundColor(Color.rgb(8, 8, 8));
    }

    void mostrarLogin() {
        prepararRoot();

        TextView title = texto("FFMX", 34);
        title.setTextColor(red);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));
        root.addView(texto("🔐 Acesso ao Painel", 23));
        root.addView(texto("Digite a senha criada no Gerador FFMX.", 15));

        final android.widget.EditText senha = new android.widget.EditText(this);
        senha.setHint("Digite sua senha");
        senha.setHintTextColor(Color.GRAY);
        senha.setTextColor(Color.WHITE);
        senha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        root.addView(senha, new LinearLayout.LayoutParams(-1, -2));

        Button entrar = botao("Abrir painel");
        root.addView(entrar, new LinearLayout.LayoutParams(-1, -2));

        TextView info = texto("A senha é usada somente para liberar este aplicativo.", 13);
        info.setTextColor(Color.GRAY);
        root.addView(info);

        entrar.setOnClickListener(v -> {
            String typed = senha.getText().toString();
            String saved = prefs.getString("senha", "");
            if (saved.isEmpty()) {
                // Na primeira abertura, a senha digitada é cadastrada no próprio APK.
                if (typed.trim().isEmpty()) {
                    Toast.makeText(this, "Digite uma senha.", Toast.LENGTH_SHORT).show();
                    return;
                }
                prefs.edit().putString("senha", typed).apply();
                Toast.makeText(this, "Senha cadastrada.", Toast.LENGTH_SHORT).show();
                mostrarPainel();
            } else if (typed.equals(saved)) {
                mostrarPainel();
            } else {
                Toast.makeText(this, "Senha incorreta.", Toast.LENGTH_SHORT).show();
            }
        });

        setContentView(root);
    }

    void mostrarPainel() {
        prepararRoot();

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = texto("🎮 Painel FFMX", 26);
        title.setTextColor(red);
        top.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        TextView badge = texto("PAINEL", 12);
        badge.setTextColor(Color.WHITE);
        badge.setGravity(Gravity.CENTER);
        badge.setBackgroundColor(red);
        top.addView(badge, new LinearLayout.LayoutParams(110, 48));
        root.addView(top);

        TextView desc = texto("Controle do painel. As opções Aimbot, ESP e Bala Mágica são controles visuais do aplicativo e não modificam nem injetam código no Free Fire.", 14);
        desc.setTextColor(Color.LTGRAY);
        root.addView(desc);

        aimbot = opcao("🎯 Aimbot", "Opção do painel.");
        esp = opcao("👁️ ESP", "Opção do painel.");
        balaMagica = opcao("🔫 Bala Mágica", "Opção do painel.");
        root.addView(aimbot.getParent() == null ? aimbot : new View(this));
        // As opções são adicionadas por addOpcao para manter o layout completo.
        root.removeView(aimbot);
        adicionarOpcao("🎯 Aimbot", "Opção do painel.", aimbot);
        adicionarOpcao("👁️ ESP", "Opção do painel.", esp);
        adicionarOpcao("🔫 Bala Mágica", "Opção do painel.", balaMagica);

        LinearLayout bgBox = caixa();
        segundoPlano = new CheckBox(this);
        segundoPlano.setText("📱 Segundo plano");
        segundoPlano.setTextColor(Color.WHITE);
        segundoPlano.setTextSize(16);
        segundoPlano.setButtonTintList(new android.content.res.ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{red, Color.GRAY}));
        segundoPlano.setChecked(prefs.getBoolean("segundoPlano", false));
        bgBox.addView(segundoPlano, new LinearLayout.LayoutParams(-1, -2));
        TextView bgInfo = texto("Mantém o serviço FFMX em primeiro plano para o botão flutuante. Depende das permissões do Android.", 12);
        bgInfo.setTextColor(Color.GRAY);
        bgBox.addView(bgInfo);
        root.addView(bgBox);

        LinearLayout overlayBox = caixa();
        TextView overlayTitle = texto("🪟 Sobrepor a outros apps", 16);
        overlayBox.addView(overlayTitle);
        TextView overlayInfo = texto("Permita a sobreposição nas configurações do Android para usar o botão flutuante FFMX.", 12);
        overlayInfo.setTextColor(Color.GRAY);
        overlayBox.addView(overlayInfo);
        Button perm = botao("⚙️ Permitir sobreposição");
        overlayBox.addView(perm);
        perm.setOnClickListener(v -> abrirPermissao());
        root.addView(overlayBox);

        Button salvar = botao("💾 Salvar");
        root.addView(salvar);
        status = texto("", 14);
        status.setTextColor(Color.rgb(85, 226, 122));
        root.addView(status);

        Button abrir = botao("🎮 Abrir Free Fire");
        root.addView(abrir);
        abrir.setOnClickListener(v -> abrirJogo());

        Button flutuante = botao("🔴 Ativar botão flutuante FFMX");
        root.addView(flutuante);
        flutuante.setOnClickListener(v -> iniciar());

        Button sair = botao("Sair do painel");
        sair.setTextColor(Color.WHITE);
        root.addView(sair);
        sair.setOnClickListener(v -> mostrarLogin());

        carregarOpcoes();

        segundoPlano.setOnClickListener(v -> {
            if (segundoPlano.isChecked()) {
                segundoPlano.setChecked(false);
                new AlertDialog.Builder(this)
                        .setTitle("📱 Permissão para segundo plano")
                        .setMessage("O aplicativo precisa manter um serviço em primeiro plano para o botão flutuante. Isso não é uma permissão para modificar o Free Fire.")
                        .setNegativeButton("Cancelar", null)
                        .setPositiveButton("Permitir", (d, w) -> {
                            segundoPlano.setChecked(true);
                            prefs.edit().putBoolean("segundoPlano", true).apply();
                            iniciar();
                        }).show();
            } else {
                prefs.edit().putBoolean("segundoPlano", false).apply();
            }
        });

        salvar.setOnClickListener(v -> salvarOpcoes());
        setContentView(root);
    }

    CheckBox opcao(String titulo, String subtitulo) {
        CheckBox c = new CheckBox(this);
        c.setText(titulo + "\n" + subtitulo);
        c.setTextColor(Color.WHITE);
        c.setTextSize(16);
        c.setPadding(8, 12, 8, 12);
        c.setButtonTintList(new android.content.res.ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{red, Color.GRAY}));
        return c;
    }

    void adicionarOpcao(String titulo, String subtitulo, CheckBox check) {
        root.addView(check, new LinearLayout.LayoutParams(-1, -2));
    }

    LinearLayout caixa() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(12, 12, 12, 12);
        box.setBackgroundColor(Color.rgb(15, 15, 15));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 14, 0, 0);
        box.setLayoutParams(p);
        return box;
    }

    void carregarOpcoes() {
        aimbot.setChecked(prefs.getBoolean("aimbot", false));
        esp.setChecked(prefs.getBoolean("esp", false));
        balaMagica.setChecked(prefs.getBoolean("balaMagica", false));
    }

    void salvarOpcoes() {
        prefs.edit()
                .putBoolean("aimbot", aimbot.isChecked())
                .putBoolean("esp", esp.isChecked())
                .putBoolean("balaMagica", balaMagica.isChecked())
                .putBoolean("segundoPlano", segundoPlano.isChecked())
                .apply();
        status.setText("Opções salvas.");
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
