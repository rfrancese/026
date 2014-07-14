package whoareyou.altervista.org;

public class Conversazione {

	private String identificativo, immagine, username, messaggiDaLeggere;


	public Conversazione(String identificativo, String immagine, String username, String messaggiDaLeggere) {

		super();

		this.identificativo = identificativo;
		this.immagine = immagine;
		this.username = username;
		this.messaggiDaLeggere = messaggiDaLeggere;

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

	public String getMessaggiDaLeggere() {
		return messaggiDaLeggere;
	}

	public void setMessaggiDaLeggere(String messaggiDaLeggere) {
		this.messaggiDaLeggere = messaggiDaLeggere;
	}

}