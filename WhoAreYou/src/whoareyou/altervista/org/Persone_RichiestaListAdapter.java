package whoareyou.altervista.org;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class Persone_RichiestaListAdapter extends ArrayAdapter<Persone_Richiesta> {

	private List<Persone_Richiesta> itemList;
	private Context context;

	private PersoneFragment personeFragment;


	public Persone_RichiestaListAdapter(List<Persone_Richiesta> itemList, Context context, PersoneFragment personeFragment) {

		super(context, R.layout.row_list_persone_richiesta_amicizia, itemList);

		this.itemList = itemList;
		this.context = context;

		this.personeFragment = personeFragment;
	}

	public int getCount() {

		if (itemList != null)

			return itemList.size();

		return 0;
	}

	public Persone_Richiesta getItem(int position) {

		if (itemList != null)

			return itemList.get(position);

		return null;
	}

	public long getItemId(int position) {

		if (itemList != null)

			return itemList.get(position).hashCode();

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		RichiestaAmiciziaListHolder holder = new RichiestaAmiciziaListHolder();


		ImageView immagine = null, accettaRichiestaAmicizia = null, rifiutaRichiestaAmicizia = null;

		// First let's verify the convertView is not null
		if (convertView == null) {

			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_list_persone_richiesta_amicizia, null); 


			// Now we can fill the layout with the right values
			TextView identificativo = (TextView) convertView.findViewById(R.id.TextView_identificativoRichiestaAmicizia_Persone);

			TextView username = (TextView) convertView.findViewById(R.id.TextView_usernameRichiestaAmicizia_Persone);


			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineRichiestaAmicizia_Persone);



			holder.identificativo = identificativo;

			holder.username = username;



			convertView.setTag(holder);


		} else {

			holder = (RichiestaAmiciziaListHolder) convertView.getTag();

			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineRichiestaAmicizia_Persone);
		}


		Persone_Richiesta richiesta = itemList.get(position);



		//img è la stringa letta dal database

		String img = richiesta.getImmagine();

		byte[] decodedString = Base64.decode(img, Base64.DEFAULT); 
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		immagine.setImageBitmap(decodedByte);




		holder.identificativo.setText(richiesta.getIdentificativo());

		holder.username.setText(richiesta.getUsername());


		accettaRichiestaAmicizia = (ImageView) convertView.findViewById(R.id.ImageView_accettaRichiestaAmicizia_Persone);

		rifiutaRichiestaAmicizia = (ImageView) convertView.findViewById(R.id.ImageView_rifiutaRichiestaAmicizia_Persone);


		accettaRichiestaAmicizia.setOnClickListener(new AccettaListener(richiesta.getIdentificativo()));

		rifiutaRichiestaAmicizia.setOnClickListener(new RifiutaListener(richiesta.getIdentificativo()));


		return convertView;

	}


	//Listener per ImageView
	public class AccettaListener implements View.OnClickListener {

		private String identificativoAmico;

		public AccettaListener(String identificativoAmico) {

			this.identificativoAmico = identificativoAmico;
		}

		@Override
		public void onClick(View v) {

			personeFragment.accettaRichiestaAmicizia(identificativoAmico);
		}

	}

	//Listener per ImageView
	public class RifiutaListener implements View.OnClickListener {

		private String identificativoAmico;

		public RifiutaListener(String identificativoAmico) {

			this.identificativoAmico = identificativoAmico;
		}

		@Override
		public void onClick(View v) {

			personeFragment.rifiutaRichiestaAmicizia(identificativoAmico);
		}

	}


	public List<Persone_Richiesta> getItemList() {
		return itemList;
	}

	public void setItemList(List<Persone_Richiesta> itemList) {
		this.itemList = itemList;
	}


	/* *********************************
	 * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

	private static class RichiestaAmiciziaListHolder {

		public TextView identificativo; 
		public TextView username;

	}

}