package io.rong.imkit.utils;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.rong.common.RLog;
import io.rong.imageloader.core.assist.ContentLengthInputStream;
import io.rong.imageloader.core.download.BaseImageDownloader;
import io.rong.imageloader.utils.IoUtils;
import io.rong.imlib.common.NetUtils;

public class RongAuthImageDownloader extends BaseImageDownloader
{
  private static final String TAG = RongAuthImageDownloader.class.getSimpleName();
  private SSLSocketFactory mSSLSocketFactory;
  final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier()
  {
    public boolean verify(String hostname, SSLSession session) {
      return true;
    }
  };

  public RongAuthImageDownloader(Context context)
  {
    super(context);
    SSLContext sslContext = sslContextForTrustedCertificates();
    if (sslContext != null)
      this.mSSLSocketFactory = sslContext.getSocketFactory();
  }

  public RongAuthImageDownloader(Context context, int connectTimeout, int readTimeout)
  {
    super(context, connectTimeout, readTimeout);
    SSLContext sslContext = sslContextForTrustedCertificates();
    if (sslContext != null)
      this.mSSLSocketFactory = sslContext.getSocketFactory();
  }

  protected InputStream getStreamFromNetwork(String imageUri, Object extra)
    throws IOException
  {
    InputStream imageStream;
    HttpURLConnection conn = null;
    try
    {
      conn = NetUtils.createURLConnection(imageUri);
      conn.setConnectTimeout(this.connectTimeout);
      conn.setReadTimeout(this.readTimeout);

      if (conn instanceof HttpsURLConnection) {
        ((HttpsURLConnection)conn).setSSLSocketFactory(this.mSSLSocketFactory);
        ((HttpsURLConnection)conn).setHostnameVerifier(this.DO_NOT_VERIFY);
      }
      conn.connect();
      if ((conn.getResponseCode() >= 300) && (conn.getResponseCode() < 400)) {
        String redirectUrl = conn.getHeaderField("Location");
        conn = (HttpURLConnection)new URL(redirectUrl).openConnection();
        conn.setConnectTimeout(this.connectTimeout);
        conn.setReadTimeout(this.readTimeout);

        if (conn instanceof HttpsURLConnection) {
          ((HttpsURLConnection)conn).setSSLSocketFactory(this.mSSLSocketFactory);
          ((HttpsURLConnection)conn).setHostnameVerifier(this.DO_NOT_VERIFY);
        }
        conn.connect();
      }
      imageStream = conn.getInputStream();
    }
    catch (IOException e)
    {
      if ((conn != null) && (conn.getContentLength() > 0) && (conn.getContentType().contains("image/"))) {
        imageStream = conn.getErrorStream();
      }
      else {
        IoUtils.readAndCloseStream(conn.getErrorStream());
        throw e;
      }
    }
    if (!(shouldBeProcessed(conn))) {
      IoUtils.closeSilently(imageStream);
      throw new IOException("Image request failed with response code " + conn.getResponseCode());
    }

    return new ContentLengthInputStream(new BufferedInputStream(imageStream, 32768), conn.getContentLength());
  }

  private SSLContext sslContextForTrustedCertificates()
  {
    TrustManager[] trustAllCerts = new TrustManager[1];
    TrustManager tm = new miTM();
    trustAllCerts[0] = tm;
    SSLContext sc = null;
    try {
      sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, null);
    } catch (NoSuchAlgorithmException e) {
      RLog.e(TAG, "sslContextForTrustedCertificates", e);
    } catch (KeyManagementException e) {
      RLog.e(TAG, "sslContextForTrustedCertificates", e);
    }
    return sc;
  }

  class miTM implements TrustManager, X509TrustManager
  {
    public X509Certificate[] getAcceptedIssuers()
    {
      return null;
    }

    public boolean isServerTrusted(X509Certificate[] certs)
    {
      return true;
    }

    public boolean isClientTrusted(X509Certificate[] certs)
    {
      return true;
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType)
      throws CertificateException
    {
      RLog.d("checkServerTrusted", "authType:" + authType);
    }

    public void checkClientTrusted(X509Certificate[] certs, String authType)
      throws CertificateException
    {
      RLog.d("checkClientTrusted", "authType:" + authType);
    }
  }
}