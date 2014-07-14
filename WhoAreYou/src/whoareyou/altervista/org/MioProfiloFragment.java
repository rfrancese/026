package whoareyou.altervista.org;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

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
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MioProfiloFragment extends Fragment{

	MyActionBarActivity myActionBarActivity;

	private String id, us, psw, num, em, city, img, dn, sex, con;
	private View v;
	public static final int SELECT_IMAGE_CODE=123;
	private Calendar cal;
	private int day;
	private int month;
	private int year;
	private String path;
	private Uri uri=null;
	private String encodedImage;


	private EditText  identificativo=null;
	private EditText  nickname=null;
	private TextView  username=null;
	private EditText password=null;
	private EditText numero=null;
	private EditText email=null;
	private EditText città=null;
	private ImageButton data, cambiaFoto, elimina;
	private EditText dataselez=null;
	private ImageView immagine;
	private RadioButton rb1, rb2;
	private RadioGroup rg;
	private ToggleButton condividiDati;
	private ImageButton modifica, salva;
	private Bundle dati;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dati = this.getArguments();

		id=dati.getString("id");
		us=dati.getString("us");
		psw=dati.getString("ps");
		num=dati.getString("num");
		em=dati.getString("em");
		city=dati.getString("cit");
		img=dati.getString("img");
		dn=dati.getString("dn");
		sex=dati.getString("sex");
		con=dati.getString("cond");


	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		v = inflater.inflate(R.layout.fragment_mio_profilo, container, false);

		username = (TextView)v.findViewById(R.id.textView1);
		nickname = (EditText)v.findViewById(R.id.editText2);
		identificativo = (EditText)v.findViewById(R.id.editText1);
		password = (EditText)v.findViewById(R.id.editText3);
		numero = (EditText)v.findViewById(R.id.editText4);
		email = (EditText)v.findViewById(R.id.editText5);
		città = (EditText)v.findViewById(R.id.editText6);
		data = (ImageButton)v.findViewById(R.id.imageButton1);
		dataselez = (EditText)v.findViewById(R.id.editText7);
		immagine=(ImageView)v.findViewById(R.id.imageView1);
		rg = (RadioGroup)v.findViewById(R.id.radioGroup);
		rb1 = (RadioButton)v.findViewById(R.id.radioButton1);
		rb2 = (RadioButton)v.findViewById(R.id.radioButton2);
		modifica = (ImageButton)v.findViewById(R.id.button2);
		cambiaFoto = (ImageButton)v.findViewById(R.id.imageButton3);
		condividiDati = (ToggleButton)v.findViewById(R.id.toggleButton1);
		salva = (ImageButton)v.findViewById(R.id.button1);
		elimina=(ImageButton)v.findViewById(R.id.imageButton2);

		identificativo.setText(id);
		nickname.setText(us);
		username.setText(us);
		password.setText(psw);
		numero.setText(num);
		email.setText(em);
		città.setText(city);
		dataselez.setText(dn);

		identificativo.setFocusable(false);
		nickname.setFocusable(false);
		password.setFocusable(false);
		numero.setFocusable(false);
		email.setFocusable(false);
		città.setFocusable(false);
		dataselez.setFocusable(false);

		rb1.setClickable(false);
		rb2.setClickable(false);
		salva.setVisibility(View.GONE);
		cambiaFoto.setVisibility(View.GONE);
		data.setClickable(false);
		condividiDati.setClickable(false);

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
		if(con.equals("true")){
			condividiDati.setChecked(true);
		}
		else{
			condividiDati.setChecked(false);
		}

		modifica.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){

				modifica.setVisibility(View.INVISIBLE);
				salva.setVisibility(View.VISIBLE);
				cambiaFoto.setVisibility(View.VISIBLE);
				data.setClickable(true);
				condividiDati.setClickable(true);
				rb1.setClickable(true);
				rb2.setClickable(true);

				//identificativo non modificabile
				nickname.setFocusableInTouchMode(true);
				password.setFocusableInTouchMode(true);
				numero.setFocusableInTouchMode(true);
				email.setFocusableInTouchMode(true);
				città.setFocusableInTouchMode(true);


				//attivo i bottoni e associo onClick

				data.setClickable(true);
				condividiDati.setClickable(true);

				cal = Calendar.getInstance();
				day = cal.get(Calendar.DAY_OF_MONTH);
				month = cal.get(Calendar.MONTH);
				year = cal.get(Calendar.YEAR);

				data.setOnClickListener(new View.OnClickListener(){
					public void onClick(View view){
						DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								dataselez.setText(dayOfMonth + "-"
										+ (monthOfYear + 1) + "-" + year);

							}
						}, year, month, day);
						dpd.show();
					}
				});

				condividiDati.setOnClickListener(new View.OnClickListener() {
					public void onClick(View view) {

						if(condividiDati.isChecked()){
							con="true";
						}
						else{
							con="false";
						}

					}
				});

			}
		});

		cambiaFoto.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){

				myActionBarActivity.startAct();

			}
		});

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				rb1 = (RadioButton)v.findViewById(rg.getCheckedRadioButtonId());
				MioProfiloFragment.this.sex = rb1.getText().toString();  

			}
		});

		salva.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){

				if(path!=null){
					//per la gestione della foto

					File file = new File(path);

					try {           
						// Reading a Image file from file system
						FileInputStream imageInFile = new FileInputStream(file);
						byte imageData[] = new byte[(int) file.length()];
						imageInFile.read(imageData);

						// Converting Image byte array into Base64 String
						encodedImage = Base64.encodeToString(imageData, Base64.DEFAULT);
						img=encodedImage;


						imageInFile.close();
					} catch (FileNotFoundException e) {

					} catch (IOException ioe) {

					}
				}
				else{

					img=dati.getString("img");
				}

				us=nickname.getText().toString();
				psw=password.getText().toString();
				num=numero.getText().toString();
				em=email.getText().toString();
				city=città.getText().toString();
				dn=dataselez.getText().toString();
				//img
				//sex
				//con
				if(id==null || us==null || psw==null || sex==null || id.equals("") || us.equals("") || psw.equals("") || sex.equals("")){

					Toast toast=Toast.makeText(getActivity(),"Attenzione: inserire campi obbligatori mancanti." ,Toast.LENGTH_LONG);
					toast.show();
				}
				else{

					Update up=new Update();

					if (Networking.isNetworkAvailable(getActivity())) {

						up.execute(id, us, psw, num, em, city, img, dn, sex, con );

						Bundle b=new Bundle();
						b.putString("id", id);
						b.putString("us", us);
						b.putString("ps", psw);
						b.putString("num", num);
						b.putString("em", em);
						b.putString("cit", city);
						b.putString("img", img);
						b.putString("dn", dn);
						b.putString("sex", sex);
						b.putString("cond", id);

						myActionBarActivity.setBundle(b);

						identificativo.setText(id);
						nickname.setText(us);
						username.setText(us);
						password.setText(psw);
						numero.setText(num);
						email.setText(em);
						città.setText(city);
						dataselez.setText(dn);

						if(path!=null){

							immagine.setImageURI(uri);
						}

						identificativo.setFocusable(false);
						nickname.setFocusable(false);
						password.setFocusable(false);
						numero.setFocusable(false);
						email.setFocusable(false);
						città.setFocusable(false);
						dataselez.setFocusable(false);

						rb1.setClickable(false);
						rb2.setClickable(false);
						salva.setVisibility(View.GONE);
						modifica.setVisibility(View.VISIBLE);
						cambiaFoto.setVisibility(View.GONE);
						data.setClickable(false);
						condividiDati.setClickable(false);


					} else

						Toast.makeText(getActivity(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

				}

			}});


		elimina.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

				builder
				.setTitle("Eliminare Conversazione")
				.setMessage("Sei sicuro di voler eliminare definitivamente il tuo profilo e tutto cio' ad esso collegato?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) {

						dialog.cancel();

						EliminazioneProfilo ep = new EliminazioneProfilo();

						if (Networking.isNetworkAvailable(getActivity())) 
							ep.execute(id);
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
			}});


		return v;

	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		myActionBarActivity = (MyActionBarActivity) getActivity();
		myActionBarActivity.setMioProfiloFragment(this);

	}


	@Override
	public void onSaveInstanceState(Bundle outState) {


		// Always call the superclass so it can save the view hierarchy state

		super.onSaveInstanceState(outState);

	}

	public void setPath(String p){

		path=p;

	}

	public void setUri(Uri u){

		uri=u;

	}


	@Override
	public void onResume() {

		super.onResume();
	}		

	@Override
	public void onPause() {

		super.onPause();

	}

	private class Update extends AsyncTask<String,String,String>  {

		private ProgressDialog dialog = new ProgressDialog(getActivity());
		private String i=null;
		private String u=null;
		private String p=null;
		private String n=null;
		private String m=null;
		private String c=null;
		private String ei=null;
		private String d=null;
		private String s=null;
		private String con=null;
		InputStream is = null;
		String result = null;


		protected void onPreExecute() {

			dialog.setCancelable(true);
			dialog.setTitle("Caricamento");
			dialog.setMessage("Sto salvando i dati...");
			dialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			i=params[0];
			u=params[1];
			p=params[2];
			n=params[3];
			m=params[4];
			c=params[5];
			ei=params[6];
			d=params[7];
			s=params[8];
			con=params[9];

			String url = "http://whoareyou.altervista.org/update.php";
			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
			HttpPost httpPost = new HttpPost(url);

			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("identif",i));
			param.add(new BasicNameValuePair("usern",u));
			param.add(new BasicNameValuePair("pass",p));
			param.add(new BasicNameValuePair("numer",n));
			param.add(new BasicNameValuePair("em",m));
			param.add(new BasicNameValuePair("cit",c));
			param.add(new BasicNameValuePair("img", ei));
			param.add(new BasicNameValuePair("dat", d));
			param.add(new BasicNameValuePair("sex", s));
			param.add(new BasicNameValuePair("condiv", con));

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

				Toast toast = Toast.makeText(getActivity(), "Dati salvati.", Toast.LENGTH_LONG);
				toast.show();

				//Intent intent = new Intent(getActivity(),Login.class);
				//startActivity(intent);

			}
			catch (JSONException e) {

			}

		}

	}


	private class EliminazioneProfilo extends AsyncTask<String,Void,Boolean>  {

		private ProgressDialog dialog = new ProgressDialog(getActivity());
		private String i=null;


		protected void onPreExecute() {

			dialog.setCancelable(true);
			dialog.setTitle("Caricamento");
			dialog.setMessage("Sto eliminando definitivamente il tuo profilo...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			i=params[0];

			String url = "http://whoareyou.altervista.org/elimina_profilo.php";
			HttpClient httpClient = HttpClientFactory.getThreadSafeClient();
			HttpPost httpPost = new HttpPost(url);

			ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
			param.add(new BasicNameValuePair("identif",i));

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

				Toast.makeText(getActivity(), "Profilo eliminato correttamente!", Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(getActivity(), MainActivity.class);
				startActivity(intent);
				myActionBarActivity.finish();

			} else {

				Toast.makeText(getActivity(), "Errore. Il profilo non e' stato eliminato. Riprova!", Toast.LENGTH_SHORT).show();

			}

		}

		protected void onCancelled() {

		}

	}


}
