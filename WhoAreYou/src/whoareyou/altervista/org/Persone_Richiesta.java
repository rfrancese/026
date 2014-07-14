package whoareyou.altervista.org;

public class Persone_Richiesta {

	private String identificativo, immagine, username;


	public Persone_Richiesta(String identificativo, String immagine, String username) {

		super();

		this.identificativo = identificativo;
		this.immagine = immagine;
		this.username = username;

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

}