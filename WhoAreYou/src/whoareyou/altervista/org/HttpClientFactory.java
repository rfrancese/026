package whoareyou.altervista.org;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;


//Classe per risolvere il problema del multithreading
public class HttpClientFactory {

	private static DefaultHttpClient client;

	public synchronized static DefaultHttpClient getThreadSafeClient() {

		if (client != null)
			return client;

		client = new DefaultHttpClient();

		ClientConnectionManager mgr = client.getConnectionManager();

		HttpParams params = client.getParams();
		client = new DefaultHttpClient( new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params );

		return client;
	} 

}
