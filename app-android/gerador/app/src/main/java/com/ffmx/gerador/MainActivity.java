package com.ffmx.gerador;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import java.security.SecureRandom;

public class MainActivity extends Activity {
    final int red = Color.rgb(255,32,32);
    final String SENHA_PRIVADA = "Natan2012";
    LinearLayout root;
    EditText acesso, senha;
    TextView status;

    @Override public void onCreate(Bundle b){ super.onCreate(b); mostrarAcesso(); }
    TextView txt(String s,int size){ TextView t=new TextView(this); t.setText(s); t.setTextColor(Color.WHITE); t.setTextSize(size); t.setPadding(20,14,20,14); return t; }
    Button btn(String s){ Button b=new Button(this); b.setText(s); return b; }
    void base(){ root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER); root.setPadding(28,28,28,28); root.setBackgroundColor(Color.rgb(8,8,8)); }
    EditText campo(String hint, boolean pass){ EditText e=new EditText(this); e.setHint(hint); e.setHintTextColor(Color.GRAY); e.setTextColor(Color.WHITE); e.setTextSize(18); if(pass)e.setInputType(129); root.addView(e,new LinearLayout.LayoutParams(-1,-2)); return e; }

    void mostrarAcesso(){
        base(); TextView t=txt("🔒 Gerador FFMX privado",28); t.setTextColor(red); t.setGravity(Gravity.CENTER); root.addView(t);
        root.addView(txt("Área exclusiva. Digite sua senha para acessar.",15)); acesso=campo("Senha de acesso",true);
        Button entrar=btn("Entrar"); root.addView(entrar,new LinearLayout.LayoutParams(-1,-2)); status=txt("",14); status.setTextColor(Color.rgb(255,80,80)); root.addView(status);
        entrar.setOnClickListener(v->{if(SENHA_PRIVADA.equals(acesso.getText().toString())) mostrarGerador(); else status.setText("Senha incorreta.");});
        acesso.setOnEditorActionListener((v,a,e)->{entrar.performClick();return true;}); setContentView(root);
    }

    void mostrarGerador(){
        base(); TextView t=txt("🔐 Gerador FFMX",28); t.setTextColor(red); t.setGravity(Gravity.CENTER); root.addView(t);
        TextView d=txt("Gere e copie sua senha de acesso ao painel.",15); d.setGravity(Gravity.CENTER); root.addView(d);
        senha=campo("Sua senha aparecerá aqui",false); senha.setGravity(Gravity.CENTER); senha.setTextIsSelectable(true); senha.setInputType(1); senha.setFocusable(false);
        Button gerar=btn("🔐 Gerar senha"); root.addView(gerar,new LinearLayout.LayoutParams(-1,-2));
        Button copiar=btn("📋 Copiar senha"); root.addView(copiar,new LinearLayout.LayoutParams(-1,-2));
        status=txt("",14); status.setTextColor(Color.rgb(85,226,122)); status.setGravity(Gravity.CENTER); root.addView(status);
        gerar.setOnClickListener(v->{String s=gerarSenha(12); senha.setText(s); getSharedPreferences("ffmx",0).edit().putString("ffmxSenha",s).apply(); status.setText("Senha gerada!");});
        copiar.setOnClickListener(v->{String s=senha.getText().toString(); if(s.isEmpty()){status.setText("Gere uma senha primeiro.");return;} ClipboardManager cm=(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE); cm.setPrimaryClip(ClipData.newPlainText("Senha FFMX",s)); status.setText("Senha copiada!");});
        setContentView(root);
    }
    String gerarSenha(int n){ String chars="ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%"; SecureRandom r=new SecureRandom(); StringBuilder s=new StringBuilder(); for(int i=0;i<n;i++)s.append(chars.charAt(r.nextInt(chars.length()))); return s.toString(); }
}
