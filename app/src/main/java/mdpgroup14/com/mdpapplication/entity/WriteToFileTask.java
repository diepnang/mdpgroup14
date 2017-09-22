package mdpgroup14.com.mdpapplication.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import mdpgroup14.com.mdpapplication.MainActivity;
import mdpgroup14.com.mdpapplication.controller.MainController;


public class WriteToFileTask extends AsyncTask<String,Void,Void> {
    public static String filename = "mdpgroup14.com.application.MDF";
    private MainController mainController = MainController.getInstance();
    private SharedPreferences sp;
    @Override
    protected Void doInBackground(String... params) {
        Context context = mainController.getMainContext();
        sp = context.getSharedPreferences(filename,Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        StringBuilder mdf_part1 = new StringBuilder();
        StringBuilder mdf_part2 = new StringBuilder();
        for(int i =0; i < params[0].length(); i+=4){
            long value = Long.parseLong(params[0].substring(i,i+4),2);
            mdf_part1.append(Long.toHexString(value));
        }
        for(int i =0; i < params[1].length(); i+=4){
            long value = Long.parseLong(params[1].substring(i, i + 4),2);
            mdf_part2.append(Long.toHexString(value));
        }
        ed.putString("MDF_Part1",mdf_part1.toString());
        ed.putString("MDF_Part2", mdf_part2.toString());
        ed.commit();
        mainController.setMDF(mdf_part1.toString(),mdf_part2.toString());
        mainController.writeToSDFile(mdf_part1.toString(),mdf_part2.toString());
        return null;
    }
    @Override
    protected void onPostExecute(Void result){
        mainController.getHandler().obtainMessage(MainActivity.MESSAGE_END_EXPLORE).sendToTarget();
    }
}
