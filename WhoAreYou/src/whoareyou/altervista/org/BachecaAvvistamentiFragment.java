package whoareyou.altervista.org;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class BachecaAvvistamentiFragment extends Fragment{


	private MyActionBarActivity myActionBarActivity;

	private TextView textView_emptyListBachecaAvvistamenti;

	private ListView listView_bachecaAvvistamenti;

	private Bundle dati;


	// The data to show
	private List<MessaggioAvvistamento> messaggioAvvistamentoList = new ArrayList<MessaggioAvvistamento>();
	private BachecaAvvistamentiListAdapter bachecaAvvistamentiListAdapter;

	private EditText editText_messaggioBachecaAvvistamenti;


	private ProfiloUtenteVicinoFragment profiloUtenteVicinoFragment = null;
	private BachecaAvvistamentiFragment bachecaAvvistamentiFragment = this;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		View root = inflater.inflate(R.layout.fragment_bacheca_avvistamenti, container, false);

		return root;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		myActionBarActivity = (MyActionBarActivity) getActivity();

		myActionBarActivity.setBachecaAvvistamentiFragment(this);



		editText_messaggioBachecaAvvistamenti = (EditText) getActivity().findViewById(R.id.EditText_messaggioBachecaAvvistamenti);  



		textView_emptyListBachecaAvvistamenti = (TextView) getActivity().findViewById(R.id.TextView_emptyListBachecaAvvistamenti);  

		// We get the ListView component from the layout
		listView_bachecaAvvistamenti = (ListView) getActivity().findViewById(R.id.ListView_BachecaAvvistamenti);

		listView_bachecaAvvistamenti.setVisibility(ListView.GONE);


		// Create an empty adapter we will use to display the loaded data.

		messaggioAvvistamentoList = null;

		bachecaAvvistamentiListAdapter = new BachecaAvvistamentiListAdapter(messaggioAvvistamentoList, getActivity());

		listView_bachecaAvvistamenti.setAdapter(bachecaAvvistamentiListAdapter);

	}	


	@Override
	public void onResume() {

		super.onResume();


		dati = myActionBarActivity.getDatiPersonali();


		mHandler = new Handler();

		mHandler.postDelayed(mRunnable, 0); // 0 ms

	}		


	public void refreshBachecaAvvistamentiList(View view) {

		refreshList();

	}


	private void refreshList() {

		dati = myActionBarActivity.getDatiPersonali();

		if( ! myActionBarActivity.getBooleanCondividiPosizione() ) {

			if( listView_bachecaAvvistamenti.isShown() )
				listView_bachecaAvvistamenti.setVisibility(TextView.GONE);

			if( ! textView_emptyListBachecaAvvistamenti.isShown() )
				textView_emptyListBachecaAvvistamenti.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Impossibile rilevare messaggi d'avvistamento inviati nelle vicinanze; permettere la condividisione della propria posizione.", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() == null ) {

			if( listView_bachecaAvvistamenti.isShown() )
				listView_bachecaAvvistamenti.setVisibility(TextView.GONE);

			if( ! textView_emptyListBachecaAvvistamenti.isShown() )
				textView_emptyListBachecaAvvistamenti.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Nessuna posizione rilevata. Impossibile rilevare messaggi d'avvistamento inviati nelle vicinanze. Aggiorna!", Toast.LENGTH_SHORT).show();

		} else if ( myActionBarActivity.getCurrentLocation() != null ) {

			BachecaAvvistamentiListTask bachecaAvvistamentiListTask = new BachecaAvvistamentiListTask();

			if (Networking.isNetworkAvailable(getActivity())) 
				bachecaAvvistamentiListTask.execute(myActionBarActivity.getCurrentLocation());
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


		if ( profiloUtenteVicinoFragment != null) {

			if ( profiloUtenteVicinoFragment.isVisible() || profiloUtenteVicinoFragment.getConversazionePrivata().isVisible() ) {

				FragmentManager fragmentManager = getFragmentManager(); 

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 


				profiloUtenteVicinoFragment.removeFragment();

				fragmentTransaction.remove(profiloUtenteVicinoFragment);


				fragmentTransaction.show(bachecaAvvistamentiFragment).commit();
			}

		}


		super.onPause();
	}


	public void vaiAlMioProfilo() {

		mHandler.removeCallbacks(mRunnable);


		if ( profiloUtenteVicinoFragment != null) {

			if ( profiloUtenteVicinoFragment.isVisible() || profiloUtenteVicinoFragment.getConversazionePrivata().isVisible() ) {

				FragmentManager fragmentManager = getFragmentManager(); 

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 


				profiloUtenteVicinoFragment.removeFragment();

				fragmentTransaction.remove(profiloUtenteVicinoFragment);


				fragmentTransaction.show(bachecaAvvistamentiFragment).commit();
			}

		}

	}



	public void invioMessaggioAvvistamento(View view) {

		if ( ! myActionBarActivity.getBooleanCondividiPosizione() ) {

			Toast.makeText(getActivity(), "Errore. Permettere la condividisione della propria posizione per inviare il messaggio d'avvistamento.", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() == null ) {

			Toast.makeText(getActivity(), "Errore. Non e' stata rilevata una posizione. Aggiorna!", Toast.LENGTH_SHORT).show();

		} else if ( editText_messaggioBachecaAvvistamenti.getText().toString().length() <= 1) { //  == null  || editText_messaggioBachecaAvvistamenti.getText().toString().length() <= 1

			Toast.makeText(getActivity(), "Errore. Inserire il contenuto del messaggio d'avvistamento!", Toast.LENGTH_SHORT).show();

		}  else if ( editText_messaggioBachecaAvvistamenti.getText().toString().length() > 250 ) { 

			Toast.makeText(getActivity(), "Errore. Inserire massimo 250 caratteri nel messaggio d'avvistamento!", Toast.LENGTH_SHORT).show();

		} else if ( myActionBarActivity.getBooleanCondividiPosizione() && myActionBarActivity.getCurrentLocation() != null && editText_messaggioBachecaAvvistamenti.getText().toString().length() > 1 && editText_messaggioBachecaAvvistamenti.getText().toString().length() <= 250 ) { // != null

			InvioMessaggioAvvistamentoTask invioMessaggioAvvistamentoTask = new InvioMessaggioAvvistamentoTask();

			if (Networking.isNetworkAvailable(getActivity())) 
				invioMessaggioAvvistamentoTask.execute(myActionBarActivity.getCurrentLocation());
			else
				Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

		} 

	}


	//metodo per l'invio del messaggio d'avvistamento
	public class InvioMessaggioAvvistamentoTask extends AsyncTask<Location, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(getActivity()); 


		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto inviando il tuo messaggio d'avvistamento...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Location... params) {

			// Get the current location from the input parameter list
			Location loc = params[0];

			double latitudine = loc.getLatitude();

			double longitudine = loc.getLongitude();


			String url_select = "http://whoareyou.altervista.org/messaggio_bacheca_avvistamenti.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


			param.add(new BasicNameValuePair("identificativo", dati.getString("id")) );


			String latString = Double.toString(latitudine);

			if ( latString.length() >= 10 )
				latString = latString.substring(0,10);

			String longString = Double.toString(longitudine);

			if ( longString.length() >= 10 )
				longString = longString.substring(0,10);

			param.add( new BasicNameValuePair("latitudine", latString ));
			param.add( new BasicNameValuePair("longitudine", longString ));


			param.add( new BasicNameValuePair( "oggetto", editText_messaggioBachecaAvvistamenti.getText().toString() ));



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

				editText_messaggioBachecaAvvistamenti.setText("");

				refreshList();

			} else {

				Toast.makeText(getActivity(), "Errore. Il tuo messaggio d'avvistamento non e' stato inviato. Riprova!", Toast.LENGTH_SHORT).show();

			}

		}

		protected void onCancelled() {

		}


	}



	public class BachecaAvvistamentiListTask extends AsyncTask<Location, Void, List<MessaggioAvvistamento>> {

		private ProgressDialog dialog = new ProgressDialog(getActivity()); 


		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto caricando i messaggi d'avvistamento inviati vicino a te...");
			dialog.show();
		}

		@Override
		protected List<MessaggioAvvistamento> doInBackground(Location... params) {

			// Get the current location from the input parameter list
			Location loc = params[0];

			double latitudine = loc.getLatitude();

			double longitudine = loc.getLongitude();


			String url_select = "http://whoareyou.altervista.org/bacheca_avvistamenti.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


			param.add(new BasicNameValuePair("identificativo", dati.getString("id")));


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

					List<MessaggioAvvistamento> resultList = new ArrayList<MessaggioAvvistamento>();

					for (int i=0; i < jsonArray.length(); i++) {

						resultList.add(convertMessaggioAvvistamento(jsonArray.getJSONObject(i)));

					}

					return resultList;
				}

			} catch (JSONException e) {

			}

			return null;

		}


		private MessaggioAvvistamento convertMessaggioAvvistamento(JSONObject obj) throws JSONException {

			String identificativo = obj.getString("identificativo");

			String immagine = obj.getString("immagine");

			String username = obj.getString("username");

			String messaggio = obj.getString("messaggio");

			String data_ora = obj.getString("data_ora");

			String distanza = obj.getString("distanza");

			return new MessaggioAvvistamento(identificativo, immagine, username, messaggio, data_ora, distanza);
		}


		@Override
		protected void onPostExecute(List<MessaggioAvvistamento> resultList) {

			super.onPostExecute(resultList);

			dialog.dismiss();


			if ( resultList == null ) {

				if( listView_bachecaAvvistamenti.isShown() )
					listView_bachecaAvvistamenti.setVisibility(TextView.GONE);

				if( ! textView_emptyListBachecaAvvistamenti.isShown() )
					textView_emptyListBachecaAvvistamenti.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "Impossibile rilevare messaggi d'avvistamento inviati nelle vicinanze. Aggiorna!", Toast.LENGTH_SHORT).show();


			} else if ( resultList.size() <= 0) {

				if( listView_bachecaAvvistamenti.isShown() )
					listView_bachecaAvvistamenti.setVisibility(TextView.GONE);

				if( ! textView_emptyListBachecaAvvistamenti.isShown() )
					textView_emptyListBachecaAvvistamenti.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "Non sono stati inviati messaggi d'avvistamento nelle vicinanze. Riprova!", Toast.LENGTH_SHORT).show();


			} else if( resultList != null && myActionBarActivity.getBooleanCondividiPosizione() ){

				messaggioAvvistamentoList = resultList;

				Comparator<MessaggioAvvistamento> comparator = new Comparator<MessaggioAvvistamento>() {

					@Override
					public int compare(MessaggioAvvistamento messaggioAvvistamento1, MessaggioAvvistamento messaggioAvvistamento2) {

						Double distanza1 = Double.parseDouble(messaggioAvvistamento1.getDistanza());

						Double distanza2 = Double.parseDouble(messaggioAvvistamento2.getDistanza());

						return distanza1.compareTo(distanza2);
					}
				};

				Collections.sort(messaggioAvvistamentoList, comparator);


				if( textView_emptyListBachecaAvvistamenti.isShown() )
					textView_emptyListBachecaAvvistamenti.setVisibility(TextView.GONE);

				if( ! listView_bachecaAvvistamenti.isShown() )
					listView_bachecaAvvistamenti.setVisibility(TextView.VISIBLE);


				bachecaAvvistamentiListAdapter.setItemList(messaggioAvvistamentoList);

				bachecaAvvistamentiListAdapter.notifyDataSetChanged();


				listView_bachecaAvvistamenti.setOnItemClickListener(new ListViewBachecaAvvistamentiListener());

			}

		}

		protected void onCancelled() {

		}

	}


	//Listener per ListView
	public class ListViewBachecaAvvistamentiListener implements  AdapterView.OnItemClickListener {

		public void onItemClick ( AdapterView<?> listView, View itemView, int position,long itemId ){

			MessaggioAvvistamento messaggioAvvistamento = (MessaggioAvvistamento) listView_bachecaAvvistamenti.getItemAtPosition(position);

			String identificativoMessaggioAvvistamento = messaggioAvvistamento.getIdentificativo();


			String identificativoUtente = dati.getString("id");


			if( ! identificativoMessaggioAvvistamento.equals(identificativoUtente) ) {


				SelezioneProfiloTask selezioneProfiloTask = new SelezioneProfiloTask();


				if (Networking.isNetworkAvailable(getActivity())) 

					selezioneProfiloTask.execute(identificativoMessaggioAvvistamento, identificativoUtente, dati.getString("img"));  // id amico o sconosciuto, id utente, immagine utente

				else
					Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();


			} else {

				Toast.makeText(getActivity(), "Attenzione! Clicca sull'apposita icona per visualizzare il tuo profilo personale.", Toast.LENGTH_LONG).show();

			}

		}

	}


	private class SelezioneProfiloTask extends AsyncTask<String,String,String>  {

		private ProgressDialog dialog = new ProgressDialog(getActivity());
		private String id_ric, id_rich, img_rich; // id amico o sconosciuto, id utente, immagine utente
		private String i, us, n , e, c, im, d, s, sa; // info amico o sconosciuto
		private InputStream is = null;
		private String result = null;




		protected void onPreExecute() {

			dialog.setCancelable(true);
			dialog.setTitle("Caricamento Profilo");
			dialog.setMessage("Attendere...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			id_ric=params[0]; // id amico, id utente, immagine utente
			id_rich=params[1];
			img_rich=params[2];


			String url = "http://whoareyou.altervista.org/selezione_profilo.php";
			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
			HttpPost httpPost = new HttpPost(url);

			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("identif_ric",id_ric)); // sconosciuto o amico
			param.add(new BasicNameValuePair("identif_rich",id_rich)); // utente

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


				JSONObject json_data = jArray.getJSONObject(0); // info amico
				i=json_data.getString("identificativo");
				us=json_data.getString("username");
				n=json_data.getString("numero");
				e=json_data.getString("email");
				c=json_data.getString("citta");
				im=json_data.getString("immagine");
				d=json_data.getString("data_nascita");
				s=json_data.getString("sesso");

				json_data = jArray.getJSONObject(1);

				sa=json_data.getString("statoAmicizia");

				this.dialog.dismiss();


				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


				profiloUtenteVicinoFragment = new ProfiloUtenteVicinoFragment();

				Bundle args = new Bundle();
				args.putString("id", i);
				args.putString("us", us);
				args.putString("num", n);
				args.putString("em", e);
				args.putString("city", c);
				args.putString("img", im);
				args.putString("data", d);
				args.putString("sex", s);
				args.putString("statoAmici", sa);
				args.putString("ric", id_ric);
				args.putString("rich", id_rich);
				args.putString("img_rich", img_rich);

				profiloUtenteVicinoFragment.setArguments(args);



				mHandler.removeCallbacks(mRunnable);


				fragmentTransaction.hide(bachecaAvvistamentiFragment);


				fragmentTransaction.add(R.id.container, profiloUtenteVicinoFragment, "profiloUtenteVicinoFragment");

				fragmentTransaction.addToBackStack(null); 

				fragmentTransaction.commit();


			}
			catch (JSONException e) {

			}

		}

	}

}