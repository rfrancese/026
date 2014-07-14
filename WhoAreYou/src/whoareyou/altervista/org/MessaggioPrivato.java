package whoareyou.altervista.org;

public class MessaggioPrivato {

	private String tipoMessaggioPrivato, immagine, messaggio, data_ora, distanza, statoMessaggio;


	public MessaggioPrivato(String tipoMessaggioPrivato, String immagine, String messaggio, String data_ora, String distanza, String statoMessaggio) { //String identificativo, String username,
		super();

		this.tipoMessaggioPrivato = tipoMessaggioPrivato;

		this.immagine = immagine;
		this.messaggio = messaggio;
		this.data_ora = data_ora;
		this.distanza = distanza;

		this.statoMessaggio = statoMessaggio;
	}


	public String getTipoMessaggioPrivato() {
		return tipoMessaggioPrivato;
	}

	public void setTipoMessaggioPrivato(String tipoMessaggioPrivato) {
		this.tipoMessaggioPrivato = tipoMessaggioPrivato;
	}

	public String getImmagine() {
		return immagine;
	}

	public void setImmagine(String immagine) {
		this.immagine = immagine;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
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


	public String getStatoMessaggio() {
		return statoMessaggio;
	}

	public void setStatoMessaggio(String statoMessaggio) {
		this.statoMessaggio = statoMessaggio;
	}

}

