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



public class ConversazioniListAdapter extends ArrayAdapter<Conversazione> {

	private List<Conversazione> itemList;
	private Context context;

	private ConversazioniFragment conversazioniFragment;


	public ConversazioniListAdapter(List<Conversazione> itemList, Context context, ConversazioniFragment conversazioniFragment) {

		super(context, R.layout.row_list_conversazioni, itemList);

		this.itemList = itemList;
		this.context = context;

		this.conversazioniFragment = conversazioniFragment;
	}

	public int getCount() {

		if (itemList != null)

			return itemList.size();

		return 0;
	}

	public Conversazione getItem(int position) {

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


		ConversazioniListHolder holder = new ConversazioniListHolder();


		ImageView immagine = null, immagineMessaggiDaLeggere = null;

		// First let's verify the convertView is not null
		if (convertView == null) {

			// This a new view we inflate the new layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_list_conversazioni, null); //parent, false


			// Now we can fill the layout with the right values
			TextView identificativo = (TextView) convertView.findViewById(R.id.TextView_identificativoConversazioni);

			TextView username = (TextView) convertView.findViewById(R.id.TextView_usernameConversazioni);

			TextView messaggiDaLeggere = (TextView) convertView.findViewById(R.id.TextView_messaggiDaLeggereConversazioni);

			immagineMessaggiDaLeggere = (ImageView) convertView.findViewById(R.id.ImageView_messaggiDaLeggereConversazioni);


			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineConversazioni);



			holder.identificativo = identificativo;

			holder.username = username;

			holder.messaggiDaLeggere = messaggiDaLeggere;


			convertView.setTag(holder);


		} else {

			holder = (ConversazioniListHolder) convertView.getTag();

			immagineMessaggiDaLeggere = (ImageView) convertView.findViewById(R.id.ImageView_messaggiDaLeggereConversazioni);

			immagine = (ImageView) convertView.findViewById(R.id.ImageView_immagineConversazioni);
		}


		Conversazione conversazione = itemList.get(position);



		//img è la stringa letta dal database

		String img = conversazione.getImmagine();

		byte[] decodedString = Base64.decode(img, Base64.DEFAULT); 
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		immagine.setImageBitmap(decodedByte);




		holder.identificativo.setText(conversazione.getIdentificativo());

		holder.username.setText(conversazione.getUsername());


		if( conversazione.getMessaggiDaLeggere().equalsIgnoreCase("0") ) {

			holder.messaggiDaLeggere.setText("");

			immagineMessaggiDaLeggere.setImageResource(R.drawable.ic_action_read);

		} else { // ci sono nuovi messaggi

			holder.messaggiDaLeggere.setText(conversazione.getMessaggiDaLeggere());

			immagineMessaggiDaLeggere.setImageResource(R.drawable.ic_action_unread);

		}



		View eliminaConversazione = (View) convertView.findViewById(R.id.ImageView_eliminaConversazioni);

		eliminaConversazione.setOnClickListener(new EliminaListener(conversazione.getIdentificativo()));



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

			conversazioniFragment.eliminazioneConversazione(identificativoAmico);
		}

	}



	public List<Conversazione> getItemList() {
		return itemList;
	}

	public void setItemList(List<Conversazione> itemList) {
		this.itemList = itemList;
	}


	/* *********************************
	 * We use the holder pattern
	 * It makes the view faster and avoid finding the component
	 * **********************************/

	private static class ConversazioniListHolder {

		public TextView identificativo; 
		public TextView username;
		public TextView messaggiDaLeggere;

	}

}