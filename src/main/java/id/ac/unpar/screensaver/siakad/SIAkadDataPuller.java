/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ac.unpar.screensaver.siakad;

import id.ac.unpar.screensaver.DataPuller;
import id.ac.unpar.siamodels.Mahasiswa;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

/**
 *
 * @author pascal
 */
public class SIAkadDataPuller extends DataPuller {

    private final SIAkad siakad;

    public SIAkadDataPuller() throws FileNotFoundException, IOException {
        Properties auth = new Properties();
        auth.load(new FileReader("login-dosen.properties"));
        String username = auth.getProperty("username");
        String password = auth.getProperty("user.password");

        this.siakad = new SIAkad();
        this.siakad.login(username, password);
    }

    @Override
    public Mahasiswa[] pullMahasiswas() {
        List<Mahasiswa> mahasiswas = null;
        Random random = new Random(13); // "stable" random
        try {
            mahasiswas = siakad.requestMahasiswaList();
            Collections.shuffle(mahasiswas, random);
        } catch (IllegalStateException ex) {
            Logger.getLogger(SIAkadDataPuller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SIAkadDataPuller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return (Mahasiswa[]) mahasiswas.toArray(new Mahasiswa[mahasiswas.size()]);
    }

    @Override
    public Mahasiswa pullMahasiswaDetail(Mahasiswa m) {
        try {
            siakad.requestDataAkademik(m);
            siakad.requestDataDiri(m);
        } catch (IllegalStateException ex) {
            Logger.getLogger(SIAkadDataPuller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SIAkadDataPuller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

}
