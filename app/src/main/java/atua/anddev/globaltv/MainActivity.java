package atua.anddev.globaltv;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.xmlpull.v1.*;

public class MainActivity extends Activity 
{
	Configuration conf;

	static ArrayList<String> globalSearchName = new ArrayList<String>();
	static ArrayList<String> globalSearchUrl = new ArrayList<String>();
	static ArrayList<String> globalSearchProv = new ArrayList<String>();
	static Channel channel = new Channel();
	static Playlist ActivePlaylist = new Playlist();
	static Playlist DisabledPlaylist = new Playlist();
	static ArrayList<String> categoryList = new ArrayList<String>();
	static String selectedCategory;
	static ArrayList<String> favoriteList = new ArrayList<String>();
	static ArrayList<String> favoriteProvList = new ArrayList<String>();
	static int selectedProvider;
	static String myPath;
	static Boolean needUpdate;
	static String lang;
	static String searchString;
	static MainActivity act;
	static String origNames[];
	static String translatedNames[];
	static String torrentKey;
	static Boolean playlistWithGroup;
	static String selectedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		//Context context = null;

        myPath = this.getApplicationContext().getFilesDir().toString();
		//myPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+context.getPackageName()+"/files";
		if (lang == null)
			lang = Locale.getDefault().getISO3Language();

		conf = getResources().getConfiguration();

		if (ActivePlaylist.size() == 0)
		{
			if (checkFile("userdata.xml"))
				setupProvider("user");
			else
				setupProvider("default");
		}
		setupProviderView();
		showLocals();
    }

	public void applyLocals()
	{
		Button updateButton = (Button) findViewById(R.id.mainButton2);
		updateButton.setText(getResources().getString(R.string.updatePlaylistButton));
		Button openButton = (Button) findViewById(R.id.mainButton1);
		openButton.setText(getResources().getString(R.string.openPlaylistButton));
		Button globalSearchButton = (Button) findViewById(R.id.mainButton3);
		globalSearchButton.setText(getResources().getString(R.string.globalSearchButton));
		Button playlistManagerButton = (Button) findViewById(R.id.mainButton4);
		playlistManagerButton.setText(getResources().getString(R.string.playlistsManagerButton));
		Button globalFavoriteButton = (Button) findViewById(R.id.mainButton5);
		globalFavoriteButton.setText(getResources().getString(R.string.favorites));
		Button updateAllButton = (Button) findViewById(R.id.mainButton6);
		updateAllButton.setText(getResources().getString(R.string.updateOutdatedPlaylists));
		TextView playlistView = (TextView) findViewById(R.id.mainTextView2);
		playlistView.setText(getResources().getString(R.string.playlist));
		TextView autoupdateView = (TextView) findViewById(R.id.mainTextView3);
		autoupdateView.setText(getResources().getString(R.string.autoUpdate));
		TextView every24hView = (TextView) findViewById(R.id.mainTextView4);
		every24hView.setText(getResources().getString(R.string.every24h));
	}

	public void showLocals()
	{
		ArrayList<String> localsList = new ArrayList<String>();
		localsList.add("English");
		localsList.add("Українська");
		localsList.add("Русский");
		Spinner spinnerView = (Spinner) findViewById(R.id.mainSpinner2);
		SpinnerAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, localsList);
		spinnerView.setAdapter(adapter);

		if (lang.equals("eng"))
			spinnerView.setSelection(0);
		if (lang.equals("ukr"))
			spinnerView.setSelection(1);
		if (lang.equals("rus"))
			spinnerView.setSelection(2);

		spinnerView.setOnItemSelectedListener(new OnItemSelectedListener()
			{

				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
				{

					String s=(String) p1.getItemAtPosition(p3);
					switch (s)
					{
						case "English":
							MainActivity.lang = "eng";
							conf.locale = new Locale("en");
							break;
						case "Українська":
							MainActivity.lang = "ukr";
							conf.locale = new Locale("uk");
							break;
						case "Русский":
							MainActivity.lang = "rus";
							conf.locale = new Locale("ru");
							break;
					}
					new Resources(getAssets(), getResources().getDisplayMetrics(), conf);
					origNames = getResources().getStringArray(R.array.categories_list_orig);
					translatedNames = getResources().getStringArray(R.array.categories_list_translated);
					applyLocals();
					checkPlaylistFile(ActivePlaylist.getFile(selectedProvider));
				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					// TODO: Implement this method
				}
			});
	}


	public void setupProviderView()
	{

		Spinner spinnerView = (Spinner) findViewById(R.id.mainSpinner1);
		SpinnerAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, ActivePlaylist.name);
		spinnerView.setAdapter(adapter);
		spinnerView.setOnItemSelectedListener(new OnItemSelectedListener()
			{

				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
				{

					String s=(String) p1.getItemAtPosition(p3);
					selectedProvider = p3;
					checkPlaylistFile(ActivePlaylist.getFile(p3));

				}

				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					// TODO: Implement this method
				}
			});
	}

	public void catlistActivity()
	{
		Intent intent = new Intent(this, CatlistActivity.class);
		startActivity(intent);
	}

	public boolean checkPlaylistFile(String fname)
	{
		TextView textView = (TextView) findViewById(R.id.mainTextView1);
		try
		{
			File file = new File(myPath + "/" + fname);
			long fileDate = file.lastModified();
			long currDate = (new Date()).getTime();
			long upDate=currDate - fileDate;
			String tmpText;
		    switch (new Date(upDate).getDate())
	    	{
				case 1:
					tmpText = getResources().getString(R.string.updated) + " " + new Date(fileDate).toLocaleString();
					needUpdate = false;
					break;
				case 2:
					tmpText = getResources().getString(R.string.updated) + " 1 " + getResources().getString(R.string.dayago);
					needUpdate = true;
					break;
				case 3:
				case 4:
				case 5:
					tmpText = getResources().getString(R.string.updated) + " " + (new Date(upDate).getDate() - 1) + " " + getResources().getString(R.string.daysago);
					needUpdate = true;
					break;
				default:
					tmpText = getResources().getString(R.string.updated) + " " + (new Date(upDate).getDate() - 1) + " " + getResources().getString(R.string.fivedaysago);
					needUpdate = true;
					break;

			}

			textView.setText(tmpText);
			InputStream myfile = new FileInputStream(myPath + "/" + fname);
		}
		catch (FileNotFoundException e)
		{
			textView.setText(getResources().getString(R.string.playlistnotexist));
			needUpdate = true;
			return false;
		}
		return true;
	}

	public boolean checkFile(String fname)
	{
		try
		{
			InputStream myfile = new FileInputStream(myPath + "/" + fname);
		}
		catch (FileNotFoundException e)
		{
			return false;
		}
		return true;
	}

	public void playlistActivity()
	{
		selectedCategory = getResources().getString(R.string.all);
		Intent intent = new Intent(this, PlaylistActivity.class);
		startActivity(intent);
	}

	public void openPlaylist(View view)
	{
		if (needUpdate)
		{
			downloadPlaylist(selectedProvider);
		}
		readPlaylist(ActivePlaylist.getFile(selectedProvider), ActivePlaylist.getType(selectedProvider));
		try
		{
			if (favoriteList.size() == 0)
				loadFavorites();
		}
		catch (IOException e)
		{
		}
		if (playlistWithGroup)
			catlistActivity();
		else
			playlistActivity();
	}

	public void downloadButton(View view)
	{
		downloadPlaylist(selectedProvider);
	}

	public void updateAll(View view)
	{
		for (int i=0; i < ActivePlaylist.size(); i++)
		{
			checkPlaylistFile(ActivePlaylist.getFile(i));
			if (needUpdate)
			{
				downloadPlaylist(i);
			}
		}
	}

	public void downloadPlaylist(final int num)
    {
		new Thread(new Runnable() {
				public void run()
				{
					try
					{
					    String path = myPath + "/" + ActivePlaylist.getFile(num);
						saveUrl(path, ActivePlaylist.getUrl(num));

						runOnUiThread(new Runnable() {
								public void run()
								{
									needUpdate = false;
									checkPlaylistFile(ActivePlaylist.getFile(selectedProvider));
									Toast.makeText(MainActivity.this, getResources().getString(R.string.playlistupdated, ActivePlaylist.getName(num)), Toast.LENGTH_SHORT).show();
								}
							});
					}
					catch (Exception e)
					{
						runOnUiThread(new Runnable() {
								public void run()
								{
									Toast.makeText(MainActivity.this, getResources().getString(R.string.updatefailed, ActivePlaylist.getName(num)), Toast.LENGTH_SHORT).show();
								}
							});
						Log.i("SDL", "Error: " + e.toString());
					}
				}
			}).start();
    } 

	public static void readPlaylist(String fname, int type)
	{
        playlistWithGroup = false;
        String lineStr, chName = "", chCategory = "", chLink = "", totalString = "";
        String groupName = "", groupName2 = "";
        channel.clear();
		try
		{
			InputStream myfile = new FileInputStream(myPath + "/" + fname);
			Scanner myInputFile = new Scanner(myfile, "UTF8").useDelimiter("[\n]");;
			while (myInputFile.hasNext()) {
                lineStr = myInputFile.next();
                if (lineStr.startsWith("acestream:") || lineStr.startsWith("http:") || lineStr.startsWith("https:")
                        || lineStr.startsWith("rtmp:") || lineStr.startsWith("rtsp:") || lineStr.startsWith("mmsh:")
                        || lineStr.startsWith("mms:") || lineStr.startsWith("rtmpt:")) {
                    chLink = lineStr;
                    if (chName.startsWith("ALLFON.TV")) {
                        chName = chName.substring(10, chName.length());
                    }
                    if (chName.startsWith(" ")) {
                        chName = chName.substring(1, chName.length());
                    }
                    chCategory = translate(chCategory);
                    channel.add(chName, chCategory, chLink);
                    if (!chCategory.equals(""))
                        playlistWithGroup = true;
                    chName = "";
                    chCategory = "";
                    chLink = "";
                    groupName = "";
                    groupName2 = "";
                }
                if ((type == 1) && lineStr.startsWith("#EXTINF:-1,") && (lineStr.indexOf("(") == lineStr.lastIndexOf("("))) {
                    chName = lineStr.substring(11, lineStr.indexOf("(") - 1);
                    chCategory = lineStr.substring(lineStr.lastIndexOf("(") + 1, lineStr.lastIndexOf(")"));
                    playlistWithGroup = true;
                }
                if ((type == 1) && lineStr.startsWith("#EXTINF:-1,") && (lineStr.indexOf("(") != lineStr.lastIndexOf("("))) {
                    chName = lineStr.substring(11, lineStr.lastIndexOf("(") - 1);
                    chCategory = lineStr.substring(lineStr.lastIndexOf("(") + 1, lineStr.lastIndexOf(")"));
                    playlistWithGroup = true;
                }
                if (lineStr.startsWith("#EXTINF:") && (type != 1)) {
                    chName = lineStr.substring(lineStr.indexOf(",") + 1, lineStr.length());
                }
                if (lineStr.contains("group-title=") && lineStr.contains(",") && (lineStr.substring(lineStr.indexOf("group-title="), lineStr.indexOf("group-title=") + 12).equals("group-title="))) {
                    groupName = lineStr.substring(lineStr.indexOf("group-title=") + 13, lineStr.indexOf('"', lineStr.indexOf("group-title=") + 13));
                    playlistWithGroup = true;
                }
                if (lineStr.contains("#EXTGRP:") && (lineStr.substring(lineStr.indexOf("#EXTGRP:"), lineStr.indexOf("#EXTGRP:") + 8).equals("#EXTGRP:"))) {
                    groupName2 = lineStr.substring(lineStr.indexOf("#EXTGRP:") + 8, lineStr.length());
                    playlistWithGroup = true;
                }
                if (!groupName.equals("")) {
                    chCategory = groupName;
                } else if (!groupName2.equals("")) {
        
                        groupName2 = groupName2.replace(" ","");
                    chCategory = groupName2;
                }
            }
			myInputFile.close();
		}
		catch (Exception E1)
		{ /*Toast.makeText(MainActivity.this, "openning playlist - failed " + E1.toString(), Toast.LENGTH_SHORT).show();*/}
	}

	static String fixKey(String str)
	{
		String output;
		output = str;
		String startStr="http://content.torrent-tv.ru/";
		String endStr;
		if (str.startsWith(startStr) && str.contains("/cdn/"))
		{
			endStr = str.substring(str.indexOf("/cdn/"), str.length());
			output = startStr + torrentKey + endStr;
		}
		return output;
	}

	public static void saveUrl(final String filename, final String urlString)
	throws MalformedURLException, IOException
	{
	    BufferedInputStream in = null;
	    FileOutputStream fout = null;
	    try
		{
	        in = new BufferedInputStream(new URL(urlString).openStream());
	        fout = new FileOutputStream(filename);

	        final byte data[] = new byte[1024];
	        int count;
	        while ((count = in.read(data, 0, 1024)) != -1)
			{
	            fout.write(data, 0, count);
	        }
	    }
		finally
		{
	        if (in != null)
			{
	            in.close();
	        }
	        if (fout != null)
			{
	            fout.close();
	        }
	    }
	}

	public void downloadAllPlaylist()
	{
		for (int i=0; i < ActivePlaylist.size(); i++)
		{
			if (!checkPlaylistFile(ActivePlaylist.getFile(i)))
			{
				downloadPlaylist(i);
			}
		}
	}

	public void openChannel(String chName)
    {
        for (int i = 0; i < channel.size(); i++)
        {
            if (chName.equals(channel.getName(i)))
            {
                openURL(channel.getLink(i));
				return;
            }
        }
    }

    public void openURL(final String chURL)
    {
        new Thread(new Runnable() {
				public void run()
				{
					try
					{
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chURL));
						if (chURL.startsWith("http"))
						{
							browserIntent.setDataAndType(Uri.parse(chURL), "video/*");
						}
						startActivity(browserIntent);

					}
					catch (Exception e)
					{
						Log.i("SDL", "Error: " + e.toString());
					}
				}
			}).start();
    }

	static public String translate(String input)
	{
		String output;
		output = input;

		if (origNames.length > 0)
		{
			for (int i=0; i < origNames.length; i++)
			{
				if (origNames[i].equalsIgnoreCase(input))
				{
					output = translatedNames[i];
					break;
				}
			}
		}
		return output;
	}

	public void globalSearch(View view)
	{
		globalSearchName.clear();
		globalSearchUrl.clear();
		globalSearchProv.clear();
		final EditText input = new EditText(this);
		input.setSingleLine();
		new AlertDialog.Builder(MainActivity.this)
			.setTitle(getResources().getString(R.string.request))
			.setMessage(getResources().getString(R.string.pleaseentertext))
			.setView(input)
			.setPositiveButton(getResources().getString(R.string.search), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					Editable value = input.getText();
					searchString = value.toString();
					globalSearchActivity();
				}
			}).setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					// Do nothing.
				}
			}).show();
	}

	public void playlistManager(View view)
	{
		playlistManagerActivity();
	}

	public void favlistActivity()
	{
		Intent intent = new Intent(this, FavlistActivity.class);
		startActivity(intent);
	}

	public void searchlistActivity()
	{
		Intent intent = new Intent(this, SearchlistActivity.class);
		startActivity(intent);
	}

	public void globalSearchActivity()
	{
		Intent intent = new Intent(this, GlobalSearchActivity.class);
		startActivity(intent);
	}

	public void playlistManagerActivity()
	{
		Intent intent = new Intent(this, PlaylistManagerActivity.class);
		startActivity(intent);
	}

	public void globalFavoriteActivity()
	{
		Intent intent = new Intent(this, GlobalFavoriteActivity.class);
		startActivity(intent);
	}
	
	public void playerActivity()
	{
		Intent intent = new Intent(this, PlayerActivity.class);
		startActivity(intent);
	}

	public void saveFavorites() throws FileNotFoundException, IOException
    {
		FileOutputStream fos;       
		fos = openFileOutput("favorites.xml", Context.MODE_WORLD_WRITEABLE);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(fos, "UTF-8");
		serializer.startDocument(null, Boolean.valueOf(true));
		serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		serializer.startTag(null, "root");

		for (int j = 0 ; j < favoriteList.size() ; j++)
		{
			serializer.startTag(null, "favorites");

			serializer.startTag(null, "channel");
			serializer.text(favoriteList.get(j));
			serializer.endTag(null, "channel");

			serializer.startTag(null, "playlist");
			serializer.text(favoriteProvList.get(j));
			serializer.endTag(null, "playlist");

			serializer.endTag(null, "favorites");
		}
		serializer.endDocument();
		serializer.flush();
		fos.close();
	}

	public void loadFavorites() throws IOException
    {
		String text = null, name = null, prov = null, endTag;
		try
		{           
			XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
			xppf.setNamespaceAware(true); 
			XmlPullParser xpp = xppf.newPullParser();

			File myXML = new File(myPath + "/favorites.xml");         
			FileInputStream fis = new FileInputStream(myXML);
			xpp.setInput(fis, null);
			int type = xpp.getEventType();
			while (type != XmlPullParser.END_DOCUMENT)
			{
				if (type == XmlPullParser.START_DOCUMENT)
				{
				}
				else if (type == XmlPullParser.START_TAG)
				{

				}
				else if (type == XmlPullParser.END_TAG)
				{
					endTag = xpp.getName();
					if (endTag.equals("channel"))
						name = text;
					if (endTag.equals("playlist"))
						prov = text;
					if (endTag.equals("favorites"))
					{
						favoriteList.add(name);
						favoriteProvList.add(prov);
					}
				}
				else if (type == XmlPullParser.TEXT)
				{
					text = xpp.getText();
				}
				type = xpp.next();
			}
		} 
		catch (XmlPullParserException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void globalFavorite(View view)
	{
		try
		{
			if (favoriteList.size() == 0)
				loadFavorites();
		}
		catch (IOException e)
		{
		}
		globalFavoriteActivity();
	}

	public void setupProvider(String opt)
	{
		int num, dnum;
		num = 0;
		dnum = 0;
		String name = null, url = null, file = null, type = null,enable = null;
		String endTag, text = null;

		try
		{
			XmlPullParser xpp;
			if (opt.equals("user"))
			{
				XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
				xppf.setNamespaceAware(true); 
				xpp = xppf.newPullParser();

				File myXML = new File(myPath + "/userdata.xml");         
				FileInputStream fis = new FileInputStream(myXML);
				xpp.setInput(fis, null);
			}
			else
			{
				xpp = getResources().getXml(R.xml.data);
			}
			while (xpp.getEventType() != XmlPullParser.END_DOCUMENT)
			{
				switch (xpp.getEventType())
				{
					case XmlPullParser.START_DOCUMENT:
						break;

					case XmlPullParser.START_TAG:
						break;

					case XmlPullParser.END_TAG:
						endTag = xpp.getName();
						if (endTag.equals("name"))
							name = text;
						if (endTag.equals("url"))
							url = text;
						if (endTag.equals("type"))
							type = text;
						if (endTag.equals("enable"))
							enable = text;
						if (endTag.equals("torrentkey"))
							torrentKey = text;
						if (endTag.equals("provider"))
						{
							file = getFileName(name);
							if (enable.equals("true"))
							{
								ActivePlaylist.add(name, url, file, Integer.parseInt(type));
								num++;
							}
							if (enable.equals("false"))
							{
								DisabledPlaylist.add(name, url, file, Integer.parseInt(type));
								dnum++;
							}
						}
						break;

					case XmlPullParser.TEXT:
						text = xpp.getText();
						break;

					default:
						break;
				}
				xpp.next();
			}
		}
		catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		if (opt.equals("default"))
		{
			downloadAllPlaylist();
		}
	}

	public String getFileName(String input)
	{
		String output="playlist_" + input + ".m3u";
		output = output.replace(" ", "_");
		output = output.replace("(", "_");
		output = output.replace(")", "_");
		output = output.replace("@", "_");
		return output;
	}
}
