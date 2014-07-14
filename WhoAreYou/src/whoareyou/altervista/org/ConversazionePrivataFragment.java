package whoareyou.altervista.org;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ConversazionePrivataFragment extends Fragment{

	private MyActionBarActivity myActionBarActivity;

	private TextView textView_emptyListConversazionePrivata;

	private ListView listView_conversazionePrivata;


	// The data to show
	private List<MessaggioPrivato> messaggioPrivatoList = new ArrayList<MessaggioPrivato>();
	private ConversazionePrivataListAdapter conversazionePrivataListAdapter;

	private EditText editText_messaggioConversazionePrivata;




	private String identificativoAmico;
	private String usernameAmico;
	private String immagineAmico;

	private String identificativoUtente; 
	private String immagineUtente;




	// costruttore aggiunto per prelevare le informazioni relative alla conversazione
	public ConversazionePrivataFragment(Bundle bundleConversazionePrivata) {

		identificativoAmico = bundleConversazionePrivata.getString("identificativoAmico");
		usernameAmico = bundleConversazionePrivata.getString("usernameAmico");
		immagineAmico = bundleConversazionePrivata.getString("immagineAmico");

		identificativoUtente = bundleConversazionePrivata.getString("identificativoUtente");
		immagineUtente = bundleConversazionePrivata.getString("immagineUtente");

	}


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState); 

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		View root = inflater.inflate(R.layout.fragment_conversazione_privata, container, false); 

		return root;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		myActionBarActivity = (MyActionBarActivity) getActivity();

		myActionBarActivity.setConversazionePrivataFragment(this);



		editText_messaggioConversazionePrivata = (EditText) getActivity().findViewById(R.id.EditText_messaggioConversazionePrivata);  


		textView_emptyListConversazionePrivata = (TextView) getActivity().findViewById(R.id.TextView_emptyListConversazionePrivata);  


		// We get the ListView component from the layout
		listView_conversazionePrivata = (ListView) getActivity().findViewById(R.id.ListView_ConversazionePrivata);

		listView_conversazionePrivata.setVisibility(ListView.GONE);



		TextView textView_identificativo = (TextView) getActivity().findViewById(R.id.TextView_identificativoConversazionePrivata);

		TextView textView_username = (TextView) getActivity().findViewById(R.id.TextView_usernameConversazionePrivata);


		textView_identificativo.setText(identificativoAmico);

		textView_username.setText(usernameAmico);




		// Create an empty adapter we will use to display the loaded data.

		messaggioPrivatoList = null;

		conversazionePrivataListAdapter = new ConversazionePrivataListAdapter(messaggioPrivatoList, getActivity());

		listView_conversazionePrivata.setAdapter(conversazionePrivataListAdapter);

	}	


	@Override
	public void onResume() {

		super.onResume();


		mHandler = new Handler();

		mHandler.postDelayed(mRunnable, 0); // 0 ms

	}		


	public void refreshConversazionePrivataList(View view) {

		refreshList();

	}


	private void refreshList() {

		if( ! myActionBarActivity.getBooleanCondividiPosizione() ) {

			if( listView_conversazionePrivata.isShown() )
				listView_conversazionePrivata.setVisibility(TextView.GONE);

			if( ! textView_emptyListConversazionePrivata.isShown() )
				textView_emptyListConversazionePrivata.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Impossibile rilevare i messaggi relativi alla conversazione; permettere la condividisione della propria posizione.", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() == null ) {

			if( listView_conversazionePrivata.isShown() )
				listView_conversazionePrivata.setVisibility(TextView.GONE);

			if( ! textView_emptyListConversazionePrivata.isShown() )
				textView_emptyListConversazionePrivata.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Nessuna posizione rilevata. Impossibile rilevare i messaggi relativi alla conversazione. Aggiorna!", Toast.LENGTH_SHORT).show();

		} else if ( myActionBarActivity.getCurrentLocation() != null ) {

			ConversazionePrivataListTask conversazionePrivataListTask = new ConversazionePrivataListTask();

			if (Networking.isNetworkAvailable(getActivity())) 
				conversazionePrivataListTask.execute(myActionBarActivity.getCurrentLocation());
			else
				Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

		}

	}


	private Handler mHandler;

	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {

			/** Do something **/

			refreshList();

			mHandler.postDelayed(mRunnable, 60000); // 1 minuto
		}
	};	


	@Override
	public void onPause() {

		mHandler.removeCallbacks(mRunnable);


		super.onPause();
	}


	public void invioMessaggioPrivato(View view) {

		if ( ! myActionBarActivity.getBooleanCondividiPosizione() ) {

			Toast.makeText(getActivity(), "Errore. Permettere la condividisione della propria posizione per inviare il messaggio.", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() == null ) {

			Toast.makeText(getActivity(), "Errore. Non e' stata rilevata una posizione. Aggiorna!", Toast.LENGTH_SHORT).show();

		} else if ( editText_messaggioConversazionePrivata.getText().toString().length() <= 1) { //  == null  || editText_messaggioBachecaAvvistamenti.getText().toString().length() <= 1

			Toast.makeText(getActivity(), "Errore. Inserire il contenuto del messaggio!", Toast.LENGTH_SHORT).show();

		}  else if ( editText_messaggioConversazionePrivata.getText().toString().length() > 250 ) { 

			Toast.makeText(getActivity(), "Errore. Inserire massimo 250 caratteri nel messaggio!", Toast.LENGTH_SHORT).show();

		} else if ( myActionBarActivity.getBooleanCondividiPosizione() && myActionBarActivity.getCurrentLocation() != null && editText_messaggioConversazionePrivata.getText().toString().length() > 1 && editText_messaggioConversazionePrivata.getText().toString().length() <= 250 ) { // != null

			InvioMessaggioPrivatoTask invioMessaggioPrivatoTask = new InvioMessaggioPrivatoTask();

			if (Networking.isNetworkAvailable(getActivity())) 
				invioMessaggioPrivatoTask.execute(myActionBarActivity.getCurrentLocation());
			else
				Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();


		} 

	}


	//metodo per l'invio del messaggio privato
	public class InvioMessaggioPrivatoTask extends AsyncTask<Location, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(getActivity()); 


		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto inviando il tuo messaggio...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Location... params) {

			// Get the current location from the input parameter list
			Location loc = params[0];

			double latitudine = loc.getLatitude();

			double longitudine = loc.getLongitude();


			String url_select = "http://whoareyou.altervista.org/messaggio_conversazione_privata.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


			param.add(new BasicNameValuePair("identificativoMittente", identificativoUtente));

			param.add(new BasicNameValuePair("identificativoDestinatario", identificativoAmico));


			String latString = Double.toString(latitudine);

			if ( latString.length() >= 10 )
				latString = latString.substring(0,10);

			String longString = Double.toString(longitudine);

			if ( longString.length() >= 10 )
				longString = longString.substring(0,10);

			param.add( new BasicNameValuePair("latitudine", latString ));
			param.add( new BasicNameValuePair("longitudine", longString ));


			param.add( new BasicNameValuePair( "oggetto", editText_messaggioConversazionePrivata.getText().toString() ));



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

				editText_messaggioConversazionePrivata.setText("");

				refreshList();

			} else {

				Toast.makeText(getActivity(), "Errore. Il tuo messaggio non e' stato inviato. Riprova!", Toast.LENGTH_SHORT).show();

			}

		}

		protected void onCancelled() {

		}


	}



	public class ConversazionePrivataListTask extends AsyncTask<Location, Void, List<MessaggioPrivato>> {

		private ProgressDialog dialog = new ProgressDialog(getActivity()); 


		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto caricando i messaggi relativi alla conversazione...");
			dialog.show();
		}

		@Override
		protected List<MessaggioPrivato> doInBackground(Location... params) {

			// Get the current location from the input parameter list
			Location loc = params[0];

			double latitudine = loc.getLatitude();

			double longitudine = loc.getLongitude();


			String url_select = "http://whoareyou.altervista.org/conversazione_privata.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


			param.add(new BasicNameValuePair("identificativoUtente", identificativoUtente));

			param.add(new BasicNameValuePair("identificativoAmico", identificativoAmico));


			String latString = Double.toString(latitudine);

			if ( latString.length() >= 10 )
				latString = latString.substring(0,10);

			String longString = Double.toString(longitudine);

			if ( longString.length() >= 10 )
				longString = longString.substring(0,10);

			param.add( new BasicNameValuePair("latitudine", latString ));
			param.add( new BasicNameValuePair("longitudine", longString ));


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

					return null;

				} else {

					JSONArray jsonArray = jsonObject.getJSONArray("result");

					List<MessaggioPrivato> resultList = new ArrayList<MessaggioPrivato>();

					for (int i=0; i < jsonArray.length(); i++) {

						resultList.add(convertMessaggioPrivato(jsonArray.getJSONObject(i)));

					}

					return resultList;
				}

			} catch (JSONException e) {

			}

			return null;

		}


		private MessaggioPrivato convertMessaggioPrivato(JSONObject obj) throws JSONException {


			String tipoMessaggioPrivato = obj.getString("tipoMessaggioPrivato");


			String immagine = null;

			if ( tipoMessaggioPrivato.equalsIgnoreCase("INVIATO") ) {

				immagine = immagineUtente;

			} else if ( tipoMessaggioPrivato.equalsIgnoreCase("RICEVUTO") ) {

				immagine = immagineAmico;
			}


			String messaggio = obj.getString("messaggio");

			String data_ora = obj.getString("data_ora");

			String distanza = obj.getString("distanza");

			String statoMessaggio = obj.getString("statoMessaggio");


			return new MessaggioPrivato(tipoMessaggioPrivato, immagine, messaggio, data_ora, distanza, statoMessaggio);// identificativo, username,
		}


		@Override
		protected void onPostExecute(List<MessaggioPrivato> resultList) {

			super.onPostExecute(resultList);

			dialog.dismiss();


			if ( resultList == null ) {

				if( listView_conversazionePrivata.isShown() )
					listView_conversazionePrivata.setVisibility(TextView.GONE);

				if( ! textView_emptyListConversazionePrivata.isShown() )
					textView_emptyListConversazionePrivata.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "Impossibile rilevare i messaggi relativi alla conversazione. Aggiorna!", Toast.LENGTH_SHORT).show();


			} else if ( resultList.size() <= 0) {

				if( listView_conversazionePrivata.isShown() )
					listView_conversazionePrivata.setVisibility(TextView.GONE);

				if( ! textView_emptyListConversazionePrivata.isShown() )
					textView_emptyListConversazionePrivata.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "La conversazione non presenta messaggi. Riprova!", Toast.LENGTH_SHORT).show();


			} else if( resultList != null && myActionBarActivity.getBooleanCondividiPosizione() ){


				messaggioPrivatoList = resultList;

				Comparator<MessaggioPrivato> comparator = new Comparator<MessaggioPrivato>() {

					@Override
					public int compare(MessaggioPrivato messaggioPrivato1, MessaggioPrivato messaggioPrivato2) {

						SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Y-m-d H:i:s in php

						Date data_ora1 = null, data_ora2 = null;

						try {

							data_ora1 = simpleDateFormat.parse(messaggioPrivato1.getData_ora());
							data_ora2 = simpleDateFormat.parse(messaggioPrivato2.getData_ora());

						} catch (ParseException e) {

						}

						return data_ora1.compareTo(data_ora2);
					}
				};

				Collections.sort(messaggioPrivatoList, comparator);


				if( textView_emptyListConversazionePrivata.isShown() )
					textView_emptyListConversazionePrivata.setVisibility(TextView.GONE);

				if( ! listView_conversazionePrivata.isShown() )
					listView_conversazionePrivata.setVisibility(TextView.VISIBLE);



				conversazionePrivataListAdapter.setItemList(messaggioPrivatoList);

				conversazionePrivataListAdapter.notifyDataSetChanged();
			}

		}

		protected void onCancelled() {

		}

	}


}