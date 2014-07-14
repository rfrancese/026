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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class ProfiloUtenteVicinoFragment extends Fragment {


	private View v;
	MyActionBarActivity myActionBarActivity;
	private String id, us, num, em, city, img, dn, sex, statoAmici,  id_ric, id_rich, img_rich;
	private TextView  nick, username,identificativo, numero, email, citta, dataNascita;
	private ImageView immagine;
	private RadioButton rb1, rb2;
	private ImageButton inviaMessaggio,eliminaAmicizia;
	private Button inviaRifiutaRichiesta;


	private ConversazionePrivataFragment conversazionePrivataFragment = null;
	private ProfiloUtenteVicinoFragment profiloUtenteVicinoFragment = this;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle dati=this.getArguments();

		id=dati.getString("id");
		us=dati.getString("us");
		num=dati.getString("num");
		em=dati.getString("em");
		city=dati.getString("city");
		img=dati.getString("img");
		dn=dati.getString("data");
		sex=dati.getString("sex");
		statoAmici=dati.getString("statoAmici");
		id_ric=dati.getString("ric"); 
		id_rich=dati.getString("rich");
		img_rich=dati.getString("img_rich");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		super.onCreateView(inflater, container, savedInstanceState);
		v = inflater.inflate(R.layout.fragment_profilo_utente_vicino, container, false);

		nick = (TextView)v.findViewById(R.id.textView1);
		username = (TextView)v.findViewById(R.id.TextView20);
		identificativo = (TextView)v.findViewById(R.id.textView9);
		numero = (TextView)v.findViewById(R.id.textView13);
		email = (TextView)v.findViewById(R.id.textView14);
		citta = (TextView)v.findViewById(R.id.textView15);
		dataNascita = (TextView)v.findViewById(R.id.textView16);
		immagine=(ImageView)v.findViewById(R.id.imageView1);
		rb1 = (RadioButton)v.findViewById(R.id.radioButton1);
		rb2 = (RadioButton)v.findViewById(R.id.radioButton2);
		inviaMessaggio=(ImageButton)v.findViewById(R.id.imageButton1);
		eliminaAmicizia=(ImageButton)v.findViewById(R.id.imageButton2);
		inviaRifiutaRichiesta=(Button)v.findViewById(R.id.button1);


		nick.setText(us);
		username.setText(us);
		identificativo.setText(id);
		numero.setText(num);
		email.setText(em);
		citta.setText(city);
		dataNascita.setText(dn);

		rb1.setClickable(false);
		rb2.setClickable(false);

		if(img.equals("")){
			//rimane l'immagine di default per gli utenti che non hanno una foto profilo
		}
		else{
			byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length); 
			immagine.setImageBitmap(decodedByte);
		}
		if(sex.equals("M")){
			rb1.setChecked(true);
		}
		else{
			rb2.setChecked(true);
		}

		if(statoAmici.equals("0")){ //amicizia cancellata

			inviaMessaggio.setVisibility(View.INVISIBLE);
			eliminaAmicizia.setVisibility(View.INVISIBLE);

			inviaRifiutaRichiesta.setText("Invia Richiesta");
			inviaRifiutaRichiesta.setClickable(true);
			inviaRifiutaRichiesta.setVisibility(View.VISIBLE);

			inviaRifiutaRichiesta.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view){

					String setStatoAmicizia="2";

					UpdateStato up=new UpdateStato();
					if (Networking.isNetworkAvailable(getActivity())) {

						up.execute(setStatoAmicizia, id_ric, id_rich);

						inviaRifiutaRichiesta.setText("Richiesta Inviata");
						inviaRifiutaRichiesta.setClickable(false);

					} else
						Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

				}
			});
		}
		else if(statoAmici.equals("1")){ //richiesta valida, amici

			inviaRifiutaRichiesta.setVisibility(View.INVISIBLE);
			inviaMessaggio.setVisibility(View.VISIBLE);
			eliminaAmicizia.setVisibility(View.VISIBLE);

			inviaMessaggio.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view){

					// collegamento alla conversazione 

					NuovaConversazionePrivataTask nc = new NuovaConversazionePrivataTask();

					if (Networking.isNetworkAvailable(getActivity())) 
						nc.execute(id_ric, id_rich); // amico o sconosciuto, utente
					else
						Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

				}
			});

			eliminaAmicizia.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view){

					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

					builder
					.setTitle("Eliminare Amicizia")
					.setMessage("Sei sicuro di voler rimuovere dagli amici l'utente: " + id_ric + "? \nSara' eliminata anche la vostra conversazione!")			
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {

							dialog.cancel();

							EliminazioneAmicoTask eliminazioneAmicoTask = new EliminazioneAmicoTask();

							if (Networking.isNetworkAvailable(getActivity())) 
								eliminazioneAmicoTask.execute(id_ric);
							else
								Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

						}

					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whichButton) {

							dialog.cancel();
						}

					})
					.setCancelable(false);


					// Create the AlertDialog
					builder.create().show();

				}
			});
		}

		else if(statoAmici.equals("2")){

			inviaRifiutaRichiesta.setText("Richiesta Inviata");
			inviaRifiutaRichiesta.setClickable(false);
			inviaRifiutaRichiesta.setVisibility(View.VISIBLE);
			inviaMessaggio.setVisibility(View.INVISIBLE);
			eliminaAmicizia.setVisibility(View.INVISIBLE);

		}

		else if(statoAmici.equals("null")){

			inviaMessaggio.setVisibility(View.INVISIBLE);
			eliminaAmicizia.setVisibility(View.INVISIBLE);

			inviaRifiutaRichiesta.setText("Invia Richiesta");
			inviaRifiutaRichiesta.setVisibility(View.VISIBLE);
			inviaRifiutaRichiesta.setClickable(true);

			inviaRifiutaRichiesta.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view){

					String setStatoAmicizia="2";

					AggiungiAmici ag=new AggiungiAmici();
					if (Networking.isNetworkAvailable(getActivity())) {
						ag.execute(setStatoAmicizia, id_ric, id_rich);

						inviaRifiutaRichiesta.setText("Richiesta Inviata");
						inviaRifiutaRichiesta.setClickable(false);
					} else
						Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

				}
			});

		}


		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);


	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);

	}

	@Override
	public void onResume() {

		super.onResume();
	}		

	@Override
	public void onPause() {


		if ( conversazionePrivataFragment != null) {

			if ( conversazionePrivataFragment.isVisible() ) {

				FragmentManager fragmentManager = getFragmentManager(); 

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 

				fragmentTransaction.remove(conversazionePrivataFragment);


				fragmentTransaction.show(profiloUtenteVicinoFragment).commit();
			}

		}


		super.onPause();

	}


	public  ConversazionePrivataFragment getConversazionePrivata() {

		return conversazionePrivataFragment;
	}


	public void removeFragment() {


		if ( conversazionePrivataFragment != null) {

			if ( conversazionePrivataFragment.isVisible() ) {

				FragmentManager fragmentManager = getFragmentManager(); 

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 

				fragmentTransaction.remove(conversazionePrivataFragment);


				fragmentTransaction.show(profiloUtenteVicinoFragment).commit();
			}

		}

	}


	private class UpdateStato extends AsyncTask<String,String,String>  {

		private ProgressDialog dialog = new ProgressDialog(getActivity());
		private String status=null;
		private String ric=null;
		private String rich=null;

		InputStream is = null;
		String result = null;


		protected void onPreExecute() {

			dialog.setCancelable(true);
			dialog.setTitle("Caricamento");
			dialog.setMessage("Attendere...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			status=params[0];
			ric=params[1];
			rich=params[2];

			String url = "http://whoareyou.altervista.org/update_stato_amicizia.php";
			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
			HttpPost httpPost = new HttpPost(url);

			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("stato",status));
			param.add(new BasicNameValuePair("id_ric",ric));
			param.add(new BasicNameValuePair("id_rich",rich));

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


				JSONObject json_data = jArray.getJSONObject(0);
				json_data.getString("salvataggio");

				this.dialog.dismiss();

			}
			catch (JSONException e) {

			}

		}

	}


	// Rimuovo l'utente dai miei amici
	public class EliminazioneAmicoTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(getActivity()); 

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto eliminando l'amicizia...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {


			String url_select = "http://whoareyou.altervista.org/eliminazione_amici_persone.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

			param.add(new BasicNameValuePair("identificativoUtente", id_rich));
			param.add(new BasicNameValuePair("identificativoAmico", params[0]));


			StringBuilder stringBuilder = new StringBuilder();


			try {

				httpPost.setEntity(new UrlEncodedFormEntity(param));

				HttpResponse httpResponse = httpClient.execute(httpPost);

				HttpEntity httpEntity = httpResponse.getEntity();


				// read content
				InputStream stream = httpEntity.getContent();

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));


				String line = "";

				while ( (line = bufferedReader.readLine()) != null ) {

					stringBuilder.append(line + "\n");
				}


				stream.close();


			} catch (Exception e) {

			}


			JSONObject jsonObject = new JSONObject();

			try {

				jsonObject = new JSONObject(stringBuilder.toString());

			} catch (JSONException e) {

			}


			try {

				String status = jsonObject.getString("status").toString();


				if( status.equalsIgnoreCase("ERROR") ) {

					return false;

				} else if( status.equalsIgnoreCase("OK") ) {

					return true;

				} 

			} catch (JSONException e) {

			}

			return false;

		}

		@Override
		protected void onPostExecute(Boolean status) {

			super.onPostExecute(status);

			dialog.dismiss();

			if( status ) {

				Toast.makeText(getActivity(), "Amicizia rimossa correttamente!", Toast.LENGTH_LONG).show();

				inviaRifiutaRichiesta.setText("Invia Richiesta");
				inviaRifiutaRichiesta.setVisibility(View.VISIBLE);
				inviaRifiutaRichiesta.setClickable(true);
				inviaMessaggio.setVisibility(View.INVISIBLE);
				eliminaAmicizia.setVisibility(View.INVISIBLE);

				inviaRifiutaRichiesta.setOnClickListener(new View.OnClickListener(){
					public void onClick(View view){

						String setStatoAmicizia="2";

						UpdateStato up=new UpdateStato();

						if (Networking.isNetworkAvailable(getActivity())) {

							up.execute(setStatoAmicizia, id_ric, id_rich);

							inviaRifiutaRichiesta.setText("Richiesta Inviata");
							inviaRifiutaRichiesta.setClickable(false);

						} else
							Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();


					}
				});


			} else {

				Toast.makeText(getActivity(), "Errore. L'amicizia non e' stata rimossa. Riprova!", Toast.LENGTH_SHORT).show();

			}

		}

		protected void onCancelled() {

		}

	}

	private class AggiungiAmici extends AsyncTask<String,String,String>  {

		private ProgressDialog dialog = new ProgressDialog(getActivity());
		private String status=null;
		private String ric=null;
		private String rich=null;
		InputStream is = null;
		String result = null;


		protected void onPreExecute() {

			dialog.setCancelable(true);
			dialog.setTitle("Caricamento");
			dialog.setMessage("Attendere...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			status=params[0];
			ric=params[1];
			rich=params[2];

			String url = "http://whoareyou.altervista.org/aggiungi_amicizia.php";
			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
			HttpPost httpPost = new HttpPost(url);

			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("stato",status));
			param.add(new BasicNameValuePair("ric",ric));
			param.add(new BasicNameValuePair("rich",rich));

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


				JSONObject json_data = jArray.getJSONObject(0);
				json_data.getString("salvataggio");

				this.dialog.dismiss();

			}
			catch (JSONException e) {

			}

		}

	}

	public class NuovaConversazionePrivataTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(getActivity());

		private String ric=null;
		private String rich=null;


		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto aprendo la conversazione...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			// amico o sconosciuto, utente
			ric=params[0];
			rich=params[1];


			String url_select = "http://whoareyou.altervista.org/nuova_conversazione_privata.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

			param.add(new BasicNameValuePair("identificativoUtente", rich));
			param.add(new BasicNameValuePair("identificativoAmico", ric));


			StringBuilder stringBuilder = new StringBuilder();


			try {

				httpPost.setEntity(new UrlEncodedFormEntity(param));

				HttpResponse httpResponse = httpClient.execute(httpPost);

				HttpEntity httpEntity = httpResponse.getEntity();


				// read content
				InputStream stream = httpEntity.getContent();

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));


				String line = "";

				while ( (line = bufferedReader.readLine()) != null ) {

					stringBuilder.append(line + "\n");
				}


				stream.close();


			} catch (Exception e) {

			}


			JSONObject jsonObject = new JSONObject();

			try {

				jsonObject = new JSONObject(stringBuilder.toString());

			} catch (JSONException e) {

			}


			try {

				String status = jsonObject.getString("status").toString();


				if( status.equalsIgnoreCase("ERROR") ) {

					return false;

				} else if( status.equalsIgnoreCase("OK") ) {

					return true;

				} 

			} catch (JSONException e) {

			}

			return false;

		}

		@Override
		protected void onPostExecute(Boolean status) {

			super.onPostExecute(status);

			dialog.dismiss();

			if( status ) {


				// APRIRE ConversazionePrivataFragment 


				Bundle bundleConversazionePrivata = new Bundle();

				bundleConversazionePrivata.putString("identificativoAmico", id);
				bundleConversazionePrivata.putString("usernameAmico", us);
				bundleConversazionePrivata.putString("immagineAmico", img);

				bundleConversazionePrivata.putString("identificativoUtente", id_rich);
				bundleConversazionePrivata.putString("immagineUtente", img_rich);


				FragmentManager fragmentManager = getFragmentManager(); 

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 


				conversazionePrivataFragment = new ConversazionePrivataFragment(bundleConversazionePrivata);


				fragmentTransaction.hide(profiloUtenteVicinoFragment);


				fragmentTransaction.add(R.id.container, conversazionePrivataFragment, "conversazionePrivataFragment");

				fragmentTransaction.addToBackStack(null); 

				fragmentTransaction.commit();


			} else {

				Toast.makeText(getActivity(), "Errore. Apertura della conversazione non riuscita. Riprova!", Toast.LENGTH_SHORT).show();

			}

		}

		protected void onCancelled() {

		}

	}
}
