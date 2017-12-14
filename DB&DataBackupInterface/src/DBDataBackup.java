import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.Color;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;

import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * @author Hadri Programa para realizar una copia de seguridad completa de un
 *         sitio web Accediendo el ftp copiando la carpeta "www" y accede
 *         tambien a la base de datos de la que tambien realiza la copia. Ambos
 *         archivos se copian en la ruta especificada.
 * @Version 0.9
 * @LastUpdates Controladas nuevas excepciones y añadidos nuevos controles en la
 *              introducción de datos. Corregidos strings, controlado error de
 *              path Mysql. Solucionada incompatibilidad del icono en linux.
 *              Corregida la posición en la que se inicia el programa. Corregido
 *              error al guardar que al crear el archivo por primera vez no
 *              guardaba correctamente. Añadido campo que faltaba de "NameDB"
 *              Añadido método de borrado, corregida la carga de datos de los
 *              hosts Corregido mensajes de dialogo de error en los modales
 *              Corregido Error que producia que la lista de host tuviera limite y al superarlo saltara una excepcion
 * 
 * 
 */
public class DBDataBackup {
	// Frame de base
	private JFrame frame;
	// Introduccion de Datos
	private JTextField txfServer;
	private JTextField txfUser;
	private JTextField txfServerBd;
	private JTextField txfUserBd;
	private JTextField txfPort;
	private JTextField txfDestino;
	private JPasswordField txfPass;
	private JPasswordField txfPassBd;
	private JTextField txfNameDB;
	// Componentes publicos
	public JTextArea consoleArea;
	public Timer timer;
	public JButton btnDestino, btnGuardar, btnGestor;
	public File hostsData;
	public ArrayList<String> listaNombresHost;
	public HostsData[] listaObjetosHost;
	public JCheckBox chkFtp;
	public JCheckBox chkBd;
	public ModalGestorHost modalGestor;
	public int nLineas;
	// Textos
	private String sBienvenida, sFtp, sServer, sUser, sPass, sCheckFtp, sCheckBd, sBd, sPuerto, sDestino, sOk, sNameDb,
			programVersion;
	// Booleanos que guardan si la opcion esta activada o no para realizar el Back
	// up
	private boolean bFtp, bBd;
	// Variables de datos
	private String hostFtpData, hostBdData, usrFtpData, usrBdData, passFtpData, passBdData, nameDbData, pathMysql;
	private String[] loadedData;
	private char[] passBd, passFtp;
	private int port;
	// Paths
	public String carpetaRemota = "www", pathDestino;
	// Genera la fecha actual
	Date myDate = new Date();
	String fecha = new SimpleDateFormat("dd_MM_yyyy").format(myDate);
	// Guarda el sistema operativo en el que se está ejecutando y cambia el path mas
	// comun de mysql según el mismo
	public String sistemaEjecucion = System.getProperty("os.name").toLowerCase();

	/**
	 * Inicia la aplicación
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DBDataBackup window = new DBDataBackup();
					window.frame.setLocationRelativeTo(null);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("ERROR:Error durante el arranque!");
				}
			}
		});
	}

	/**
	 * Constructor de la aplicacion
	 */
	public DBDataBackup() {

		cargaTextos();
		configMysql(sistemaEjecucion);
		lanzaVentana();
		// datosPrueba();
		consoleArea.append("OS:" + sistemaEjecucion + "\n");
		consoleArea.append("Date: " + fecha + "\n");
		consoleArea.append("PathMysql:" + pathMysql + "\n");
		consoleArea.append("Cargando ventana\n");
		System.err.println("Iniciado Constructor");
		consoleArea.append("Iniciado Constructor\n");

	}

	/**
	 * Realiza una carga de los textos de la aplicacion
	 */
	private void cargaTextos() {
		sBienvenida = "Programa de realizacion de Backups";
		sFtp = "FTP Backup";
		sServer = "Host:";
		sUser = "User:";
		sPass = "Pass:";
		sCheckFtp = "Backup FTP";
		sCheckBd = "Backup BD";
		sBd = "BD Backup";
		sPuerto = "Port:";
		sDestino = "Destino:";
		sOk = "Ejecutar Backup";
		sNameDb = "NameDB:";
		programVersion = "  v 0.9 | Hadri  ";
	}

	/**
	 * Carga todo el contenido del programa
	 */
	private void lanzaVentana() {
		System.err.println(fecha);
		System.err.println("Cargando ventana...");
		frame = new JFrame();
		frame.setFont(new Font("Monospaced", Font.PLAIN, 12));
		if (sistemaEjecucion.startsWith("mac")) {
			frame.setIconImage(Toolkit.getDefaultToolkit().getImage(DBDataBackup.class
					.getResource("/com/sun/javafx/scene/control/skin/caspian/dialog-more-details@2x.png")));
		}

		frame.setResizable(false);
		frame.setTitle("DB&DataBackup");
		frame.setMinimumSize(frame.getSize());

		frame.setBounds(100, 100, 483, 502);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblInicio = new JLabel(sBienvenida);

		JLabel lblFtpBackup = new JLabel(sFtp);

		chkFtp = new JCheckBox(sCheckFtp);

		JLabel lblServer = new JLabel(sServer);

		txfServer = new JTextField();
		txfServer.setColumns(10);

		JLabel lblUser = new JLabel(sUser);

		txfUser = new JTextField();
		txfUser.setColumns(10);

		JLabel lblPass = new JLabel(sPass);

		JLabel lblBdBackup = new JLabel(sBd);

		JLabel lblServer_1 = new JLabel(sServer);

		JLabel lblUser_1 = new JLabel(sUser);

		JLabel lblPass_1 = new JLabel(sPass);

		JLabel lblPort = new JLabel(sPuerto);

		JLabel lblDestino = new JLabel(sDestino);

		txfServerBd = new JTextField();
		txfServerBd.setColumns(10);

		txfUserBd = new JTextField();
		txfUserBd.setColumns(10);

		txfPort = new JTextField();
		txfPort.setColumns(10);

		txfDestino = new JTextField();
		txfDestino.setColumns(10);

		JButton btnBackup = new JButton(sOk);
		btnBackup.setToolTipText("Ejecuta las tareas de Backup seleccionadas");
		chkBd = new JCheckBox(sCheckBd);
		btnBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.err.println("Boton backup");
				consoleArea.append("Boton backup\n");
				if (chkBd.isSelected()) {
					bBd = true;

				} else {
					bBd = false;

				}
				if (chkFtp.isSelected()) {
					bFtp = true;

				} else {
					bFtp = false;

				}
				if (bBd == false && bFtp == false) {
					JOptionPane.showMessageDialog(frame, "No se ha selecionado ningun tipo de Backup",
							"Selecciona el tipo de Backup", JOptionPane.WARNING_MESSAGE);
					consoleArea.append("ERROR: No se ha selecionado ningun tipo de Backup\n");
				}
				boolean correcto = recopilaDatos();
				System.out.println("Ftp: " + bFtp + " " + "Bd: " + bBd);
				if (bFtp) {
					// Ejecuta el Backup de FTP con los datos introducidos
					System.out.println("Ejecutando Backup de FTP");
					consoleArea.append("Ejecutando Backup de FTP\n");
					if (correcto) {
						metodoBackupFtp(hostFtpData, usrFtpData, passFtpData, carpetaRemota, pathDestino);
					}

				}
				if (bBd) {
					// Ejecuta el Backup de Bd con los datos introducidos
					System.out.println("Ejecutando Backup de BD");
					consoleArea.append("Ejecutando Backup de BD\n");
					if (correcto) {
						metodoBackupBd(hostBdData, usrBdData, passBdData, nameDbData, port, pathDestino);
					}

				}

			}
		});

		txfPass = new JPasswordField();

		txfPassBd = new JPasswordField();

		txfNameDB = new JTextField();
		txfNameDB.setColumns(10);

		JLabel lblNamebd = new JLabel(sNameDb);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JLabel lblConsoleInfo = new JLabel("Console info:");

		btnDestino = new JButton("...");
		btnDestino.addActionListener(ac);
		btnDestino.setToolTipText("Cambia el destino de los Backup");

		btnGestor = new JButton("Gestionar datos");
		btnGestor.addActionListener(ac);
		btnGestor.setToolTipText("Abre la ventana de gestión de datos");

		btnGuardar = new JButton("Guardar datos");
		btnGuardar.addActionListener(ac);
		btnGuardar.setToolTipText("Guarda los datos de conexión actuales ");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
				.createSequentialGroup()
				.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
						.createSequentialGroup().addGap(15)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
								.createSequentialGroup().addComponent(lblDestino)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(txfDestino, GroupLayout.PREFERRED_SIZE, 189, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnDestino, GroupLayout.PREFERRED_SIZE, 54, Short.MAX_VALUE).addGap(161))
								.addGroup(groupLayout.createSequentialGroup()
										.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
												.addGroup(groupLayout
														.createSequentialGroup().addComponent(lblConsoleInfo)
														.addPreferredGap(ComponentPlacement.RELATED, 167,
																Short.MAX_VALUE)
														.addComponent(lblNamebd))
												.addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout
														.createParallelGroup(Alignment.TRAILING)
														.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
																.addComponent(lblServer).addComponent(lblUser))
														.addComponent(lblPass)).addGap(18)
														.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
																.addComponent(chkFtp)
																.addComponent(lblBdBackup, Alignment.TRAILING)
																.addGroup(groupLayout.createSequentialGroup()
																		.addGroup(groupLayout
																				.createParallelGroup(Alignment.TRAILING,
																						false)
																				.addComponent(txfServer)
																				.addComponent(txfUser,
																						GroupLayout.DEFAULT_SIZE, 154,
																						Short.MAX_VALUE)
																				.addComponent(txfPass,
																						Alignment.LEADING))
																		.addPreferredGap(ComponentPlacement.RELATED, 68,
																				Short.MAX_VALUE)
																		.addGroup(groupLayout
																				.createParallelGroup(Alignment.LEADING)
																				.addComponent(lblUser_1,
																						Alignment.TRAILING)
																				.addComponent(lblPass_1,
																						Alignment.TRAILING)
																				.addComponent(lblServer_1,
																						Alignment.TRAILING)))))
												.addGroup(groupLayout.createSequentialGroup()
														.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 237,
																GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(ComponentPlacement.RELATED, 42,
																Short.MAX_VALUE)
														.addComponent(lblPort)))
										.addGap(18)
										.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
												.addComponent(txfNameDB, Alignment.LEADING)
												.addComponent(txfPassBd, Alignment.LEADING)
												.addComponent(txfServerBd, Alignment.LEADING)
												.addComponent(txfUserBd, Alignment.LEADING)
												.addComponent(chkBd, Alignment.LEADING)
												.addComponent(txfPort, Alignment.LEADING))
										.addGap(13))))
						.addGroup(groupLayout.createSequentialGroup().addGap(15)
								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(lblFtpBackup)
										.addGroup(groupLayout.createSequentialGroup().addComponent(btnGuardar)
												.addGap(18).addComponent(btnGestor).addGap(18).addComponent(btnBackup,
														GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE))))
						.addGroup(groupLayout.createSequentialGroup().addGap(125).addComponent(lblInicio)))
				.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup().addGap(22).addComponent(lblInicio).addGap(30)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnGestor)
								.addComponent(btnGuardar).addComponent(btnBackup))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblFtpBackup)
								.addComponent(lblBdBackup))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout
								.createParallelGroup(Alignment.BASELINE).addComponent(chkBd).addComponent(chkFtp))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(lblServer)
								.addComponent(txfServer, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(txfServerBd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblServer_1))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout
								.createParallelGroup(Alignment.BASELINE).addComponent(lblUser)
								.addComponent(txfUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(
										txfUserBd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblUser_1))
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
								.createSequentialGroup().addGap(4)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(txfPassBd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(lblPass_1))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(txfNameDB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(lblNamebd)))
								.addGroup(groupLayout.createSequentialGroup()
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(lblPass).addComponent(txfPass, GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(lblConsoleInfo)))
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout
								.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(txfPort, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(lblPort)))
								.addGroup(groupLayout.createSequentialGroup().addGap(23).addComponent(scrollPane,
										GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)))
						.addGap(18).addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblDestino).addComponent(txfDestino, GroupLayout.PREFERRED_SIZE,
										GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnDestino))
						.addGap(25)));

		consoleArea = new JTextArea();
		consoleArea.setEditable(false);
		consoleArea.setRows(1);
		consoleArea.setLineWrap(true);
		consoleArea.setForeground(Color.RED);
		consoleArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
		consoleArea.setBackground(Color.LIGHT_GRAY);
		scrollPane.setViewportView(consoleArea);
		frame.getContentPane().setLayout(groupLayout);
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnPropiedades = new JMenu("Propiedades");
		menuBar.add(mnPropiedades);

		JMenuItem mntmConfMysql = new JMenuItem("Conf. Mysql");
		mntmConfMysql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Lanza el modal de configuración del path donde se encuentra mysql
				MysqlModal confMysql = new MysqlModal(frame, true);
				confMysql.setAlwaysOnTop(true);
				confMysql.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				confMysql.setResizable(false);
				confMysql.setLocationRelativeTo(null);
				confMysql.txfPathMysql.setText(pathMysql);

				confMysql.okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (e.getSource() == confMysql.okButton) {
							pathMysql = confMysql.txfPathMysql.getText();
							System.err.println("Aceptada la configuracion");
							if (pathMysql.equals("")) {
								JOptionPane.showMessageDialog(confMysql,
										"Faltan datos para la conexión o guardado de datos", "Falta el path de Mysql",
										JOptionPane.ERROR_MESSAGE);
								consoleArea.append("ERROR: Path Mysql vacío\n");
							} else {
								confMysql.setPathMysql(pathMysql);
								System.err.println("Nuevo path: " + pathMysql);
								consoleArea.append("Nuevo path: " + pathMysql + "\n");
								confMysql.setVisible(false);

							}

						}
					}
				});
				confMysql.setVisible(true);

			}
		});
		mnPropiedades.add(mntmConfMysql);

		JMenu mnInfo = new JMenu("Acerca de");
		menuBar.add(mnInfo);

		JLabel lblVBy = new JLabel(programVersion);
		mnInfo.add(lblVBy);
	}

	/**
	 * Realiza una recopilacion de los datos introducidos por el usuario Realiza las
	 * comprobaciones de que los datos sean validos Realiza alguna conversion si
	 * fuera necesaria y los actualiza en las variables.
	 */
	public boolean recopilaDatos() {
		boolean flagFTP, flagBD;
		flagFTP = false;
		flagBD = false;
		System.err.println("Recopilando datos");
		consoleArea.append("Recopilando datos\n");
		if (bFtp) {
			if (txfServer.getText().trim().isEmpty() || txfUser.getText().trim().isEmpty()
					|| txfPass.getPassword().toString().isEmpty() || txfDestino.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Faltan datos para la conexión o guardado de datos",
						"Alguno de los campos están vacios", JOptionPane.ERROR_MESSAGE);
				consoleArea.append("ERROR: Alguno de los campos están vacios\n");
			} else {
				hostFtpData = txfServer.getText().trim();
				usrFtpData = txfUser.getText().trim();
				passFtp = txfPass.getPassword();
				passFtpData = new String(passFtp);
				pathDestino = txfDestino.getText().trim();
				flagFTP = true;
			}
		}
		if (bBd) {
			if (txfServerBd.getText().trim().isEmpty() || txfUserBd.getText().trim().isEmpty()
					|| txfPassBd.getPassword().toString().isEmpty() || txfPort.getText().trim().isEmpty()
					|| txfNameDB.getText().trim().isEmpty() || txfDestino.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Faltan datos para la conexión o guardado de datos",
						"Alguno de los campos están vacios", JOptionPane.ERROR_MESSAGE);
				consoleArea.append("ERROR: Alguno de los campos están vacios\n");
			} else {
				hostBdData = txfServerBd.getText().trim();
				usrBdData = txfUserBd.getText().trim();
				passBd = txfPassBd.getPassword();
				passBdData = new String(passBd);
				nameDbData = txfNameDB.getText().trim();
				pathDestino = txfDestino.getText().trim();
				try {
					port = Integer.parseInt(txfPort.getText().trim());
				} catch (NumberFormatException e) {
					consoleArea.append("ERROR:NumberFormatException\n");
					JOptionPane.showMessageDialog(frame, "El campo Port no es válido (Formato inválido)",
							"El puerto no es válido", JOptionPane.ERROR_MESSAGE);
				}

				flagBD = true;
			}
		}
		// Crea la carpeta especificada en destino si es necesario
		if (bBd == true || bFtp == true) {
			File fileDestino;
			try {
				fileDestino = new File(pathDestino);

				if (fileDestino.exists()) {
					consoleArea.append("La carpeta ya existe no es necesario crearla\n");
				} else {
					consoleArea.append("Carpeta del backup creada!\\n");
					fileDestino.mkdir();
				}
			} catch (NullPointerException e) {
				System.err.println("nullPointerException el File pathDestino");
			}
		}

		// Muestra en consola los campos de BD

		System.err.println("Estado de variables de FTP despues de añadir");
		System.err.println("=====================================");
		System.err.println("HostFTP:" + hostFtpData);
		System.err.println("Usr: " + usrFtpData);
		System.err.println("Pass: " + passFtpData);
		System.err.println("Estado de variables de BD despues de añadir");
		System.err.println("=====================================");
		System.err.println("Host BD:" + hostBdData);
		System.err.println("Usr BD: " + usrBdData);
		System.err.println("Pass BD: " + passBdData);
		System.err.println("Port BD: " + port);
		// Consola en grafico
		consoleArea.append("Estado de variables de FTP despues de añadir\n");
		consoleArea.append("=====================================\n");
		consoleArea.append("HostFTP: " + hostFtpData + "\n");
		consoleArea.append("Usr: " + usrFtpData + "\n");
		consoleArea.append("Pass : " + passFtpData + "\n");
		consoleArea.append("Estado de variables de BD despues de añadir\n");
		consoleArea.append("=====================================\n");
		consoleArea.append("Host BD: " + hostBdData + "\n");
		consoleArea.append("Usr BD: " + usrBdData + "\n");
		consoleArea.append("Pass BD: " + passBdData + "\n");
		consoleArea.append("Port BD: " + port + "\n");
		if (flagFTP == true || flagBD == true) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Metodo que realizara la conexion a traves de FTP Buscara la carpeta "www" y
	 * la descargara en el path de destino indicado.
	 * 
	 * @param host
	 *            recibe el host a conectarse
	 * @param usr
	 *            recibe el user a conectarse
	 * @param pass
	 *            la contraseña del usuario
	 * @param carpetaRemota
	 *            la carpeta a la que se le va a hacer la copia
	 * @throws InterruptedException
	 */

	public void metodoBackupFtp(String host, String usr, String pass, String carpetaRemota, String destino) {
		// *Nota al parecer no es posible con
		// Ftp client comprimir antes de descargar, aun asi se deja el metodo de
		// compresion sin probar

		boolean connected, disconnected;
		try {
			FTPClient clienteFtp = new FTPClient();
			// FTPUtil downFtp = new FTPUtil();
			System.err.println("Datos de conexión\nHost:" + host + "\nUser:" + usr + "\nPass:" + pass);
			clienteFtp.connect(host);
			connected = clienteFtp.login(usr, pass);
			clienteFtp.enterLocalPassiveMode();
			clienteFtp.setFileType(FTP.BINARY_FILE_TYPE);
			if (connected) {
				System.out.println("Conectado al FTP!");
				consoleArea.append("Conectado al FTP!");
				JOptionPane.showMessageDialog(frame, "Conectado al FTP => " + host, "Conectado al FTP!",
						JOptionPane.INFORMATION_MESSAGE);
				consoleArea.append("Conectado al FTP => " + host + "\n");
				System.err.println("Descarga de carpeta Carpeta Remota: " + carpetaRemota + "Destino: " + pathDestino);
				consoleArea.append(
						"Descarga de carpeta Carpeta Remota: " + carpetaRemota + "Destino: " + pathDestino + "\n");
			} else {
				JOptionPane.showMessageDialog(frame,
						"No se ha podido establecer una conexión (Revisa los datos de conexión)",
						"Fallo la conexión al FTP => " + host, JOptionPane.ERROR_MESSAGE);
				consoleArea.append("ERROR:Fallo la conexión al FTP => " + host + "\n");

			}

			clienteFtp.enterLocalPassiveMode();

			FTPFile[] files = clienteFtp.listFiles();

			String[] sfiles = null;
			if (files != null) {
				sfiles = new String[files.length];
				for (int i = 0; i < files.length; i++) {
					System.out.println(sfiles[i] = files[i].getName());

				}

			}
			// Se descarga el archivo
			FTPDown.retrieveDir(clienteFtp, carpetaRemota, destino);
			disconnected = clienteFtp.logout();
			if (disconnected) {
				JOptionPane.showMessageDialog(frame, "Desconectado de " + host, "Logout",
						JOptionPane.INFORMATION_MESSAGE);
				consoleArea.append("Desconectado de " + host + "\n");

			}
			clienteFtp.disconnect();
		} catch (SocketException e) {
			JOptionPane.showMessageDialog(frame, "Fallo la conexión al FTP => " + host, "Error del servidor",
					JOptionPane.ERROR_MESSAGE);
			consoleArea.append("ERROR:SocketException\n");
		} catch (IOException e) {
			consoleArea.append("ERROR:IOException\n");
		}
		consoleLogCreator(bFtp, bBd);
	}

	/**
	 * Método basado en la ejecucion de mysqldump (Por lo que es necesario que este
	 * instalado) Realiza una copia de la base de datos y la guarda en la carpeta de
	 * destino.
	 * 
	 * @param host
	 *            Host al que se conecta
	 * 
	 * @param usr
	 *            usuario de la base de datos
	 * @param pass
	 *            contraseña de la base de datos
	 * @param puerto
	 *            puerto por el que se conecta
	 */
	public void metodoBackupBd(String host, String usr, String pass, String nombreDb, int port, String destino) {
		System.err.println("Metodo Bd");
		consoleArea.append("Metodo BD\n");
		String portF = port + "";
		// Ejemplo comando /Applications/XAMPP/bin/mysqldump --host=localhost
		// --port=1500 --user=root --password= PruebaDBPrograma >
		// /Users/imac/Desktop/Pruebas/copia24_10_2017localhost.sql
		String comando = pathMysql + "mysqldump --host=" + host + " --port=" + portF + " --user=" + usr + " --password="
				+ pass + " " + nombreDb;

		try {
			if (pathMysql.trim().equals(" ")) {
				JOptionPane.showMessageDialog(frame, "El path para ejecutar Mysqldump está vacío", "Erro Path Mysql",
						JOptionPane.ERROR_MESSAGE);
				consoleArea.append("ERROR: Path Mysql vacío\n");
			} else {
				Process p = Runtime.getRuntime().exec(comando);

				System.err.println("Ejecutando comando: " + comando);
				consoleArea.append("Ejecutando comando: " + comando + "\n");
				InputStream is = p.getInputStream();
				FileOutputStream fos = new FileOutputStream(destino + "/copia" + fecha + nombreDb + ".sql");
				byte[] buffer = new byte[1000];

				int leido = is.read(buffer);
				while (leido > 0) {

					fos.write(buffer, 0, leido);
					leido = is.read(buffer);
				}
				is.close();
				fos.close();
				int infoProceso = p.waitFor();

				if (infoProceso == 0) {
					JOptionPane.showMessageDialog(frame,
							"Se ha completado correctamente el Backup de la DB de " + hostBdData + "!",
							"Backup Completo", JOptionPane.INFORMATION_MESSAGE);
					consoleArea.append("BackUp Completo!\n");
				} else {
					JOptionPane.showMessageDialog(frame, "No se pudo crear el backup", "Backup Error",
							JOptionPane.INFORMATION_MESSAGE);
					consoleArea.append("ERROR:BackUp error\n");
				}
				System.err.println(pathMysql + "mysqldump --host=" + host + " --port=" + portF + " --user=" + usr
						+ " --password=" + pass + " " + nombreDb + " > " + destino + "/copia" + fecha + host + ".sql");
				p.destroy();
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame,
					"No se ha podido inicar el backup (Carpeta de destino no existe) o revisa el path de Mysql ",
					"No se pudo ejecutar la acción ", JOptionPane.ERROR_MESSAGE);
			consoleArea.append("No se ha podido inicar el backup (Carpeta de destino no existe) \n");
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(frame, "No se pudo ejecutar la acción", "Operación interrumpida!",
					JOptionPane.ERROR_MESSAGE);
			consoleArea.append("ERROR:Operación interrumpida!\n");
		}
		consoleLogCreator(bFtp, bFtp);
	}

	/**
	 * Obtiene la informacion de la miniconsola y genera un log con la misma en la
	 * carpeta destino
	 * 
	 * @param bckupFtp
	 * @param bckupBd
	 *            los boolean especifican si es necesario generar el log (Es decir
	 *            que se ha ejecutado alguno de los bakup)
	 * @return devuelve true o false si se completa correctamente o no
	 */
	public boolean consoleLogCreator(boolean bckupFtp, boolean bckupBd) {
		consoleArea.append("Generando log de la consola!\n");
		if (bckupFtp == true || bckupBd == true) {
			File log = new File(pathDestino + "/log.txt");
			try {
				PrintWriter logWriter = new PrintWriter(log);
				logWriter.write(consoleArea.getText());
				logWriter.close();
				return true;
			} catch (FileNotFoundException e) {
				consoleArea.append("ERROR:Archivo no encontrado\n");
				return false;
			}
		}
		return false;

	}

	/**
	 * Action listener que recoge el evento de los botones destino,guardar y gestor
	 * y divide sus acciones
	 */
	public ActionListener ac = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnDestino) {
				System.err.println("Action btnDestino");
				JFileChooser chooserDestino = new JFileChooser();
				chooserDestino.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int respuesta = chooserDestino.showSaveDialog(frame);
				if (respuesta == JFileChooser.APPROVE_OPTION) {
					System.err.println("Destino aceptado");
					consoleArea.append("Destino aceptado\n");
					txfDestino.setText(chooserDestino.getSelectedFile().getAbsolutePath());
				}
			}
			if (e.getSource() == btnGuardar) {
				// Añade los datos actuales al archivo de hosts guardados
				System.err.println("Action btnGuardar");
				int opcion;
				opcion = JOptionPane.showConfirmDialog(frame, "¿Guardar los datos actuales?", "Agregar",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				switch (opcion) {
				case JOptionPane.OK_OPTION:
					System.err.println("Confirma agregar");
					consoleArea.append("Guardado aceptado\n");
					// Arranca el método de guardado
					try {
						guardarDatos();
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(frame, "No hay datos que guardar",
								"No se puede guardar un host vacío", JOptionPane.WARNING_MESSAGE);
					}

					break;
				case JOptionPane.NO_OPTION:
					System.err.println("Cancelado");
					consoleArea.append("Guardado cancelado\n");

					break;
				default:

					break;
				}
			}
			if (e.getSource() == btnGestor) {
				// Gestiona la seleccion de de hosts ya guardados para cargarlos y su
				// eliminación
				System.err.println("Action btnGestor");
				ArrayList<String> listaNombres;
				listaNombres = cargarDatosModal();
				modalGestor = new ModalGestorHost(frame, true);
				modalGestor.setAlwaysOnTop(true);
				modalGestor.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
				modalGestor.setResizable(false);
				modalGestor.setLocationRelativeTo(null);
				modalGestor.jListConf(listaNombres);
				modalGestor.btnEliminar.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						System.err.println("Ejecutando eliminación");
						String seleccionEliminar = modalGestor.getSelectedHost();
						int lineaEliminar = modalGestor.listHosts.getSelectedIndex();
						if (lineaEliminar < 0) {
							JOptionPane.showMessageDialog(modalGestor, "No se ha eliminado",
									"No hay datos que eliminar", JOptionPane.WARNING_MESSAGE);
						} else {
							System.out.println("Eliminación de " + seleccionEliminar);

							modalGestor.modelo.remove(lineaEliminar); // Actualiza la información de la lista quitando
																		// el
																		// elemento

							eliminaDatos(hostsData, lineaEliminar);
						}

					}
				});
				modalGestor.btnConfirm.addActionListener(new ActionListener() { // Gestión de selección de el host

					@Override
					public void actionPerformed(ActionEvent e) {
						String hostSeleccionado = modalGestor.getSelectedHost();

						System.out.println("Seleccionando Host");
						System.out.println("Seleccionado" + hostSeleccionado);
						actualizaDatos(hostSeleccionado);
						modalGestor.setVisible(false);
					}
				});

				modalGestor.setVisible(true);

			}

		}
	};

	/**
	 * @deprecated Timer para realizar una pausa minima y permitir que la consola se
	 *             pueda actualizar durante la ejecucion de un backup
	 * 
	 * @param active
	 * 
	 */
	public void timerCreator(boolean active) { // usar el timer para intentar que se actualice la consola
												// "Simultaneamente" mientras se realizan los backup (No funciona

		timer = new Timer(4000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.err.println("Ejecuto timer!");
				consoleArea.append("----Timer----\n");

				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					System.err.println("ERROR:SleepError");
					consoleArea.append("ERROR:SleepError\n");

				}
			}
		});
		if (active) {
			timer.start();
		} else {
			timer.stop();
		}

	}

	/**
	 * Método que guarda los datos que introduce el usuario y los guarda en el
	 * archivo HOSTS.data
	 */
	public void guardarDatos() {

		System.err.println("Metodo guardar datos");
		boolean correcto = false;
		try {
			hostsData = new File("HOSTS.data");
			if (hostsData.exists()) {
				System.err.println("El archivo ya existe, no es necesario crear");
				consoleArea.append("El archivo HOSTS.data ya existe, no es necesario crear\n");
				bFtp = true;
				bBd = true;
				correcto = recopilaDatos();
			} else {
				System.err.println("El archivo no existe, se creará");
				consoleArea.append("El archivo HOSTS.data no existe, se creará\n");

				hostsData.createNewFile();
				guardarDatos();

			}
			if (correcto) {
				BufferedWriter bw = new BufferedWriter(new FileWriter(hostsData, true));
				System.err.println(hostFtpData + "," + usrFtpData + "," + passFtpData);
				bw.write(hostFtpData + "," + usrFtpData + "," + passFtpData);
				bw.write("," + hostBdData + "," + usrBdData + "," + passBdData + "," + nameDbData + "," + port + "\n");
				bw.close();
				bFtp = false;
				bFtp = false;
			}

		} catch (IOException e) {
			System.err.println("IOEXCEPTION");
			consoleArea.append("IOEXCEPTION\n");

		}
	}

	/**
	 * Método que gestiona la carga
	 * 
	 * @return devuelve la lista de nombre que puede usar la lista del modal
	 */
	public ArrayList<String> cargarDatosModal() {

		String tempDatos = "";
		int contPos = 0;
		hostsData = new File("HOSTS.data");

		listaObjetosHost = null;
		if (hostsData.exists()) {
			nLineas = 0;
			try {
				Scanner lector = new Scanner(hostsData);
				listaNombresHost = new ArrayList<String>();

				while (lector.hasNext()) {
					tempDatos += lector.nextLine() + ",";
					nLineas++;
					// Prueba System.out.println("bucle while " + tempDatos); // Corregida la
					// lectura por comas
				}
				loadedData = tempDatos.split(",");
				System.out.println("Tamaño load data: " + loadedData.length);
				listaObjetosHost = new HostsData[loadedData.length];
				System.err.println(listaObjetosHost.length);
				// Crea un arraylist
				int contDatos = 0;

				for (int i = nLineas; i < loadedData.length; i++) {
					contDatos++;

					if (contDatos == 7) {

						listaObjetosHost[i] = new HostsData();
						System.out.println("Antes de excepción pos" + contPos);
						System.out.println("Antes de excepción i" + i);
						listaObjetosHost[i].setHostFtp(loadedData[contPos]);
						contPos++;
						listaObjetosHost[i].setUsrFtp(loadedData[contPos]);
						contPos++;
						listaObjetosHost[i].setPassFtp(loadedData[contPos]);
						contPos++;
						listaObjetosHost[i].setHostBd(loadedData[contPos]);
						contPos++;
						listaObjetosHost[i].setUsrBd(loadedData[contPos]);
						contPos++;
						listaObjetosHost[i].setPassBd(loadedData[contPos]);
						contPos++;
						listaObjetosHost[i].setNameBd(loadedData[contPos]);
						contPos++;
						listaObjetosHost[i].setPortBd(loadedData[contPos]);
						contPos++;

						System.out.println("Linea host: " + contPos);
						System.out.println("Host FTP cargado: " + listaObjetosHost[i].getHostFtp());
						System.out.println("Usr FTP cargado: " + listaObjetosHost[i].getUsrFtp());
						System.out.println("Pass FTP cargado: " + listaObjetosHost[i].getPassFtp());
						System.out.println("========================================");
						System.out.println("Host BD cargado: " + listaObjetosHost[i].getHostBd());
						System.out.println("USR BDcargado: " + listaObjetosHost[i].getUsrBd());
						System.out.println("Pass BDcargado: " + listaObjetosHost[i].getPassBd());
						System.out.println("Port BD cargado: " + listaObjetosHost[i].getPortBd());
						listaNombresHost.add("FTP: " + listaObjetosHost[i].getHostFtp() + "| BD: "
								+ listaObjetosHost[i].getHostBd());
						contDatos = 0;

					}

				}
				contPos = 0;

				System.out.println("listade objetos:" + listaObjetosHost.length);
				for (int j = 0; j < listaNombresHost.size(); j++) {
					System.out.println("Nombres:" + listaNombresHost.get(j));
				}
				lector.close();

			} catch (FileNotFoundException e) {
				System.err.println("No se pudo abrir el archivo");
			}

		}
		return listaNombresHost;
	}

	/**
	 * Método que actualiza los datos de los campos del formulario según el host
	 * escogido
	 * 
	 * @param hostSeleccionado
	 *            Especifica el host que se desea cargar los datos
	 */
	public void actualizaDatos(String hostSeleccionado) {

		try {
			hostSeleccionado = hostSeleccionado.substring(5).trim();
		} catch (StringIndexOutOfBoundsException e) {
			JOptionPane.showMessageDialog(modalGestor, "Selecciona uno de lo host de la lista o pulsa \"Cancelar\"",
					"No se ha selecionado ningun host", JOptionPane.WARNING_MESSAGE);
			consoleArea.append("ERROR: No se ha seleccionado ningun host\n");
		}

		System.err.println("DATO: " + hostSeleccionado);
		int contDatos = 0;

		for (int i = nLineas; i < loadedData.length; i++) {
			contDatos++;

			if (contDatos == 7) {
				System.out.println(contDatos);

				contDatos = 0;
				System.out.println(
						"FTP a comparar " + listaObjetosHost[i].getHostFtp() + "host Comparado " + hostSeleccionado);
				if (hostSeleccionado.startsWith(listaObjetosHost[i].getHostFtp())
						&& hostSeleccionado.endsWith(listaObjetosHost[i].getHostBd())) {
					System.out.println("Coinsidensia!"); // La actualización de datos se realiza aquí

					txfServer.setText(listaObjetosHost[i].getHostFtp());
					txfUser.setText(listaObjetosHost[i].getUsrFtp());
					txfPass.setText(listaObjetosHost[i].getPassFtp());
					/////////////////
					txfServerBd.setText(listaObjetosHost[i].getHostBd());
					txfUserBd.setText(listaObjetosHost[i].getUsrBd());
					txfPassBd.setText(listaObjetosHost[i].getPassBd());
					txfNameDB.setText(listaObjetosHost[i].getNameBd());
					txfPort.setText(listaObjetosHost[i].getPortBd());
					chkFtp.setSelected(true);
					chkBd.setSelected(true);
				} else {
					System.out.println("No coinside!");
				}

				System.out.println("Host FTP cargado: " + listaObjetosHost[i].getHostFtp());
				System.out.println("Usr FTP cargado: " + listaObjetosHost[i].getUsrFtp());
				System.out.println("Pass FTP cargado: " + listaObjetosHost[i].getPassFtp());
				System.out.println("========================================");
				System.out.println("Host BD cargado: " + listaObjetosHost[i].getHostBd());
				System.out.println("USR BDcargado: " + listaObjetosHost[i].getUsrBd());
				System.out.println("Pass BDcargado: " + listaObjetosHost[i].getPassBd());
				System.out.println("Name BD cargado: " + listaObjetosHost[i].getNameBd());
				System.out.println("Port BD cargado: " + listaObjetosHost[i].getPortBd());

			}
		}

	}

	/**
	 * Método que obtiene el indice seleccionado de la lista de hosts, busca en el
	 * archivo y lo elimina.
	 * 
	 * @param f
	 *            archivo de lectura
	 * @param indice
	 *            indice del host a eliminar
	 */
	public void eliminaDatos(File f, int indice) {
		System.out.println("Metodo elimina datos");
		File archivoTemp = new File("Temp.data");
		try {
			BufferedReader bReader = new BufferedReader(new FileReader(f));
			BufferedWriter bWriter = new BufferedWriter(new FileWriter(archivoTemp));
			// String host=hostSel.substring(5).trim();
			String lineaLeida;
			int cont = 0;
			while ((lineaLeida = bReader.readLine()) != null) {
				// System.out.println("Linea Leida "+lineaNoSpace+"\n Linea a eliminar "+
				// indice);

				if (cont != indice)
					bWriter.write(lineaLeida + "\n");

				cont++;
			}
			bWriter.close();
			bReader.close();
			String nombreArchivo = f.getName();
			System.out.println(nombreArchivo);
			archivoTemp.renameTo(f);
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}

	}

	/**
	 * Configura un path predeterminado para mysqldump según el sistema operativo en
	 * el que se ejecute el programa
	 * 
	 * @param sistema
	 *            Obtiene el string perteneciente al sistema
	 */
	public void configMysql(String sistema) {
		if (sistema.startsWith("mac")) {

			pathMysql = "/Applications/XAMPP/bin/";
		}
		if (sistema.startsWith("win")) {

			pathMysql = "C:\\Program Files\\MySQL\\";
		}
		if (sistema.startsWith("lin")) {
			pathMysql = "/usr/bin/";
		}
		System.err.println("Predeterminada: " + pathMysql);
	}

}
