package io.rong.photoview.log;

public abstract interface Logger
{
  public abstract int v(String paramString1, String paramString2);

  public abstract int v(String paramString1, String paramString2, Throwable paramThrowable);

  public abstract int d(String paramString1, String paramString2);

  public abstract int d(String paramString1, String paramString2, Throwable paramThrowable);

  public abstract int i(String paramString1, String paramString2);

  public abstract int i(String paramString1, String paramString2, Throwable paramThrowable);

  public abstract int w(String paramString1, String paramString2);

  public abstract int w(String paramString1, String paramString2, Throwable paramThrowable);

  public abstract int e(String paramString1, String paramString2);

  public abstract int e(String paramString1, String paramString2, Throwable paramThrowable);
}