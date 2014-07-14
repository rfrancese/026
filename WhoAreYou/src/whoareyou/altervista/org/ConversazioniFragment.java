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



public class ConversazioniFragment extends Fragment {


	private MyActionBarActivity myActionBarActivity;

	private TextView textView_emptyListConversazioni;

	private ListView listView_conversazioni;

	private Bundle dati;


	// The data to show
	private List<Conversazione> conversazioneList = new ArrayList<Conversazione>();
	private ConversazioniListAdapter conversazioniListAdapter;


	private ConversazionePrivataFragment conversazionePrivataFragment = null;
	private ConversazioniFragment conversazioniFragment = this;


	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState); 

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		View root = inflater.inflate(R.layout.fragment_conversazioni, container, false);

		return root;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		myActionBarActivity = (MyActionBarActivity) getActivity();

		myActionBarActivity.setConversazioniFragment(this);


		textView_emptyListConversazioni = (TextView) getActivity().findViewById(R.id.TextView_emptyListConversazioni);  


		// We get the ListView component from the layout
		listView_conversazioni = (ListView) getActivity().findViewById(R.id.ListView_Conversazioni);              


		listView_conversazioni.setVisibility(ListView.GONE);



		// Create an empty adapter we will use to display the loaded data.

		conversazioneList = null;

		conversazioniListAdapter = new ConversazioniListAdapter(conversazioneList, getActivity(), this);

		listView_conversazioni.setAdapter(conversazioniListAdapter);

	}	


	@Override
	public void onResume() {

		super.onResume();

		dati = myActionBarActivity.getDatiPersonali();


		mHandler = new Handler();

		mHandler.postDelayed(mRunnable, 0); // 0 ms

	}		


	public void refreshConversazioniList(View view) {

		refreshList();

	}


	private void refreshList() {

		dati = myActionBarActivity.getDatiPersonali();


		if( ! myActionBarActivity.getBooleanCondividiPosizione() ) {

			if( listView_conversazioni.isShown() )
				listView_conversazioni.setVisibility(TextView.GONE);

			if( ! textView_emptyListConversazioni.isShown() )
				textView_emptyListConversazioni.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Impossibile rilevare le conversazioni; permettere la condividisione della propria posizione.", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() == null ) {

			if( listView_conversazioni.isShown() )
				listView_conversazioni.setVisibility(TextView.GONE);

			if( ! textView_emptyListConversazioni.isShown() )
				textView_emptyListConversazioni.setVisibility(TextView.VISIBLE);

			Toast.makeText(getActivity(), "Nessuna posizione rilevata. Impossibile rilevare le conversazioni. Aggiorna!", Toast.LENGTH_LONG).show();

		} else if ( myActionBarActivity.getCurrentLocation() != null ) {

			ConversazioniListTask conversazioniListTask = new ConversazioniListTask();

			if (Networking.isNetworkAvailable(getActivity())) 
				conversazioniListTask.execute(myActionBarActivity.getCurrentLocation());
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

			mHandler.postDelayed(mRunnable, 120000); // 2 minuti
		}
	};


	@Override
	public void onPause() {

		mHandler.removeCallbacks(mRunnable);


		if ( conversazionePrivataFragment != null) {

			if ( conversazionePrivataFragment.isVisible() ) {

				FragmentManager fragmentManager = getFragmentManager(); 

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 

				fragmentTransaction.remove(conversazionePrivataFragment);


				fragmentTransaction.show(conversazioniFragment).commit();
			}

		}

		super.onPause();
	}


	public void vaiAlMioProfilo() {

		mHandler.removeCallbacks(mRunnable);


		if ( conversazionePrivataFragment != null) {

			if ( conversazionePrivataFragment.isVisible() ) {

				FragmentManager fragmentManager = getFragmentManager(); 

				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 

				fragmentTransaction.remove(conversazionePrivataFragment);


				fragmentTransaction.show(conversazioniFragment).commit();
			}
		}

	}



	public class ConversazioniListTask extends AsyncTask<Location, Void, List<Conversazione>> {

		private ProgressDialog dialog = new ProgressDialog(getActivity());  


		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto caricando l'elenco delle conversazioni...");
			dialog.show();
		}

		@Override
		protected List<Conversazione> doInBackground(Location... params) {

			// Get the current location from the input parameter list
			Location loc = params[0];

			double latitudine = loc.getLatitude();

			double longitudine = loc.getLongitude();


			String url_select = "http://whoareyou.altervista.org/conversazioni.php";

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

					List<Conversazione> resultList = new ArrayList<Conversazione>();

					for (int i=0; i < jsonArray.length(); i++) {

						resultList.add(convertConversazione(jsonArray.getJSONObject(i)));

					}

					return resultList;
				}

			} catch (JSONException e) {

			}

			return null;

		}


		private Conversazione convertConversazione(JSONObject obj) throws JSONException {

			String identificativo = obj.getString("identificativo");

			String immagine = obj.getString("immagine");

			String username = obj.getString("username");

			String messaggiDaLeggere = obj.getString("messaggiDaLeggere");


			return new Conversazione(identificativo, immagine, username, messaggiDaLeggere);
		}


		@Override
		protected void onPostExecute(List<Conversazione> resultList) {

			super.onPostExecute(resultList);

			dialog.dismiss();


			if ( resultList == null ) {

				if( listView_conversazioni.isShown() )
					listView_conversazioni.setVisibility(TextView.GONE);

				if( ! textView_emptyListConversazioni.isShown() )
					textView_emptyListConversazioni.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "Impossibile rilevare le conversazioni. Aggiorna!", Toast.LENGTH_SHORT).show();

			} else if (resultList.size() <= 0) {

				if( listView_conversazioni.isShown() )
					listView_conversazioni.setVisibility(TextView.GONE);

				if( ! textView_emptyListConversazioni.isShown() )
					textView_emptyListConversazioni.setVisibility(TextView.VISIBLE);

				Toast.makeText(getActivity(), "Non sono presenti conversazioni!", Toast.LENGTH_SHORT).show();

			} else if( resultList != null && myActionBarActivity.getBooleanCondividiPosizione() ){


				conversazioneList = resultList;

				Comparator<Conversazione> comparator = new Comparator<Conversazione>() {

					@Override
					public int compare(Conversazione conversazione1, Conversazione conversazione2) {

						Integer messaggiDaLeggere1 = Integer.parseInt(conversazione1.getMessaggiDaLeggere());

						Integer messaggiDaLeggere2 = Integer.parseInt(conversazione2.getMessaggiDaLeggere());

						return messaggiDaLeggere1.compareTo(messaggiDaLeggere2);
					}
				};

				Collections.sort(conversazioneList, Collections.reverseOrder(comparator));  //Collections.sort(conversazioneList, comparator);


				if( textView_emptyListConversazioni.isShown() )
					textView_emptyListConversazioni.setVisibility(TextView.GONE);

				if( ! listView_conversazioni.isShown() )
					listView_conversazioni.setVisibility(TextView.VISIBLE);



				conversazioniListAdapter.setItemList(conversazioneList);

				conversazioniListAdapter.notifyDataSetChanged();


				listView_conversazioni.setOnItemClickListener( new ListViewListener() );

			}   
		}

		protected void onCancelled() {

		}

	}


	//Listener per ListView
	public class ListViewListener implements  AdapterView.OnItemClickListener {

		public void onItemClick ( AdapterView<?> listView, View itemView, int position, long itemId ) { 

			Conversazione conversazione = (Conversazione) listView_conversazioni.getItemAtPosition(position);

			String identificativoAmico = conversazione.getIdentificativo();
			String usernameAmico = conversazione.getUsername();
			String immagineAmico = conversazione.getImmagine();

			Bundle bundleConversazionePrivata = new Bundle();

			bundleConversazionePrivata.putString("identificativoAmico", identificativoAmico);
			bundleConversazionePrivata.putString("usernameAmico", usernameAmico);
			bundleConversazionePrivata.putString("immagineAmico", immagineAmico);

			bundleConversazionePrivata.putString("identificativoUtente", dati.getString("id"));
			bundleConversazionePrivata.putString("immagineUtente", dati.getString("img"));



			FragmentManager fragmentManager = getFragmentManager(); 

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); 


			conversazionePrivataFragment = new ConversazionePrivataFragment(bundleConversazionePrivata);


			mHandler.removeCallbacks(mRunnable);


			fragmentTransaction.hide(conversazioniFragment);


			fragmentTransaction.add(R.id.container, conversazionePrivataFragment, "conversazionePrivataFragment");

			fragmentTransaction.addToBackStack(null); 

			fragmentTransaction.commit();

		}

	}



	public void eliminazioneConversazione(final String identificativoAmico) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder
		.setTitle("Eliminare Conversazione")
		.setMessage("Sei sicuro di voler eliminare la conversazione con l'utente: " + identificativoAmico + "?")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {

				dialog.cancel();

				EliminazioneConversazioneTask eliminazioneConversazioneTask = new EliminazioneConversazioneTask();

				if (Networking.isNetworkAvailable(getActivity())) 
					eliminazioneConversazioneTask.execute(identificativoAmico);
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



	public class EliminazioneConversazioneTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(getActivity()); 

		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto eliminando la conversazione...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {


			String url_select = "http://whoareyou.altervista.org/eliminazione_conversazione_privata.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


			//NOTA BENE:				
			//controllare se si deve usare $_SESSION['identificativo']; per recuperare l'id dell'utente lato PHP

			//per prova
			param.add(new BasicNameValuePair("identificativoUtente", dati.getString("id")));
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

				Toast.makeText(getActivity(), "Tutti i tuoi messaggi e tutti i messaggi letti dall'utente sono stati eliminati correttamente!", Toast.LENGTH_LONG).show();

			} else {

				Toast.makeText(getActivity(), "Errore. La conversazione non e' stata eliminata. Riprova!", Toast.LENGTH_SHORT).show();

			}

		}

		protected void onCancelled() {

		}

	}


}