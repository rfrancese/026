package whoareyou.altervista.org;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class VicinoAMeListAdapter extends ArrayAdapter<UtenteVicinoAMe> {

	private List<UtenteVicinoAMe> itemList;
	private Context context;


	public VicinoAMeListAdapter(List<UtenteVicinoAMe> itemList, Context context) {

		super(context, R.layout.row_list_vicino_a_me, itemList);
		
		this.itemList = itemList;
		this.context = context;
	
	}

	public int getCount() {

		if (itemList != null)

			return itemList.size();

		return 0;
	}

	public UtenteVicinoAMe getItem(int position) {

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


		VicinoAMeListHolder holder = new VicinoAMeListHolder();

		
		ImageView immagine = null;
		
		// First let's verify the convertView is not null
		if (convertView == null) {

			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_list_vicino_a_me, null); 
			
			// Now we can fill the layout with the right values
			TextView identificativo = (TextView) convertView.findViewById(R.id.TextView_identificativoVicinoAMe);

			TextView username = (TextView) convertView.findViewById(R.id.TextView_usernameVicinoAMe);

			TextView data_ora = (TextView) convertView.findViewById(R.id.TextView_dataEOraVicinoAMe);

			TextView distanza = (TextView) convertView.findViewById(R.id.TextView_distanzaVicinoAMe);


			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineVicinoAMe);



			holder.identificativo = identificativo;

			holder.username = username;

			holder.data_ora = data_ora;

			holder.distanza = distanza; 


			convertView.setTag(holder);


		} else {

			holder = (VicinoAMeListHolder) convertView.getTag();

			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineVicinoAMe);
		}

		
		UtenteVicinoAMe utenteVicinoAMe = itemList.get(position);

		
		//img è la stringa letta dal database
		
		String img = utenteVicinoAMe.getImmagine();
		
		byte[] decodedString = Base64.decode(img, Base64.DEFAULT); 
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		immagine.setImageBitmap(decodedByte);
		
		

		
		holder.identificativo.setText(utenteVicinoAMe.getIdentificativo());

		holder.username.setText(utenteVicinoAMe.getUsername());

		
		
		Date data_oraUtenteVicinoAMe = null;
		
		String relativeTimeSpanString =	"mm:ss";
		
		try {
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Y-m-d H:i:s in php
			
			data_oraUtenteVicinoAMe = simpleDateFormat.parse(utenteVicinoAMe.getData_ora());
			
			long data_oraUtenteVicinoAMeTime = data_oraUtenteVicinoAMe.getTime();
			
			Date now = new Date();
	      
			//DateUtils.MINUTE_IN_MILLIS
			relativeTimeSpanString = (String) DateUtils.getRelativeTimeSpanString(data_oraUtenteVicinoAMeTime, now.getTime(), 0, DateUtils.FORMAT_ABBREV_RELATIVE);
			
		} catch (ParseException e) {

		}
		
		
		holder.data_ora.setText(relativeTimeSpanString);

		
		
		
		String distanzaUtenteVicinoAMeString = "";
		
		double distanzaUtenteVicinoAMe = Double.parseDouble(utenteVicinoAMe.getDistanza());
		
		if ( Math.round(distanzaUtenteVicinoAMe) < 1) {
			
			int mDistanzaUtenteVicinoAMe = (int) Math.round(distanzaUtenteVicinoAMe*1000);
			
			distanzaUtenteVicinoAMeString = mDistanzaUtenteVicinoAMe + " meters from you";
		
		} else if( Math.round(distanzaUtenteVicinoAMe) >= 1) {
			
			int kmDistanzaUtenteVicinoAMe = (int) Math.round(distanzaUtenteVicinoAMe);
			
			distanzaUtenteVicinoAMeString = kmDistanzaUtenteVicinoAMe + " km from you";
		}
		
		holder.distanza.setText(distanzaUtenteVicinoAMeString);


		
		return convertView;

	}

	public List<UtenteVicinoAMe> getItemList() {
		return itemList;
	}

	public void setItemList(List<UtenteVicinoAMe> itemList) {
		this.itemList = itemList;
	}


	/* *********************************
	 * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

	private static class VicinoAMeListHolder {

		public TextView identificativo; 
		public TextView username;
		public TextView data_ora;
		public TextView distanza;

	}

}