package mdpgroup14.com.mdpapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import mdpgroup14.com.mdpapplication.MainActivity;
import mdpgroup14.com.mdpapplication.R;
import mdpgroup14.com.mdpapplication.controller.MainController;
import mdpgroup14.com.mdpapplication.entity.SendMessageAsyncTask;


public class ConfigurationFragment extends Fragment{
    public final static String TAG = "ConfigurationFragment";
    private static final String SET_CMD_PREF = "Set command Preferences";
    private Button F1,F2,F3;
    private Button setCmdPref;
    private MainController mainController = MainController.getInstance();
    private SharedPreferences sharedPreferences;
    private JSONObject jObj;

    public ConfigurationFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(CommandPrefFragment.PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.configure_fragment, container, false);

        setCmdPref = (Button) view.findViewById(R.id.btn_set_pre_str);
        setCmdPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommandPrefFragment commandPrefFragment = new CommandPrefFragment();
                commandPrefFragment.show(getActivity().getSupportFragmentManager(),SET_CMD_PREF);
            }
        });

        F1 = (Button) view.findViewById(R.id.btn_F1);
        F1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jObj = new JSONObject();
                try {
                    jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                    jObj.put(MainActivity.MSG,sharedPreferences.getString(CommandPrefFragment.CMD_F1,CommandPrefFragment.CMD_F1_DEFAULT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendMessageAsyncTask().execute(jObj.toString(), MainController.NOT_UPDATE_MAP);
            }
        });

        F2 = (Button) view.findViewById(R.id.btn_F2);
        F2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jObj = new JSONObject();
                try {
                    jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                    jObj.put(MainActivity.MSG,sharedPreferences.getString(CommandPrefFragment.CMD_F2,CommandPrefFragment.CMD_F2_DEFAULT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendMessageAsyncTask().execute(jObj.toString(),MainController.NOT_UPDATE_MAP);
            }
        });

        F3 = (Button) view.findViewById(R.id.btn_F3);
        F3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jObj = new JSONObject();
                try {
                    jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                    jObj.put(MainActivity.MSG,sharedPreferences.getString(CommandPrefFragment.CMD_F3,CommandPrefFragment.CMD_F3_DEFAULT));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendMessageAsyncTask().execute(jObj.toString(),MainController.NOT_UPDATE_MAP);
            }
        });
        return view;
    }
}