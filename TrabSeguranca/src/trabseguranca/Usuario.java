/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabseguranca;

public class Usuario {

    private String name;
    private String iv;
    private String key;

    public Usuario(String name, String iv, String key) {
        this.name = name;
        this.iv = iv; //gerar iv, setar via Cifra.generateIv();
        this.key = key; //gerar key gerar sal pra gerar chave pra cifrar/decifrar arquivo usar Cifra.generateKey()
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static Usuario register(String name) throws Exception {
        String iv = Cifra.generateIv();
        String salt = Cifra.getSalt();
        String key = Cifra.generateKey(iv, salt, 1000);

        Usuario user = new Usuario(name, iv, key);
        UserFile.writeFile(name, key, iv);
        return user;
    }

}
