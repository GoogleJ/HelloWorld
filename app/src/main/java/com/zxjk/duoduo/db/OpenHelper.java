package com.zxjk.duoduo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.zxjk.duoduo.bean.BurnAfterReadMessageLocalBeanDao;
import com.zxjk.duoduo.bean.DaoMaster;
import com.zxjk.duoduo.bean.SlowModeLocalBeanDao;

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
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, BurnAfterReadMessageLocalBeanDao.class, SlowModeLocalBeanDao.class);
    }
}
