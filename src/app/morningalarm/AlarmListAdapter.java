package app.morningalarm;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import app.database.AlarmDbAdapter;

public class AlarmListAdapter extends ArrayAdapter<Alarm> {

	private ArrayList<Alarm> alarms;

	public AlarmListAdapter(Context context, int textViewResourceId,
			ArrayList<Alarm> objects) {
		super(context, textViewResourceId, objects);
		this.alarms = objects;
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_main, null);
            }
            
            final Alarm li = alarms.get(position);
            if (li != null) {
                    ImageView iv= (ImageView) v.findViewById(R.id.alarm_iv);
                    TextView tv = (TextView) v.findViewById(R.id.alarm_tv);
                    ToggleButton tb=(ToggleButton) v.findViewById(R.id.alarm_tb);
                    tb.setOnClickListener(new OnClickListener(){

						public void onClick(View arg0) {
							if(li.isEnabled() == Alarm.ALARM_ENABLED)
								li.setEnabled(Alarm.ALARM_DISABLED);
							else
								li.setEnabled(Alarm.ALARM_ENABLED);
							AlarmDbAdapter mDbHelper= new AlarmDbAdapter(AlarmListAdapter.this.getContext());
							mDbHelper.open();
							mDbHelper.updateAlarm(li);
						}
                    	
                    });
                    if (iv != null) {
                    	if(li.getWakeUpMode().equals("simple"))
                    		iv.setImageResource(R.drawable.ic_launcher);
                    	if(li.getWakeUpMode().equals("logic"))
                    		iv.setImageResource(R.drawable.ic_launcher);
                    	if(li.getWakeUpMode().equals("scanner"))
                    		iv.setImageResource(R.drawable.ic_launcher);
                    }
                    if(tv != null){
                    	Calendar c = Calendar.getInstance();
                    	c.setTimeInMillis(Long.parseLong(li.getTime()));
                    	DateFormat df=DateFormat.getTimeInstance(DateFormat.SHORT);
                		String time=df.format(c.getTime());
                        tv.setText(time);
                    }
                    if(tb!=null){
                    	if(li.isEnabled() == Alarm.ALARM_ENABLED)
                    		tb.setChecked(true);
                    	else
                    		tb.setChecked(false);
                    }
            }
            
            return v;
    }

}
