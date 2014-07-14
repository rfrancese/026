package whoareyou.altervista.org;

public class UtenteVicinoAMe {

	private String identificativo, immagine, username, data_ora, distanza;


	public UtenteVicinoAMe(String identificativo, String immagine, String username, String data_ora, String distanza) {
		super();

		this.identificativo = identificativo;
		this.immagine = immagine;
		this.username = username;
		this.data_ora = data_ora;
		this.distanza = distanza;
	}

	public String getIdentificativo() {
		return identificativo;
	}

	public void setIdentificativo(String identificativo) {
		this.identificativo = identificativo;
	}

	public String getImmagine() {
		return immagine;
	}

	public void setImmagine(String immagine) {
		this.immagine = immagine;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getData_ora() {
		return data_ora;
	}

	public void setData_ora(String data_ora) {
		this.data_ora = data_ora;
	}

	public String getDistanza() {
		return distanza;
	}

	public void setDistanza(String distanza) {
		this.distanza = distanza;
	}

}
