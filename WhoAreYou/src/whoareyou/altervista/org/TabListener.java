package whoareyou.altervista.org;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar.Tab;


public class TabListener<T extends Fragment> implements android.support.v7.app.ActionBar.TabListener {

	private Fragment mFragment;
	private final Activity mActivity;
	private final String mTag;
	private final Class<T> mClass;


	private MioProfiloFragment mioProfiloFragment = null;


	/** Constructor used each time a new tab is created.
	 * @param activity  The host Activity, used to instantiate the fragment
	 * @param tag  The identifier tag for the fragment
	 * @param clz  The fragment's Class, used to instantiate the fragment
	 */
	public TabListener(Activity activity, String tag, Class<T> clz) {
		mActivity = activity;
		mTag = tag;
		mClass = clz;
	}

	/* The following are each of the ActionBar.TabListener callbacks */

	public void onTabSelected(Tab tab, FragmentTransaction ft) {

		// Check if the fragment is already initialized
		if (mFragment == null) {

			// If not, instantiate and add it to the activity
			mFragment = Fragment.instantiate(mActivity, mClass.getName());

			ft.add(R.id.container, mFragment, mTag);

		} else {

			if( mioProfiloFragment != null && mioProfiloFragment.isVisible() ) {

				ft.remove(mioProfiloFragment);
			}

			if( mFragment.isHidden() ) {

				ft.show(mFragment);

			}

			// If it exists, simply attach it in order to show it
			ft.attach(mFragment);
		}
	}


	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		if( mioProfiloFragment != null && mioProfiloFragment.isVisible() ) {

			ft.remove(mioProfiloFragment);
		}

		if (mFragment != null) {

			if( mFragment.isHidden() ) {

				ft.show(mFragment);
			}

			// Detach the fragment, because another one is being attached
			ft.detach(mFragment);
		}

	}

	public void onTabReselected(Tab tab, FragmentTransaction ft) {

		// User selected the already selected tab.

		if( mioProfiloFragment != null && mioProfiloFragment.isVisible() ) {

			ft.remove(mioProfiloFragment);


			if( mFragment.isHidden() ) {

				ft.show(mFragment);

			}

		}


		if( mFragment.isHidden() ) {

			hideFragment();

			ft.show(mFragment);

		}

	}



	public Fragment getFragment() {

		return mFragment;
	}


	public void vaiAlMioProfilo(FragmentManager fragmentManager, Bundle datipassati) {

		if ( mioProfiloFragment == null) {

			mioProfiloFragment = new MioProfiloFragment();

			mioProfiloFragment.setArguments(datipassati);
		}


		if( mioProfiloFragment != null && mioProfiloFragment.isVisible() ) {

			// il profilo è già visibile 

		} else if( mioProfiloFragment != null && ! mioProfiloFragment.isVisible() ) {

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


			hideFragment();


			fragmentTransaction.hide(mFragment);


			fragmentTransaction.add(R.id.container, mioProfiloFragment, "MioProfilo");


			fragmentTransaction.addToBackStack(null);


			fragmentTransaction.commit();

		}

	}


	private void hideFragment() {

		// metodo da chiamare per rimuovere i fragment aggiunti a quelli standard dei tab; ad esempio in ConversazioniFragment andrà a rimuovere ConversazionePrivataFragment, nelle altre classi rimuoverà il fragment per la visualizzazione del profilo di un utente


		if (mClass.getName().contains("VicinoAMeFragment")) {

			((VicinoAMeFragment) mFragment).vaiAlMioProfilo();

		} else if (mClass.getName().contains("BachecaAvvistamentiFragment")) {

			((BachecaAvvistamentiFragment) mFragment).vaiAlMioProfilo();

		} else if (mClass.getName().contains("ConversazioniFragment")) {

			((ConversazioniFragment) mFragment).vaiAlMioProfilo();

		} else if (mClass.getName().contains("PersoneFragment")) {

			((PersoneFragment) mFragment).vaiAlMioProfilo();

		}

	}


}