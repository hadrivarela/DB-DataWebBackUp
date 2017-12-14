import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 * Clase utilizada para realizar la descarga de archivos desde un FTP basado en
 * la librería FTPCLient
 * 
 * @author Hadri
 * @version 1.0
 */
public class FTPDown {
	public static DBDataBackup info= new DBDataBackup();
	/**
	 * Descarga un solo archivo del FTP
	 *
	 * @param clienteFTP
	 * 
	 * @param pathRemoto
	 *            Path del servidor FTP
	 * @param archivoLocal
	 *            Path del archivo en el que se va a guardar
	 * @return true si el archivo se descarga correctamente
	 * @throws IOException
	 * 
	 */
	public static boolean retrieveFile(FTPClient clienteFTP, String pathRemoto, String archivoLocal)
			throws IOException {
		//DBDataBackup info = new DBDataBackup();Para evitar sobrecargar la memoria
		System.err.println("RetrieveFile");
		System.err.println("Path remoto:" + pathRemoto + "Path guardado: " + archivoLocal);
		info.consoleArea.append("RetrieveFile\n");
		info.consoleArea.append("Path remoto:" + pathRemoto + "Path guardado: " + archivoLocal + "\n");

		File archivoDescarga = new File(archivoLocal);
		File directorio = archivoDescarga.getParentFile();

		if (!directorio.exists()) {
			info.consoleArea.append("Archivo: " + directorio.getName() + "\n");
			directorio.mkdir();
		}
		try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(archivoDescarga))) {
			clienteFTP.setFileType(FTP.BINARY_FILE_TYPE);
			return clienteFTP.retrieveFile(pathRemoto, outputStream);
		} catch (IOException ex) {
			throw ex;
		}
	}

	/**
	 * Descarga un directorio de un FTP
	 *
	 * @param clienteFtp
	 *            El cliente FTP que se va a usar
	 * @param directorioRemoto
	 *            El directorio que se va a descargar
	 *
	 * 
	 * @param directorioLocal
	 *            Path donde se guardara la copia del directorio
	 * @throws IOException
	 *             Excepción de cualquier error de archivo
	 */
	public static void retrieveDir(FTPClient clienteFtp, String directorioRemoto, String directorioLocal)
			throws IOException {
		directorioLocal += "/www";
		//DBDataBackup info = new DBDataBackup();Para evitar sobrecargar la memoria
		System.err.println("RetrieveDir");
		info.consoleArea.append("RetrieveDir\n");
		System.err.println("Path remoto:" + directorioRemoto + "Path guardado: " + directorioLocal);
		info.consoleArea.append("Path remoto:" + directorioRemoto + "Path guardado: " + directorioLocal + "\n");
		FTPFile[] ftpFiles = clienteFtp.listFiles(directorioRemoto);
		if (ftpFiles == null || ftpFiles.length == 0) {
			return;
		}
		for (FTPFile ftpFile : ftpFiles) {
			info.consoleArea.append("Directorio: " + ftpFile.getName() + "\n");
			String ftpFileNombre = ftpFile.getName();
			if (ftpFileNombre.equals(".") || ftpFileNombre.equals("..")) {

				continue;
			}
			String archivoLocal = directorioLocal + "/" + ftpFileNombre;
			String archivoRemoto = directorioRemoto + "/" + ftpFileNombre;
			if (ftpFile.isDirectory()) {

				File nuevoDirectorio = new File(archivoLocal);
				nuevoDirectorio.mkdirs();

				retrieveDir(clienteFtp, archivoRemoto, archivoLocal);
			} else {

				retrieveFile(clienteFtp, archivoRemoto, archivoLocal);
			}
		}
		
	}
}
