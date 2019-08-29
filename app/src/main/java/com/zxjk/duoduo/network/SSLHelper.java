package com.zxjk.duoduo.network;

import com.blankj.utilcode.util.Utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLHelper {

    public static X509TrustManager[] trustManagers;

    public static SSLSocketFactory getContext() {
        try {
            if (trustManagers == null) {
                trustManagers = new X509TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                                if (chain == null) {
                                    throw new IllegalArgumentException("Check Server x509Certificates is null");
                                }

                                for (X509Certificate cert : chain) {
                                    cert.checkValidity();
                                    try {
                                        InputStream inputStream = new BufferedInputStream(Utils.getApp().getAssets().open("cacert.cer"));
                                        CertificateFactory instance = CertificateFactory.getInstance("X.509");
                                        X509Certificate certificate = (X509Certificate) instance.generateCertificate(inputStream);
                                        cert.verify(certificate.getPublicKey());
                                    } catch (Exception e) {
                                    }
                                }
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                };
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(null, trustManagers, null);

            return sslContext.getSocketFactory();

        } catch (Exception e) {

        }
        return null;
    }
}
