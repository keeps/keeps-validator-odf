package pt.keep.validator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

	public static void unzip(File inputFile, File outputDir) throws IOException{
		ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFile));
		ZipEntry ze = zis.getNextEntry();
		while(ze!=null){
			String entryName = ze.getName();
			File f = new File(outputDir + File.separator +  entryName);
			f.getParentFile().mkdirs();
			FileOutputStream fos = new FileOutputStream(f);
			int len;
			byte buffer[] = new byte[1024];
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();  
			ze = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}

}
