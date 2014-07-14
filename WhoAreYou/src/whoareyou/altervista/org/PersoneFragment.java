package whoareyou.altervista.org;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;



public class PersoneFragment extends Fragment {


	private MyActionBarActivity myActionBarActivity;

	private TextView textView_emptyListRichiestaAmicizia_Persone;

	private ListView listView_RichiestaAmicizia_Persone;

	private TextView textView_emptyListAmici_Persone;

	private ListView listView_Amici_Persone;


	private Bundle dati;


	// The data to show
	private List<Persone_Richiesta> richiestaAmicizia_PersoneList = new ArrayList<Persone_Richiesta>();
	private Persone_RichiestaListAdapter richiestaAmicizia_PersoneListAdapter;

	private List<Persone_Amico> amici_PersoneList = new ArrayList<Persone_Amico>();
	private Persone_AmicoListAdapter amici_PersoneListAdapter;


	private ProfiloUtenteVicinoFragment profiloUtenteVicinoFragment = null;
	private PersoneFragment personeFragment = this;



	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState); 

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		View root = inflater.inflate(R.layout.fragment_persone, container, false);

		return root;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		myActionBarActivity = (MyActionBarActivity) getActivity();

		myActionBarActivity.setPersoneFragment(this);


		// RICHIESTE

		textView_emptyListRichiestaAmicizia_Persone = (TextView) getActivity().findViewById(R.id.TextView_emptyListRichiestaAmicizia_Persone);  


		// We get the ListView component from the layout
		listView_RichiestaAmicizia_Persone = (ListView) getActivity().findViewById(R.id.ListView_RichiestaAmicizia_Persone);              


		listView_RichiestaAmicizia_Persone.setVisibility(ListView.GONE);



		// Create an empty adapter we will use to display the loaded data.

		richiestaAmicizia_PersoneList = null;

		richiestaAmicizia_PersoneListAdapter = new Persone_RichiestaListAdapter(richiestaAmicizia_PersoneList, getActivity(), this);

		listView_RichiestaAmicizia_Persone.setAdapter(richiestaAmicizia_PersoneListAdapter);


		//	AMICI

		textView_emptyListAmici_Persone = (TextView) getActivity().findViewById(R.id.TextView_emptyListAmici_Persone);  


		// We get the ListView component from the layout
		listView_Amici_Persone = (ListView) getActivity().findViewById(R.id.ListView_Amici_Persone);              


		listView_Amici_Persone.setVisibility(ListView.GONE);



		// Create an empty adapter we will use to display the loaded data.

		amici_PersoneList = null;

		amici_PersoneListAdapter = new Persone_AmicoListAdapter(amici_PersoneList, getActivity(), this);

		listView_Amici_Persone.setAdapter(amici_PersoneListAdapter);


	}	


	@Override
	public void onResume() {

		super.onResume();

		dati = myActionBarActivity.getDatiPersonali();


		//  RICHIESTE
		mHandlerRichiestaAmicizia_Persone = new Handler();

		mHandlerRichiestaAmicizia_Persone.postDelayed(mRunnableRichiestaAmicizia_Persone, 0); // 0 ms


		//  AMICI
		mHandlerAmici_Persone = new Handler();

		mHandlerAmici_Persone.postDelayed(mRunnableAmici_Persone, 0); // 0 ms

	}		


	public void refreshRichiestaAmicizia_PersoneList(View view) {

		refreshRichiestaAmiciziaList();
	}

	public void refreshAmici_PersoneList(View view) {

		refreshAmiciList();
	}


	private void refreshRichiestaAmiciziaList() {

		dati = myActionBarActivity.getDatiPersonali();


		if( ! myActionBarActivity.getBooleanCondividiPosizione() ) {

			if( listView_RichiestaAmicizia_Persone.isShown() )
				listView_RichiestaAmicizia_Persone.setVisibility(TextView.GONE);

			if( ! textView_emptyListRichiestaAmicizia_Persone.isShown() )
				textView_emptyListRichiestaAmicizia_Persone.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Impossibile mostrare le richieste d'amicizia; permettere la condividisione della propria posizione.", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() == null ) {

			if( listView_RichiestaAmicizia_Persone.isShown() )
				listView_RichiestaAmicizia_Persone.setVisibility(TextView.GONE);

			if( ! textView_emptyListRichiestaAmicizia_Persone.isShown() )
				textView_emptyListRichiestaAmicizia_Persone.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Nessuna posizione rilevata. Impossibile mostrare le richieste d'amicizia. Aggiorna!", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() != null ) {

			RichiestaAmicizia_PersoneListTask richiestaAmicizia_PersoneListTask = new RichiestaAmicizia_PersoneListTask();

			if (Networking.isNetworkAvailable(getActivity())) 
				richiestaAmicizia_PersoneListTask.execute(myActionBarActivity.getCurrentLocation());
			else
				Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

		}

	} 


	private void refreshAmiciList() {

		dati = myActionBarActivity.getDatiPersonali();


		if( ! myActionBarActivity.getBooleanCondividiPosizione() ) {

			if( listView_Amici_Persone.isShown() )
				listView_Amici_Persone.setVisibility(TextView.GONE);

			if( ! textView_emptyListAmici_Persone.isShown() )
				textView_emptyListAmici_Persone.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Impossibile mostrare gli amici; permettere la condividisione della propria posizione.", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() == null ) {

			if( listView_Amici_Persone.isShown() )
				listView_Amici_Persone.setVisibility(TextView.GONE);

			if( ! textView_emptyListAmici_Persone.isShown() )
				textView_emptyListAmici_Persone.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Nessuna posizione rilevata. Impossibile mostrare gli amici. Aggiorna!", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() != null ) {

			Amici_PersoneListTask amici_PersoneListTask = new Amici_PersoneListTask();

			if (Networking.isNetworkAvailable(getActivity())) 
				amici_PersoneListTask.execute(myActionBarActivity.getCurrentLocation());
			else
				Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

		}

	}



	private Handler mHandlerRichiestaAmicizia_Persone;

	private Runnable mRunnableRichiestaAmicizia_Persone = new Runnable() {

		@Override
		public void run() {

			/** Do something **/

			refreshRichiestaAmiciziaList();

			mHandlerRichiestaAmicizia_Persone.postDelayed(mRunnableRichiestaAmicizia_Persone, 60000); // 1 minuti
		}
	};


	private Handler mHandlerAmici_Persone;

	private Runnable mRunnableAmici_Persone = new Runnable() {

		@Override
		public void run() {

			/** Do something **/

			refreshAmiciList();

			mHandlerAmici_Persone.postDelayed(mRunnableAmici_Persone, 120000); // 2 minuti
		}
	};


	@Override
	public void onPause() {

		// RICHIESTE
		mHandlerRichiestaAmicizia_Persone.removeCallbacks(mRunnableRichiestaAmicizia_Persone);

		// AMICI
		mHandlerAmici_Persone.removeCallbacks(mRunnableAmici_Persone);



		if ( profiloUtenteVicinoFragment != null) {

			if ( profiloUtenteVicinoFragment.isVisible() || profiloUtenteVicinoFragment.getConversazionePrivata().isVisible() ) {

				FragmentManager fragmentManager = getFragmentManager(); 

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 


				profiloUtenteVicinoFragment.removeFragment();

				fragmentTransaction.remove(profiloUtenteVicinoFragment);


				fragmentTransaction.show(personeFragment).commit();
			}

		}

		super.onPause();
	}


	public void vaiAlMioProfilo() {

		// RICHIESTE
		mHandlerRichiestaAmicizia_Persone.removeCallbacks(mRunnableRichiestaAmicizia_Persone);

		// AMICI 

		mHandlerAmici_Persone.removeCallbacks(mRunnableAmici_Persone);


		if ( profiloUtenteVicinoFragment != null) {

			if ( profiloUtenteVicinoFragment.isVisible() || profiloUtenteVicinoFragment.getConversazionePrivata().isVisible() ) {

				FragmentManager fragmentManager = getFragmentManager(); 

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 


				profiloUtenteVicinoFragment.removeFragment();

				fragmentTransaction.remove(profiloUtenteVicinoFragment);


				fragmentTransaction.show(personeFragment).commit();
			}

		}

	}


	// RICHIESTE 

	public class RichiestaAmicizia_PersoneListTask extends AsyncTask<Location, Void, List<Persone_Richiesta>> {

		private ProgressDialog dialog = new ProgressDialog(getActivity());


		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto caricando le richieste d'amicizia...");
			dialog.show();
		}

		@Override
		protected List<Persone_Richiesta> doInBackground(Location... params) {

			// Get the current location from the input parameter list
			Location loc = params[0];

			double latitudine = loc.getLatitude();

			double longitudine = loc.getLongitude();


			String url_select = "http://whoareyou.altervista.org/richiesta_amicizia_persone.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


			param.add(new BasicNameValuePair("identificativo", dati.getString("id") ));


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

					List<Persone_Richiesta> resultList = new ArrayList<Persone_Richiesta>();

					for (int i=0; i < jsonArray.length(); i++) {

						resultList.add(convertPersone_Richiesta(jsonArray.getJSONObject(i)));

					}

					return resultList;
				}

			} catch (JSONException e) {

			}

			return null;

		}


		private Persone_Richiesta convertPersone_Richiesta(JSONObject obj) throws JSONException {

			String identificativo = obj.getString("identificativo");

			String immagine = obj.getString("immagine");

			String username = obj.getString("username");


			return new Persone_Richiesta(identificativo, immagine, username);
		}


		@Override
		protected void onPostExecute(List<Persone_Richiesta> resultList) {

			super.onPostExecute(resultList);

			dialog.dismiss();


			if ( resultList == null ) {

				if( listView_RichiestaAmicizia_Persone.isShown() )
					listView_RichiestaAmicizia_Persone.setVisibility(TextView.GONE);

				if( ! textView_emptyListRichiestaAmicizia_Persone.isShown() )
					textView_emptyListRichiestaAmicizia_Persone.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "Impossibile mostrare le richieste d'amicizia. Aggiorna!", Toast.LENGTH_SHORT).show();

			} else if (resultList.size() <= 0) {

				if( listView_RichiestaAmicizia_Persone.isShown() )
					listView_RichiestaAmicizia_Persone.setVisibility(TextView.GONE);

				if( ! textView_emptyListRichiestaAmicizia_Persone.isShown() )
					textView_emptyListRichiestaAmicizia_Persone.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "Non sono presenti richieste d'amicizia!", Toast.LENGTH_SHORT).show();

			} else if( resultList != null && myActionBarActivity.getBooleanCondividiPosizione() ){


				richiestaAmicizia_PersoneList = resultList;


				if( textView_emptyListRichiestaAmicizia_Persone.isShown() )
					textView_emptyListRichiestaAmicizia_Persone.setVisibility(TextView.GONE);

				if( ! listView_RichiestaAmicizia_Persone.isShown() )
					listView_RichiestaAmicizia_Persone.setVisibility(TextView.VISIBLE);



				richiestaAmicizia_PersoneListAdapter.setItemList(richiestaAmicizia_PersoneList);

				richiestaAmicizia_PersoneListAdapter.notifyDataSetChanged();


				listView_RichiestaAmicizia_Persone.setOnItemClickListener( new RichiestaAmicizia_PersoneListener() );

			}   
		}

		protected void onCancelled() {

		}

	}


	//Listener per ListView
	public class RichiestaAmicizia_PersoneListener implements  AdapterView.OnItemClickListener {

		public void onItemClick ( AdapterView<?> listView, View itemView, int position, long itemId ) { 

			Persone_Richiesta richiesta = (Persone_Richiesta) listView_RichiestaAmicizia_Persone.getItemAtPosition(position);

			String identificativoPersone_Richiesta = richiesta.getIdentificativo();


			String identificativoUtente = dati.getString("id");


			if( ! identificativoPersone_Richiesta.equals(identificativoUtente) ) {


				SelezioneProfiloTask selezioneProfiloTask = new SelezioneProfiloTask();


				if (Networking.isNetworkAvailable(getActivity())) 

					selezioneProfiloTask.execute(identificativoPersone_Richiesta, identificativoUtente, dati.getString("img"));  // id amico o sconosciuto, id utente, immagine utente

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



				mHandlerRichiestaAmicizia_Persone.removeCallbacks(mRunnableRichiestaAmicizia_Persone);
				mHandlerAmici_Persone.removeCallbacks(mRunnableAmici_Persone);



				fragmentTransaction.hide(personeFragment);


				fragmentTransaction.add(R.id.container, profiloUtenteVicinoFragment, "profiloUtenteVicinoFragment");

				fragmentTransaction.addToBackStack(null); 

				fragmentTransaction.commit();


			}
			catch (JSONException e) {

			}

		}

	}



	public void accettaRichiestaAmicizia(final String identificativoAmico) {

		String accettaRichiestaAmicizia = "accetta";

		RichiestaAmiciziaTask richiestaAmiciziaTask = new RichiestaAmiciziaTask();
		if (Networking.isNetworkAvailable(getActivity())) 
			richiestaAmiciziaTask.execute(identificativoAmico, accettaRichiestaAmicizia);
		else
			Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

	}

	public void rifiutaRichiestaAmicizia(final String identificativoAmico) {

		String rifiutaRichiestaAmicizia = "rifiuta";

		RichiestaAmiciziaTask richiestaAmiciziaTask = new RichiestaAmiciziaTask();

		if (Networking.isNetworkAvailable(getActivity())) 
			richiestaAmiciziaTask.execute(identificativoAmico, rifiutaRichiestaAmicizia);
		else
			Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

	}


	// accetta o rifiuta una richiesta d'amicizia
	public class RichiestaAmiciziaTask extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

		}

		@Override
		protected Boolean doInBackground(String... params) {


			String url_select = "http://whoareyou.altervista.org/gestione_richiesta_amicizia_persone.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


			param.add(new BasicNameValuePair("identificativoUtente", dati.getString("id") ));
			param.add(new BasicNameValuePair("identificativoAmico", params[0]));
			param.add(new BasicNameValuePair("richiesta", params[1]));


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


			if( status ) {

				refreshRichiestaAmiciziaList();
				refreshAmiciList();

			} else {

				Toast.makeText(getActivity(), "Errore. Riprova!", Toast.LENGTH_SHORT).show();

			}

		}

		protected void onCancelled() {

		}

	}




	// AMICI 

	public class Amici_PersoneListTask extends AsyncTask<Location, Void, List<Persone_Amico>> {

		private ProgressDialog dialog = new ProgressDialog(getActivity());


		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto caricando l'elenco degli amici...");
			dialog.show();
		}

		@Override
		protected List<Persone_Amico> doInBackground(Location... params) {

			// Get the current location from the input parameter list
			Location loc = params[0];

			double latitudine = loc.getLatitude();

			double longitudine = loc.getLongitude();


			String url_select = "http://whoareyou.altervista.org/amici_persone.php";


			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


			param.add(new BasicNameValuePair("identificativo", dati.getString("id") ));


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

					List<Persone_Amico> resultList = new ArrayList<Persone_Amico>();

					for (int i=0; i < jsonArray.length(); i++) {

						resultList.add(convertPersone_Amico(jsonArray.getJSONObject(i)));

					}

					return resultList;
				}

			} catch (JSONException e) {

			}

			return null;

		}


		private Persone_Amico convertPersone_Amico(JSONObject obj) throws JSONException {

			String identificativo = obj.getString("identificativo");

			String immagine = obj.getString("immagine");

			String username = obj.getString("username");


			return new Persone_Amico(identificativo, immagine, username);
		}


		@Override
		protected void onPostExecute(List<Persone_Amico> resultList) {

			super.onPostExecute(resultList);

			dialog.dismiss();


			if ( resultList == null ) {

				if( listView_Amici_Persone.isShown() )
					listView_Amici_Persone.setVisibility(TextView.GONE);

				if( ! textView_emptyListAmici_Persone.isShown() )
					textView_emptyListAmici_Persone.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "Impossibile mostrare gli amici. Aggiorna!", Toast.LENGTH_SHORT).show();

			} else if (resultList.size() <= 0) {

				if( listView_Amici_Persone.isShown() )
					listView_Amici_Persone.setVisibility(TextView.GONE);

				if( ! textView_emptyListAmici_Persone.isShown() )
					textView_emptyListAmici_Persone.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "Non hai ancora amici.", Toast.LENGTH_SHORT).show();

			} else if( resultList != null && myActionBarActivity.getBooleanCondividiPosizione() ){

				amici_PersoneList = resultList;


				if( textView_emptyListAmici_Persone.isShown() )
					textView_emptyListAmici_Persone.setVisibility(TextView.GONE);

				if( ! listView_Amici_Persone.isShown() )
					listView_Amici_Persone.setVisibility(TextView.VISIBLE);


				amici_PersoneListAdapter.setItemList(amici_PersoneList);

				amici_PersoneListAdapter.notifyDataSetChanged();


				listView_Amici_Persone.setOnItemClickListener( new Amici_PersoneListener() );

			}   
		}

		protected void onCancelled() {

		}

	}


	//Listener per ListView
	public class Amici_PersoneListener implements  AdapterView.OnItemClickListener {

		public void onItemClick ( AdapterView<?> listView, View itemView, int position, long itemId ) { 

			Persone_Amico amico = (Persone_Amico) listView_Amici_Persone.getItemAtPosition(position);

			String identificativoPersone_Amico = amico.getIdentificativo();


			String identificativoUtente = dati.getString("id");


			if( ! identificativoPersone_Amico.equals(identificativoUtente) ) {


				SelezioneProfiloTask selezioneProfiloTask = new SelezioneProfiloTask();


				if (Networking.isNetworkAvailable(getActivity())) 

					selezioneProfiloTask.execute(identificativoPersone_Amico, identificativoUtente, dati.getString("img"));  // id amico o sconosciuto, id utente, immagine utente

				else
					Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();


			} else {

				Toast.makeText(getActivity(), "Attenzione! Clicca sull'apposita icona per visualizzare il tuo profilo personale.", Toast.LENGTH_LONG).show();

			}
		}

	}



	public void eliminazioneAmico(final String identificativoAmico) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder
		.setTitle("Eliminare Amicizia")
		.setMessage("Sei sicuro di voler rimuovere dagli amici l'utente: " + identificativoAmico + "? \nSara' eliminata anche la vostra conversazione!")			
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

				dialog.cancel();

				EliminazioneAmici_PersoneTask eliminazioneAmici_PersoneTask = new EliminazioneAmici_PersoneTask();

				if (Networking.isNetworkAvailable(getActivity())) 
					eliminazioneAmici_PersoneTask.execute(identificativoAmico);
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



	public class EliminazioneAmici_PersoneTask extends AsyncTask<String, Void, Boolean> {

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


			param.add(new BasicNameValuePair("identificativoUtente", dati.getString("id") ));
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

				refreshRichiestaAmiciziaList();
				refreshAmiciList();

			} else {

				Toast.makeText(getActivity(), "Errore. L'amicizia non e' stata rimossa. Riprova!", Toast.LENGTH_SHORT).show();

			}

		}

		protected void onCancelled() {

		}

	}

}