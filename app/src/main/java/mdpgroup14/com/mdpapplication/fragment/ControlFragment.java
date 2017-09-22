package mdpgroup14.com.mdpapplication.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import mdpgroup14.com.mdpapplication.MainActivity;
import mdpgroup14.com.mdpapplication.R;
import mdpgroup14.com.mdpapplication.controller.MainController;
import mdpgroup14.com.mdpapplication.entity.SendMessageAsyncTask;


public class ControlFragment extends Fragment{
    public final static String TAG = "ControllFragment";
    public static final String SHORTEST_PHASE = "run";
    public static final String EXPLORATION_PHASE = "exploration" ;

    private Switch switchUpdateMode;
    private TextView isAuto;
    private Button updateMap;
    private ImageButton rightMove, leftMove, fowardMove,backwardMove;
    private final String AUTO_UPDATE_OFF ="OFF";
    private final String AUTO_UPDATE_ON ="ON";
    private RadioButton exploration;
    private RadioButton shortest_path;
    private Button start;
    private Button reset;
    private JSONObject jObj = null;
    private MainController mainController = MainController.getInstance();
    private Handler mHandler = mainController.getHandler();
    private Switch switchTilt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.control_fragment, container, false);
        //find view TextView is_auto
        isAuto =(TextView) view.findViewById(R.id.is_auto);
        isAuto.setText(AUTO_UPDATE_ON);
        //find view Button Update
        updateMap =(Button) view.findViewById(R.id.btn_update_map);
        updateMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainController.getHandler().obtainMessage(MainActivity.MESSAGE_MANUAL_UPDATE_MOVE).sendToTarget();
            }
        });
        mainController.setRobotMode(EXPLORATION_PHASE,false);
        start = (Button) view.findViewById(R.id.btn_start);
        start.setText(EXPLORATION_PHASE);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mainController.isRobotFinishRunning()){
                    return;
                }
                jObj = new JSONObject();
                try {
                    jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                    if (mainController.getRobotMode().equals(EXPLORATION_PHASE)) {
                        jObj.put(MainActivity.MSG, MainActivity.ROBOT_STATE_EXPLORE);
                        mainController.startCoord(1,1);
                        new SendMessageAsyncTask().execute(jObj.toString(), String.valueOf(MainController.BTN_RIGHT));

                    } else if (mainController.getRobotMode().equals(SHORTEST_PHASE)) {
                        jObj.put(MainActivity.MSG, MainActivity.ROBOT_STATE_RUN);
                        new SendMessageAsyncTask().execute(jObj.toString(),String.valueOf(MainController.NOT_UPDATE_MAP));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        //find view Button moving left,right,foward,backward
        leftMove = (ImageButton) view.findViewById(R.id.btn_left_move);
        leftMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mainController.isRobotFinishRunning()){
                    return;
                }
                jObj = new JSONObject();
                try {
                    jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                    if (MainController.MOVE_VERTICAL) {
                        if (MainController.START_TO_GOAL) {
                            jObj.put(MainActivity.MSG,MainActivity.ROBOT_TURN_LEFT);
                        }
                        else{
                            jObj.put(MainActivity.MSG,MainActivity.ROBOT_TURN_RIGHT);
                        }
                        new SendMessageAsyncTask().execute(jObj.toString(),String.valueOf(MainController.BTN_LEFT));
                    }
                    else{
                        if(!MainController.LEFT_TO_RIGHT) {
                            jObj.put(MainActivity.MSG, MainActivity.ROBOT_MOVE_FORWARD);
                            new SendMessageAsyncTask().execute(jObj.toString(), String.valueOf(MainController.BTN_LEFT));
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        rightMove = (ImageButton) view.findViewById(R.id.btn_right_move);
        rightMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mainController.isRobotFinishRunning()){
                    return;
                }
                jObj = new JSONObject();
                try {
                    jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                    if (MainController.MOVE_VERTICAL) {
                        if (MainController.START_TO_GOAL) {
                            jObj.put(MainActivity.MSG, MainActivity.ROBOT_TURN_RIGHT);
                        } else {
                            jObj.put(MainActivity.MSG, MainActivity.ROBOT_TURN_LEFT);
                        }
                        new SendMessageAsyncTask().execute(jObj.toString(), String.valueOf(MainController.BTN_RIGHT));
                    } else {
                        if (MainController.LEFT_TO_RIGHT) {
                            jObj.put(MainActivity.MSG, MainActivity.ROBOT_MOVE_FORWARD);
                            new SendMessageAsyncTask().execute(jObj.toString(), String.valueOf(MainController.BTN_RIGHT));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        fowardMove = (ImageButton) view.findViewById(R.id.btn_forward_move);
        fowardMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mainController.isRobotFinishRunning()){
                    return;
                }
                jObj = new JSONObject();
                try {
                    jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                    if (MainController.MOVE_VERTICAL) {
                        if (MainController.START_TO_GOAL) {
                            jObj.put(MainActivity.MSG, MainActivity.ROBOT_MOVE_FORWARD);

                            new SendMessageAsyncTask().execute(jObj.toString(), String.valueOf(MainController.BTN_FORWARD));
                        }
                    }
                    else{
                        if(MainController.LEFT_TO_RIGHT) {
                            jObj.put(MainActivity.MSG, MainActivity.ROBOT_TURN_LEFT);
                        }
                        else{
                            jObj.put(MainActivity.MSG,MainActivity.ROBOT_TURN_RIGHT);
;                        }
                        new SendMessageAsyncTask().execute(jObj.toString(), String.valueOf(MainController.BTN_FORWARD));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }


            }
        });
        backwardMove = (ImageButton) view.findViewById(R.id.btn_backward_move);
        backwardMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mainController.isRobotFinishRunning()){
                    return;
                }
                jObj = new JSONObject();
                try {
                    jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                    if (MainController.MOVE_VERTICAL) {
                        if (!MainController.START_TO_GOAL) {
                            jObj.put(MainActivity.MSG, MainActivity.ROBOT_MOVE_FORWARD);

                            new SendMessageAsyncTask().execute(jObj.toString(), String.valueOf(MainController.BTN_BACKWARD));
                        }
                    }
                    else{
                        if(MainController.LEFT_TO_RIGHT) {
                            jObj.put(MainActivity.MSG, MainActivity.ROBOT_TURN_RIGHT);
                        }
                        else{
                            jObj.put(MainActivity.MSG,MainActivity.ROBOT_TURN_LEFT);
                            ;                        }
                        new SendMessageAsyncTask().execute(jObj.toString(), String.valueOf(MainController.BTN_BACKWARD));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        //find view Switch Update
        switchUpdateMode = (Switch) view.findViewById(R.id.switch_update_mode);
        switchUpdateMode.setChecked(true);
        switchUpdateMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAuto.setText(AUTO_UPDATE_ON);
                    updateMap.setVisibility(View.INVISIBLE);
                    mainController.setManualUpdate(false);
                } else {
                    isAuto.setText(AUTO_UPDATE_OFF);
                    updateMap.setVisibility(View.VISIBLE);
                    mainController.setManualUpdate(true);
                }
            }
        });

        switchTilt = (Switch) view.findViewById(R.id.switch_tilt);
        switchTilt.setChecked(false);
        switchTilt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    mainController.setTilt(true);
                }else{
                    mainController.setTilt(false);
                }
            }
        });

        exploration = (RadioButton) view.findViewById(R.id.btn_exploration);
        exploration.setChecked(true);
        exploration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    start.setText(EXPLORATION_PHASE);
                    mainController.setRobotMode(EXPLORATION_PHASE,false);

            }
        });

        shortest_path = (RadioButton) view.findViewById(R.id.btn_shortest_path);
        shortest_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setText(SHORTEST_PHASE);
                mainController.setRobotMode(SHORTEST_PHASE,true);
            }
        });

        reset = (Button) view.findViewById(R.id.btn_reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jObj = new JSONObject();
                try {
                    jObj.put(MainActivity.JSONDATA_TYPE, MainActivity.CMD);
                    jObj.put(MainActivity.MSG, MainActivity.ROBOT_STATE_RESET);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new SendMessageAsyncTask().execute(jObj.toString(), MainController.RESET_MAP);
            }
        });
        return view;
    }



}