
package mdpgroup14.com.mdpapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mdpgroup14.com.mdpapplication.adapter.BottomTabAdapter;
import mdpgroup14.com.mdpapplication.adapter.GridCellAdapter;
import mdpgroup14.com.mdpapplication.controller.MainController;
import mdpgroup14.com.mdpapplication.entity.BluetoothChatService;
import mdpgroup14.com.mdpapplication.entity.SendMessageAsyncTask;
import mdpgroup14.com.mdpapplication.fragment.ControlFragment;

public class MainActivity extends AppCompatActivity
    implements SensorEventListener
{
    public static final int MESSAGE_UPDATE_START_COORD = 0;
    public static final int MESSAGE_UPDATE_MOVE = 1;
    public static final int MESSAGE_DEVICE_NAME =4 ;
    public static final String DEVICE_NAME = "device_name";
    public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_STATE_CHANGE =6 ;
    public static final String JSONDATA_TYPE ="type";
    public static final String ROBOT_STATE_CHANGE="stc";
    public static final String ROBOT_STATE_EXPLORE="explore";
    public static final String ROBOT_STATE_RUN="run";
    public static final String ROBOT_STATE_END_EXPLORE="ee";
    public static final String ROBOT_STATE_RESET="reset";
    public static final String ROBOT_STATE_END_RUN = "EndState";
    public static final String ROBOT_MOVE ="rm";
    public static final String MSG = "msg";
    public static final String ROBOT_MOVE_FORWARD = "mf";
    public static final String ROBOT_TURN_RIGHT = "tr";
    public static final String ROBOT_TURN_LEFT = "tl";
    public static final String ROBOT_TURN_BACK = "tb";
    private static final int REQUEST_ENABLE_BT = 11;
    public static final String MAP_UPDATE = "mu";
    public static final String CMD = "cmd";
    public static final int MESSAGE_RESET = 7 ;
    public static final int MESSAGE_MANUAL_UPDATE_MOVE = 8;
    public static final String SETROBOTPOS = "setrobotpos";
    public  static final String ROBOT_UPDATE = "ur";
    public static final int MESSAGE_END_EXPLORE = 10;
    public static final String HEAD_NORTH = "1";
    public static final String HEAD_EAST = "3";
    public static final String HEAD_SOUTH = "5";
    public static final String HEAD_WEST = "7";
    public static final String NEW_MAP_UPDATE = "ums";
    private Context context = this;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TextView timer;
    long timeInMillisecond = 0L;
    long timeSwapBuff = 0L;
    long timeUpdate = 0L;
    private long startTime = 0L;

    private MainController mainController = MainController.getInstance();
    private int TabIcons[] ={
            R.drawable.ic_control_tab,
            R.drawable.ic_key_start_tab,
            R.drawable.ic_bluetooth_tab,
            R.drawable.ic_setting_tab,
            R.drawable.ic_key_start_tab
    };
    private GridView map;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mChatService = null;
    private TextView connection_status;
    private TextView robot_status;
    private GridCellAdapter adapter;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connection_status = (TextView) findViewById(R.id.connection_feedback);
        robot_status = (TextView) findViewById(R.id.status_feedback);
        timer= (TextView) findViewById(R.id.timer);

        Log.d("Debug","before bt");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }
        mainController.setMainContext(this.getApplicationContext());
        mainController.initiateMapData();
        mainController.setBluetoothAdapter(mBluetoothAdapter);
        mainController.setHandler(mHandler);

        Log.d("debug", "sensor set up");
        mSensorManager = (SensorManager) getSystemService(MainActivity.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    private void setUpTab(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new BottomTabAdapter(getSupportFragmentManager(),MainActivity.this));

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) findViewById(R.id.fixed_tabs);
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(TabIcons[0]);
        tabLayout.getTabAt(1).setIcon(TabIcons[1]);
        tabLayout.getTabAt(2).setIcon(TabIcons[2]);
        tabLayout.getTabAt(3).setIcon(TabIcons[3]);
        tabLayout.getTabAt(4).setIcon(TabIcons[4]);
    }

    @Override
    public void onStart(){
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            Log.d("debug","bt is enabled");
            Log.d("debug", "cs" + String.valueOf(mChatService));
            if (mChatService == null) {
                Log.d("debug","chat service initial");
                setupChat();
            }

        }
        map = (GridView) findViewById(R.id.map_view);

        mainController.createAdapter();
        map.setAdapter(mainController.getAdapter());
        setUpTab();
    }

    @Override
    public synchronized void onResume(){

        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                Log.d("debug","start chatting");
                mChatService.start();
                mainController.setChatService(mChatService);
            }
        }
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public synchronized void onPause(){

        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    private void setupChat(){
        Log.d("SETUP","true");

        mChatService = new BluetoothChatService(this, mHandler);
    }
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_UPDATE_START_COORD:
                        //adpater.swapItems(mainController.getMapData());
                    map.invalidateViews();
                     map.setAdapter(mainController.getAdapter());
                    //mainController.getAdapter().notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "Change Start Coordinate", Toast.LENGTH_SHORT).show();

                    break;

                case MESSAGE_UPDATE_MOVE:
                        if(!mainController.isManualUpdate()){
                            Log.d("üpdate grid", "updated");
                            map.invalidateViews();
                             map.setAdapter(mainController.getAdapter());
                            //mainController.getAdapter().notifyDataSetChanged();
                            //adpater.swapItems(mainController.getMapData());
                        }
                    break;
                case MESSAGE_MANUAL_UPDATE_MOVE:
                    map.invalidateViews();
                     map.setAdapter(mainController.getAdapter());
                    //mainController.getAdapter().notifyDataSetChanged();
                    //adpater.swapItems(mainController.getMapData());
                    break;
                case MESSAGE_STATE_CHANGE:
                    switch(msg.arg1){
                        case BluetoothChatService.STATE_CONNECTED:
                            connection_status.setText("connected");
                            mainController.setRobotFinishRunning();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            connection_status.setText("connecting");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            connection_status.setText("Listening...");
                            break;
                        default:
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    try {
                        Log.d("MESSAGE", readMessage);
                        JSONObject jObj = new JSONObject(readMessage);
                        processDataRead(jObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MESSAGE_RESET:
                    mainController.setRobotOnRunning();
                    mainController.startCoord(1, 1);
                    mainController.updateRobotCoord(MainController.BTN_RIGHT);
                    stopTimer();
                    //adpater.swapItems(mainController.getMapData());
                    map.invalidateViews();
                    map.setAdapter(mainController.getAdapter());
                    break;
                case MESSAGE_END_EXPLORE:
                    robot_status.setText("stop");
                    mainController.setRobotFinishRunning();
                    //mainController.setRobotMode(ControlFragment.SHORTEST_PHASE,false);
                    stopTimer();

                    map.invalidateViews();
                    map.setAdapter(mainController.getAdapter());
                    break;
                default:
                    break;
            }
        }
    };

    private class MapUpdate extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.d("finish process data: ","1");
        }
    }
    private void processDataRead(JSONObject jObj) throws JSONException {
                //Log.d("key - value",JSONDATA_TYPE + " : "+ jObj.getString(JSONDATA_TYPE));
                switch(jObj.getString(JSONDATA_TYPE)){
                    case ROBOT_STATE_CHANGE:
                            Log.d("key - value",MSG + " : "+ jObj.getString(MSG));
                            switch (jObj.getString(MSG)) {
                                case ROBOT_STATE_EXPLORE:
                                    if(mainController.isRobotFinishRunning()) {
                                        robot_status.setText("start explore");
                                        mainController.setRobotOnRunning();
                                        mainController.startCoord(1, 1);
                                        mainController.updateRobotCoord(MainController.BTN_RIGHT);
                                        startTimer();
                                        map.invalidateViews();
                                        map.setAdapter(mainController.getAdapter());
                                    }
                                    break;
                                case ROBOT_STATE_RUN:
                                    mainController.setRobotOnRunning();
                                    robot_status.setText("fastest run");
                                    Toast.makeText(this,"start fastest run",Toast.LENGTH_SHORT).show();
                                    //gee3v-*+*-mainController.startFastestRun();
                                    startTimer();
                                    map.invalidateViews();
                                     map.setAdapter(mainController.getAdapter());
                                    break;
                                case ROBOT_STATE_END_EXPLORE:
                                    mainController.setEndExplore();
                                    robot_status.setText("stop");
                                    mainController.setRobotFinishRunning();
                                    stopTimer();
                                    map.invalidateViews();
                                     map.setAdapter(mainController.getAdapter());
                                    break;
                                case ROBOT_STATE_RESET:
                                    mainController.reset(true);
                                    mainController.setRobotOnRunning();
                                    mainController.startCoord(1, 1);
                                    JSONObject jObjSend =  new JSONObject();
                                    jObjSend.put(JSONDATA_TYPE, CMD);
                                    jObjSend.put(MSG, ROBOT_STATE_EXPLORE);
                                    new SendMessageAsyncTask().execute(jObjSend.toString(), String.valueOf(MainController.BTN_RIGHT));
                                    map.invalidateViews();
                                     map.setAdapter(mainController.getAdapter());

                                    stopTimer();
                                    //adpater.swapItems(mainController.getMapData());
                                    break;
                                case ROBOT_STATE_END_RUN:
                                    robot_status.setText("end run");
                                    stopTimer();
                                    mainController.setRobotFinishRunning();
                                    break;
                                default:
                                    break;
                            }

                        break;
                    case ROBOT_MOVE:
                            switch (jObj.getString(MSG)){
                                case ROBOT_MOVE_FORWARD:
                                    robot_status.setText("move forward");
                                    if(mainController.MOVE_VERTICAL) {
                                        if(mainController.START_TO_GOAL) {
                                            mainController.updateMap(mainController.BTN_FORWARD);
                                        }
                                        else{
                                            mainController.updateMap(mainController.BTN_BACKWARD);
                                        }
                                    }
                                    else{
                                        if(mainController.LEFT_TO_RIGHT){
                                            mainController.updateMap(mainController.BTN_RIGHT);
                                        }
                                        else{
                                            mainController.updateMap((mainController.BTN_LEFT));
                                        }
                                    }
                                    break;
                                case ROBOT_TURN_RIGHT:
                                    robot_status.setText("turn right");
                                    if(mainController.MOVE_VERTICAL){
                                        if(mainController.START_TO_GOAL){
                                            mainController.updateMap(mainController.BTN_RIGHT);
                                        }
                                        else{
                                            mainController.updateMap(mainController.BTN_LEFT);
                                        }
                                    }
                                    else{
                                        if(mainController.LEFT_TO_RIGHT){
                                            mainController.updateMap(mainController.BTN_BACKWARD);
                                        }
                                        else{
                                            mainController.updateMap(mainController.BTN_FORWARD);
                                        }
                                    }
                                    break;
                                case ROBOT_TURN_LEFT:
                                    robot_status.setText("turn left");
                                    if(mainController.MOVE_VERTICAL){
                                        if(mainController.START_TO_GOAL){
                                            mainController.updateMap(mainController.BTN_LEFT);
                                        }
                                        else{
                                            mainController.updateMap(mainController.BTN_RIGHT);
                                        }
                                    }
                                    else{
                                        if(mainController.LEFT_TO_RIGHT){
                                            mainController.updateMap(mainController.BTN_FORWARD);
                                        }
                                        else{
                                            mainController.updateMap(mainController.BTN_BACKWARD);
                                        }
                                    }
                                    break;
                                case ROBOT_TURN_BACK:
                                    robot_status.setText("turn back");
                                    if(mainController.MOVE_VERTICAL){
                                        if(mainController.START_TO_GOAL){
                                            mainController.updateMap(mainController.BTN_BACKWARD);
                                        }
                                        else{
                                            mainController.updateMap(mainController.BTN_FORWARD);
                                        }
                                    }
                                    else{
                                        if(mainController.LEFT_TO_RIGHT){
                                            mainController.updateMap(mainController.BTN_LEFT);
                                        }
                                        else{
                                            mainController.updateMap(mainController.BTN_RIGHT);
                                        }
                                    }
                                default:
                                    break;
                            }

                        break;
                    case MAP_UPDATE:
                            String data = jObj.getString(MSG);
                            String[] sensorData = data.trim().split(",");
                            int[] sensorDetectData = new int[6];
                            for(int i =0 ; i<6; ++i){
                                if(i==1 ||i==2) {
                                    sensorDetectData[3-i] = Integer.parseInt(sensorData[i]);
                                }
                                else{
                                    sensorDetectData[i] = Integer.parseInt(sensorData[i]);
                                }
                            }
                            mainController.setSensorData(sensorDetectData);
                        break;

                    case ROBOT_UPDATE:

                        String msg = jObj.getString(MSG);
                        Log.d("ur",msg);
                        String[] msgSplit = msg.trim().split(",");
                        mainController.setRobotCoordNew(Integer.parseInt(msgSplit[0]),Integer.parseInt(msgSplit[1]) );
                        switch (msgSplit[2]) {
                            case HEAD_NORTH:
                                mainController.updateMapWithOrientation(HEAD_NORTH);
                                break;
                            case HEAD_EAST:
                                mainController.updateMapWithOrientation(HEAD_EAST);
                                break;
                            case HEAD_SOUTH:
                                mainController.updateMapWithOrientation(HEAD_SOUTH);
                                break;
                            case HEAD_WEST:
                                mainController.updateMapWithOrientation(HEAD_WEST);
                                break;
                        }
                        break;

                    case NEW_MAP_UPDATE:
                        String mData = jObj.getString(MSG).trim();
                        String[] gridData = mData.split(",");
                        int realLength = ((gridData.length -1)/2)*3;
                        String[] reGridData = new String[realLength];
                        reGridData[0] = gridData[0];
                        for(int i =1,j=1; i < gridData.length-2;i+=2,j+=3){
                            reGridData[j] = gridData[i];
                            reGridData[j+1] = gridData[i+1].substring(0,1);
                            Log.d("1",reGridData[j+1]);
                            reGridData[j+2] = gridData[i+1].substring(2);
                            Log.d("1",reGridData[j+2]);
                        }
                        reGridData[realLength-2]= gridData[gridData.length-2];
                        reGridData[realLength-1] = gridData[gridData.length-1];
                        mainController.updateSensorGridData(reGridData);
                        break;
                    default:
                        break;
                }

    }


    /*Tilt*/
    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}

    private Boolean flag = false;
    private Boolean flag2 = false;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float z1 = event.values[2];
        float x1 = event.values[0];
        float y1 = event.values[1];

        int x = (int) (x1 * 1000);
        int y = (int) (y1 * 1000);
        int z = (int) (z1 * 1000);

        if (mainController.getTilt()) {
            if (x > 5000) {
                //left
                if (flag == true) {
                    System.out.println("Check!!!");
                    Toast.makeText(getBaseContext(), "YOU TILTED LEFT", Toast.LENGTH_SHORT).show();
                    mainController.updateMap(mainController.BTN_LEFT);
                    mainController.getHandler().obtainMessage(MainActivity.MESSAGE_MANUAL_UPDATE_MOVE).sendToTarget();
                    flag = false;
                }
            } else if (x < -5000) {
                //right
                if (flag == true) {
                    Toast.makeText(getBaseContext(), "YOU TILTED RIGHT", Toast.LENGTH_SHORT).show();
                    mainController.updateMap(mainController.BTN_RIGHT);
                    mainController.getHandler().obtainMessage(MainActivity.MESSAGE_MANUAL_UPDATE_MOVE).sendToTarget();
                    flag = false;
                }
            } else {
                flag = true;
            }

            if (y < 3000) {
                if (flag2 == true) {
                    Toast.makeText(getBaseContext(), "FORWARD", Toast.LENGTH_SHORT).show();
                    mainController.updateMap(mainController.BTN_FORWARD);
                    mainController.getHandler().obtainMessage(MainActivity.MESSAGE_MANUAL_UPDATE_MOVE).sendToTarget();
                    flag2 = false;
                }
            } else {
                flag2 = true;
            }
        }

    }

    private void startTimer(){
        Log.d("time","startTimer");
        startTime = SystemClock.uptimeMillis();
        mHandler.postDelayed(updateTimerThread, 0);
    }

    private void stopTimer(){
        timer.setText("00:00:00");
        mHandler.removeCallbacks(updateTimerThread);
        timeUpdate = timeInMillisecond = timeSwapBuff = 0L;
    }
    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMillisecond = SystemClock.uptimeMillis() - startTime;
            timeUpdate = timeSwapBuff + timeInMillisecond;
            int secs = (int) (timeUpdate/1000);
            int mins = secs/ 60;
            secs = secs % 60;
            int milliseconds = (int) (timeUpdate % 1000);
            timer.setText("" + mins +":" + String.format("%02d",secs)+":"
                            + String.format("%03d",milliseconds));
            mHandler.postDelayed(this, 0);
        }
    };
}
