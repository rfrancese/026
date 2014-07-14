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
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.RelativeLayout.LayoutParams;


public class ConversazionePrivataListAdapter extends ArrayAdapter<MessaggioPrivato> {

	private List<MessaggioPrivato> itemList;
	private Context context;


	public ConversazionePrivataListAdapter(List<MessaggioPrivato> itemList, Context context) {

		super(context, R.layout.row_list_conversazione_privata, itemList);
		
		this.itemList = itemList;
		this.context = context;
		
	}

	public int getCount() {

		if (itemList != null)

			return itemList.size();

		return 0;
	}

	public MessaggioPrivato getItem(int position) {

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


		ConversazionePrivataListHolder holder = new ConversazionePrivataListHolder();

		
		ImageView immagine = null, statoMessaggio = null;
		
		// First let's verify the convertView is not null
		if (convertView == null) {

			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_list_conversazione_privata, null); 
			
			// Now we can fill the layout with the right values
			TextView messaggio = (TextView) convertView.findViewById(R.id.TextView_messaggioConversazionePrivata);
			
			TextView data_ora = (TextView) convertView.findViewById(R.id.TextView_dataEOraConversazionePrivata);

			TextView distanza = (TextView) convertView.findViewById(R.id.TextView_distanzaConversazionePrivata);


			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineConversazionePrivata);

			statoMessaggio = (ImageView) convertView.findViewById(R.id.ImageView_statoMessaggioConversazionePrivata);


			
			holder.messaggio = messaggio;
			
			holder.data_ora = data_ora;

			holder.distanza = distanza; 


			convertView.setTag(holder);


		} else {

			holder = (ConversazionePrivataListHolder) convertView.getTag();

			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineConversazionePrivata);
			
			statoMessaggio = (ImageView) convertView.findViewById(R.id.ImageView_statoMessaggioConversazionePrivata);

		}

		
		MessaggioPrivato messaggioPrivato = itemList.get(position);

		
		//img è la stringa letta dal database
		
		String img = messaggioPrivato.getImmagine();
		
		byte[] decodedString = Base64.decode(img, Base64.DEFAULT); 
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		immagine.setImageBitmap(decodedByte);
		

		
		holder.messaggio.setText(messaggioPrivato.getMessaggio());

		
		Date data_oraMessaggioPrivato = null;
		
		String relativeTimeSpanString =	"mm:ss";
		
		try {
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Y-m-d H:i:s in php
			
			data_oraMessaggioPrivato = simpleDateFormat.parse(messaggioPrivato.getData_ora());
			
			long data_oraMessaggioPrivatoTime = data_oraMessaggioPrivato.getTime();
			
			Date now = new Date();
	      
			//DateUtils.MINUTE_IN_MILLIS
			relativeTimeSpanString = (String) DateUtils.getRelativeTimeSpanString(data_oraMessaggioPrivatoTime, now.getTime(), 0, DateUtils.FORMAT_ABBREV_RELATIVE);
			
		} catch (ParseException e) {

		}
		
		
		holder.data_ora.setText(relativeTimeSpanString);

		
		
		
		String distanzaMessaggioPrivatoString = "";
		
		double distanzaMessaggioPrivato = Double.parseDouble(messaggioPrivato.getDistanza());
		
		if ( Math.round(distanzaMessaggioPrivato) < 1) {
			
			int mDistanzaMessaggioPrivato = (int) Math.round(distanzaMessaggioPrivato*1000);
			
			distanzaMessaggioPrivatoString = mDistanzaMessaggioPrivato + " meters from you";
		
		} else if( Math.round(distanzaMessaggioPrivato) >= 1) {
			
			int kmDistanzaMessaggioPrivato = (int) Math.round(distanzaMessaggioPrivato);
			
			distanzaMessaggioPrivatoString = kmDistanzaMessaggioPrivato + " km from you";
		}
		
		holder.distanza.setText(distanzaMessaggioPrivatoString);


		
		LayoutParams layoutParamsImmagine = (LayoutParams) immagine.getLayoutParams();
		LayoutParams layoutParamsMessaggio = (LayoutParams) holder.messaggio.getLayoutParams();
		LayoutParams layoutParamsData_ora = (LayoutParams) holder.data_ora.getLayoutParams();
		LayoutParams layoutParamsDistanza = (LayoutParams) holder.distanza.getLayoutParams();
		LayoutParams layoutParamsStatoMessaggio = (LayoutParams) statoMessaggio.getLayoutParams();

		
		//Check whether message is mine to set Layout
		
		if ( messaggioPrivato.getTipoMessaggioPrivato().equalsIgnoreCase("INVIATO") ) {
			
		
			if ( messaggioPrivato.getStatoMessaggio().equalsIgnoreCase("1") ) {
			
				// Messaggio Letto dal destinatario : setto l'icona con la doppia spunta
				 
				  statoMessaggio.setImageResource(R.drawable.ic_msg_letto); 
			
			} else if ( messaggioPrivato.getStatoMessaggio().equalsIgnoreCase("0") ) {
			
				// Messaggio Non Letto dal destinatario : setto l'icona con la spunta
				
				statoMessaggio.setImageResource(R.drawable.ic_action_accept); 
			}	
			
		
			layoutParamsImmagine.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			layoutParamsImmagine.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			
			
			layoutParamsMessaggio.addRule(RelativeLayout.RIGHT_OF, 0);
			layoutParamsMessaggio.addRule(RelativeLayout.LEFT_OF, R.id.ImageView_immagineConversazionePrivata); 
			layoutParamsMessaggio.setMargins(20, 10, 20, 0);
			
			
			layoutParamsData_ora.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
			layoutParamsData_ora.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			
			
			layoutParamsDistanza.addRule(RelativeLayout.RIGHT_OF, 0);
			layoutParamsDistanza.addRule(RelativeLayout.LEFT_OF, R.id.TextView_dataEOraConversazionePrivata); 
			
			holder.distanza.setVisibility(TextView.GONE);
			
			
			
			layoutParamsStatoMessaggio.addRule(RelativeLayout.RIGHT_OF, 0);
			layoutParamsStatoMessaggio.addRule(RelativeLayout.LEFT_OF, R.id.TextView_distanzaConversazionePrivata); 
						
			statoMessaggio.setVisibility(TextView.VISIBLE);
			
			
			
			holder.messaggio.setLayoutParams(layoutParamsMessaggio);
			holder.data_ora.setLayoutParams(layoutParamsData_ora);
			holder.distanza.setLayoutParams(layoutParamsDistanza);
			
			
		} else if (messaggioPrivato.getTipoMessaggioPrivato().equalsIgnoreCase("RICEVUTO")) { //If not mine then it is from sender to show orange background and align to left
			
						
			
			layoutParamsImmagine.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
			layoutParamsImmagine.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			
			
			layoutParamsMessaggio.addRule(RelativeLayout.LEFT_OF, 0);
			layoutParamsMessaggio.addRule(RelativeLayout.RIGHT_OF, R.id.ImageView_immagineConversazionePrivata); 
			layoutParamsMessaggio.setMargins(10, 10, 10, 0);
			
			
			layoutParamsData_ora.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
			layoutParamsData_ora.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
			
			
			layoutParamsDistanza.addRule(RelativeLayout.LEFT_OF, 0);
			layoutParamsDistanza.addRule(RelativeLayout.RIGHT_OF, R.id.TextView_dataEOraConversazionePrivata);
						
			holder.distanza.setVisibility(TextView.VISIBLE);
			
			
			layoutParamsStatoMessaggio.addRule(RelativeLayout.LEFT_OF, 0);
			layoutParamsStatoMessaggio.addRule(RelativeLayout.RIGHT_OF, R.id.TextView_distanzaConversazionePrivata); 
						
			statoMessaggio.setVisibility(TextView.GONE);
			
			
			
			holder.messaggio.setLayoutParams(layoutParamsMessaggio);
			holder.data_ora.setLayoutParams(layoutParamsData_ora);
			holder.distanza.setLayoutParams(layoutParamsDistanza);
			
		}
		
		
		return convertView;

	}

	public List<MessaggioPrivato> getItemList() {
		return itemList;
	}

	public void setItemList(List<MessaggioPrivato> itemList) {
		this.itemList = itemList;
	}


	/* *********************************
	 * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

	private static class ConversazionePrivataListHolder {

		public TextView messaggio;
		public TextView data_ora;
		public TextView distanza;

	}

}