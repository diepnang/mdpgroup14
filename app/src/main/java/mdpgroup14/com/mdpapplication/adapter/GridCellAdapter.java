package mdpgroup14.com.mdpapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.Toast;

import mdpgroup14.com.mdpapplication.R;
import mdpgroup14.com.mdpapplication.controller.MainController;


public class GridCellAdapter extends BaseAdapter {
    public static final String UNEXPLORED = "2";
    public static final String OBSTACLE = "1";
    public static final String FREESPACE ="0";
    public static final String STARTGOAL = "3";
    public static final String RUN_PATH = "4";
    private int numRow, numColumn;
    private String[][] gridData;
    private Context context;
    private MainController mainController = MainController.getInstance();
    public GridCellAdapter(int numRow, int numColumn, String[][] mapData, Context context){
        this.numRow = numRow;
        this.numColumn = numColumn;
        gridData = mapData;
        this.context = context;
    }

    @Override
    public int getCount() {
        return numRow * numColumn;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.layout_grid_cell,parent,false);
            int row =  position / MainController.NUM_COLUMNS;
            int column = position - row* 15;
            if(row == mainController.getCoordY() && column == mainController.getCoordX()) {
                gridView.setBackgroundColor(0xffffa500);
            }
            else if(row == mainController.getRobotDirection()[0] && column == mainController.getRobotDirection()[1]){

                //Log.d("DIRECTION", String.valueOf(mainController.getRobotDirection()[0]));
                 gridView.setBackgroundColor(0xffff4081);
            }
            else if(row <= mainController.getRobotArea()[2] && row >= mainController.getRobotArea()[3]
                    && column <= mainController.getRobotArea()[0] && column >= mainController.getRobotArea()[1]){
                gridView.setBackgroundColor(0xff0dfd05);
            }
            else{
                switch (gridData[row][column]) {
                    case UNEXPLORED:
                        gridView.setBackgroundColor(Color.WHITE);
                        break;
                    case OBSTACLE:
                        gridView.setBackgroundColor(0xff000000);
                        break;
                    case FREESPACE:
                        gridView.setBackgroundColor(0xff87cefa);
                        break;
                    case STARTGOAL:
                        gridView.setBackgroundColor(0xffffff00);
                        break;
                    case RUN_PATH:
                        gridView.setBackgroundColor(Color.GRAY);
                        break;

                    default:
                        Toast.makeText(context, "Error Drawing", Toast.LENGTH_SHORT).show();
                        gridView.setBackgroundColor(0xffffffff);
                        break;
                }

            }
        }
        else{
            gridView = (View) convertView;
        }
        return gridView;
    }

}
