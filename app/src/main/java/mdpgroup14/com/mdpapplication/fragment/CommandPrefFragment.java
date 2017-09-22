package mdpgroup14.com.mdpapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import mdpgroup14.com.mdpapplication.R;

/**
 * Created by nhattran on 2/18/16.
 */
public class CommandPrefFragment  extends AppCompatDialogFragment {
    public static final String CMD_F1_DEFAULT = "command F1" ;
    public static final String CMD_F2_DEFAULT = "command F2";
    public static final String CMD_F3_DEFAULT = "command F3";
    private SharedPreferences sharedPreferences;
    public final static String PREFS_NAME= "Group1Pref";
    public final static String CMD_F1="cmd_F1";
    public final static String CMD_F2="cmd_F2";
    public final static String CMD_F3="cmd_F3";
    private Button saveCmd;
    private EditText F1,F2,F3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.layout_command_pref,container,false);
        F1 = (EditText) view.findViewById(R.id.F1_input);
        F2 = (EditText) view.findViewById(R.id.F2_input);
        F3 = (EditText) view.findViewById(R.id.F3_input);
        saveCmd = (Button) view.findViewById(R.id.btn_save_cmd);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        getDialog().setTitle("Command Prefs");
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        F1.setText(sharedPreferences.getString(CMD_F1,CMD_F1_DEFAULT));
        F2.setText(sharedPreferences.getString(CMD_F2,CMD_F2_DEFAULT));
        F3.setText(sharedPreferences.getString(CMD_F3,CMD_F3_DEFAULT));

        saveCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cmd1,cmd2,cmd3;
                cmd1 = F1.getText().toString();
                cmd1 = cmd1.length() > 0 ? cmd1 : CMD_F1_DEFAULT;
                cmd2 = F2.getText().toString();
                cmd2 = cmd2.length() > 0 ? cmd2 : CMD_F2_DEFAULT;
                cmd3 = F3.getText().toString();
                cmd3 = cmd3.length() > 0 ? cmd3 : CMD_F3_DEFAULT;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CMD_F1, cmd1);
                editor.putString(CMD_F2, cmd2);
                editor.putString(CMD_F3, cmd3);
                editor.apply();

                Toast.makeText(getActivity(), "Preferences saved.", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });
    }
}
