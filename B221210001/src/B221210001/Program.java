/**
*
* @author Elif Özhan - elif.ozhan1@ogr.sakarya.edu.tr
* @since 02.04.24
* <p>
* Program, ödevde istenilen tüm işlemlein tek bir sınıf içinde gerçekleştirilebilmesini sağlar.
* </p>
*/

package B221210001;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

public class Program {
	static String hedefKlasor;
	public static void main(String[] args) {
    try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Kullanıcıdan GitHub proje linkini al
        System.out.print("Lütfen GitHub proje linkini giriniz: ");
        String projeLinki = reader.readLine();

        // GitHub projesini klonla
        klonlaGitHubProjesi(projeLinki);
        System.out.println("Proje başarıyla klonlandı.");

        // ".java" dosyalarını Java_files klasörüne kopyala
        kopyalaJavaDosyalari("Java_files");
        System.out.println("Java dosyaları başarıyla kopyalandı.");
        
        String[] dosya_isimleri = dosyaIsimleriniAl("Java_files");
        System.out.println("Dosya isimleri başarıyla alındı.");
        
        // Dosyaları sırayla oku ve analiz et
        for (String dosya_ismi : dosya_isimleri)
        {
        	if(dosya_ismi.startsWith("I"))
        		continue;
            javaKodAnalizi(dosya_ismi);
        }
        
    }
    
    catch (IOException e)
    {
        System.err.println("Hata: " + e.getMessage());
    }
}

public static void klonlaGitHubProjesi(String projeLinki) throws IOException {
    // GitHub proje linki
    URL url = new URL(projeLinki + "/archive/master.zip");

    // Geçici dosya oluştur
    Path tempDosya = Files.createTempFile("proje", ".zip");

    // İndirme işlemi
    try (InputStream in = url.openStream())
    {
        Files.copy(in, tempDosya, StandardCopyOption.REPLACE_EXISTING);
    }

    // Zip dosyasını aç
    try (FileSystem zipFileSystem = FileSystems.newFileSystem(Paths.get(tempDosya.toUri()), Map.of()))
    {
        Path zipRoot = zipFileSystem.getPath("/");

        // Klonlanacak klasör adını belirle
        String klonlanacakKlasorAdi = projeLinki.substring(projeLinki.lastIndexOf('/') + 1);
        hedefKlasor = klonlanacakKlasorAdi + "-klon";

        // Hedef klasörü oluştur
        Files.createDirectories(Paths.get(hedefKlasor));

        // Dosyaları kopyala
        Files.walkFileTree(zipRoot, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                Path targetDir = Paths.get(hedefKlasor, zipRoot.relativize(dir).toString());
                if (!Files.exists(targetDir))
                {
                    Files.createDirectories(targetDir);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                Files.copy(file, Paths.get(hedefKlasor, zipRoot.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    // Geçici dosyayı sil
    Files.delete(tempDosya);
}

public static void kopyalaJavaDosyalari(String hedefKlsr) throws IOException {
    // Java_files klasörünü oluştur
    Files.createDirectories(Paths.get(hedefKlsr));

    // Mevcut klonlanmış klasör içinde gez ve .java dosyalarını kopyala
    Files.walkFileTree(Paths.get("./"+hedefKlasor), new SimpleFileVisitor<Path>()
    {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
        {
            if (file.toString().endsWith(".java"))
            {
                Files.copy(file, Paths.get(hedefKlsr, file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
            }
            return FileVisitResult.CONTINUE;
        }
    });
}

// Dosyaların isimlerini dizide tut
public static String[] dosyaIsimleriniAl(String klasorYolu) {
    File klasor = new File(klasorYolu);
    File[] dosyalar = klasor.listFiles();
    String[] dosyaIsimleri = new String[dosyalar.length];
    
    for (int i = 0; i < dosyalar.length; i++)
    {
        dosyaIsimleri[i] = dosyalar[i].getName();
    }
    return dosyaIsimleri;
}

public static void javaKodAnalizi(String dosya_ismi)
{
    try {
    	
    	// Dosyaları oku
        BufferedReader reader = new BufferedReader(new FileReader("./Java_files/" + dosya_ismi));
        String satir;
        int javadocYorumSatirSayisi = 0;
        int yorumSatirSayisi = 0;
        int kodSatirSayisi = 0;
        int herSeyDahilKodSatirSayisi = 0;
        int fonksiyonSayisi = 0;
        boolean fonksiyonIcerisinde = false;
        double YG = 0;
        double YH = 0;
        double yorumSapmaYuzdesi = 0;
        boolean sinifVarMi = false;
        DecimalFormat df = new DecimalFormat("#.##");
        
        // Satır satır değerlendirme
        while ((satir = reader.readLine()) != null)
        {
        	satir = satir.trim();
        	herSeyDahilKodSatirSayisi++;
        			
        	// Sınıf kontrolü
            if(satir.contains("class"))
            {
            	sinifVarMi = true;
            }
            
            // Javadoc yorum satırı kontrolü
        	if (satir.startsWith("/**")) 
        	{
                while ((satir = reader.readLine()) != null)
                {
                	herSeyDahilKodSatirSayisi++;
                	if (satir.endsWith("*/"))
                    {
                        break;
                    }
                	
                	if(satir != "*")
                	{
                		javadocYorumSatirSayisi++;
                	}
                }
            }
        	
        	// Yorum satırı kontrolü
        	else if (satir.startsWith("/*"))
        	{
        		while ((satir = reader.readLine()) != null)
                {
        			herSeyDahilKodSatirSayisi++;
        			if (satir.endsWith("*/"))
                    {
                        break;
                    }
        			
                	if(satir != "*")
                	{
                		yorumSatirSayisi++;
                	}
                }
            }
        	
        	else if (satir.contains("//"))
        	{
        		if (!(satir.startsWith("//")))
        		{
        			kodSatirSayisi++;
        		}
        		
                yorumSatirSayisi++;
            }
            
        	// Fonksiyon kontrolü
            else if (!satir.isEmpty())
            {
                if ((satir.contains("public") || satir.contains("private") || satir.contains("protected")) && satir.contains("("))
                {
                    fonksiyonSayisi++;
                    fonksiyonIcerisinde = true;
                }
                
                kodSatirSayisi++;
            }
            
            if (satir.contains("}"))
            {
                fonksiyonIcerisinde = false;
            }
            
        }
        
        reader.close();
        
        // Yorum sapma yüzdesi hesaplama (sıfıra bölme hatası için sonucu sıfır ver)
        if(fonksiyonSayisi != 0)
        {
        	YG = ((Double.valueOf(javadocYorumSatirSayisi) + Double.valueOf(yorumSatirSayisi))*0.8)/Double.valueOf(fonksiyonSayisi);
        	YH = (Double.valueOf(kodSatirSayisi)/Double.valueOf(fonksiyonSayisi))*0.3;
        	if(YH != 0)
        	{
        		yorumSapmaYuzdesi = ((100*YG)/YH)-100;
        	}
        	
        	else yorumSapmaYuzdesi = 0;
        }
        
        else yorumSapmaYuzdesi = 0;

        // Sınıfları ekrana yazdır
        if(sinifVarMi == true)
        {
        	System.out.println("------------------------------------------");
        	System.out.println("Dosya: " + dosya_ismi);
        	System.out.println("Javadoc Yorum Satır Sayısı: " + javadocYorumSatirSayisi);
        	System.out.println("Yorum Satır Sayısı: " + yorumSatirSayisi);
        	System.out.println("Kod Satır Sayısı: " + kodSatirSayisi);
        	System.out.println("LOC: " + herSeyDahilKodSatirSayisi);
        	System.out.println("Fonksiyon Sayısı: " + fonksiyonSayisi);
        	System.out.println("Yorum Sapma Yüzdesi: %" + df.format(yorumSapmaYuzdesi));
        }

    }
    
    catch (IOException e) {
        System.err.println("Hata: " + e.getMessage());
    }
}
}