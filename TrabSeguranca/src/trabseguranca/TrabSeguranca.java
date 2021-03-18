/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabseguranca;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Scanner;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

public class TrabSeguranca {

    public static final String MASTER_PW = "123456";

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException, Exception {
        int addProvider = Security.addProvider(new BouncyCastleFipsProvider());
        isFirstExec();
        start();
    }

    public static void action(String option) throws Exception {
        Scanner sc = new Scanner(System.in);
        if ("1".equals(option)) {
            System.out.println("Qual o nome do usuário a ser cadastrado?");
            String name = sc.nextLine();
            exitProgram(name);
            Usuario usuario = Usuario.register(name);
            System.out.println("Usuário " + name + " cadastrado com sucesso");
            start();
        }
        if ("2".equals(option)) {
            System.out.println("Insira a mensagem a ser enviada");
            String message = sc.nextLine();
            System.out.println("Insira o usuário a quem você gostaria de mandar as mensagens");
            String name = sc.nextLine();
            exitProgram(name);
            Cifra.enviaMensagem(name, message);
            start();
        }
        exitProgram(option);
    }

    //Lê os arquivos e os encripta novamente antes de fechar o programa.
    public static void exitProgram(String text) throws Exception {
        if ("0".equals(text)) {
            encriptFiles();
            System.exit(0);
        }
    }

    public static void startMenu() {
        System.out.println("1. Cadastrar usuário");
        System.out.println("2. Enviar mensagem");
        System.out.println("0. Finalizar programa");
    }

    public static void start() throws Exception {
        Scanner sc = new Scanner(System.in);
        startMenu();
        String option = sc.nextLine();
        action(option);
    }

    //Verifica se é a primeira execução, caso seja cria senha e parametros do gerenciador, caso contrário, pede a senha mestre *123456*
    public static void isFirstExec() throws Exception {
        File pwFile = new File(System.getProperty("user.dir") + "\\Usuarios" + "\\password.txt");
        if (!pwFile.exists()) {
            String iv = Cifra.generateIv();
            String key = Cifra.generateKey(iv, MASTER_PW, 1000);
            UserFile.writeFile("password", key, iv);

        } else {
            decriptFiles(pwFile);
        }
    }

    public static void decriptFiles(File pwFile) throws Exception {
        Scanner sc = new Scanner(System.in);
        String pw = "";
        while (!pw.equals(MASTER_PW)) {
            System.out.println("Insira a senha mestre");
            pw = sc.nextLine();
            if (!pw.equals(MASTER_PW)) {
                System.out.println("Senha incorreta, tente novamente");
            }
        }
        String[] params = getPwParams();

        String pwIv = params[1];
        String pwKey = params[0];

        //desencripta todos os arquivos caso a senha mestre esteja correta
        File folder = new File(System.getProperty("user.dir") + "\\Usuarios");
        File[] files = folder.listFiles();
        for (File file : files) {
            if (!file.equals(pwFile)) {
                Scanner myReader = new Scanner(file);
                String name = myReader.nextLine();
                String key = myReader.nextLine();
                String iv = myReader.nextLine();

                byte[] biv = Hex.decodeHex(iv.toCharArray());
                byte[] bkey = Hex.decodeHex(key.toCharArray());

                iv = Cifra.decipher(biv, pwIv, pwKey);
                key = Cifra.decipher(bkey, pwIv, pwKey);

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(folder + "\\" + name + ".txt"), "utf-8"))) {
                    writer.write(name + "\n" + key + "\n" + iv);
                }
                myReader.close();
            }
        }
    }
    
    public static void encriptFiles() throws Exception{
        File pwFile = new File(System.getProperty("user.dir") + "\\Usuarios" + "\\password.txt");
            File folder = new File(System.getProperty("user.dir") + "\\Usuarios");
            File[] files = folder.listFiles();
            String[] params = getPwParams();

            String pwIv = params[1];
            String pwKey = params[0];
            for (File file : files) {
                if (!file.equals(pwFile)) {
                    Scanner myReader = new Scanner(file);
                    String name = myReader.nextLine();
                    String key = myReader.nextLine();
                    String iv = myReader.nextLine();

                    key = Hex.encodeHexString(Cifra.cipher(key, pwIv, pwKey));
                    iv = Hex.encodeHexString(Cifra.cipher(iv, pwIv, pwKey));

                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(folder + "\\" + name + ".txt"), "utf-8"))) {
                        writer.write(name + "\n" + key + "\n" + iv);
                    }
                    myReader.close();
                }
            }
    }

    //Busca o IV e a KEY do gerenciador
    public static String[] getPwParams() throws Exception {
        File pwFile = new File(System.getProperty("user.dir") + "\\Usuarios" + "\\password.txt");
        Scanner password = new Scanner(pwFile);
        String n = password.nextLine();
        String pwKey = password.nextLine();
        String pwIv = password.nextLine();
        String[] params = {pwKey, pwIv};
        return params;
    }

}
