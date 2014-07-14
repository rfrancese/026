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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;


public class Registrazione extends Activity{


	private EditText  identificativo=null;
	private EditText  username=null;
	private EditText password=null;
	private EditText numero=null;
	private EditText email=null;
	private EditText città=null;
	private RadioGroup rg;
	private RadioButton rb;
	private ImageButton data;
	private EditText dataselez=null;
	private ImageButton setimmagine;
	private ImageView immagine;
	private ToggleButton condividiDati;
	private ImageButton salva;
	public static final int SELECT_IMAGE_CODE=123;
	private String absoluteFilePath;
	private String sesso;
	private String condivisione="false";

	private String encodedImage;

	private Calendar cal;
	private int day;
	private int month;
	private int year;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrazione);

		identificativo = (EditText)findViewById(R.id.editText1);
		username = (EditText)findViewById(R.id.editText2);
		password = (EditText)findViewById(R.id.editText3);
		numero = (EditText)findViewById(R.id.editText4);
		email = (EditText)findViewById(R.id.editText5);
		città = (EditText)findViewById(R.id.editText6);
		data = (ImageButton)findViewById(R.id.imageButton1);
		dataselez = (EditText)findViewById(R.id.editText7);
		immagine=(ImageView)findViewById(R.id.imageView1);
		setimmagine = (ImageButton)findViewById(R.id.button1);
		condividiDati = (ToggleButton)findViewById(R.id.toggleButton1);
		salva = (ImageButton)findViewById(R.id.button2);
		rg = (RadioGroup)findViewById(R.id.radioGroup);
		rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());

		dataselez.setFocusable(false);

		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);

		data.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				DatePickerDialog dpd = new DatePickerDialog(Registrazione.this, new DatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
						dataselez.setText(dayOfMonth + "-"
								+ (monthOfYear + 1) + "-" + year);

					}
				}, year, month, day);
				dpd.show();
			}
		});

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
				Registrazione.this.sesso = rb.getText().toString();  

			}
		});

		setimmagine.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){

				startActivityForResult(new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),SELECT_IMAGE_CODE);

			}
		});

		condividiDati.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				if(condividiDati.isChecked()){
					condivisione="true";
				}
				else{
					condivisione="false";
				}

			}
		});
		salva.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){


				String id=identificativo.getText().toString();
				String user=username.getText().toString();
				String psw=password.getText().toString();
				String num=numero.getText().toString();
				String mail=email.getText().toString();
				String city=città.getText().toString();
				String datanasc=dataselez.getText().toString();



				if(absoluteFilePath!=null){
					//per la gestione della foto

					File file = new File(absoluteFilePath);

					try {           
						// Reading a Image file from file system
						FileInputStream imageInFile = new FileInputStream(file);
						byte imageData[] = new byte[(int) file.length()];
						imageInFile.read(imageData);

						// Converting Image byte array into Base64 String
						encodedImage = Base64.encodeToString(imageData, Base64.DEFAULT);

						imageInFile.close();

					} catch (FileNotFoundException e) {

					} catch (IOException ioe) {

					}
				}
				else{

					encodedImage = "";
				}


				if( id.length() >= 21 || user.length() >= 21 || psw.length() >= 11 ) {

					Toast.makeText(getApplicationContext(),"Attenzione! Inserire max 20 caratteri per identificativo e username. Inserire max 10 caratteri per password." ,Toast.LENGTH_LONG).show();

				} else if (id==null || user==null || psw==null || sesso==null || id.equals("") || user.equals("") || psw.equals("") || sesso.equals("")){

					Toast toast=Toast.makeText(getApplicationContext(),"Attenzione: inserire campi obbligatori mancanti." ,Toast.LENGTH_LONG);
					toast.show();

				} else {

					Registration reg=new Registration();
					if (Networking.isNetworkAvailable(getApplicationContext())) 
						reg.execute(id, user, psw, num, mail, city, encodedImage, datanasc, sesso, condivisione );
					else
						Toast.makeText(getApplicationContext(), "Connessione NON disponibile.", Toast.LENGTH_LONG).show();

				}
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {     
		if(requestCode==SELECT_IMAGE_CODE&&resultCode==Activity.RESULT_OK){

			Uri pathimg = data.getData(); 

			immagine.setImageURI(pathimg);

			//per ricavare il path dell'immagine selezionata
			Cursor cursor = getContentResolver().query(pathimg,null, null, null, null);
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			absoluteFilePath = cursor.getString(idx);


		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private class Registration extends AsyncTask<String,String,String>  {

		private ProgressDialog dialog = new ProgressDialog(
				Registrazione.this);
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

			String url = "http://whoareyou.altervista.org/registrazione.php";
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

				if(jArray.length()==1){	

					this.dialog.dismiss();

					Toast toast = Toast.makeText(getApplicationContext(), "Dati salvati. Accedi per iniziare a utilizzare Who Are You?", Toast.LENGTH_LONG);
					toast.show();

					Intent intent = new Intent(getApplicationContext(),MainActivity.class);
					startActivity(intent);
					finish();

				}
				else{

					JSONObject json_data = jArray.getJSONObject(0);

					json_data.getString("errore");

					this.dialog.dismiss();
					Toast toast=Toast.makeText(getApplicationContext(),"Identificativo già esistente. Inserire un nuovo identificativo.",Toast.LENGTH_LONG);
					toast.show();
				} 
			}
			catch (JSONException e) {

			}

		}

	}

}