package atua.anddev.globaltv;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.util.*;
import java.io.*;

public class SearchlistActivity extends CatlistActivity
{
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.searchlist);

		showSearchResults();

	}

	public void showSearchResults()
	{
		final ArrayList<String> playlist = new ArrayList<String>();
		final ArrayList<String> playlistUrl = new ArrayList<String>();
		String chName;		
		for (int i = 0; i < channel.size(); i++)
		{
			chName = channel.getName(i).toLowerCase();
			if (chName.contains(searchString.toLowerCase()))
			{
				playlist.add(channel.getName(i));
				playlistUrl.add(channel.getLink(i));
			}
		}

		TextView textView = (TextView) findViewById(R.id.searchlistTextView1);
		textView.setText(getResources().getString(R.string.resultsfor) + " '" + searchString + "' - " + playlist.size() + " " + getResources().getString(R.string.channels));

		ListView listView = (ListView) findViewById(R.id.searchlistListView1);
		final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, playlist);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					String s=(String) p1.getItemAtPosition(p3);
					Toast.makeText(SearchlistActivity.this, s, Toast.LENGTH_SHORT).show();
					openURL(playlistUrl.get(p3));
				}

			});
		listView.setOnItemLongClickListener(new OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					// TODO: Implement this method
					final String s=(String) p1.getItemAtPosition(p3);
					Toast.makeText(SearchlistActivity.this, s, Toast.LENGTH_SHORT).show();
					if (!favoriteList.contains(s))
					{
						// Add to favourite list dialog
						runOnUiThread(new Runnable() {
								public void run()
								{
									AlertDialog.Builder builder = new AlertDialog.Builder(SearchlistActivity.this);
									builder.setTitle(getResources().getString(R.string.request));
									builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.add) + " '" + s + "' to " + getResources().getString(R.string.tofavorites));
									builder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface p1, int p2)
											{
												MainActivity.favoriteList.add(s);
												MainActivity.favoriteProvList.add(ActivePlaylist.getName(selectedProvider));
												try
												{
													saveFavorites();
												}
												catch (IOException e)
												{}
											}
										});
									builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface p1, int p2)
											{
												// TODO: Implement this method
											}
										});
									AlertDialog alert = builder.create();
									alert.setOwnerActivity(SearchlistActivity.this);
									alert.show();
								}
							});
					}

					return true;
				}
			});
	}

}
