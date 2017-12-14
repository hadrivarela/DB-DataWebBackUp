import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 * Clase que crea un modal en el que se gestionará todo el apartado de los hosts
 * guardados en archivo Su selección para uso datos sobre los que contiene el
 * archivo y opción para su eliminación.
 * 
 * @author Hadri
 * 
 */
@SuppressWarnings("serial")
public class ModalGestorHost extends JDialog implements ActionListener {
	// Elementos de la ventana
	private final JPanel contentPanel = new JPanel();
	public JButton btnConfirm;
	private JButton btnCancel;
	public JButton btnEliminar;
	public JList<String> listHosts;
	public DefaultListModel<String> modelo;

	// Array en que se guardaran los datos para posteriormente mostrar
	/**
	 * Launch the application.
	 * 
	 * public static void main(String[] args) { try { ModalGestorHost dialog = new
	 * ModalGestorHost(); dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	 * dialog.setVisible(true); } catch (Exception e) { e.printStackTrace(); } }
	 */
	/**
	 * Create the dialog.
	 */
	public ModalGestorHost(JFrame form1, boolean modal) {
		super(form1, modal);
		setBounds(100, 100, 500, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel lblListaDeHosts = new JLabel("Lista de Hosts guardados:");

		JScrollPane scrollPane = new JScrollPane();

		JLabel lblSeleccionado = new JLabel("Seleccionado:");

		btnEliminar = new JButton("Eliminar");
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup().addGroup(gl_contentPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPanel.createSequentialGroup().addContainerGap().addComponent(lblListaDeHosts))
						.addGroup(gl_contentPanel.createSequentialGroup().addGap(19).addGroup(gl_contentPanel
								.createParallelGroup(Alignment.LEADING).addComponent(lblSeleccionado)
								.addGroup(gl_contentPanel.createSequentialGroup()
										.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 351,
												GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED).addComponent(btnEliminar)))))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		gl_contentPanel.setVerticalGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPanel
				.createSequentialGroup()
				.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup().addContainerGap()
								.addComponent(lblListaDeHosts).addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(lblSeleccionado)
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 174, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup().addGap(90).addComponent(btnEliminar)))
				.addContainerGap()));

		listHosts = new JList<>();

		modelo = new DefaultListModel<String>();
		listHosts.setModel(modelo);
		listHosts.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {// Logica de cambio de elemento seleccionado
				String s = "Seleccionado: ";
				if (listHosts.isSelectionEmpty()) {
					s += "Ninguno";
				} else {
					for (Object item : listHosts.getSelectedValuesList()) {
						s += (String) item + " ";
					}
					lblSeleccionado.setText(s);
				}

			}
		});
		listHosts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		scrollPane.setViewportView(listHosts);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnConfirm = new JButton("Selecionar");
				btnConfirm.setActionCommand("OK");
				btnConfirm.addActionListener(this);
				buttonPane.add(btnConfirm);
				getRootPane().setDefaultButton(btnConfirm);
			}
			{
				btnCancel = new JButton("Cancelar");
				btnCancel.setActionCommand("Cancel");
				btnCancel.addActionListener(this);
				buttonPane.add(btnCancel);
			}
		}
	}

	/**
	 * Método que configura los nombres que se mostraran en el Jlist a partir de la
	 * lista de objetos del formulario principal
	 * @param nombresHost la lista de nombres de los hosts desde el formulario principal
	 */
	public void jListConf(ArrayList<String> nombresHost) {
		System.err.println("JLISTARRAY");

		for (int i = 0; i < nombresHost.size(); i++) {
			
			modelo.addElement(nombresHost.get(i));
		}

	}

	/**
	 * Devuelve el host seleccionado en la lista
	 * 
	 * @return String del host seleccionado
	 */
	public String getSelectedHost() {
		String selected = "";
		for (Object item : listHosts.getSelectedValuesList()) {
			selected += (String) item + " ";
		}
		return selected;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnCancel) {
			this.setVisible(false);
		}

	}

}
