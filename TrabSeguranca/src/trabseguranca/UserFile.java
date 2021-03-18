/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabseguranca;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class UserFile {

    public static void writeFile(String name, String key, String iv) throws IOException {

        File folder = new File(System.getProperty("user.dir") + "\\Usuarios");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(folder + "\\" + name + ".txt"), "utf-8"))) {
            writer.write(name + "\n" + key + "\n" + iv);
        }
    }
}
