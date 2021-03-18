/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabseguranca;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Arquivo {
    public void createFile(String fileName, String key, String iv, String username) throws IOException{
        FileWriter file = new FileWriter("d:\\"+fileName+".txt", true);
        this.writeFile(file, key, iv, username);
    }
    
    public void writeFile(FileWriter file, String key, String iv, String username)throws IOException{
        PrintWriter printer = new PrintWriter(file);
        printer.printf("Username: "+username+" IV: " + iv+ " Key: " + key + "%n");
        file.close();
        System.out.println("O registro do usuário e seus parâmetros foi feito com sucesso!");
    }
}
