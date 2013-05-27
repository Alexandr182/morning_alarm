package app.morningalarm;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import app.database.AlarmDbAdapter;
import app.database.AlarmDbUtilities;

public class AlarmListActivity extends Activity {
	
	private AlarmDbAdapter mDbHelper;
	private ArrayList<Alarm> list;
	private String lastId;
	private int lastIndex;
	private AlarmAdapter ad;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDbHelper = new AlarmDbAdapter(this);
        mDbHelper.open();
        list = AlarmDbUtilities.fetchCursor(mDbHelper.fetchAllAlarms());
        
        Button b = (Button)this.findViewById(R.id.add_btn);
        b.setOnClickListener(new OnClickListener(){

			public void onClick(View arg0) {
				mDbHelper.createAlarm();
				Cursor cur = mDbHelper.fetchNewAlarm();
				ArrayList<Alarm> newAlarm = AlarmDbUtilities.fetchCursor(cur);
				lastIndex = list.size();
				list.add(newAlarm.get(0));
				Intent i;
				if (Build.VERSION.SDK_INT < 11) {
				    i = new Intent(AlarmListActivity.this, AlarmSettingsActivity.class);
				} else {
					i= new Intent(AlarmListActivity.this, AlarmFragmentsSettingsActivity.class);
				}
				i.putExtra(AlarmDbAdapter.KEY_ID, newAlarm.get(0).getId());
				lastId = newAlarm.get(0).getId();
				AlarmListActivity.this.startActivityForResult(i,0);
				
			}
        	
        });
        
        ad=new AlarmAdapter(this,R.layout.list_item_main,list);
        ListView lv=(ListView)this.findViewById(R.id.listView1);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(new OnItemClickListener(){
        
			@SuppressLint("NewApi")
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i;
				if (Build.VERSION.SDK_INT < 11) {
				    i = new Intent(AlarmListActivity.this, AlarmSettingsActivity.class);
				} else {
					i= new Intent(AlarmListActivity.this, AlarmFragmentsSettingsActivity.class);
				}
				i.putExtra("id", list.get((int)arg3).getId());
				lastId = list.get((int)arg3).getId();
				lastIndex = (int)arg3;
				AlarmListActivity.this.startActivityForResult(i,0);
			}
        });
        
        emptyTextViewVisibility();
        this.registerForContextMenu(lv);
    }
    
    private void emptyTextViewVisibility(){
    	if(list.size()>0)
        	findViewById(R.id.id_empty_list_text_view).setVisibility(View.GONE);
    	else
    		findViewById(R.id.id_empty_list_text_view).setVisibility(View.VISIBLE);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item){
    	super.onContextItemSelected(item);
    	switch(item.getItemId()){
    		case R.id.delete_option:
    			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    			Alarm a = list.get((int)info.id);
    			mDbHelper.deleteAlarm(a);
    			list.remove(a);
    			emptyTextViewVisibility();
    			ad.notifyDataSetChanged();
    			break;
    	}
    	return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	SharedPreferences sp= this.getSharedPreferences(lastId, Context.MODE_PRIVATE);
    	
		//Integer enabled = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_ENABLED)));
		String description = sp.getString("description", null);
		String time = sp.getString("time", null);
		String duration = sp.getString("duration", null);
		String daysOfWeek = sp.getString("days_of_week", null);
		String wakeUpMode = sp.getString("wake_up_mode", null);
		String ringtone = sp.getString("ringtone", null);
		Alarm alarm = list.get(lastIndex);
		if(time == null){
			Calendar c = Calendar.getInstance();
			DateFormat df=DateFormat.getTimeInstance(DateFormat.SHORT);
			time=df.format(c.getTime());
		}
		alarm.setDescription(description);
		alarm.setTime(time);
		alarm.setDuration(duration);
		alarm.setWakeUpMode(wakeUpMode);
		alarm.setDaysOfWeek(daysOfWeek);
		alarm.setRingtone(ringtone);
		this.mDbHelper.updateAlarm(alarm);
		emptyTextViewVisibility();
		ad.notifyDataSetChanged();
    }
}
