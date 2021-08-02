package id.ac.unpar.screensaver;

import id.ac.unpar.screensaver.siakad.SIAkadDataPuller;
import id.ac.unpar.siamodels.Mahasiswa;
import id.ac.unpar.siamodels.TahunSemester;
import java.io.ByteArrayInputStream;
import javafx.scene.image.Image;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PrimaryController implements Initializable {

    @FXML
    private Text nama, angkatan, usia, status, email, toefl, ipk, sks;

    @FXML
    private ImageView foto;

    private int indexOfMahasiswa = 0;
    private Mahasiswa[] listMahasiswa;
    private boolean[] mahasiswaLoaded;
    private DataPuller puller;

    public int getIndexOfMahasiswa() {
        return indexOfMahasiswa;
    }

    public void setIndexOfMahasiswaAndPreload(int indexOfMahasiswa) {
        this.indexOfMahasiswa = indexOfMahasiswa;
        if (!mahasiswaLoaded[indexOfMahasiswa]) {
            new MahasiswaDetailPuller(listMahasiswa[indexOfMahasiswa]).start();
        } else {
            System.out.println("No longer pulling mahasiswa detail for " + listMahasiswa[indexOfMahasiswa].getNama() + " because already pulled before");
        }
    }
    
    public PrimaryController() throws IOException {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            puller = new SIAkadDataPuller();
            listMahasiswa = puller.pullMahasiswas();
            listMahasiswa[this.getIndexOfMahasiswa()] = puller.pullMahasiswaDetail(listMahasiswa[this.getIndexOfMahasiswa()]);
            mahasiswaLoaded = new boolean[listMahasiswa.length];
            this.updateView(listMahasiswa[this.getIndexOfMahasiswa()]);
            this.setIndexOfMahasiswaAndPreload(this.getIndexOfMahasiswa() + 1);
            Timeline timeline = new Timeline(
                    new KeyFrame(
                            Duration.seconds(15), // May need to adjust longer if internet is slow
                            event -> {
                                try {
                                    if (mahasiswaLoaded[this.getIndexOfMahasiswa()]) {
                                        // Update view only if mahasiswa is loaded. Otherwise, wait for next turn
                                        this.updateView(listMahasiswa[this.getIndexOfMahasiswa()]);
                                        this.setIndexOfMahasiswaAndPreload((this.getIndexOfMahasiswa() + 1) % listMahasiswa.length);
                                    } else {
                                        System.out.println("Mahasiswa " + listMahasiswa[this.getIndexOfMahasiswa()].getNama() + " is not ready. Waiting for next turn...");
                                    }
                                } catch (IOException ex) {
                                    Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                    )
            );
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

        } catch (IllegalStateException | IOException ex) {
            Logger.getLogger(PrimaryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateView(Mahasiswa mahasiswa) throws IOException {
        System.out.println("Updating view with " + mahasiswa.getNama());
        if (mahasiswa.getPhotoPath() != null) {
            ByteArrayInputStream bais = new ByteArrayInputStream(mahasiswa.getPhotoImage());
            Image image = new Image(bais);
            this.foto.setImage(image);

            this.foto.setVisible(true);
        } else {
            this.foto.setVisible(false);
        }
        this.nama.setText(mahasiswa.getNama());
        this.angkatan.setText(mahasiswa.getTahunAngkatan() + "");
        this.usia.setText(
            mahasiswa.getTanggalLahir() != null ?
            (Period.between(mahasiswa.getTanggalLahir(), LocalDate.now()).getYears() + " tahun " + Period.between(mahasiswa.getTanggalLahir(), LocalDate.now()).getMonths() + " bulan (lahir " + mahasiswa.getTanggalLahir().toString() + ")") :
            "Tidak tersedia"
        );
        this.status.setText(mahasiswa.getStatus() != null ? mahasiswa.getStatus().toString() : "Tidak Tersedia");
        this.email.setText(mahasiswa.getEmailAddress());
        if (mahasiswa.getNilaiTOEFL() != null && mahasiswa.getNilaiTOEFL().size() > 0) {
            this.toefl.setText(mahasiswa.getNilaiTOEFL().get(mahasiswa.getNilaiTOEFL().firstKey()).toString());
        } else {
            this.toefl.setText("Tidak Tersedia");
        }
        TahunSemester tahunSemesterTerakhir = null;
        for (Mahasiswa.Nilai nilai: mahasiswa.getRiwayatNilai()) {
            if (tahunSemesterTerakhir == null || nilai.getTahunSemester().compareTo(tahunSemesterTerakhir) > 0) {
                tahunSemesterTerakhir = nilai.getTahunSemester();
            }
        }
        this.ipk.setText(mahasiswa.getRiwayatNilai().isEmpty() ?
                "Tidak tersedia" :
                Math.round(mahasiswa.calculateIPS(tahunSemesterTerakhir) * 100.0) / 100.0 + "/" + Math.round(mahasiswa.calculateIPK() * 100.0) / 100.0
        );
        this.sks.setText(+mahasiswa.calculateSKSLulus() + "/" + mahasiswa.calculateSKSTempuh(false));
    }

    private class MahasiswaDetailPuller extends Thread {
        private Mahasiswa mahasiswa;
        public MahasiswaDetailPuller(Mahasiswa mahasiswa) {
            this.mahasiswa = mahasiswa;
        }
        public void run() {
            System.out.println("Pulling mahasiswa detail for " + mahasiswa.getNama());
            puller.pullMahasiswaDetail(mahasiswa);
            for (int i = 0; i < listMahasiswa.length; i++) {
                if (listMahasiswa[i] == this.mahasiswa) {
                    mahasiswaLoaded[i] = true;
                    System.out.println("Pulled mahasiswa detail for " + mahasiswa.getNama() + " (index " + i + ")");
                    break;
                }
            }
            
        }
    }
}
