package mdpgroup14.com.mdpapplication.controller;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import mdpgroup14.com.mdpapplication.MainActivity;
import mdpgroup14.com.mdpapplication.adapter.GridCellAdapter;
import mdpgroup14.com.mdpapplication.entity.BluetoothChatService;
import mdpgroup14.com.mdpapplication.entity.WriteToFileTask;
import mdpgroup14.com.mdpapplication.fragment.ControlFragment;


public class MainController {
    public static final int BTN_FORWARD = 8;
    public static final int BTN_RIGHT =9 ;
    public static final int BTN_LEFT =10 ;
    public static final String STANDBY = "stand by";
    public static final int BTN_BACKWARD = 11;
    public static final String NOT_UPDATE_MAP = "noupdate";
    public static final String RESET_MAP = "resetmap";
    public static boolean LEFT_TO_RIGHT = false;
    public static boolean MOVE_VERTICAL = true;
    private int exploredPercent = 0;
    private static final int NO_OBSTACLE = -1;
    private static final int OBSTACLE_BESIDE = 0;
    public static boolean START_TO_GOAL = true ;
    public static boolean isRun = false;
    boolean[][] explored = new boolean[NUM_ROWS][NUM_COLUMNS];
    private Handler handler;
    private int[] robotDirection={-1,-1};
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothChatService chatService;
    private boolean manualUpdate =false;
    private boolean tilt = false;
    private String robotMode = ControlFragment.EXPLORATION_PHASE;
    private boolean robotFinishRunning = true;
    private String[][] mapExplored = new String[NUM_ROWS][NUM_COLUMNS];
    private int numOfObstacles = 0;
    public int passX = -1;
    public int passY = -1;
    public static String mdf1 = "";
    public static String mdf2 = "";
    private GridCellAdapter adapter;
    private MainController(){
        for(int i =0; i < NUM_ROWS; i++){
            for(int j =0; j <NUM_COLUMNS; j++) {
                if ((i > 16 && j < 3) || (i < 3 && j > 11)) {
                    mapData[i][j] = GridCellAdapter.STARTGOAL;
                    explored[i][j] = true;
                }
                else {
                    explored[i][j] = false;
                }
            }
        }

    }
    private int[] robotArea={-1,-1,-1,-1};

    public void createAdapter(){
        adapter= new GridCellAdapter(NUM_ROWS,NUM_COLUMNS,mapData,mainContext);
    }
    public void saveMap() {
        for(int i =0; i < NUM_ROWS; ++i){
            for(int j = 0; j < NUM_COLUMNS; ++j){
                mapExplored[i][j] = mapData[i][j];
            }
        }
    }

    public void setRobotDirection(int x,int y) {
        robotDirection[0] = y;
        robotDirection[1] = x;
        robotArea = new int[]{coordX + 1, coordX-1, coordY + 1, coordY -1};
    }

    public void startFastestRun() {
        MOVE_VERTICAL = false;
        LEFT_TO_RIGHT = true;
        START_TO_GOAL = true;
        coordX = 1;
        coordY = NUM_ROWS - 2;
        robotDirection = new int[]{coordY+1, coordX};
        robotArea = new int[]{coordX + 1, coordX-1, coordY + 1, coordY -1};
    }

    public void setMDF(String s, String s1) {
        this.mdf1 = "mdf part1 : " +s;
        this.mdf2 = "mdf part 2: " + s1;
    }

    public void setRobotCoordNew(int coordX,int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
        Log.d("x:y", String.format("%s : %s",this.coordX,this.coordY));
        if(isRun) {
            mapData[this.coordY][this.coordX] = GridCellAdapter.RUN_PATH;
        }
    }

    public void updateMapWithOrientation(String orientation) {
        Log.d("d",orientation);
        new UpdateMapWithOriTask().execute(orientation);
    }

    public void setEndExplore() {
        START_TO_GOAL =false;
        LEFT_TO_RIGHT =false;
        MOVE_VERTICAL=true;
    }

    public GridCellAdapter getAdapter() {
        return adapter;
    }

    static class MainControllerHolder{
        static MainController instance = new MainController();
    }
    public static MainController getInstance(){
        return MainControllerHolder.instance;
    }
    public static final int NUM_ROWS=20;
    public static  final int NUM_COLUMNS = 15;
    private String[][] mapData = new String[NUM_ROWS][NUM_COLUMNS];
    private int coordX = -1;
    private int coordY = -1;
    private int direction;
    private int[] sensorDetect = {-1,-1,-1,-1,-1};
    private Context mainContext;
    public void initiateMapData(){
        for(int i =0; i < NUM_ROWS; i++){
            for(int j =0; j <NUM_COLUMNS; j++){
                if(!explored[i][j]) {
                        mapData[i][j] = GridCellAdapter.UNEXPLORED;

                }
            }
        }
    }
    public void startCoord(int coordXInput, int coordYInput){
        resetMap();
        MOVE_VERTICAL = true;
        LEFT_TO_RIGHT = true;
        START_TO_GOAL = true;
        coordX = coordXInput;
        coordY = NUM_ROWS - coordYInput -1;
        /*new UpdateMapTask().execute(coordXInput, coordYInput, MOVE_UP);*/

        robotDirection = new int[]{coordY-1,coordX};
    }
    public void updateRobotCoord(int directionInput){
        this.direction = directionInput;
        robotArea = new int[]{coordX + 1, coordX-1, coordY + 1, coordY -1};
        switch(direction){
            case BTN_FORWARD:
                if(MOVE_VERTICAL) {
                    if(START_TO_GOAL) {
                        robotDirection[0]--;
                    }
                    else{
                        robotDirection[0]-=2;
                    }

                }
                else{
                    MOVE_VERTICAL = true;
                    if(LEFT_TO_RIGHT){
                            robotDirection[1]--;
                    }
                    else{
                            robotDirection[1]++;
                    }
                    robotDirection[0]--;
                }
                START_TO_GOAL = true;

                break;
            case BTN_LEFT:
                if(MOVE_VERTICAL) {
                    MOVE_VERTICAL = false;
                    if(START_TO_GOAL){
                        robotDirection[0]++;
                    }
                    else{
                        robotDirection[0]--;
                    }
                    robotDirection[1]--;
                }
                else{
                    if(!LEFT_TO_RIGHT) {
                        robotDirection[1]--;
                    }
                    else{
                        robotDirection[1]-= 2;
                    }

                }
                LEFT_TO_RIGHT = false;
                break;
            case BTN_RIGHT:
                if(MOVE_VERTICAL) {
                    MOVE_VERTICAL = false;

                    if(START_TO_GOAL){
                        robotDirection[0]++;
                    }
                    else{
                        robotDirection[0]--;
                    }
                    robotDirection[1]++;
                }
                else{
                    if(LEFT_TO_RIGHT) {
                        robotDirection[1]++;
                    }
                    else{
                        robotDirection[1]+=2;
                    }
                }
                LEFT_TO_RIGHT = true;
                break;
            case BTN_BACKWARD:
                if(MOVE_VERTICAL) {
                    if(!START_TO_GOAL) {
                        robotDirection[0]++;
                    }
                    else{
                        robotDirection[0]+=2;
                    }
                }
                else{
                    MOVE_VERTICAL = true;
                    if(LEFT_TO_RIGHT){
                        robotDirection[1]--;
                    }
                    else{
                        robotDirection[1]++;
                    }
                    robotDirection[0]++;
                }
                START_TO_GOAL = false;
            default:
                break;
        }
        Log.d("Log", String.valueOf(MOVE_VERTICAL) + String.valueOf(LEFT_TO_RIGHT) + String.valueOf(START_TO_GOAL));

    }

    public void setMainContext(Context context){
        mainContext = context;
    }
    public Context getMainContext(){
        return mainContext;
    }

    public String[][] getMapData() {
        return mapData;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
    }

    public void updateMap(int message) {
        if(isRun) {
            mapData[coordY][coordX] = GridCellAdapter.RUN_PATH;
        }
        switch (message) {
            case BTN_FORWARD:
                if ( this.coordY <= 0 ){
                    Toast.makeText(mainContext, "cannot go ", Toast.LENGTH_SHORT).show();
                } else {
                    if(MOVE_VERTICAL) {
                        coordY--;
                    }
                    new UpdateMapTask().execute(BTN_FORWARD);
                }
                break;
            case BTN_LEFT:
                if(this.coordX <=0 ){
                    Toast.makeText(mainContext, "cannot go ", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!MOVE_VERTICAL) {
                        coordX--;
                    }
                    new UpdateMapTask().execute(BTN_LEFT);

                }
                break;
            case BTN_RIGHT:
                if(this.coordX >=NUM_COLUMNS-1 ){
                    Toast.makeText(mainContext, "cannot go ", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(!MOVE_VERTICAL) {
                        coordX++;
                    }
                    new UpdateMapTask().execute(BTN_RIGHT);

                }
                break;
            case BTN_BACKWARD:
                if(this.coordY >= NUM_ROWS-1){
                    Toast.makeText(mainContext, "cannot go ", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(MOVE_VERTICAL) {
                       coordY++;
                    }
                    new UpdateMapTask().execute(BTN_BACKWARD);
                }
                break;
            default:
                break;
        }
    }

    public int getCoordX() {
        return coordX;
    }

    public int getCoordY() {
        return coordY;
    }

    public int[] getRobotArea() {
        return robotArea;
    }

    public int[] getRobotDirection() {
        return robotDirection;
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
    public void reset(boolean isRobotReset){
        resetMap();
        setRobotFinishRunning();
        if(handler!= null && !isRobotReset){
            handler.obtainMessage(MainActivity.MESSAGE_RESET).sendToTarget();
        }
    }
    private void resetMap() {
        for(int i =0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLUMNS; j++) {
                if ((i > 16 && j < 3) || (i < 3 && j > 11)) {
                    mapData[i][j] = GridCellAdapter.STARTGOAL;
                    explored[i][j] = true;
                } else {
                    mapData[i][j] = GridCellAdapter.UNEXPLORED;
                    explored[i][j] = false;
                }

            }
        }

    }

    public void setSensorData(int[] sensorData) {
        this.sensorDetect = sensorData;
        new UpdateSensorDataTask().execute();
    }

    public void setExploredPercent(int exploredPercent) {
        this.exploredPercent = exploredPercent;
    }

    public void setChatService(BluetoothChatService chatService) {
        this.chatService = chatService;
    }

    public void setManualUpdate(boolean manualUpdate) {
        this.manualUpdate = manualUpdate;
    }

    public boolean isManualUpdate() {
        return manualUpdate;
    }

    public void setTilt(boolean tilt){this.tilt = tilt;}

    public boolean getTilt(){return tilt;}

    public void setRobotMode(String robotMode,boolean isShortestRun) {
        this.robotMode = robotMode;
        isRun = isShortestRun;
    }

    public String getRobotMode() {
        return robotMode;
    }

    public boolean isRobotFinishRunning() {
        return robotFinishRunning;
    }

    public void setRobotOnRunning() {
        robotFinishRunning = false;
    }

    public void setRobotFinishRunning() {
        robotFinishRunning = true;
    }

    public BluetoothChatService getChatService() {
        return chatService;
    }

    private class UpdateMapWithOriTask extends AsyncTask<String,Void , Void>{

        @Override
        protected Void doInBackground(String... params) {
            updateRobotAreaAndOrientation(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if (handler != null) {
                handler.obtainMessage(MainActivity.MESSAGE_UPDATE_MOVE).sendToTarget();
                if((robotDirection[0]==18 && robotDirection[1] == 0) || (robotDirection[0]==19 && robotDirection[1]==1) ){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder str_1 = new StringBuilder();
                            str_1.append("11");
                            StringBuilder str_2 = new StringBuilder();
                            int numOfUnexplored = 0;
                            boolean isSetUnexploredAsFree = true;
                            for (int i = 0; i < NUM_ROWS; ++i) {
                                for (int j = 0; j < NUM_COLUMNS; ++j) {
                                    if (mapData[NUM_ROWS - i - 1][j].equals(GridCellAdapter.UNEXPLORED)) {
                                        numOfUnexplored++;
                                    }
                                }
                            }
                            if(numOfObstacles < 30){
                                if(numOfUnexplored == 30-numOfObstacles){
                                    isSetUnexploredAsFree = false;
                                }
                                else{
                                    isSetUnexploredAsFree = true;
                                }
                            }
                            else{
                                isSetUnexploredAsFree = true;
                            }
                            for (int i = 0; i < NUM_ROWS; ++i) {
                                for (int j = 0; j < NUM_COLUMNS; ++j) {
                                    str_1.append("1");
                                    if (mapData[NUM_ROWS - i - 1][j].equals(GridCellAdapter.UNEXPLORED)) {
                                        if(isSetUnexploredAsFree){
                                            mapData[NUM_ROWS - i - 1][j] = GridCellAdapter.FREESPACE;
                                        }
                                        else{
                                            mapData[NUM_ROWS - i - 1][j] = GridCellAdapter.OBSTACLE;
                                        }
                                    }

                                    if (mapData[NUM_ROWS - i - 1][j].equals(GridCellAdapter.OBSTACLE)) {
                                        str_2.append("1");
                                    } else {
                                        str_2.append("0");
                                    }
                                }
                            }
                            str_1.append("11");
                            while (str_2.length() % 8 != 0) {
                                str_2.append("0");
                            }
                            new WriteToFileTask().execute(str_1.toString(), str_2.toString());
                        }
                    }, 2000);
                }
            }
            if(robotMode.equals(ControlFragment.SHORTEST_PHASE)) {
                passX = coordX;
                passY = coordY;
            }
        }
    }

    private void updateRobotAreaAndOrientation(String orientation) {
        Log.d("o",orientation);
        robotArea = new int[]{coordX + 1, coordX-1, coordY + 1, coordY -1};
        switch(orientation){
            case MainActivity.HEAD_NORTH:
                robotDirection[0] = this.coordY -1;
                robotDirection[1] = this.coordX;
                break;
            case MainActivity.HEAD_EAST:
                robotDirection[0] = this.coordY;
                robotDirection[1] = this.coordX+1;
                break;
            case MainActivity.HEAD_SOUTH:
                robotDirection[0] = this.coordY +1;
                robotDirection[1] =this.coordX;
                break;
            case MainActivity.HEAD_WEST:
                robotDirection[0] = this.coordY;
                robotDirection[1] = this.coordX - 1;
                break;
            default:
                break;
        }
    }

    private class UpdateMapTask extends AsyncTask<Integer,Void,Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            updateRobotCoord(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if (handler != null) {
                handler.obtainMessage(MainActivity.MESSAGE_UPDATE_MOVE).sendToTarget();
                if((robotDirection[0]==18 && robotDirection[1] == 0) || (robotDirection[0]==19 && robotDirection[1]==1) ){
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           StringBuilder str_1 = new StringBuilder();
                           str_1.append("11");
                           StringBuilder str_2 = new StringBuilder();
                           for (int i = 0; i < NUM_ROWS; ++i) {
                               for (int j = 0; j < NUM_COLUMNS; ++j) {
                                   if (mapData[NUM_ROWS - i - 1][j].equals(GridCellAdapter.UNEXPLORED)) {
                                       str_1.append("0");
                                   } else {
                                       str_1.append("1");
                                       if (mapData[NUM_ROWS - i - 1][j].equals(GridCellAdapter.OBSTACLE)) {
                                           str_2.append("1");
                                       } else {
                                           str_2.append("0");
                                       }
                                   }
                               }
                           }
                           str_1.append("11");
                           while (str_2.length() % 8 != 0) {
                               str_2.append("0");
                           }
                           new WriteToFileTask().execute(str_1.toString(), str_2.toString());
                       }
                   }, 2000);
                }
            }
            if(robotMode.equals(ControlFragment.SHORTEST_PHASE)) {
                passX = coordX;
                passY = coordY;
            }
        }
    }

    private class UpdateSensorDataTask extends  AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            updateMapData(sensorDetect);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            if (handler != null) {
                handler.obtainMessage(MainActivity.MESSAGE_UPDATE_MOVE).sendToTarget();
            }
        }
    }

    private void updateMapData(int[] sensorDetect){
        if(MOVE_VERTICAL){
            if(START_TO_GOAL){
                for(int j =-1; j<2 ;++j) {
                    handleSensorUp(sensorDetect[j+1],j,2);
                }

                if(coordX>0){
                    handleSensorLeft(sensorDetect[3],1,2);
                }

                if(coordX < NUM_COLUMNS -1){
                    handleSensorRight(sensorDetect[4],-1,2);
                    handleSensorRight(sensorDetect[5],1,2);
                }
            }
            else{
                for(int j =-1; j<2 ;++j) {
                    handleSensorDown(sensorDetect[1-j], j,2);
                }

                if(coordX>0){
                    handleSensorRight(sensorDetect[3],-1,2);
                }

                if(coordX < NUM_COLUMNS -1){
                    handleSensorLeft(sensorDetect[4],1,2);
                    handleSensorLeft(sensorDetect[4],-1,2);
                }
            }
        }
        else{
            if(LEFT_TO_RIGHT){
                for(int j =-1; j<2 ;++j) {
                    handleSensorRight(sensorDetect[j+1],j,2);
                }

                if(coordY>0){
                    handleSensorUp(sensorDetect[3],-1,2);
                }

                if(coordY < NUM_ROWS -1){
                    handleSensorDown(sensorDetect[4],1,2);
                    handleSensorDown(sensorDetect[5],-1,2);
                }
            }
            else{
                for(int j =-1; j<2 ;++j) {
                    handleSensorLeft(sensorDetect[1-j],j,2);
                }

                if(coordY>0){
                    handleSensorUp(sensorDetect[4],-1,2);
                    handleSensorUp(sensorDetect[5],1,2);
                }

                if(coordY < NUM_ROWS -1){
                    handleSensorDown(sensorDetect[3],1,2);
                }
            }
        }
    }

    private void handleSensorLeft(int sensorDetectCode,int sensorPos,int range){
        Log.d("SENSOR", "LEFT");
        if(sensorDetectCode > OBSTACLE_BESIDE){
            if(coordX-sensorDetectCode-2>=0 && !explored[coordY+sensorPos][coordX-sensorDetectCode-2] ){
                mapData[coordY + sensorPos][coordX-sensorDetectCode-2]=GridCellAdapter.OBSTACLE;
                explored[coordY+sensorPos][coordX-sensorDetectCode-2]=true;
            }
            for(int i =2; i< sensorDetectCode + 2;++i ){
                if(coordX-i >=0){
                    if(!explored[coordY+sensorPos][coordX-i]){
                        mapData[coordY+sensorPos][coordX-i] = GridCellAdapter.FREESPACE;
                        explored[coordY+sensorPos][coordX-i]=true;
                    }
                }
                else{
                    break;
                }

            }
        }
        else if(sensorDetectCode == OBSTACLE_BESIDE){
            if(coordX - sensorDetectCode -2 >= 0 && !explored[coordY+sensorPos][coordX-sensorDetectCode-2]){
                mapData[coordY + sensorPos][coordX-sensorDetectCode-2]=GridCellAdapter.OBSTACLE;
                explored[coordY+sensorPos][coordX-sensorDetectCode-2]=true;
            }
        }
        else{
            for(int i=2; i < 2 + range;++i){
                if(coordX-i >=0){
                    if(!explored[coordY + sensorPos][coordX-i] /*|| mapData[coordY+sensorPos][coordX-i].equals(GridCellAdapter.OBSTACLE)*/) {
                        mapData[coordY + sensorPos][coordX - i] = GridCellAdapter.FREESPACE;
                        explored[coordY + sensorPos][coordX - i] = true;
                    }
                }
                else{
                    break;
                }
            }
        }
    }

    private void handleSensorRight(int sensorDetectCode,int sensorPos,int range){
        Log.d("SENSOR", "RIGHT");
        if(sensorDetectCode > OBSTACLE_BESIDE){
            if(coordX+sensorDetectCode+2 <= NUM_COLUMNS-1 && !explored[coordY+sensorPos][coordX+sensorDetectCode+2]){
                mapData[coordY+sensorPos][coordX+sensorDetectCode+2]=GridCellAdapter.OBSTACLE;
                explored[coordY+sensorPos][coordX+sensorDetectCode+2]=true;
            }
            for(int i =2; i< sensorDetectCode+2;++i ){
                if(coordX+i <= NUM_COLUMNS-1) {
                    if (!explored[coordY + sensorPos][coordX + i]) {
                        mapData[coordY + sensorPos][coordX + i] = GridCellAdapter.FREESPACE;
                        explored[coordY + sensorPos][coordX + i] = true;
                    }
                    else{
                        break;
                    }
                }
            }

        }
        else if(sensorDetectCode == OBSTACLE_BESIDE){
            if(coordX+sensorDetectCode+2 <= NUM_COLUMNS-1 && !explored[coordY+sensorPos][coordX+sensorDetectCode+2]){
                mapData[coordY+sensorPos][coordX+sensorDetectCode+2]=GridCellAdapter.OBSTACLE;
                explored[coordY+sensorPos][coordX+sensorDetectCode+2]=true;
            }
        }
        else{
            for(int i=2; i < 2 + range;++i){
                if(coordX+i <= NUM_COLUMNS-1) {
                    if(!explored[coordY+sensorPos][coordX+i]) {
                        mapData[coordY + sensorPos][coordX + i] = GridCellAdapter.FREESPACE;
                        explored[coordY + sensorPos][coordX + i] = true;
                    }
                }
                else{
                    break;
                }
            }

        }
    }

    private void handleSensorUp(int sensorDetectCode, int sensorPos, int range){
        Log.d("SENSOR", "UP");
        if (sensorDetectCode > OBSTACLE_BESIDE) {
            if (coordY - sensorDetectCode -2 >= 0 && !explored[coordY-sensorDetectCode -2][coordX+sensorPos]) {
                mapData[coordY - sensorDetectCode -2][coordX+sensorPos] = GridCellAdapter.OBSTACLE;
                explored[coordY-sensorDetectCode -2][coordX+sensorPos] = true;
            }
            for (int k = 2; k < sensorDetectCode+2; ++k) {
                if(coordY-k >=0 ) {
                    if (!explored[coordY - k][coordX + sensorPos]) {
                        mapData[coordY - k][coordX + sensorPos] = GridCellAdapter.FREESPACE;
                        explored[coordY - k][coordX + sensorPos] = true;
                    }
                    else{
                        break;
                    }
                }
            }
        }
        else if (sensorDetectCode == OBSTACLE_BESIDE) {
            if (coordY - sensorDetectCode -2 >= 0 && !explored[coordY-sensorDetectCode -2][coordX+sensorPos]){
                mapData[coordY - sensorDetectCode -2][coordX+sensorPos] = GridCellAdapter.OBSTACLE;
                explored[coordY-sensorDetectCode -2][coordX+sensorPos] = true;
            }
        }
        else {
            for (int i = 2; i < 2 + range; ++i) {
                if (coordY - i >= 0){
                    if(!explored[coordY - i][coordX+sensorPos]) {
                        mapData[coordY - i][coordX + sensorPos] = GridCellAdapter.FREESPACE;
                        explored[coordY - i][coordX + sensorPos] = true;
                    }
                }
                else{
                    break;
                }
            }

        }
    }

    private void handleSensorDown(int sensorDetectCode, int sensorPos,int range){
        Log.d("SENSOR", "DOWN");
        if (sensorDetectCode > OBSTACLE_BESIDE) {
            if (coordY + sensorDetectCode+2 <= NUM_ROWS-1 && !explored[coordY + sensorDetectCode + 2][coordX+sensorPos]) {
                mapData[coordY + sensorDetectCode + 2][coordX+sensorPos] = GridCellAdapter.OBSTACLE;
                explored[coordY + sensorDetectCode+2][coordX+sensorPos] = true;
            }
            for (int k = 2; k < sensorDetectCode+2; ++k) {
                if(coordY+k <= NUM_ROWS-1) {
                    if (!explored[coordY + k][coordX + sensorPos]) {
                        mapData[coordY + k][coordX + sensorPos] = GridCellAdapter.FREESPACE;
                        explored[coordY + k][coordX + sensorPos] = true;
                    }
                    else{
                        break;
                    }
                }
            }

        }
        else if (sensorDetectCode == OBSTACLE_BESIDE) {
            if (coordY + sensorDetectCode+2 <= NUM_ROWS-1 && !explored[coordY + sensorDetectCode+2][coordX+sensorPos]){
                mapData[coordY + sensorDetectCode + 2][coordX+sensorPos] = GridCellAdapter.OBSTACLE;
                explored[coordY + sensorDetectCode+2][coordX+sensorPos] = true;
            }
        }
        else {
            for (int i = 2; i < 2+ range; ++i) {
                if (coordY + i <= NUM_ROWS -1){
                    if(!explored[coordY + i][coordX+sensorPos]) {
                        mapData[coordY + i][coordX + sensorPos] = GridCellAdapter.FREESPACE;
                        explored[coordY + i][coordX + sensorPos] = true;
                    }
                }
                else{
                    break;
                }
            }
        }
    }

    public void sendMessage(String message) {
        if (chatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //Log.d(TAG, R.string.not_connected + "");
            return;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatService.write(send);
            Log.d("send to bluetooth", "MSG sent: " + new String(send));
        }
    }

    public void updateSensorGridData(String[] gridData){
        new UpdateSensorGridDataTask().execute(gridData);
    }

    private class UpdateSensorGridDataTask extends AsyncTask<String[],Void,Void>{

        @Override
        protected Void doInBackground(String[]... params) {
            updateMapGridData(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (handler != null) {
                handler.obtainMessage(MainActivity.MESSAGE_UPDATE_MOVE).sendToTarget();
            }
        }
    }

    private void updateMapGridData(String[] gData){
        Log.d("grid",String.valueOf(gData));
        int x =-1,y=-1;
        for(int i =0; i < gData.length; i+=3){

            x = Integer.parseInt(gData[i]);
            y = Integer.parseInt(gData[i+1]);
            Log.d("x,y",String.format("%d,%d",x,y));
            if(explored[y][x]){
                continue;
            }
            mapData[y][x] = gData[i+2];
            if(gData[i+2].equals(GridCellAdapter.OBSTACLE)){
                numOfObstacles++;
            }
            explored[y][x] = true;

        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public void writeToSDFile(String mdf1, String mdf2){

        // Find the root of the external storage.
        // See http://developer.android.com/guide/topics/data/data-  storage.html#filesExternal

        File root = android.os.Environment.getExternalStorageDirectory();

        // See http://stackoverflow.com/questions/3551821/android-write-to-sd-card-folder

        File dir = new File (root.getAbsolutePath() + "/mdpfolder");
        Log.d("root",root.getAbsolutePath());
        dir.mkdirs();
        File file = new File(dir, "myData.txt");

        try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println(mdf1);
            pw.println(mdf2);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("write to file", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
