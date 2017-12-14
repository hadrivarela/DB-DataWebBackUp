/**
 * 
 * @author Hadri Clase para crear objetos usables por el ArrayList y
 *         posteriormente por el JList. Estructura los datos.
 */
public class HostsData {
	private String hostFtp, usrFtp, passFtp, hostBd, usrBd, passBd,nameBd, portBd;

	
	public HostsData() {
		//super();
	}
	public String getNameBd() {
		return nameBd;
	}

	public void setNameBd(String nameBd) {
		this.nameBd = nameBd;
	}
	public String getHostFtp() {
		return hostFtp;
	}

	public void setHostFtp(String hostFtp) {
		this.hostFtp = hostFtp;
	}

	public String getUsrFtp() {
		return usrFtp;
	}

	public void setUsrFtp(String usrFtp) {
		this.usrFtp = usrFtp;
	}

	public String getPassFtp() {
		return passFtp;
	}

	public void setPassFtp(String passFtp) {
		this.passFtp = passFtp;
	}

	public String getHostBd() {
		return hostBd;
	}

	public void setHostBd(String hostBd) {
		this.hostBd = hostBd;
	}

	public String getUsrBd() {
		return usrBd;
	}

	public void setUsrBd(String usrBd) {
		this.usrBd = usrBd;
	}

	public String getPassBd() {
		return passBd;
	}

	public void setPassBd(String passBd) {
		this.passBd = passBd;
	}

	public String getPortBd() {
		return portBd;
	}

	public void setPortBd(String portBd) {
		this.portBd = portBd;
	}

}
