package com.pfhosa.sprinklecity.ui;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.pfhosa.sprinklecity.R;
import com.pfhosa.sprinklecity.database.CustomHttpClient;
import com.pfhosa.sprinklecity.database.Database;
import com.pfhosa.sprinklecity.database.InventoryItemToRemote;
import com.pfhosa.sprinklecity.fragments.InventoryDetailFragment;
import com.pfhosa.sprinklecity.fragments.InventoryDetailFragment.OnInventoryExchangeListener;
import com.pfhosa.sprinklecity.fragments.InventoryExchangeFragment;
import com.pfhosa.sprinklecity.fragments.InventoryExchangeFragment.OnNfcNeededListener;
import com.pfhosa.sprinklecity.fragments.InventoryListFragment;
import com.pfhosa.sprinklecity.fragments.InventoryListFragment.OnInventoryItemSelectedListener;
import com.pfhosa.sprinklecity.model.InventoryItem;
import com.pfhosa.sprinklecity.model.InventoryList;

public class InventoryActivity extends FragmentActivity implements OnInventoryItemSelectedListener, 
OnNfcNeededListener, OnInventoryExchangeListener {

	Bundle mCharacterData;
	InventoryList mInventory;
	InventoryListFragment mInventoryListFragment;	

	NfcAdapter mNfcAdapter;
	NdefMessage mNdefMessage;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
	private String[][] mNFCTechLists;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inventory);	

		mCharacterData = getIntent().getExtras();

		mInventory = new InventoryList(mCharacterData.getString("Username"));
		new InventoryLoaderAsyncTask().execute();		

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// create an intent with tag data and deliver to this activity
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// set an intent filter for all MIME data
		IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefIntent.addDataType("*/*");
			mIntentFilters = new IntentFilter[] {ndefIntent};
		} catch (Exception e) {
			Log.e("TagDispatch", e.toString());
		}		

	}

	public void onResume() {
		super.onResume();

		if (mNfcAdapter != null) {
			mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);
		} 
	}

	public void openInventoryFragment() {
		if (findViewById(R.id.fragment_container_inventory) != null) {

			mInventoryListFragment = new InventoryListFragment();

			mInventoryListFragment.setArguments(mCharacterData);

			getSupportFragmentManager().beginTransaction()
			.add(R.id.fragment_container_inventory, mInventoryListFragment)
			.addToBackStack("mInventoryListFragment")
			.commit();
		}
	}

	public void mInventoryLoader() {
		Database db = Database.getInstance(this);
		db.loadInventoryToLocal(mInventory);		
		openInventoryFragment();	
	}

	public class InventoryLoaderAsyncTask extends AsyncTask<Void, Void, Void> {

		protected Void doInBackground(Void... params) {
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();

			postParameters.add(new BasicNameValuePair("Username", mCharacterData.getString("Username")));
			String response = null, item = null, creator = null;
			int value = 0;
			long timeCreated = 0;
			boolean usable;

			try {
				response = CustomHttpClient.executeHttpPost("http://www2.macs.hw.ac.uk/~ph109/DBConnect/getInventory.php", postParameters);

				String result = response.toString();  

				try{
					JSONArray jArray = new JSONArray(result);		

					for(int i = 0; i < jArray.length(); ++i) {
						JSONObject json_data = jArray.getJSONObject(i);
						creator = json_data.getString("Username");
						item = json_data.getString("Item");
						value = Integer.parseInt(json_data.getString("Value"));
						timeCreated = Long.parseLong(json_data.getString("TimeCreated"));
						usable = "1".equals(json_data.getString("Usable").toString());	

						InventoryItem mInventoryItem = new InventoryItem(creator, item, value, timeCreated, usable);
						mInventory.addItem(item, mInventoryItem);
					}
				} 
				catch (JSONException e){Log.e("log_tag", "Error parsing data "+ e.toString());} 
			} catch (Exception e) {Log.e("log_tag","Error in http connection!!" + e.toString());}  

			return null;
		}
		protected void onPostExecute(Void result) {mInventoryLoader();}
	}

	@Override
	public void onBackPressed() {
		if (mInventoryListFragment.isVisible()) finish();
		else openInventoryFragment();
	}

	@Override
	public void onItemSelected(int position) {
		if (findViewById(R.id.fragment_container_inventory) != null) {

			InventoryDetailFragment inventoryDetailFragment = new InventoryDetailFragment();

			Bundle extendedCharacterData = mCharacterData;
			extendedCharacterData.putString("Item", InventoryList.listFinder(position));
			inventoryDetailFragment.setArguments(mCharacterData);

			getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_container_inventory, inventoryDetailFragment)
			.addToBackStack("inventoryDetailFragment")
			.commit();
		}
	}

	@Override
	public void nfcNeeded(String out) {

		if(mNfcAdapter == null) {
			Toast.makeText(getApplicationContext(), "This phone is not NFC enabled.", Toast.LENGTH_LONG).show();
			return;
		}

		Toast.makeText(getApplicationContext(), "NFC is on.", Toast.LENGTH_LONG).show();

		mNdefMessage = new NdefMessage(
				new NdefRecord[] {
						createNewTextRecord(out, Locale.ENGLISH, true)});


		if (mNfcAdapter != null) {
			mNfcAdapter.setNdefPushMessage(mNdefMessage, this);
		}    

		//mNfcAdapter = null;
	}

	public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char)(utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte)status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
	}

	@Override
	public void onNewIntent(Intent intent) {
		String newItem = "";

		// parse through all NDEF messages and their records and pick text type only
		Parcelable[] data = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (data != null) {
			try {
				for (int i = 0; i < data.length; i++) {
					NdefRecord [] recs = ((NdefMessage)data[i]).getRecords();
					for (int j = 0; j < recs.length; j++) {
						if (recs[j].getTnf() == NdefRecord.TNF_WELL_KNOWN &&
								Arrays.equals(recs[j].getType(), NdefRecord.RTD_TEXT)) {
							byte[] payload = recs[j].getPayload();
							String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
							int langCodeLen = payload[0] & 0077;

							newItem = new String(payload, langCodeLen + 1, payload.length - langCodeLen - 1,
									textEncoding);
						}
					}
				}
			} catch(Exception e) {Log.e("TagDispatch", e.toString());}
		}


		openExchangeDialog(newItem);
	}

	public void openExchangeDialog(final String item) {
		final InventoryItem itemToSwap = new InventoryItem(item);
		new AlertDialog.Builder(this)
		.setTitle("Exchange item")
		.setMessage("Do you want to accept " + itemToSwap.getItem() +  " from " + itemToSwap.getCreator() + "?")
		.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				executeAcceptItem(itemToSwap);
			}
		})
		.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {}
		}).show();
	}

	private void executeAcceptItem(InventoryItem item) {
		item.setCreator(mCharacterData.getString("Username"));
		item.setTimeCollected(true);
		new InventoryItemToRemote(item).execute();
		openInventoryFragment();
	}

	@Override
	public void exchangeSelected(InventoryItem item) {
		if (findViewById(R.id.fragment_container_inventory) == null) return;

		InventoryExchangeFragment inventoryExchangeFragment = new InventoryExchangeFragment();

		Bundle extendedData = mCharacterData;
		mCharacterData.putParcelable("ExchangedItem", item);
		inventoryExchangeFragment.setArguments(extendedData);

		getSupportFragmentManager().beginTransaction()
		.replace(R.id.fragment_container_inventory, inventoryExchangeFragment)
		.addToBackStack("inventoryDetailFragment")
		.commit();

	}
}
