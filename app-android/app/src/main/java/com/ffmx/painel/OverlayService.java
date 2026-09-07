package com.ffmx.painel;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class OverlayService extends Service {
    WindowManager wm;
    View bubble;
    View menu;
    final int id = 1001;
    final int red = Color.rgb(255, 32, 32);

    @Override public void onCreate() {
        super.onCreate();
        criarNotificacao();
        mostrarBolha();
    }

    void criarNotificacao() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel("ffmx", "FFMX", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(c);
        }
        Notification.Builder b = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, "ffmx")
                : new Notification.Builder(this);
        b.setContentTitle("FFMX ativo")
                .setContentText("Botão flutuante FFMX em execução")
                .setSmallIcon(android.R.drawable.ic_dialog_info);
        startForeground(id, b.build());
    }

    GradientDrawable fundo(int cor, float raio) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(cor);
        g.setCornerRadius(raio);
        g.setStroke(1, Color.rgb(55, 55, 55));
        return g;
    }

    TextView titulo(String texto, int tamanho) {
        TextView t = new TextView(this);
        t.setText(texto);
        t.setTextColor(Color.WHITE);
        t.setTextSize(tamanho);
        t.setPadding(18, 14, 18, 14);
        return t;
    }

    Button botao(String texto) {
        Button b = new Button(this);
        b.setText(texto);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackground(fundo(Color.rgb(32, 32, 32), 16));
        return b;
    }

    CheckBox opcao(String texto, boolean marcada) {
        CheckBox c = new CheckBox(this);
        c.setText(texto);
        c.setTextColor(Color.WHITE);
        c.setTextSize(16);
        c.setPadding(8, 8, 8, 8);
        c.setChecked(marcada);
        if (Build.VERSION.SDK_INT >= 21) {
            c.setButtonTintList(new android.content.res.ColorStateList(
                    new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                    new int[]{red, Color.GRAY}));
        }
        return c;
    }

    void mostrarBolha() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        TextView v = new TextView(this);
        v.setText("FFMX");
        v.setTextColor(Color.WHITE);
        v.setTextSize(13);
        v.setGravity(Gravity.CENTER);
        v.setTypeface(null, 1);
        v.setBackground(fundo(red, 100));

        int w = 76, h = 76;
        int type = Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                w, h, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                -3);
        p.gravity = Gravity.TOP | Gravity.START;
        p.x = 20;
        p.y = 220;
        wm.addView(v, p);
        bubble = v;

        v.setOnTouchListener(new View.OnTouchListener() {
            float dx, dy;
            long down;
            boolean moved;

            public boolean onTouch(View view, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dx = p.x - e.getRawX();
                        dy = p.y - e.getRawY();
                        down = System.currentTimeMillis();
                        moved = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        p.x = (int) (e.getRawX() + dx);
                        p.y = (int) (e.getRawY() + dy);
                        moved = true;
                        wm.updateViewLayout(view, p);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (!moved && System.currentTimeMillis() - down < 500) {
                            abrirMenu(p.x, p.y);
                        }
                        return true;
                }
                return true;
            }
        });
    }

    void abrirMenu(int x, int y) {
        if (menu != null) return;

        SharedPreferences pref = getSharedPreferences("ffmx", MODE_PRIVATE);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(12, 12, 12, 12);
        box.setBackground(fundo(Color.rgb(12, 12, 12), 22));

        TextView header = titulo("🎮 Painel FFMX", 21);
        header.setTextColor(red);
        box.addView(header);

        TextView sub = titulo("Funções do painel", 12);
        sub.setTextColor(Color.LTGRAY);
        box.addView(sub);

        CheckBox aimbot = opcao("🎯 Aimbot", pref.getBoolean("aimbot", false));
        CheckBox esp = opcao("👁️ ESP", pref.getBoolean("esp", false));
        CheckBox bala = opcao("🔫 Bala Mágica", pref.getBoolean("balaMagica", false));
        CheckBox segundo = opcao("📱 Segundo plano", pref.getBoolean("segundoPlano", false));
        box.addView(aimbot);
        box.addView(esp);
        box.addView(bala);
        box.addView(segundo);

        Button salvar = botao("💾 Salvar");
        Button abrirJogo = botao("🎮 Abrir Free Fire");
        Button permissao = botao("🪟 Permitir sobreposição");
        Button fechar = botao("✕ Fechar painel");
        box.addView(salvar);
        box.addView(abrirJogo);
        box.addView(permissao);
        box.addView(fechar);

        TextView aviso = titulo("As opções Aimbot, ESP e Bala Mágica são controles da interface e não alteram o jogo.", 11);
        aviso.setTextColor(Color.GRAY);
        box.addView(aviso);

        int type = Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        // O menu não recebe foco do sistema. Assim, abrir o painel não congela
        // nem toma o foco da tela do aplicativo que está por baixo.
        WindowManager.LayoutParams mp = new WindowManager.LayoutParams(
                dp(300), WindowManager.LayoutParams.WRAP_CONTENT, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                -3);
        mp.gravity = Gravity.TOP | Gravity.START;
        mp.x = Math.max(8, Math.min(x - 210, getResources().getDisplayMetrics().widthPixels - dp(308)));
        mp.y = Math.max(8, y - dp(170));

        menu = box;
        wm.addView(menu, mp);

        salvar.setOnClickListener(v -> {
            pref.edit()
                    .putBoolean("aimbot", aimbot.isChecked())
                    .putBoolean("esp", esp.isChecked())
                    .putBoolean("balaMagica", bala.isChecked())
                    .putBoolean("segundoPlano", segundo.isChecked())
                    .apply();
            Toast.makeText(this, "Opções salvas.", Toast.LENGTH_SHORT).show();
        });

        segundo.setOnClickListener(v -> {
            pref.edit().putBoolean("segundoPlano", segundo.isChecked()).apply();
            if (segundo.isChecked()) {
                Toast.makeText(this, "Segundo plano ativado.", Toast.LENGTH_SHORT).show();
            }
        });

        abrirJogo.setOnClickListener(v -> abrirFreeFire());
        permissao.setOnClickListener(v -> abrirPermissao());
        fechar.setOnClickListener(v -> fecharMenu());
    }

    int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density + 0.5f);
    }

    void fecharMenu() {
        if (menu != null) {
            try { wm.removeView(menu); } catch (Exception ignored) {}
            menu = null;
        }
    }

    void abrirPermissao() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        android.net.Uri.parse("package:" + getPackageName()));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Abra Configurações > Apps > Acesso especial > Sobrepor a outros apps.", Toast.LENGTH_LONG).show();
        }
    }

    void abrirFreeFire() {
        try {
            Intent i = getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");
            if (i != null) {
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            } else {
                Toast.makeText(this, "Free Fire não encontrado.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir o jogo.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override public int onStartCommand(Intent i, int f, int s) {
        return START_STICKY;
    }

    @Override public void onDestroy() {
        fecharMenu();
        if (wm != null && bubble != null) {
            try { wm.removeView(bubble); } catch (Exception ignored) {}
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent i) { return null; }
}
