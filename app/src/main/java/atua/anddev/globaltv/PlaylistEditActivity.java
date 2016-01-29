package atua.anddev.globaltv;

import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import java.util.*;
import org.w3c.dom.*;
import java.io.*;

public class PlaylistEditActivity extends PlaylistManagerActivity
{
	int selectedType;
	Button addEditButton;
	Button deleteButton;
	Editable name, url;
	public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        // Set sub.xml as user interface layout
        setContentView(R.layout.playlistedit);

		applyLocalsEdit();
		showEdit();
	}

	public void applyLocalsEdit()
	{
		TextView textViewName = (TextView) findViewById(R.id.playlisteditTextView1);
		textViewName.setText(getResources().getString(R.string.name));
		TextView textViewUrl = (TextView) findViewById(R.id.playlisteditTextView2);
		textViewUrl.setText(getResources().getString(R.string.url));
		TextView textViewType = (TextView) findViewById(R.id.playlisteditTextView4);
		textViewType.setText(getResources().getString(R.string.type));
		deleteButton = (Button) findViewById(R.id.playlisteditButton1);
		deleteButton.setText(getResources().getString(R.string.delete));
		addEditButton = (Button) findViewById(R.id.playlisteditButton2);
		if (editAction.equals("add"))
		{
			addEditButton.setText(getResources().getString(R.string.add));
			deleteButton.setVisibility(View.GONE);
		}
		if (editAction.equals("modify"))
		{
			addEditButton.setText(getResources().getString(R.string.modify));
			deleteButton.setVisibility(View.VISIBLE);
		}

	}

	public void showEdit()
	{
		ArrayList<String> typeList = new ArrayList<String>();
		typeList.add(getResources().getString(R.string.standardplaylist));
		typeList.add(getResources().getString(R.string.torrenttvplaylist));
		Spinner spinnerView = (Spinner) findViewById(R.id.playlisteditSpinner1);
		SpinnerAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, typeList);
		spinnerView.setAdapter(adapter);

		EditText editTextName = (EditText) findViewById(R.id.playlisteditEditText1);
		EditText editTextUrl = (EditText) findViewById(R.id.playlisteditEditText2);
		if (editAction.equals("modify"))
		{
			if (enable)
			{
				editTextName.setText(ActivePlaylist.getName(editNum));
				editTextUrl.setText(ActivePlaylist.getUrl(editNum));
				selectedType = ActivePlaylist.getType(editNum);
				spinnerView.setSelection(ActivePlaylist.getType(editNum));
			}
			else
			{
				editTextName.setText(DisabledPlaylist.getName(editNum));
				editTextUrl.setText(DisabledPlaylist.getUrl(editNum));
				selectedType = DisabledPlaylist.getType(editNum);
				spinnerView.setSelection(DisabledPlaylist.getType(editNum));
			}
		}
		spinnerView.setOnItemSelectedListener(new OnItemSelectedListener()
			{
				@Override
				public void onNothingSelected(AdapterView<?> p1)
				{
					// TODO: Implement this method
				}
				@Override
				public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4)
				{

					String s=(String) p1.getItemAtPosition(p3);
					selectedType = p3;
				}
			});
		name = editTextName.getText();
		url = editTextUrl.getText();	
	}

	public void addEdit(View view)
	{
		Boolean success=false;
		if (editAction.equals("modify"))
		{
			if (name.toString().length() == 0 || url.toString().length() == 0)
			{
				Toast.makeText(PlaylistEditActivity.this, getResources().getString(R.string.pleasefillallfields), Toast.LENGTH_SHORT).show();
			}
			else
			{
				success=true;
				if (enable)
				{
					ActivePlaylist.set(editNum, name.toString(), url.toString(), getFileName(name.toString()), selectedType);
				}
				else
				{
					DisabledPlaylist.set(editNum, name.toString(), url.toString(), getFileName(name.toString()), selectedType);
				}
			}
		}
		if (editAction.equals("add"))
		{
			if (name.toString().length() == 0 || url.toString().length() == 0)
			{
				Toast.makeText(PlaylistEditActivity.this, getResources().getString(R.string.pleasefillallfields), Toast.LENGTH_SHORT).show();
			}
			else
			{
				success=true;
				ActivePlaylist.add(name.toString(), url.toString(), getFileName(name.toString()), selectedType);
			}
		}
		try
		{
			if (success)
			saveData();
		}
		catch (IOException e)
		{}
		if (success)
		super.onBackPressed();
	}

	public void deletePlaylist(View view)
	{
		if (enable)
		{
			ActivePlaylist.remove(editNum);
		}
		else
		{
			DisabledPlaylist.remove(editNum);
		}
		super.onBackPressed();
	}

}
