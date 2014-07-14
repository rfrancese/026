package whoareyou.altervista.org;

import java.util.Date;

public class DataNascita {

	private Date datanasc;

	public DataNascita(){
		datanasc=null;
	}

	public void setDataNascita(Date d){

		datanasc=d;
	}

	public Date getDataNascita(){

		return datanasc;
	}
}
