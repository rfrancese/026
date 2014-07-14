package whoareyou.altervista.org;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {

	private EditText  username=null;
	private EditText  password=null;
	private Button login;
	private Button registrati;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		username = (EditText)findViewById(R.id.editText1);
		password = (EditText)findViewById(R.id.editText2);
		login = (Button)findViewById(R.id.button1);
		registrati = (Button)findViewById(R.id.button2);


		login.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){

				if((username.getText().toString()==null || username.getText().toString().equals("")) && (password.getText().toString()==null || password.getText().toString().equals(""))){

					Toast toast=Toast.makeText(getApplicationContext(),"Attenzione, inserire identificativo e password.",Toast.LENGTH_LONG);
					toast.show();

				} else if( username.getText().toString().length() >= 21 || password.getText().toString().length() >= 11 ) {

					Toast.makeText(getApplicationContext(),"Attenzione! Inserire max 20 caratteri per identificativo e username. Inserire max 10 caratteri per password." ,Toast.LENGTH_LONG).show();

				} else{
					Authentification login=new Authentification();
					String s=username.getText().toString();
					String p=password.getText().toString();

					if (Networking.isNetworkAvailable(getApplicationContext())) 
						login.execute(s, p);
					else
						Toast.makeText(getApplicationContext(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

				}
			}
		});

		registrati.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){

				Intent intent = new Intent(getApplicationContext(),Registrazione.class);
				startActivity(intent);
			}
		});
	}

	private class Authentification extends AsyncTask<String,String,String>  {

		private ProgressDialog dialog = new ProgressDialog(
				MainActivity.this);
		private String id=null;
		private String psw=null;
		InputStream is = null;
		String result = null;

		private String i,u,p,n,e,c,im,d,s,con;


		protected void onPreExecute() {

			dialog.setCancelable(true);
			dialog.setTitle("Caricamento");
			dialog.setMessage("Accesso in corso...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			id=params[0];
			psw=params[1];

			String url_select = "http://whoareyou.altervista.org/accesso.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
			HttpPost httpPost = new HttpPost(url_select);

			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("identif",id));
			param.add(new BasicNameValuePair("pass",psw));

			try {
				httpPost.setEntity(new UrlEncodedFormEntity(param));

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();

				// read content
				is = httpEntity.getContent();

			} catch (Exception e) {

			}
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();


			} catch (Exception e) {

			}

			return result;



		}

		protected void onPostExecute(String res) {

			JSONArray jArray;
			try {
				jArray = new JSONArray(res);

				if(jArray.length()==1){	

					for (int j = 0; j <jArray.length(); j++){

						JSONObject json_data = jArray.getJSONObject(j);
						i=json_data.getString("identificativo");
						u=json_data.getString("username");
						p=json_data.getString("password");
						n=json_data.getString("numero");
						e=json_data.getString("email");
						c=json_data.getString("citta");
						im=json_data.getString("immagine");
						d=json_data.getString("data_nascita");
						s=json_data.getString("sesso");
						con=json_data.getString("dati_condivisi");

					}

					this.dialog.dismiss();


					Intent intent = new Intent(getApplicationContext(), MyActionBarActivity.class);

					intent.putExtra("id", i.toString());
					intent.putExtra("us", u.toString());
					intent.putExtra("ps", p.toString());
					intent.putExtra("num", n.toString());
					intent.putExtra("em", e.toString());
					intent.putExtra("cit", c.toString());
					intent.putExtra("img", im.toString());
					intent.putExtra("dn", d.toString());
					intent.putExtra("sex", s.toString());
					intent.putExtra("cond", con.toString());

					startActivity(intent);					

				}
				else{

					JSONObject json_data = jArray.getJSONObject(0);

					json_data.getString("errore");

					this.dialog.dismiss();
					Toast toast=Toast.makeText(getApplicationContext(),"Accesso negato. Contollare identificativo e password.",Toast.LENGTH_LONG);
					toast.show();
				} 
			}
			catch (JSONException e) {

			}

		}

	}
}
