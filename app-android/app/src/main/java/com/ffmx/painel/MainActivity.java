package com.ffmx.painel;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private LinearLayout root;
    private final int red = Color.rgb(255, 32, 32);
    private final int REQUEST_NOTIFICATIONS = 77;
    private static final long VALIDADE_24H = 24L * 60L * 60L * 1000L;
    private SharedPreferences prefs;
    private CheckBox aimbot, esp, balaMagica, segundoPlano;
    private TextView status;
    private static final Uri GERADOR_URI = Uri.parse("content://com.ffmx.gerador.senha/senha");
    private final Handler handler = new Handler();
    private final Runnable expiracao = () -> verificarSessao();

    @Override public void onCreate(Bundle b) { super.onCreate(b); prefs = getSharedPreferences("ffmx", MODE_PRIVATE); solicitarNotificacao(); mostrarLogin(); }
    @Override protected void onResume() { super.onResume(); if (prefs.getBoolean("logado", false)) verificarSessao(); }
    void solicitarNotificacao() { if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATIONS); }
    TextView texto(String t, int size) { TextView v = new TextView(this); v.setText(t); v.setTextColor(Color.WHITE); v.setTextSize(size); v.setPadding(24,18,24,18); return v; }
    Button botao(String texto) { Button b = new Button(this); b.setText(texto); return b; }
    void prepararRoot() { root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24,28,24,24); root.setBackgroundColor(Color.rgb(8,8,8)); }

    boolean obterKey(String[] out) {
        Cursor c = null;
        try {
            c = getContentResolver().query(GERADOR_URI, new String[]{"senha", "criada_em"}, null, null, null);
            if (c != null && c.moveToFirst()) { out[0] = c.getString(c.getColumnIndexOrThrow("senha")); out[1] = String.valueOf(c.getLong(c.getColumnIndexOrThrow("criada_em"))); return true; }
        } catch (Exception ignored) {} finally { if (c != null) c.close(); }
        return false;
    }

    void verificarSessao() {
        String key = prefs.getString("senha", "");
        long criadaEm = prefs.getLong("senhaCriadaEm", 0L);
        if (key.isEmpty() || criadaEm <= 0L || System.currentTimeMillis() - criadaEm >= VALIDADE_24H) {
            handler.removeCallbacks(expiracao);
            prefs.edit().remove("senha").remove("senhaCriadaEm").putBoolean("logado", false).apply();
            Toast.makeText(this, "Sua key expirou. Gere uma nova key no Gerador FFMX.", Toast.LENGTH_LONG).show();
            mostrarLogin();
            return;
        }
        handler.removeCallbacks(expiracao);
        handler.postDelayed(expiracao, Math.max(1000L, (criadaEm + VALIDADE_24H) - System.currentTimeMillis()));
    }

    void mostrarLogin() {
        prepararRoot();
        TextView title = texto("FFMX",34); title.setTextColor(red); title.setGravity(Gravity.CENTER); root.addView(title,new LinearLayout.LayoutParams(-1,-2));
        root.addView(texto("🔐 Acesso ao Painel",23)); root.addView(texto("Use a key criada no Gerador FFMX. Cada key vale por 24 horas.",15));
        final android.widget.EditText senha = new android.widget.EditText(this); senha.setHint("Digite sua key"); senha.setHintTextColor(Color.GRAY); senha.setTextColor(Color.WHITE); senha.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD); root.addView(senha,new LinearLayout.LayoutParams(-1,-2));
        Button entrar = botao("Abrir painel"); root.addView(entrar,new LinearLayout.LayoutParams(-1,-2));
        TextView info = texto("A validade é controlada pela data em que a key foi gerada. Não é necessário atualizar manualmente.",13); info.setTextColor(Color.GRAY); root.addView(info);
        entrar.setOnClickListener(v -> {
            String typed = senha.getText().toString(); String[] dados = new String[2];
            if (!obterKey(dados)) { Toast.makeText(this,"Nenhuma key encontrada. Abra o Gerador FFMX e gere uma nova key.",Toast.LENGTH_LONG).show(); return; }
            long criadaEm; try { criadaEm = Long.parseLong(dados[1]); } catch(Exception e) { Toast.makeText(this,"Data da key inválida. Gere uma nova key.",Toast.LENGTH_LONG).show(); return; }
            if (System.currentTimeMillis() - criadaEm >= VALIDADE_24H) { Toast.makeText(this,"Key expirada. Gere uma nova key no Gerador FFMX.",Toast.LENGTH_LONG).show(); return; }
            if (typed.equals(dados[0])) { prefs.edit().putString("senha",dados[0]).putLong("senhaCriadaEm",criadaEm).putBoolean("logado",true).apply(); mostrarPainel(); verificarSessao(); }
            else Toast.makeText(this,"Key incorreta.",Toast.LENGTH_SHORT).show();
        });
        senha.setOnEditorActionListener((v,actionId,event)->{ entrar.performClick(); return true; }); setContentView(root);
    }

    void mostrarPainel() {
        prepararRoot(); LinearLayout top=new LinearLayout(this); top.setOrientation(LinearLayout.HORIZONTAL); top.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=texto("🎮 Painel FFMX",26); title.setTextColor(red); top.addView(title,new LinearLayout.LayoutParams(0,-2,1)); TextView badge=texto("24H",12); badge.setTextColor(Color.WHITE); badge.setGravity(Gravity.CENTER); badge.setBackgroundColor(red); top.addView(badge,new LinearLayout.LayoutParams(110,48)); root.addView(top);
        TextView desc=texto("Sua key é válida por 24 horas. Depois desse período, gere uma nova key no Gerador FFMX. As opções Aimbot, ESP e Bala Mágica são apenas controles visuais do aplicativo.",14); desc.setTextColor(Color.LTGRAY); root.addView(desc);
        aimbot=opcao("🎯 Aimbot","Opção do painel."); esp=opcao("👁️ ESP","Opção do painel."); balaMagica=opcao("🔫 Bala Mágica","Opção do painel."); adicionarOpcao("🎯 Aimbot","Opção do painel.",aimbot); adicionarOpcao("👁️ ESP","Opção do painel.",esp); adicionarOpcao("🔫 Bala Mágica","Opção do painel.",balaMagica);
        LinearLayout bgBox=caixa(); segundoPlano=new CheckBox(this); segundoPlano.setText("📱 Segundo plano"); segundoPlano.setTextColor(Color.WHITE); segundoPlano.setTextSize(16); segundoPlano.setButtonTintList(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{red,Color.GRAY})); segundoPlano.setChecked(prefs.getBoolean("segundoPlano",false)); bgBox.addView(segundoPlano,new LinearLayout.LayoutParams(-1,-2)); TextView bgInfo=texto("Mantém o serviço FFMX em primeiro plano para o botão flutuante. Depende das permissões do Android.",12); bgInfo.setTextColor(Color.GRAY); bgBox.addView(bgInfo); root.addView(bgBox);
        LinearLayout overlayBox=caixa(); overlayBox.addView(texto("🪟 Sobrepor a outros apps",16)); TextView overlayInfo=texto("Permita a sobreposição nas configurações do Android para usar o botão flutuante FFMX.",12); overlayInfo.setTextColor(Color.GRAY); overlayBox.addView(overlayInfo); Button perm=botao("⚙️ Permitir sobreposição"); overlayBox.addView(perm); perm.setOnClickListener(v->abrirPermissao()); root.addView(overlayBox);
        Button salvar=botao("💾 Salvar"); root.addView(salvar); status=texto("",14); status.setTextColor(Color.rgb(85,226,122)); root.addView(status);
        Button abrir=botao("🎮 Abrir Free Fire"); root.addView(abrir); abrir.setOnClickListener(v->abrirJogo());
        Button flutuante=botao("🔴 Ativar botão flutuante FFMX"); root.addView(flutuante); flutuante.setOnClickListener(v->iniciar());
        Button sair=botao("Sair do painel"); sair.setTextColor(Color.WHITE); root.addView(sair); sair.setOnClickListener(v->{prefs.edit().putBoolean("logado",false).apply(); handler.removeCallbacks(expiracao); mostrarLogin();});
        carregarOpcoes(); segundoPlano.setOnClickListener(v->{ if(segundoPlano.isChecked()){ segundoPlano.setChecked(false); new AlertDialog.Builder(this).setTitle("📱 Permissão para segundo plano").setMessage("O aplicativo precisa manter um serviço em primeiro plano para o botão flutuante. Isso não é uma permissão para modificar o Free Fire.").setNegativeButton("Cancelar",null).setPositiveButton("Permitir",(d,w)->{segundoPlano.setChecked(true);prefs.edit().putBoolean("segundoPlano",true).apply();iniciar();}).show(); } else prefs.edit().putBoolean("segundoPlano",false).apply(); });
        salvar.setOnClickListener(v->salvarOpcoes()); setContentView(root);
    }
    CheckBox opcao(String titulo,String subtitulo){ CheckBox c=new CheckBox(this); c.setText(titulo+"\n"+subtitulo); c.setTextColor(Color.WHITE); c.setTextSize(16); c.setPadding(8,12,8,12); c.setButtonTintList(new android.content.res.ColorStateList(new int[][]{new int[]{android.R.attr.state_checked},new int[]{}},new int[]{red,Color.GRAY})); return c; }
    void adicionarOpcao(String titulo,String subtitulo,CheckBox check){ root.addView(check,new LinearLayout.LayoutParams(-1,-2)); }
    LinearLayout caixa(){ LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(12,12,12,12); box.setBackgroundColor(Color.rgb(15,15,15)); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,14,0,0); box.setLayoutParams(p); return box; }
    void carregarOpcoes(){ aimbot.setChecked(prefs.getBoolean("aimbot",false)); esp.setChecked(prefs.getBoolean("esp",false)); balaMagica.setChecked(prefs.getBoolean("balaMagica",false)); }
    void salvarOpcoes(){ prefs.edit().putBoolean("aimbot",aimbot.isChecked()).putBoolean("esp",esp.isChecked()).putBoolean("balaMagica",balaMagica.isChecked()).putBoolean("segundoPlano",segundoPlano.isChecked()).apply(); status.setText("Opções salvas."); }
    void abrirPermissao(){ if(Build.VERSION.SDK_INT>=23) startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()))); }
    void iniciar(){ if(Build.VERSION.SDK_INT>=23&&!android.provider.Settings.canDrawOverlays(this)){Toast.makeText(this,"Conceda primeiro a permissão de sobreposição.",Toast.LENGTH_LONG).show();abrirPermissao();return;} Intent i=new Intent(this,OverlayService.class);if(Build.VERSION.SDK_INT>=26)startForegroundService(i);else startService(i);Toast.makeText(this,"Botão FFMX ativado.",Toast.LENGTH_SHORT).show(); }
    void abrirJogo(){ try{Intent i=getPackageManager().getLaunchIntentForPackage("com.dts.freefireth");if(i!=null)startActivity(i);else Toast.makeText(this,"Free Fire não encontrado.",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"Não foi possível abrir o jogo.",Toast.LENGTH_SHORT).show();} }
}
