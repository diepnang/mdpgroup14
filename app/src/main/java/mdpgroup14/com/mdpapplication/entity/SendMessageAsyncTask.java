package mdpgroup14.com.mdpapplication.entity;

import android.os.AsyncTask;
import android.util.Log;

import mdpgroup14.com.mdpapplication.MainActivity;
import mdpgroup14.com.mdpapplication.controller.MainController;


public class SendMessageAsyncTask extends AsyncTask<String,Void,Integer> {
    private MainController mainController = MainController.getInstance();
    private static final int MSG_SENT = 1;
    private String updateMapAction;
    @Override
    protected Integer doInBackground(String... params) {
        mainController.sendMessage(params[0]);
        updateMapAction = params[1];
        if(updateMapAction.equals(MainController.RESET_MAP)) {
            mainController.reset(false);
            updateMapAction = mainController.NOT_UPDATE_MAP;
            Log.d("reset", "true");
        }
        return MSG_SENT;
    }

    @Override
    protected void onPostExecute(Integer result){
        if(result.equals(MSG_SENT) && !updateMapAction.equals(mainController.NOT_UPDATE_MAP)){
            mainController.updateMap(Integer.parseInt(updateMapAction));
        }
    }
}
