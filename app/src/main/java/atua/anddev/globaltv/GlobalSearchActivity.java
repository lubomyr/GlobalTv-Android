package atua.anddev.globaltv;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.util.*;

public class GlobalSearchActivity extends MainActivity
{

	private ProgressDialog progress;

	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);

        // Set sub.xml as user interface layout
        setContentView(R.layout.globalsearch);
		if (globalSearchName.size() == 0)
			runProgressBar();
		else
			showSearchResults();
	}

	private void runProgressBar()
	{
		progress = new ProgressDialog(this);
		progress.setMessage(getResources().getString(R.string.searching));
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setMax(ActivePlaylist.size());
		progress.setProgress(0);
		progress.show();

		Thread t = new Thread() {
			@Override
			public void run()
			{

				prepare_globalSearch();
				progress.dismiss();
				runOnUiThread(new Runnable() {
						public void run()
						{
							showSearchResults();
						}
					});
			}
		};
		t.start();
	}

	private void prepare_globalSearch()
	{
		for (int i=0; i < ActivePlaylist.size(); i++)
		{
			progress.setProgress(i);
			readPlaylist(ActivePlaylist.getFile(i), ActivePlaylist.getType(i));

			String chName;		
			for (int j = 0; j < channel.size(); j++)
			{
				chName = channel.getName(j).toLowerCase();
				if (chName.contains(searchString.toLowerCase()))
				{
					globalSearchName.add(channel.getName(j));
					globalSearchUrl.add(channel.getLink(j));
					globalSearchProv.add(ActivePlaylist.getName(i));
				}
			}
		}

	}

	public void showSearchResults()
	{
		TextView textView = (TextView) findViewById(R.id.globalsearchTextView1);
		textView.setText(getResources().getString(R.string.resultsfor) + " '" + searchString + "' - " + globalSearchName.size() + " " + getResources().getString(R.string.channels));

		GlobalAdapter adapter=new GlobalAdapter(this, globalSearchName, globalSearchProv);
		ListView list = (ListView)findViewById(R.id.globalsearchListView1);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					String s=(String) p1.getItemAtPosition(p3);
					Toast.makeText(GlobalSearchActivity.this, s, Toast.LENGTH_SHORT).show();
					openURL(globalSearchUrl.get(p3));
				}

			});
		list.setOnItemLongClickListener(new OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
				{
					final String s=(String) p1.getItemAtPosition(p3);
					final int selectedItem=p3;
					Toast.makeText(GlobalSearchActivity.this, s, Toast.LENGTH_SHORT).show();
					Boolean changesAllowed=true;
					for (int i=0; i < favoriteList.size(); i++)
					{
						if (globalSearchName.get(selectedItem).equals(favoriteList.get(i)) && globalSearchProv.get(selectedItem).equals(favoriteProvList.get(i)))
							changesAllowed = false;
					}
					if (changesAllowed)
					{
						// Add to favourite list dialog
						runOnUiThread(new Runnable() {
								public void run()
								{
									AlertDialog.Builder builder = new AlertDialog.Builder(GlobalSearchActivity.this);
									builder.setTitle(getResources().getString(R.string.request));
									builder.setMessage(getResources().getString(R.string.doyouwant) + " " + getResources().getString(R.string.add) + " '" + s + "' to " + getResources().getString(R.string.tofavorites));
									builder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface p1, int p2)
											{
												MainActivity.favoriteList.add(s);
												MainActivity.favoriteProvList.add(globalSearchProv.get(selectedItem));
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
									alert.setOwnerActivity(GlobalSearchActivity.this);
									alert.show();
								}
							});
					}

					return true;
				}
			});
	}
}
