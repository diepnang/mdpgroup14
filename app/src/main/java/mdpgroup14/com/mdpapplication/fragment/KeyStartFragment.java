
package mdpgroup14.com.mdpapplication.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mdpgroup14.com.mdpapplication.MainActivity;
import mdpgroup14.com.mdpapplication.R;
import mdpgroup14.com.mdpapplication.controller.MainController;
import mdpgroup14.com.mdpapplication.entity.SendMessageAsyncTask;


public class KeyStartFragment extends Fragment {
    public final static String TAG = "KeyStartFragment";
    private TextView coordXText;
    private TextView coordYText;
    private MainController mainController = MainController.getInstance();
    private Button updateCoord;
    private Context context;
    private RadioButton robotUp;
    private RadioButton robotDown;
    private RadioButton robotLeft;
    private RadioButton robotRight;

    public KeyStartFragment(){}
    private int initialHead = MainController.BTN_FORWARD;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.key_start_fragment, container, false);
        coordXText = (TextView) view.findViewById(R.id.coordX_input_field);
        coordXText.setText("0");
        coordYText = (TextView) view.findViewById(R.id.coordY_input_field);
        coordYText.setText("0");
        updateCoord = (Button) view.findViewById(R.id.btn_update_start_coord);
        updateCoord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int coordX = Integer.parseInt(coordXText.getText().toString());
                    int coordY = Integer.parseInt(coordYText.getText().toString());
                    JSONObject jObj = new JSONObject();
                    try {
                        jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                        jObj.put(MainActivity.MSG, MainActivity.ROBOT_STATE_EXPLORE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mainController.setRobotFinishRunning();
                    mainController.startCoord(coordX, coordY);
                    JSONObject jObjStartCoord = new JSONObject();
                    try {
                        jObjStartCoord.put(MainActivity.JSONDATA_TYPE, MainActivity.SETROBOTPOS);
                        jObjStartCoord.put(MainActivity.MSG, String.format("%s,%s", coordX, coordY));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new SendMessageAsyncTask().execute(jObjStartCoord.toString(), MainController.NOT_UPDATE_MAP);
                    new SendMessageAsyncTask().execute(jObj.toString(), MainController.NOT_UPDATE_MAP);
                    if(initialHead == MainController.BTN_FORWARD){
                        mainController.setRobotDirection(coordX, mainController.NUM_ROWS - coordY-2);
                    }
                    else if(initialHead == MainController.BTN_BACKWARD) {
                        mainController.setRobotDirection(coordX, mainController.NUM_ROWS - coordY );
                        mainController.START_TO_GOAL = false;
                    }
                    else if (initialHead == MainController.BTN_LEFT){
                        mainController.setRobotDirection(coordX-1,mainController.NUM_ROWS - coordY -1);
                        mainController.MOVE_VERTICAL = false;
                        mainController.LEFT_TO_RIGHT = false;
                    }
                    else {
                        mainController.setRobotDirection(coordX+1,mainController.NUM_ROWS - coordY - 1);
                        mainController.MOVE_VERTICAL = false;
                    }
                    mainController.getHandler().obtainMessage(MainActivity.MESSAGE_UPDATE_START_COORD).sendToTarget();
                } catch (NumberFormatException e) {
                    Toast.makeText(mainController.getMainContext(), "Wrong Input", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }
            }
        });

        robotUp = (RadioButton) view.findViewById(R.id.robotUp);
        robotUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initialHead = MainController.BTN_FORWARD;
            }
        });
        robotUp.setChecked(true);
        robotDown = (RadioButton) view.findViewById(R.id.robotDown);
        robotDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initialHead = MainController.BTN_BACKWARD;
            }
        });

        robotLeft = (RadioButton) view.findViewById(R.id.robotLeft);
        robotLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initialHead = MainController.BTN_LEFT;
            }
        });

        robotRight = (RadioButton) view.findViewById(R.id.robotRight);
        robotRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                initialHead = MainController.BTN_RIGHT;
            }
        });
        return view;
    }
}
