package com.ffmx.painel;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    LinearLayout root;
    int red = Color.rgb(255,32,32);
    @Override public void onCreate(Bundle b){ super.onCreate(b); montar(); }
    TextView texto(String t,int size){ TextView v=new TextView(this); v.setText(t); v.setTextColor(Color.WHITE); v.setTextSize(size); v.setPadding(24,18,24,18); return v; }
    void montar(){
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(24,40,24,24); root.setBackgroundColor(Color.rgb(8,8,8));
        TextView title=texto("FFMX",32); title.setTextColor(red); title.setGravity(Gravity.CENTER); root.addView(title,new LinearLayout.LayoutParams(-1,-2));
        root.addView(texto("Painel Android com botão flutuante",18));
        TextView info=texto("Para mostrar a logo FFMX sobre outros aplicativos, o Android exige a permissão oficial de sobreposição.",15); root.addView(info);
        Button perm=new Button(this); perm.setText("⚙️ Permitir sobrepor a outros apps"); perm.setOnClickListener(v->abrirPermissao()); root.addView(perm,new LinearLayout.LayoutParams(-1,-2));
        Button start=new Button(this); start.setText("🔴 Ativar botão flutuante FFMX"); start.setOnClickListener(v->iniciar()); root.addView(start,new LinearLayout.LayoutParams(-1,-2));
        Button game=new Button(this); game.setText("🎮 Abrir Free Fire"); game.setOnClickListener(v->abrirJogo()); root.addView(game,new LinearLayout.LayoutParams(-1,-2));
        setContentView(root);
    }
    void abrirPermissao(){ if(Build.VERSION.SDK_INT>=23){ Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName())); startActivity(i); } }
    void iniciar(){ if(Build.VERSION.SDK_INT>=23 && !Settings.canDrawOverlays(this)){ Toast.makeText(this,"Conceda primeiro a permissão de sobreposição.",Toast.LENGTH_LONG).show(); abrirPermissao(); return; } Intent i=new Intent(this,OverlayService.class); if(Build.VERSION.SDK_INT>=26) startForegroundService(i); else startService(i); Toast.makeText(this,"Botão FFMX ativado.",Toast.LENGTH_SHORT).show(); }
    void abrirJogo(){ try{ Intent i=getPackageManager().getLaunchIntentForPackage("com.dts.freefireth"); if(i!=null) startActivity(i); else Toast.makeText(this,"Free Fire não encontrado.",Toast.LENGTH_SHORT).show(); }catch(Exception e){ Toast.makeText(this,"Não foi possível abrir o jogo.",Toast.LENGTH_SHORT).show(); } }
}