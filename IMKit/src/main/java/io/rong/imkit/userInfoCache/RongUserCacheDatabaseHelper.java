//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.userInfoCache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import io.rong.imlib.common.SavePathUtils;
import java.io.File;

class RongUserCacheDatabaseHelper extends SQLiteOpenHelper {
  private static final String DB_NAME = "IMKitUserInfoCache";
  private static final int DB_VERSION = 2;
  private static String dbPath;
  private SQLiteDatabase database;

  public RongUserCacheDatabaseHelper(Context context) {
    this(context, "IMKitUserInfoCache", (CursorFactory)null, 2);
  }

  private RongUserCacheDatabaseHelper(Context context, String name, CursorFactory factory, int version) {
    super(new RongDatabaseContext(context, dbPath), name, factory, version);
  }

  public static void setDbPath(Context context, String appKey, String currentUserId) {
    dbPath = SavePathUtils.getSavePath(context.getFilesDir().getAbsolutePath());
    dbPath = dbPath + File.separator + appKey + File.separator + currentUserId;
  }

  public void onCreate(SQLiteDatabase db) {
    this.database = db;
    db.execSQL("CREATE TABLE users (id TEXT PRIMARY KEY NOT NULL UNIQUE, name TEXT, portrait TEXT, extra TEXT)");
    db.execSQL("CREATE INDEX IF NOT EXISTS id_idx_users ON users(id)");
    db.execSQL("CREATE TABLE group_users (group_id TEXT NOT NULL, user_id TEXT NOT NULL, nickname TEXT)");
    db.execSQL("CREATE TABLE groups (id TEXT PRIMARY KEY NOT NULL UNIQUE, name TEXT, portrait TEXT)");
    db.execSQL("CREATE TABLE discussions (id TEXT PRIMARY KEY NOT NULL UNIQUE, name TEXT, portrait TEXT)");
  }

  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    switch(oldVersion) {
      case 1:
        db.execSQL("ALTER TABLE users ADD COLUMN extra TEXT");
      default:
    }
  }
}
