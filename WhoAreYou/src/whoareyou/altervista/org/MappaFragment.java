package whoareyou.altervista.org;


import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;


import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import android.app.Dialog;

import android.support.v4.app.Fragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import android.widget.Toast;
import android.widget.ToggleButton;


public class MappaFragment extends Fragment {

	private boolean booleanCondividiPosizione;
	private ToggleButton toggleButtonCondividiPosizione;

	private MapView mapView;
	private GoogleMap googleMap;

	private Location mCurrentLocation; 

	private MyActionBarActivity myActionBarActivity;

	private GetAddressTask addressTask;
	private String addressString;



	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState); 

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);

		View root = inflater.inflate(R.layout.fragment_mappa, container, false); 

		// Gets the MapView from the XML layout and creates it
		mapView = (MapView) root.findViewById(R.id.MapView);
		mapView.onCreate(savedInstanceState);


		initilizeMap(); 


		MapsInitializer.initialize(this.getActivity());


		return root;
	}


	private void initilizeMap() {

		// Getting Google Play availability status
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

		// Showing status
		if(status != ConnectionResult.SUCCESS){ // Google Play Services are not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, getActivity(), requestCode);
			dialog.show();

		} else { // Google Play Services are available

			// Gets to GoogleMap from the MapView and does initialization stuff
			googleMap = mapView.getMap();
			googleMap.getUiSettings().setMyLocationButtonEnabled(false);
			googleMap.setMyLocationEnabled(true);

		}
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		myActionBarActivity = (MyActionBarActivity) getActivity();


		toggleButtonCondividiPosizione = (ToggleButton) getActivity().findViewById(R.id.ToggleButton_condividiPosizione);

		toggleButtonCondividiPosizione.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					// The toggle is enabled
					booleanCondividiPosizione = true;

					myActionBarActivity.setBooleanCondividiPosizione(booleanCondividiPosizione); // setto il valore e chiamo condividi posizione


				} else {
					// The toggle is disabled
					booleanCondividiPosizione = false;

					myActionBarActivity.setBooleanCondividiPosizione(booleanCondividiPosizione);

				}
			}
		} );


		// Check whether we're recreating a previously destroyed instance
		if (savedInstanceState != null) {
			// Restore last state for toggle button.
			booleanCondividiPosizione = savedInstanceState.getBoolean("condividiPosizione");

			toggleButtonCondividiPosizione.setChecked(booleanCondividiPosizione);


		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		// Save the user's state for toggle button.

		outState.putBoolean("condividiPosizione", booleanCondividiPosizione );


		// Always call the superclass so it can save the view hierarchy state

		super.onSaveInstanceState(outState);

	}


	@Override
	public void onResume() {

		currentLocation();

		mapView.onResume();

		super.onResume();
	}		

	@Override
	public void onPause() {

		super.onPause();

	}


	public void  currentLocation() {

		mCurrentLocation = myActionBarActivity.getCurrentLocation(); 

		addressString = null; // Ripristino l'indirizzo al valore nullo 


		if (mCurrentLocation != null) {

			if ( addressTask != null) {

				addressTask.cancel(true);

				addressTask = null;

			}

			if ( addressTask == null) {

				addressTask = new GetAddressTask();

				if (Networking.isNetworkAvailable(getActivity())) 
					addressTask.execute(mCurrentLocation);
				else
					Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

			} 


			LatLng mCurrentLocationLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());


			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrentLocationLatLng, 13));

			CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()))      // Sets the center of the map to location user
			.zoom(13)                   // Sets the zoom
			.build();                   // Creates a CameraPosition from the builder
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


			googleMap.addMarker(new MarkerOptions()
			.title("Tu sei qui!")
			.position(mCurrentLocationLatLng));


		}	else {

			Toast.makeText(getActivity(), "Non e' stata rilevata una posizione. Aggiorna!" , Toast.LENGTH_SHORT).show();

		}

	}


	public void infoCurrentLocation(View view) {

		String latLongString = "No location found";

		if(addressString == null)
			addressString = "No address found";


		// If mCurrentLocation is available
		if ( mCurrentLocation != null ) {

			double latitude = mCurrentLocation.getLatitude();

			double longitude = mCurrentLocation.getLongitude();

			// Report the current location
			latLongString = "Lat:" + Double.toString(latitude) 
					+ "\nLong:" + Double.toString(longitude);

		}

		String msg = "Your Current Position is:\n" + latLongString + "\n\n" + addressString;

		Toast.makeText(getActivity(), msg , Toast.LENGTH_LONG).show();

	}


	public void refreshCurrentLocation(View view) {

		googleMap.clear();

		currentLocation();

		Toast.makeText(getActivity(), "Info: Mappa aggiornata.", Toast.LENGTH_SHORT).show();

	}

	public void newLocation() {

		Toast.makeText(getActivity(), "Rilevata nuova posizione, aggiorna!", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onDestroy() {

		super.onDestroy();

		mapView.onDestroy();

	}

	@Override
	public void onLowMemory() {

		super.onLowMemory();

		mapView.onLowMemory();

	}


	//classe per convertire le coordinate in un indirizzo
	public class GetAddressTask extends AsyncTask<Location, Void, String> {


		private double latitude;
		private double longitude;

		@Override
		protected void onPreExecute() {

		}

		private JSONObject getLocationInfo(double lat, double lng) {

			HttpGet httpGet = new HttpGet("http://maps.googleapis.com/maps/api/geocode/json?latlng="+ lat+","+lng +"&sensor=true");

			HttpClient client = HttpClientFactory.getThreadSafeClient();

			HttpResponse response;

			StringBuilder stringBuilder = new StringBuilder();

			try {
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {

					stringBuilder.append((char) b);
				}

			} catch (ClientProtocolException e) {


			} catch (IOException e) {

			}

			JSONObject jsonObject = new JSONObject();

			try {

				jsonObject = new JSONObject(stringBuilder.toString());

			} catch (JSONException e) {

			}

			return jsonObject;
		}

		@Override
		protected String doInBackground(Location... params) {

			String addressString = "No address found";

			// Get the current location from the input parameter list
			Location loc = params[0];

			latitude = loc.getLatitude();

			longitude = loc.getLongitude();


			JSONObject jsonObj = getLocationInfo(latitude, longitude);

			String street_address = null;
			String postal_code = null; 


			try {

				String status = jsonObj.getString("status").toString();


				if(status.equalsIgnoreCase("OK")){

					JSONArray results = jsonObj.getJSONArray("results");

					int i = 0;	    		       
					do {

						JSONObject r = results.getJSONObject(i);

						JSONArray typesArray = r.getJSONArray("types");

						String types = typesArray.getString(0);


						if(types.equalsIgnoreCase("street_address")) {

							street_address = r.getString("formatted_address");

						} else if(types.equalsIgnoreCase("postal_code")) {

							postal_code = r.getString("formatted_address");

						}


						if(street_address != null && postal_code == null) {

							addressString = street_address;

							i = results.length();
						}

						if(street_address == null && postal_code != null) {

							addressString = postal_code;

							i = results.length();
						}


						i++;

					} while( i<results.length() );


					return addressString;
				}

			} catch (JSONException e) {

			}


			return addressString;
		}

		@Override
		protected void onPostExecute(String address) {

			addressString = address;
		}

		@Override
		protected void onCancelled() {

		}


	}

}