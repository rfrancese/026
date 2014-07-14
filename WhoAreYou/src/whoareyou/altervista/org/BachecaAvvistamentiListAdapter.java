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


public class BachecaAvvistamentiListAdapter extends ArrayAdapter<MessaggioAvvistamento> {

	private List<MessaggioAvvistamento> itemList;
	private Context context;


	public BachecaAvvistamentiListAdapter(List<MessaggioAvvistamento> itemList, Context context) {

		super(context, R.layout.row_list_bacheca_avvistamenti, itemList);

		this.itemList = itemList;
		this.context = context;

	}

	public int getCount() {

		if (itemList != null)

			return itemList.size();

		return 0;
	}

	public MessaggioAvvistamento getItem(int position) {

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


		BachecaAvvistamentiListHolder holder = new BachecaAvvistamentiListHolder();


		ImageView immagine = null;

		// First let's verify the convertView is not null
		if (convertView == null) {

			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_list_bacheca_avvistamenti, null);


			// Now we can fill the layout with the right values
			TextView identificativo = (TextView) convertView.findViewById(R.id.TextView_identificativoBachecaAvvistamenti);

			TextView username = (TextView) convertView.findViewById(R.id.TextView_usernameBachecaAvvistamenti);

			TextView messaggio = (TextView) convertView.findViewById(R.id.TextView_messaggioBachecaAvvistamenti);

			TextView data_ora = (TextView) convertView.findViewById(R.id.TextView_dataEOraBachecaAvvistamenti);

			TextView distanza = (TextView) convertView.findViewById(R.id.TextView_distanzaBachecaAvvistamenti);


			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineBachecaAvvistamenti);



			holder.identificativo = identificativo;

			holder.username = username;

			holder.messaggio = messaggio;

			holder.data_ora = data_ora;

			holder.distanza = distanza; 


			convertView.setTag(holder);


		} else {

			holder = (BachecaAvvistamentiListHolder) convertView.getTag();

			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineBachecaAvvistamenti);
		}


		MessaggioAvvistamento messaggioAvvistamento = itemList.get(position);



		//img è la stringa letta dal database

		String img = messaggioAvvistamento.getImmagine();

		byte[] decodedString = Base64.decode(img, Base64.DEFAULT); 
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		immagine.setImageBitmap(decodedByte);




		holder.identificativo.setText(messaggioAvvistamento.getIdentificativo());

		holder.username.setText(messaggioAvvistamento.getUsername());

		holder.messaggio.setText(messaggioAvvistamento.getMessaggio());


		Date data_oraMessaggioAvvistamento = null;

		String relativeTimeSpanString =	"mm:ss";

		try {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Y-m-d H:i:s in php

			data_oraMessaggioAvvistamento = simpleDateFormat.parse(messaggioAvvistamento.getData_ora());

			long data_oraMessaggioAvvistamentoTime = data_oraMessaggioAvvistamento.getTime();

			Date now = new Date();

			//DateUtils.MINUTE_IN_MILLIS
			relativeTimeSpanString = (String) DateUtils.getRelativeTimeSpanString(data_oraMessaggioAvvistamentoTime, now.getTime(), 0, DateUtils.FORMAT_ABBREV_RELATIVE);

		} catch (ParseException e) {

		}


		holder.data_ora.setText(relativeTimeSpanString);




		String distanzaMessaggioAvvistamentoString = "";

		double distanzaMessaggioAvvistamento = Double.parseDouble(messaggioAvvistamento.getDistanza());

		if ( Math.round(distanzaMessaggioAvvistamento) < 1) {

			int mDistanzaMessaggioAvvistamento = (int) Math.round(distanzaMessaggioAvvistamento*1000);

			distanzaMessaggioAvvistamentoString = mDistanzaMessaggioAvvistamento + " meters from you";

		} else if( Math.round(distanzaMessaggioAvvistamento) >= 1) {

			int kmDistanzaMessaggioAvvistamento = (int) Math.round(distanzaMessaggioAvvistamento);

			distanzaMessaggioAvvistamentoString = kmDistanzaMessaggioAvvistamento + " km from you";
		}

		holder.distanza.setText(distanzaMessaggioAvvistamentoString);


		return convertView;

	}

	public List<MessaggioAvvistamento> getItemList() {
		return itemList;
	}

	public void setItemList(List<MessaggioAvvistamento> itemList) {
		this.itemList = itemList;
	}


	/* *********************************
	 * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

	private static class BachecaAvvistamentiListHolder {

		public TextView identificativo; 
		public TextView username;
		public TextView messaggio;
		public TextView data_ora;
		public TextView distanza;

	}

}