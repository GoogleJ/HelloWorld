package com.zxjk.duoduo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zxjk.duoduo.bean.DaoMaster;

import org.greenrobot.greendao.database.Database;

public class OpenHelper extends DaoMaster.OpenHelper {
    public OpenHelper(Context context, String name) {
        super(context, name);
    }

    public OpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
    }
}
