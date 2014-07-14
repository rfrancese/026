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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;


import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



public class MyActionBarActivity extends ActionBarActivity {

	private TabListener<MappaFragment> tabListenerMappaFragment;
	private TabListener<VicinoAMeFragment> tabListenerVicinoAMeFragment;
	private TabListener<BachecaAvvistamentiFragment> tabListenerBachecaAvvistamentiFragment;
	private TabListener<ConversazioniFragment> tabListenerConversazioniFragment;
	private TabListener<PersoneFragment> tabListenerPersoneFragment;

	public static final int SELECT_IMAGE_CODE=123;
	private String absoluteFilePath;


	private LocationManager locationManager;
	private final Criteria criteria = new Criteria();
	private static int minUpdateTime = (int) (5 * 60.000); // in milliseconds, 1 min => 60.000 millisecondi 
	private static int minUpdateDistance = 500; // in meters 


	private Location currentLocation;

	private boolean booleanCondividiPosizione;


	private ActionBar actionBar;
	private MappaFragment mappaFragment;
	private VicinoAMeFragment vicinoAMeFragment;
	private BachecaAvvistamentiFragment bachecaAvvistamentiFragment;
	private ConversazioniFragment conversazioniFragment;
	private ConversazionePrivataFragment conversazionePrivataFragment;
	private PersoneFragment personeFragment;
	private MioProfiloFragment mioProfiloFragment;


	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";


	// Passo i dati dell'utente presi con la query al login
	private Bundle datipassati; 


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_action_bar);


		// dati passati dal login
		datipassati = getIntent().getExtras(); 


		// setup action bar for tabs
		actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);



		//AGGIUNTO IL 16/06/2014

		tabListenerMappaFragment = new TabListener<MappaFragment>(this, "mappa", MappaFragment.class);

		Tab tab = actionBar.newTab()
				.setIcon(R.drawable.ic_action_map)
				.setText(R.string.action_mappa)
				.setTabListener(tabListenerMappaFragment);
		actionBar.addTab(tab, 0);



		tabListenerVicinoAMeFragment = new TabListener<VicinoAMeFragment>(this, "vicinoAMe", VicinoAMeFragment.class);

		tab = actionBar.newTab()
				.setIcon(R.drawable.ic_action_place)
				.setText(R.string.action_vicinoAMe)
				.setTabListener(tabListenerVicinoAMeFragment);
		actionBar.addTab(tab, 1);



		tabListenerBachecaAvvistamentiFragment = new TabListener<BachecaAvvistamentiFragment>(this, "bachecaAvvistamenti", BachecaAvvistamentiFragment.class);

		tab = actionBar.newTab()
				.setIcon(R.drawable.ic_action_view_as_list)
				.setText(R.string.action_bachecaAvvistamenti)
				.setTabListener(tabListenerBachecaAvvistamentiFragment);
		actionBar.addTab(tab, 2);


		tabListenerConversazioniFragment = new TabListener<ConversazioniFragment>(this, "conversazioni", ConversazioniFragment.class);

		tab = actionBar.newTab()
				.setIcon(R.drawable.ic_action_email_tab)
				.setText(R.string.action_conversazioni)
				.setTabListener(tabListenerConversazioniFragment);
		actionBar.addTab(tab, 3);


		tabListenerPersoneFragment = new TabListener<PersoneFragment>(this, "persone", PersoneFragment.class);

		tab = actionBar.newTab()
				.setIcon(R.drawable.ic_action_add_group)
				.setText(R.string.action_persone)
				.setTabListener(tabListenerPersoneFragment);
		actionBar.addTab(tab, 4);



		// Get a reference to the Location Manager
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		// Specify Location Provider criteria
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(true);


		mappaFragment = ((MappaFragment) tabListenerMappaFragment.getFragment());

	}


	/* Main Activity Actions - Profilo e Logout */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle presses on the action bar items
		switch (item.getItemId()) {

		case R.id.action_mioProfilo:

			vaiAlMioProfilo();

			return true;

		case R.id.action_logout:

			eseguiLogout();

			return true;

		default:

			return super.onOptionsItemSelected(item);
		}

	}



	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current tab position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current tab position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}


	/*  
	  @Override
	public void onBackPressed() {

		  FragmentManager fragmentManager = getSupportFragmentManager();

		  FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		  switch ( actionBar.getSelectedNavigationIndex() ) {

			case 0: //action_mappa

				tabListenerMappaFragment.onTabUnselected(actionBar.getSelectedTab(), fragmentTransaction);
				tabListenerMappaFragment.onTabSelected(actionBar.getTabAt(0), fragmentTransaction);

				break;

			case 1: //action_vicinoAMe

				tabListenerVicinoAMeFragment.onTabUnselected(actionBar.getSelectedTab(), fragmentTransaction);
				tabListenerMappaFragment.onTabSelected(actionBar.getTabAt(0), fragmentTransaction);

				break;

			case 2: //action_bachecaAvvistamenti 

				tabListenerBachecaAvvistamentiFragment.onTabUnselected(actionBar.getSelectedTab(), fragmentTransaction);
				tabListenerMappaFragment.onTabSelected(actionBar.getTabAt(0), fragmentTransaction);

				break;

			case 3: //action_conversazioni

				tabListenerConversazioniFragment.onTabUnselected(actionBar.getSelectedTab(), fragmentTransaction);
				tabListenerMappaFragment.onTabSelected(actionBar.getTabAt(0), fragmentTransaction);

				break;

			case 4: //action_persone

				tabListenerPersoneFragment.onTabUnselected(actionBar.getSelectedTab(), fragmentTransaction);
				tabListenerMappaFragment.onTabSelected(actionBar.getTabAt(0), fragmentTransaction);

				break;

			default:

				Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show();
				break;
		}

		  //	super.onBackPressed();
	}
	 */  


	public Bundle getDatiPersonali() {

		return datipassati;

	}

	public void startAct(){
		startActivityForResult(new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),SELECT_IMAGE_CODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {     
		if(requestCode==SELECT_IMAGE_CODE&&resultCode==Activity.RESULT_OK){

			Uri pathimg=data.getData();
			((ImageView)findViewById(R.id.imageView1)).setImageURI(pathimg);

			//per ricavare il path dell'immagine selezionata
			Cursor cursor = getContentResolver().query(pathimg,null, null, null, null);
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			absoluteFilePath = cursor.getString(idx);

			mioProfiloFragment.setPath(absoluteFilePath);
			mioProfiloFragment.setUri(pathimg);


		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void setBundle(Bundle b){

		datipassati = b;

	}

	public void setMioProfiloFragment(MioProfiloFragment mioProfiloFragment) {

		this.mioProfiloFragment = mioProfiloFragment;
	}




	private void vaiAlMioProfilo() {

		FragmentManager fragmentManager = getSupportFragmentManager();

		switch ( actionBar.getSelectedNavigationIndex() ) {

		case 0: //action_mappa

			tabListenerMappaFragment.vaiAlMioProfilo(fragmentManager, datipassati);
			break;

		case 1: //action_vicinoAMe

			tabListenerVicinoAMeFragment.vaiAlMioProfilo(fragmentManager, datipassati);
			break;

		case 2: //action_bachecaAvvistamenti 

			tabListenerBachecaAvvistamentiFragment.vaiAlMioProfilo(fragmentManager, datipassati);
			break;

		case 3: //action_conversazioni

			tabListenerConversazioniFragment.vaiAlMioProfilo(fragmentManager, datipassati);
			break;

		case 4: //action_persone

			tabListenerPersoneFragment.vaiAlMioProfilo(fragmentManager, datipassati);
			break;

		default:

			Toast.makeText(this, "Errore", Toast.LENGTH_SHORT).show();
			break;
		}


	}

	private void eseguiLogout() {

		LogoutTask logoutTask = new LogoutTask();

		if (Networking.isNetworkAvailable(this)) 
			logoutTask.execute();

		else
			Toast.makeText(this, "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

	}



	public void infoCurrentLocation(View view) {

		mappaFragment.infoCurrentLocation(view);

	}


	public void refreshCurrentLocation(View view) {

		mappaFragment.refreshCurrentLocation(view);

		condividiPosizione();
	}


	public Location getCurrentLocation() {

		if( currentLocation == null )
			currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

		return currentLocation;
	}



	public void setVicinoAMeFragment(VicinoAMeFragment vicinoAMeFragment) {

		this.vicinoAMeFragment = vicinoAMeFragment;
	}

	public void refreshVicinoAMeList(View view) {

		vicinoAMeFragment.refreshVicinoAMeList(view);

		condividiPosizione();
	}




	public void setBachecaAvvistamentiFragment(BachecaAvvistamentiFragment bachecaAvvistamentiFragment) {

		this.bachecaAvvistamentiFragment = bachecaAvvistamentiFragment;
	}

	public void refreshBachecaAvvistamentiList(View view) {

		bachecaAvvistamentiFragment.refreshBachecaAvvistamentiList(view);

		condividiPosizione();

	}

	public void invioMessaggioAvvistamento(View view) {

		bachecaAvvistamentiFragment.invioMessaggioAvvistamento(view);
	}




	public void setConversazioniFragment(ConversazioniFragment conversazioniFragment) {

		this.conversazioniFragment = conversazioniFragment;
	}

	public void refreshConversazioniList(View view) {

		conversazioniFragment.refreshConversazioniList(view);

		condividiPosizione();
	}




	public void setConversazionePrivataFragment(ConversazionePrivataFragment conversazionePrivataFragment) {

		this.conversazionePrivataFragment = conversazionePrivataFragment;
	}

	public void refreshConversazionePrivataList(View view) {

		conversazionePrivataFragment.refreshConversazionePrivataList(view);

		condividiPosizione();

	}

	public void invioMessaggioPrivato(View view) {

		conversazionePrivataFragment.invioMessaggioPrivato(view);
	}




	public void setPersoneFragment(PersoneFragment personeFragment) {

		this.personeFragment = personeFragment;
	}

	public void refreshRichiestaAmicizia_PersoneList(View view) {

		personeFragment.refreshRichiestaAmicizia_PersoneList(view);

		condividiPosizione();
	}

	public void refreshAmici_PersoneList(View view) {

		personeFragment.refreshAmici_PersoneList(view);

		condividiPosizione();
	}



	@Override
	protected void onPause() {

		unregisterAllListeners();

		super.onPause();
	}

	@Override
	protected void onResume() {

		super.onResume();

		registerListener();
	}


	private void registerListener() {

		unregisterAllListeners();

		String bestProvider = locationManager.getBestProvider(criteria, false);

		String bestAvailableProvider = locationManager.getBestProvider(criteria, true);


		if (bestProvider == null)

			Toast.makeText(this, "No Location Providers exist on device.", Toast.LENGTH_SHORT).show();

		else if ( bestProvider.equals(bestAvailableProvider) )

			locationManager.requestLocationUpdates(bestAvailableProvider,
					minUpdateTime, minUpdateDistance,
					bestAvailableProviderListener);
		else {

			locationManager.requestLocationUpdates(bestProvider,
					minUpdateTime, minUpdateDistance, bestProviderListener);

			if (bestAvailableProvider != null)

				locationManager.requestLocationUpdates(bestAvailableProvider,
						minUpdateTime, minUpdateDistance,
						bestAvailableProviderListener);
			else {

				List<String> allProviders = locationManager.getAllProviders();

				for (String provider : allProviders)
					locationManager.requestLocationUpdates(provider, 0, 0,
							bestProviderListener);

			}
		}
	}


	private void unregisterAllListeners() {

		locationManager.removeUpdates(bestProviderListener);

		locationManager.removeUpdates(bestAvailableProviderListener);
	}

	private void reactToLocationChange(Location location) {

		currentLocation = location;

		condividiPosizione();

		mappaFragment.newLocation();

	}


	private LocationListener bestProviderListener = new LocationListener() {

		public void onLocationChanged(Location location) {

			reactToLocationChange(location);
		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {

			registerListener();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	};


	private LocationListener bestAvailableProviderListener = new LocationListener() {

		public void onProviderEnabled(String provider) {

		}

		public void onProviderDisabled(String provider) {

			registerListener();
		}

		public void onLocationChanged(Location location) {

			reactToLocationChange(location);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

	};


	public boolean getBooleanCondividiPosizione() {

		return booleanCondividiPosizione;

	}

	public void setBooleanCondividiPosizione(boolean bool) {

		booleanCondividiPosizione = bool;

		condividiPosizione();

	}



	// metodo per gestire la condivisione della posizione

	public void condividiPosizione() {

		CondividiPosizioneTask condividiPosizioneTask;

		if ( booleanCondividiPosizione ) { // si vuole condividere la propria posizione 

			condividiPosizioneTask = new CondividiPosizioneTask();


			if( currentLocation != null) {  

				if (Networking.isNetworkAvailable(this)) 
					condividiPosizioneTask.execute(currentLocation);
				else
					Toast.makeText(this, "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

			}	


		} else { // NON si vuole condividere la propria posizione 


			if( currentLocation != null ) {

				condividiPosizioneTask = new CondividiPosizioneTask();

				Location nullLocation = new Location("NULL");

				if (Networking.isNetworkAvailable(this)) 
					condividiPosizioneTask.execute(nullLocation);
				else
					Toast.makeText(this, "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

			}

		} 
	}



	//classe per per gestire la condivisione della posizione

	public class CondividiPosizioneTask extends AsyncTask<Location, Void, Boolean> {

		private static final double LATITUDINE_NULL = (double) 0;
		private static final double LONGITUDINE_NULL = (double) 0;

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Boolean doInBackground(Location... params) {

			// Get the current location from the input parameter list
			Location loc = params[0];

			double latitudine = loc.getLatitude();

			double longitudine = loc.getLongitude();


			String url_select = "http://whoareyou.altervista.org/condividi_posizione.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();

			// i dati dell'utente dal login sono salvati nella variabile Bundle datipassati
			param.add(new BasicNameValuePair("identificativo", datipassati.getString("id")));


			if (latitudine == LATITUDINE_NULL && longitudine == LONGITUDINE_NULL) 

				param.add(new BasicNameValuePair("posizione_condivisa", Boolean.toString(false)));

			else
				param.add(new BasicNameValuePair("posizione_condivisa", Boolean.toString(true)));



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


				if( status.equalsIgnoreCase("OK") ) {

					return true;
				}

			} catch (JSONException e) {

			}

			return false;

		}	

		protected void onPostExecute(boolean status) {

			if(! status) 
				Toast.makeText(MyActionBarActivity.this, "Errore nella condivisione della posizione. Riprova!", Toast.LENGTH_SHORT).show();

		}

		@Override
		protected void onCancelled() {

		}


	}



	//metodo per il logout
	public class LogoutTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog dialog = new ProgressDialog(MyActionBarActivity.this); 


		@Override
		protected void onPreExecute() {

			super.onPreExecute();

			dialog.setMessage("Sto eseguendo il logout...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {


			String url_select = "http://whoareyou.altervista.org/logout.php";

			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();

			HttpPost httpPost = new HttpPost(url_select);


			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();


			param.add(new BasicNameValuePair("identificativo", datipassati.getString("id")));


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

				Toast.makeText(MyActionBarActivity.this, "Logout eseguito correttamente. A presto!", Toast.LENGTH_SHORT).show();

				Intent intent=new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();

			} else {

				Toast.makeText(null, "Errore. Il logout non e' stato eseguito. Riprova!", Toast.LENGTH_SHORT).show();

			}

		}

		protected void onCancelled() {

		}

	}


}