package com.ffmx.painel;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;

public class OverlayService extends Service {
    WindowManager wm; View bubble;
    final int id=1001;
    @Override public void onCreate(){ super.onCreate(); criarNotificacao(); mostrarBolha(); }
    void criarNotificacao(){
        if(Build.VERSION.SDK_INT>=26){ NotificationChannel c=new NotificationChannel("ffmx","FFMX",NotificationManager.IMPORTANCE_LOW); getSystemService(NotificationManager.class).createNotificationChannel(c); }
        Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,"ffmx"):new Notification.Builder(this); b.setContentTitle("FFMX ativo").setContentText("Botão flutuante FFMX em execução").setSmallIcon(android.R.drawable.ic_dialog_info); startForeground(id,b.build());
    }
    void mostrarBolha(){
        wm=(WindowManager)getSystemService(WINDOW_SERVICE); TextView v=new TextView(this); v.setText("FFMX"); v.setTextColor(Color.WHITE); v.setTextSize(13); v.setGravity(Gravity.CENTER); v.setTypeface(null,1); GradientDrawable g=new GradientDrawable(); g.setColor(Color.rgb(255,32,32)); g.setShape(GradientDrawable.OVAL); v.setBackground(g); int w=76,h=76; int type=Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE; WindowManager.LayoutParams p=new WindowManager.LayoutParams(w,h,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,-3); p.gravity=Gravity.TOP|Gravity.START; p.x=20;p.y=220; wm.addView(v,p); bubble=v;
        v.setOnTouchListener(new View.OnTouchListener(){float dx,dy;int sx,sy;long down; boolean moved; public boolean onTouch(View view,android.view.MotionEvent e){switch(e.getAction()){case 0:dx=p.x-e.getRawX();dy=p.y-e.getRawY();sx=p.x;sy=p.y;down=System.currentTimeMillis();moved=false;return true;case 2:p.x=(int)(e.getRawX()+dx);p.y=(int)(e.getRawY()+dy);moved=true;wm.updateViewLayout(view,p);return true;case 1:if(!moved&&System.currentTimeMillis()-down<400) Toast.makeText(OverlayService.this,"FFMX ativo — botão flutuante",Toast.LENGTH_SHORT).show();return true;}return true;}});
    }
    @Override public int onStartCommand(Intent i,int f,int s){return START_STICKY;}
    @Override public void onDestroy(){if(wm!=null&&bubble!=null)try{wm.removeView(bubble);}catch(Exception e){} super.onDestroy();}
    @Override public IBinder onBind(Intent i){return null;}
}