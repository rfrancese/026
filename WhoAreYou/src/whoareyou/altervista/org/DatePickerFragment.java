package whoareyou.altervista.org;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;


public class DatePickerFragment extends DialogFragment
implements OnDateSetListener {


	private DatePickerDialog dpd;
	DataNascita data=new DataNascita();

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker

		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it

		dpd=new DatePickerDialog(getActivity(), this, year, month, day);

		return dpd;
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user

		GregorianCalendar cal=new GregorianCalendar(year, month, day);

		data.setDataNascita(new Date(cal.getTimeInMillis()));

	}

}