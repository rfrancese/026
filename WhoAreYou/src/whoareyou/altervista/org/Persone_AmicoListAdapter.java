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



public class Persone_AmicoListAdapter extends ArrayAdapter<Persone_Amico> {

	private List<Persone_Amico> itemList;
	private Context context;

	private PersoneFragment personeFragment;


	public Persone_AmicoListAdapter(List<Persone_Amico> itemList, Context context, PersoneFragment personeFragment) {

		super(context, R.layout.row_list_persone_amico, itemList);

		this.itemList = itemList;
		this.context = context;

		this.personeFragment = personeFragment;
	}

	public int getCount() {

		if (itemList != null)

			return itemList.size();

		return 0;
	}

	public Persone_Amico getItem(int position) {

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


		AmiciListHolder holder = new AmiciListHolder();


		ImageView immagine = null;

		// First let's verify the convertView is not null
		if (convertView == null) {

			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_list_persone_amico, null);


			// Now we can fill the layout with the right values
			TextView identificativo = (TextView) convertView.findViewById(R.id.TextView_identificativoAmici_Persone);

			TextView username = (TextView) convertView.findViewById(R.id.TextView_usernameAmici_Persone);


			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineAmici_Persone);



			holder.identificativo = identificativo;

			holder.username = username;



			convertView.setTag(holder);


		} else {

			holder = (AmiciListHolder) convertView.getTag();

			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineAmici_Persone);
		}


		Persone_Amico amico = itemList.get(position);



		//img è la stringa letta dal database

		String img = amico.getImmagine();

		byte[] decodedString = Base64.decode(img, Base64.DEFAULT); 
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		immagine.setImageBitmap(decodedByte);




		holder.identificativo.setText(amico.getIdentificativo());

		holder.username.setText(amico.getUsername());



		View eliminaAmici_Persone = (View) convertView.findViewById(R.id.ImageView_eliminaAmici_Persone);

		eliminaAmici_Persone.setOnClickListener(new EliminaListener(amico.getIdentificativo()));


		return convertView;

	}


	//Listener per ImageView
	public class EliminaListener implements View.OnClickListener {

		private String identificativoAmico;

		public EliminaListener(String identificativoAmico) {

			this.identificativoAmico = identificativoAmico;
		}

		@Override
		public void onClick(View v) {

			personeFragment.eliminazioneAmico(identificativoAmico);
		}

	}


	public List<Persone_Amico> getItemList() {
		return itemList;
	}

	public void setItemList(List<Persone_Amico> itemList) {
		this.itemList = itemList;
	}


	/* *********************************
	 * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

	private static class AmiciListHolder {

		public TextView identificativo; 
		public TextView username;

	}

}