package com.edisa.galileo.digitalsign;

import org.jboss.aerogear.security.otp.Totp;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Authenticator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;

public class TestFirmaDigitalSign {


//    @Test
//    void signPDF() {
//
//        //CertificateChain
//        String b64CertificateChain = "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";
//        byte[] certificateChain = Base64.getDecoder().decode(b64CertificateChain);
//        String b64SignedHash = "bGthanNsa2pzYWRqYWxrc2RqbGthc2pkbGthanNkYQ==";
//        byte[] signedHash = Base64.getDecoder().decode(b64SignedHash);
//
//        String pdfFile = "C:\\Temp\\digitalsign\\PDFSignTest.pdf";
//        String signedPdfFile = "C:\\Temp\\digitalsign\\PDFSignTest_signed.pdf";
//        try {
//            FileInputStream fl = new FileInputStream(pdfFile);
//            FirmaDigitalSign firmaDigitalSign =
//                    FirmaDigitalSignBuilder.newInstance()
//                            .setCertificateChain(certificateChain)
//                            .setPdfContent(fl.readAllBytes())
//                            .setSignedHash(signedHash)
//                            .setReason("Reason: Teste")
//                            .setLocation("Location: Teste")
//                            .build();
//
//
//            DocToSignResponse docToSignResponse = firmaDigitalSign.getDocHashToSign64();
//            String docHashToSignB64 = new String(Base64.getEncoder().encode(docToSignResponse.getDocToSignHash()));
//
//            HttpClient client = HttpClient.newBuilder()
//                    .version(HttpClient.Version.HTTP_1_1)
//                    .followRedirects(HttpClient.Redirect.NORMAL)
//                    .connectTimeout(Duration.ofSeconds(20))
//                    .build();
//
//            Totp generator = new Totp("A7GZNWVTA2FG556Q");
//            String totp = generator.now();
//
//            String json = "{\n" +
//                    "    \"certAlias\": \"oyeyw6ulocautq4kl75mxd8u01h1rmqz\",\n" +
//                    "    \"sigReqDescr\": \"Firma documento\",\n" +
//                    "    \"totpID\": \"hea593gibsvldk2c3nvhba8tt7fkuomegv3t\",\n" +
//                    "    \"totpValue\": \""+totp+"\",\n" +
//                    "    \"docsToSign\": [\n" +
//                    "        {\n" +
//                    "            \"docAlias\": \"doc_sign_ee5e64bb-1497-4daf-90c6-3efeb9dc83d1.pdf\",\n" +
//                    "            \"hashAlg\": \"2.16.840.1.101.3.4.2.1\",\n" +
//                    "            \"hashToSign_64\": \""+docHashToSignB64+"\"\n" +
//                    "        }\n" +
//                    "    ]\n" +
//                    "}";
//            System.out.println(json);
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://qscd-dev.digitalsign.pt/totp/sigCompleteTOTPPolling"))
//                    .header("Accept", "application/json")
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", "Bearer c4b0ae29-3107-40ce-bf67-a9a7c79372f6")
//                    .POST(HttpRequest.BodyPublishers.ofString(json))
//                    .build();
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("response: "+response.body());
//            String sigReqId = response.body().replace("{\"sigReqID\":\"","").replace("\"}","");
//            System.out.println("sigReqId: "+sigReqId);
//
//            Thread.sleep(10000);
//
//            json = "{\n" +
//                    "    \"sigReqID\": \""+sigReqId+"\"\n" +
//                    "}";
//
//            request = HttpRequest.newBuilder()
//                    .uri(URI.create("https://qscd-dev.digitalsign.pt/totp/sigFinalize"))
//                    .header("Accept", "application/json")
//                    .header("Content-Type", "application/json")
//                    .header("Authorization", "Bearer c4b0ae29-3107-40ce-bf67-a9a7c79372f6")
//                    .POST(HttpRequest.BodyPublishers.ofString(json))
//                    .build();
//
//            response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("response: "+response.body());
//
//            String cad = response.body();
//            int pos = cad.indexOf("hashSig") + 10;
//            String hashSignedb64 = cad.substring(pos).replace("\"}]}","");
//            System.out.println("hashSignedb64: "+hashSignedb64);
//
//
//            firmaDigitalSign.setDigestHash(docToSignResponse.getDigestHash());
//            firmaDigitalSign.setSignedHash(Base64.getDecoder().decode(hashSignedb64));
//
//            byte[] signedPDF = firmaDigitalSign.signPDF();
//            //try (FileOutputStream stream = new FileOutputStream(signedPdfFile)) { stream.write(signedPDF); }
//            fl.close();
//
//            //firmaDigitalSign.setDigestHash(docToSignResponse.getDigestHash());
//            //byte[] signedPDF = firmaDigitalSign.signPDF();
//            //try (FileOutputStream stream = new FileOutputStream(signedPdfFile)) { stream.write(signedPDF); }
//            //fl.close();
//
//
//            /*
//
//            firmaDigitalSign.setSignedHash(Base64.getDecoder().decode(hashSignedb64));
//            byte[] signedPDF = firmaDigitalSign.signPDF();
//            try (FileOutputStream stream = new FileOutputStream(signedPdfFile)) { stream.write(signedPDF); }
//
//
//            //String docHashToSign64 = firmaDigitalSign.getDocHashToSign64();
//            //System.out.println("docHashToSign64: "+docHashToSign64);
//
//            //byte[] signedPDF = firmaDigitalSign.signPDF();
//            //try (FileOutputStream stream = new FileOutputStream(signedPdfFile)) { stream.write(signedPDF); }
//            fl.close();
//            */
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Test
    void signPDF() {

        //CertificateChain
        String b64CertificateChain = "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";
        byte[] certificateChain = Base64.getDecoder().decode(b64CertificateChain);

        String pdfFile = "C:\\Temp\\digitalsign\\PDFSignTest.pdf";
        String signedPdfFile = "C:\\Temp\\digitalsign\\PDFSignTest_signed.pdf";
        try {
            FileInputStream fl = new FileInputStream(pdfFile);
            FirmaDigitalSign firmaDigitalSign =
                    FirmaDigitalSignBuilder.newInstance()
                            .setCertificateChain(certificateChain)
                            .setPdfContent(fl.readAllBytes())
                            .setReason("Reason: Teste")
                            .setLocation("Location: Teste")
                            .build();

            byte[] signedPDF = firmaDigitalSign.signPDF();
            try (FileOutputStream stream = new FileOutputStream(signedPdfFile)) { stream.write(signedPDF); }
            fl.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
