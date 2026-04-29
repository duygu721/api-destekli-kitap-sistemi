import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class deneme {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Ana Menü ===");
            System.out.println("1 - API'den Veri Çek ve Kaydet");
            System.out.println("2 - Listele");
            System.out.println("3 - Güncelle");
            System.out.println("4 - Sil");
            System.out.println("5 - Çikiş");
            System.out.println("6 - Favori Kitaplar Menüsü");
            System.out.print("Seçiminiz: ");

            int secim = scanner.nextInt();
            scanner.nextLine();

            switch (secim) {
                case 1: veriCekVeKaydet(); 
                break;
                case 2: Listele();
                 break;
                case 3: guncelle();
                 break;
                case 4: sil();
                 break;
                case 6: favoriKitaplarMenusu(); 
                break;
                case 5: System.out.println("Çikiliyor..."); 
                return;
                default: System.out.println("Geçersiz seçim!");
            }
        }
    }
    public static void veriCekVeKaydet() {
        try {
            String apiUrl = "https://www.googleapis.com/books/v1/volumes?q=subject:classics&langRestrict=fr&maxResults=40";

            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new FileWriter("kitaplar.txt"));

            String line;
            int sayac = 0;
            String baslik = null, yazarlar = null, basimYili = null;
            boolean inAuthorsArray = false;
            StringBuilder authorsBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("\"title\":")) {
                    if (baslik != null) {
                        writer.write("Kitap " + (sayac + 1) + ": " + baslik + "\n");
                        writer.write("Yazar(lar): " + (yazarlar != null ? yazarlar : "Bilgi yok") + "\n");
                        writer.write("Basım Yılı: " + (basimYili != null ? basimYili : "Bilgi yok") + "\n\n");
                        sayac++;
                    }
                    baslik = line.split(":", 2)[1].trim().replace("\"", "").replace(",", "");
                    yazarlar = null;
                    basimYili = null;
                    authorsBuilder.setLength(0);
                    inAuthorsArray = false;
                } else if (line.startsWith("\"authors\":")) {
                    inAuthorsArray = true;
                    authorsBuilder.setLength(0);
                    if (line.contains("[")) {
                        String afterBracket = line.substring(line.indexOf('[') + 1).trim();
                        if (afterBracket.contains("]")) {
                            String inside = afterBracket.substring(0, afterBracket.indexOf(']'));
                            authorsBuilder.append(inside);
                            inAuthorsArray = false;
                        } else {
                            authorsBuilder.append(afterBracket);
                        }
                    }
                } else if (inAuthorsArray) {
                    if (line.contains("]")) {
                        String beforeBracket = line.substring(0, line.indexOf(']')).trim();
                        authorsBuilder.append(beforeBracket);
                        inAuthorsArray = false;
                    } else {
                        authorsBuilder.append(line);
                    }
                } else if (line.startsWith("\"publishedDate\":")) {
                    basimYili = line.split(":", 2)[1].trim().replace("\"", "").replace(",", "");
                }
                if (!inAuthorsArray && authorsBuilder.length() > 0 && yazarlar == null) {
                    String allAuthors = authorsBuilder.toString();
                    allAuthors = allAuthors.replace("\"", "").replace(",", ", ").trim();
                    yazarlar = allAuthors;
                }
            }
            if (baslik != null) {
                writer.write("Kitap " + (sayac + 1) + ": " + baslik + "\n");
                writer.write("Yazar(lar): " + (yazarlar != null ? yazarlar : "Bilgi yok") + "\n");
                writer.write("Basım Yılı: " + (basimYili != null ? basimYili : "Bilgi yok") + "\n\n");
            }
            reader.close();
            writer.close();
            System.out.println("Veriler 'kitaplar.txt' dosyasına yazıldı.");
        } catch (Exception e) {
            System.out.println("Veri çekilirken hata oluştu:");
            e.printStackTrace();
        }
    }
    public static void Listele() {
        boolean cikis = false;
        while (!cikis) {
            System.out.println("\nListeleme Seçenekleri:");
            System.out.println("1 - Hepsini listele");
            System.out.println("2 - Yazara göre listele");
            System.out.println("3 - Yila göre listele");
            System.out.println("4 - Çikiş");
            int secim = scanner.nextInt();
            scanner.nextLine();
            switch (secim) {
                case 1: hepsiniListele(); break;
                case 2: yazaraGoreListele(); break;
                case 3: yilaGoreListele(); break;
                case 4: cikis = true; break;
                default: System.out.println("Geçersiz seçim!");
            }
        }
    }
    public static void hepsiniListele() {
        try (BufferedReader reader = new BufferedReader(new FileReader("kitaplar.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            System.out.println("Dosya okunamadı.");
        }
    }
    public static void yazaraGoreListele() {
    System.out.print("Yazar adi girin: ");
    String yazarAranan = scanner.nextLine().toLowerCase();
    try (BufferedReader reader = new BufferedReader(new FileReader("kitaplar.txt"))) {
        boolean bulundu = false;
        while (true) {
            String satir1 = reader.readLine();
            if (satir1 == null) break;
            String satir2 = reader.readLine();
            String satir3 = reader.readLine();
            String bosSatir = reader.readLine();

            if (satir2 == null || satir3 == null) break;

            if (satir2.toLowerCase().contains(yazarAranan)) {
                System.out.println(satir1);
                System.out.println(satir2);
                System.out.println(satir3);
                System.out.println();
                bulundu = true;
            }
        }
        if (!bulundu) System.out.println("Bu yazara ait kitap bulunamadı.");
    } catch (IOException e) {
        System.out.println("Dosya okunurken hata oluştu.");
    }
}
  public static void yilaGoreListele() {
    System.out.print("Yıl girin: ");
    String yilAranan = scanner.nextLine().trim();
    try (BufferedReader reader = new BufferedReader(new FileReader("kitaplar.txt"))) {
        boolean bulundu = false;
        while (true) {
            String satir1 = reader.readLine();
            if (satir1 == null) break;
            String satir2 = reader.readLine();
            String satir3 = reader.readLine();
            String bosSatir = reader.readLine();

            if (satir2 == null || satir3 == null) break;

            if (satir3.contains(yilAranan)) {
                System.out.println(satir1);
                System.out.println(satir2);
                System.out.println(satir3);
                System.out.println();
                bulundu = true;
            }
        }
        if (!bulundu) System.out.println("Bu yila ait kitap bulunamadi.");
    } catch (IOException e) {
        System.out.println("Dosya okunurken hata oluştu.");
    }
}
    public static void guncelle() {
        try {
            File inputFile = new File("kitaplar.txt");
            if (!inputFile.exists()) {
                System.out.println("kitaplar.txt dosyası bulunamadı.");
                return;
            }
            List<String> yeniSatirlar = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            System.out.print("Güncellemek istediğiniz kitap adını girin: ");
            String arananBaslik = scanner.nextLine().toLowerCase();
            String satir1, satir2, satir3, bosSatir;
            boolean bulundu = false;
            while ((satir1 = reader.readLine()) != null) {
                satir2 = reader.readLine();
                satir3 = reader.readLine();
                bosSatir = reader.readLine();
                if (satir1.toLowerCase().contains(arananBaslik)) {
                    System.out.println(satir1 + "\n" + satir2 + "\n" + satir3);
                    System.out.print("Bu veriyi güncellemek istiyor musunuz? (e/h): ");
                    String cevap = scanner.nextLine();
                    if (cevap.equalsIgnoreCase("e")) {
                        System.out.print("Yeni kitap adı: ");
                        String yeniBaslik = scanner.nextLine();
                        System.out.print("Yeni yazar(lar): ");
                        String yeniYazar = scanner.nextLine();
                        System.out.print("Yeni basım yılı: ");
                        String yeniYil = scanner.nextLine();
                        satir1 = "Kitap: " + yeniBaslik;
                        satir2 = "Yazar(lar): " + yeniYazar;
                        satir3 = "Basim Yili: " + yeniYil;
                        bulundu = true;
                    }
                }
                yeniSatirlar.add(satir1);
                yeniSatirlar.add(satir2);
                yeniSatirlar.add(satir3);
                yeniSatirlar.add(bosSatir);
            }
            reader.close();
            if (bulundu) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile, false));
                for (String satir : yeniSatirlar) writer.write(satir + "\n");
                writer.close();
                System.out.println("Veri başarıyla güncellendi.");
            } else {
                System.out.println("Aranan kitap başlığı bulunamadı.");
            }
        } catch (IOException e) {
            System.out.println("Dosya işlemi sırasında hata oluştu:");
            e.printStackTrace();
        }
    }
    public static void sil() {
        try {
            List<String> satirlar = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader("kitaplar.txt"));
            System.out.print("Silmek istediğiniz kitap başlığını girin: ");
            String arananBaslik = scanner.nextLine().toLowerCase();
            String satir1, satir2, satir3, bosSatir;
            boolean silindi = false;
            while ((satir1 = reader.readLine()) != null) {
                satir2 = reader.readLine();
                satir3 = reader.readLine();
                bosSatir = reader.readLine();
                if (satir1.toLowerCase().contains(arananBaslik)) {
                    System.out.println(satir1 + "\n" + satir2 + "\n" + satir3);
                    System.out.print("Bu veriyi silmek istiyor musunuz? (e/h): ");
                    String cevap = scanner.nextLine();
                    if (cevap.equalsIgnoreCase("e")) {
                        silindi = true;
                        continue;}
                }
                satirlar.add(satir1);
                satirlar.add(satir2);
                satirlar.add(satir3);
                satirlar.add(bosSatir);
            }
            reader.close();
            if (silindi) {
                BufferedWriter writer = new BufferedWriter(new FileWriter("kitaplar.txt"));
                for (String s : satirlar) writer.write(s + "\n");
                writer.close();
                System.out.println("Veri silindi.");
            } else System.out.println("Silinecek kitap bulunamadı.");
        } catch (IOException e) {
            System.out.println("Silme işlemi sırasında hata oluştu.");
        }
    }
    public static void favoriKitaplarMenusu() {
        boolean devam = true;
        while (devam) {
            System.out.println("\n=== Favori Kitaplar Menüsü ===");
            System.out.println("1 - Favorilere Kitap Ekle");
            System.out.println("2 - Favori Kitaplari Listele");
            System.out.println("3 - Favori Kitap Sil");
            System.out.println("4 - Geri Dön");

            System.out.print("Seçiminiz: ");
            int secim = scanner.nextInt();
            scanner.nextLine();

            switch (secim) {
                case 1: favoriEkle();
                 break;
                case 2: favoriListele();
                 break;
                case 3: favoriSil(); 
                break;
                case 4: devam = false;
                 break;
                default: System.out.println("Geçersiz seçim!");
            }
        }
    }
    public static void favoriEkle() {
        System.out.print("Favorilere eklemek istediğiniz kitap başlığını girin: ");
        String aranan = scanner.nextLine().toLowerCase();
        boolean bulundu = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("kitaplar.txt"));
             BufferedWriter writer = new BufferedWriter(new FileWriter("favori_kitaplar.txt", true))) {
            String satir1, satir2, satir3, bosSatir;
            while ((satir1 = reader.readLine()) != null) {
                satir2 = reader.readLine();
                satir3 = reader.readLine();
                bosSatir = reader.readLine();
                System.out.println("okunan başlik:"+satir1);
                if (satir1.toLowerCase().contains(aranan)) {
                    System.out.println("\nbulunan kitap:");
                    System.out.println(satir1);
                    System.out.println(satir2);
                    System.out.println(satir3);
                    System.out.print("Favorilere eklemek istiyor musunuz? (e/h): ");
                    String cevap = scanner.nextLine();
                    if (cevap.equalsIgnoreCase("e")) {
                        writer.write(satir1 + "\n" + satir2 + "\n" + satir3 + "\n\n");
                        System.out.println("Kitap favorilere eklendi:"+satir1);
                        bulundu = true;
                    }
                }
            }
            if (!bulundu) System.out.println("Aradığınız kitap bulunamadı.");
        } catch (IOException e) {
            System.out.println("Favori ekleme sırasında hata oluştu.");
        }
    }
    public static void favoriListele() {
        System.out.println("\n Favori Kitaplar ");
        try (BufferedReader reader = new BufferedReader(new FileReader("favori_kitaplar.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            System.out.println("Favori kitaplar dosyası okunamadı.");
        }
    }
    public static void favoriSil() {
        System.out.print("Silmek istediğiniz favori kitap başlığını girin: ");
        String aranan = scanner.nextLine().toLowerCase();
        boolean silindi = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("favori_kitaplar.txt"))) {
            List<String> yeniFavoriler = new ArrayList<>();
            String satir1, satir2, satir3, bosSatir;
            while ((satir1 = reader.readLine()) != null) {
                satir2 = reader.readLine();
                satir3 = reader.readLine();
                bosSatir = reader.readLine();
                if (satir1.toLowerCase().contains(aranan)) {
                    System.out.println(satir1 + "\n" + satir2 + "\n" + satir3);
                    System.out.print("Bu favori kitabı silmek istiyor musunuz? (e/h): ");
                    String cevap = scanner.nextLine();
                    if (cevap.equalsIgnoreCase("e")) {
                        silindi = true;
                        continue;
                    }
                }
                yeniFavoriler.add(satir1);
                yeniFavoriler.add(satir2);
                yeniFavoriler.add(satir3);
                yeniFavoriler.add(bosSatir);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("favori_kitaplar.txt"))) {
                for (String s : yeniFavoriler) writer.write(s + "\n");
            }
            if (silindi) System.out.println("Favori kitap silindi.");
            else System.out.println("Silinecek kitap bulunamadı.");
        } catch (IOException e) {
            System.out.println("Favori kitap silme sırasında hata oluştu.");
        }
    }
}