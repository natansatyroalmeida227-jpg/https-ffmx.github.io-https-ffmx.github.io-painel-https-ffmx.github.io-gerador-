package com.ffmx.gerador;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

public class SenhaProvider extends ContentProvider {
    public static final String AUTHORITY = "com.ffmx.gerador.senha";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/senha");

    @Override public boolean onCreate() { return true; }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (getContext() == null) return null;
        String senha = getContext().getSharedPreferences("ffmx", 0).getString("ffmxSenha", "");
        MatrixCursor c = new MatrixCursor(new String[]{"senha"});
        if (!senha.isEmpty()) c.addRow(new Object[]{senha});
        return c;
    }
    @Override public String getType(Uri uri) { return "vnd.android.cursor.item/vnd.ffmx.senha"; }
    @Override public Uri insert(Uri uri, ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int delete(Uri uri, String selection, String[] selectionArgs) { throw new UnsupportedOperationException(); }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { throw new UnsupportedOperationException(); }
}
