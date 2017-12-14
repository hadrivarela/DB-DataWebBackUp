import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import java.awt.SystemColor;
import javax.swing.JTextField;

/**
 * 
 * @author Hadri Formulario modal para la configuración adicional del path en el
 *         que se encuentra Mysql dump para que el programa pueda hacer uso del
 *         mismo.
 */
@SuppressWarnings("serial")
public class MysqlModal extends JDialog {

	private final JPanel contentPanel = new JPanel();
	public JTextField txfPathMysql;
	public JButton okButton;
	private String pathMysql;
	DBDataBackup dataBack;

	/**
	 * Crea la ventana de dialogo
	 * 
	 * @param b
	 *            especifica si es o no modal
	 * @param frame
	 *            la referencia al formulario principal
	 */
	public MysqlModal(JFrame frame, boolean b) {
		super(frame, b);
		setModal(true);
		setBounds(100, 100, 384, 215);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel lblIndicaElPath = new JLabel("Indica el path de Mysql");

		JLabel lblPathInstalacinmysql = new JLabel("Path Mysql:");

		JTextArea txtrIndicaDondeSe = new JTextArea();
		txtrIndicaDondeSe.setBackground(SystemColor.window);
		txtrIndicaDondeSe.setEditable(false);
		txtrIndicaDondeSe.setLineWrap(true);
		txtrIndicaDondeSe.setText(
				"Indica donde se encuentra la carpeta de mysql \"bin\" en tu equipo para poder realizar la copia de BD utilizando la \nherramienta “mysqldump”.");

		txfPathMysql = new JTextField();
		txfPathMysql.setColumns(10);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel
				.setHorizontalGroup(
						gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup().addGap(7)
										.addComponent(txtrIndicaDondeSe, GroupLayout.PREFERRED_SIZE, 341,
												Short.MAX_VALUE)
										.addContainerGap())
								.addGroup(Alignment.TRAILING,
										gl_contentPanel.createSequentialGroup().addContainerGap()
												.addComponent(lblPathInstalacinmysql).addGap(18)
												.addComponent(txfPathMysql, GroupLayout.PREFERRED_SIZE, 191,
														GroupLayout.PREFERRED_SIZE)
												.addGap(87))
								.addGroup(gl_contentPanel.createSequentialGroup().addGap(109)
										.addComponent(lblIndicaElPath).addContainerGap(120, Short.MAX_VALUE)));
		gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPanel
				.createSequentialGroup().addComponent(lblIndicaElPath).addGap(18)
				.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE).addComponent(lblPathInstalacinmysql)
						.addComponent(txfPathMysql, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE))
				.addGap(18).addComponent(txtrIndicaDondeSe, GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)));
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Aceptar");
				okButton.setActionCommand("OK");

				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}

	}

	/**
	 * 
	 * @return devuelve el path de mysql
	 */
	public String getPathMysql() {
		return pathMysql;
	}

	/**
	 * Carga el path desde el formulario principal
	 * 
	 * @param pathMysql
	 */
	public void setPathMysql(String pathMysqlFrame) {

		this.pathMysql = pathMysqlFrame;
		txfPathMysql.setText(pathMysqlFrame);

		System.err.println("Setting" + pathMysql);

	}

}
