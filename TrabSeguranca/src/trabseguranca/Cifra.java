/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabseguranca;

import java.io.File;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Scanner;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import static trabseguranca.TrabSeguranca.getPwParams;

public class Cifra {

    //Gera um IV com SecureRandom
    public static String generateIv() throws NoSuchAlgorithmException, NoSuchProviderException {
        byte iv[] = new byte[16];
        SecureRandom random = SecureRandom.getInstance("DEFAULT", "BCFIPS");
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        String newIv = Utils.toHex(iv);
        return newIv;
    }
    
    //Gera uma key utilizando o PBKDF2 com HMAC
    public static String generateKey(String iv, String salt, Integer iterations) {
        String key = null;
        SecretKeyFactory pbkdf2 = null;
        PBEKeySpec spec = new PBEKeySpec(iv.toCharArray(), salt.getBytes(), iterations, 128);
        try {
            pbkdf2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "BCFIPS");
            SecretKey sk = pbkdf2.generateSecret(spec);
            key = Hex.encodeHexString(sk.getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }

    //Gera salt
    public static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return Hex.encodeHexString(salt);
    }

    //Cifra mensagens/chaves/ivs
    public static byte[] cipher(String message, String iv, String key) throws Exception {
        Cipher in;

        in = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS");

        byte[] byteIV = org.apache.commons.codec.binary.Hex.decodeHex(iv.toCharArray());
        GCMParameterSpec​ gcmParameters = new GCMParameterSpec​(128, byteIV);

        byte[] byteKey = org.apache.commons.codec.binary.Hex.decodeHex(key.toCharArray());

        Key newKey = new SecretKeySpec(byteKey, "AES");

        in.init(Cipher.ENCRYPT_MODE, newKey, gcmParameters);

        byte[] P = message.getBytes();
        byte[] enc = in.doFinal(P);

        System.out.println("Msg cifrada = " + Hex.encodeHexString(enc));

        return enc;
    }

    //Decifra mensagens/chaves/ivs
    public static String decipher(byte[] encrypted, String iv, String key) throws Exception {
        Cipher out;

        out = Cipher.getInstance("AES/GCM/NoPadding", "BCFIPS");

        byte[] byteIV = org.apache.commons.codec.binary.Hex.decodeHex(iv.toCharArray());
        GCMParameterSpec​ gcmParameters = new GCMParameterSpec​(128, byteIV);

        byte[] byteKey = org.apache.commons.codec.binary.Hex.decodeHex(key.toCharArray());

        Key newKey = new SecretKeySpec(byteKey, "AES");

        out.init(Cipher.DECRYPT_MODE, newKey, gcmParameters);

        byte[] out2;
        out2 = out.doFinal(encrypted);

        System.out.println("Mensagem decifrada:" + Utils.toString(out2));

        return Utils.toString(out2);
    }

    //Faz o envio da mensagem chamando os métodos cipher e decipher
    public static void enviaMensagem(String user, String message) throws Exception {
        File folder = new File(System.getProperty("user.dir") + "\\Usuarios");
        String file = folder + "\\" + user + ".txt";
        
        Scanner myReader = new Scanner(new File(file));
        
        String name = myReader.nextLine();
        String key = myReader.nextLine();
        String iv = myReader.nextLine();

        myReader.close();
        byte[] result = cipher(message, iv, key);
        decipher(result, iv, key);
    }
}
