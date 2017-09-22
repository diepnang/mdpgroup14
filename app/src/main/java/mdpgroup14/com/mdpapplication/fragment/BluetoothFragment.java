package mdpgroup14.com.mdpapplication.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import mdpgroup14.com.mdpapplication.R;
import mdpgroup14.com.mdpapplication.controller.MainController;


public class BluetoothFragment extends Fragment{
    public final static String TAG = "BluetoothFragment";
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private Button bluetoothDiscover;
    private Button bluetoothConnect;
    private BluetoothAdapter mBluetoothAdapter;
    private MainController mainController = MainController.getInstance();
    public BluetoothFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = mainController.getBluetoothAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.bluetooth_fragment, container, false);
        bluetoothDiscover = (Button) view.findViewById(R.id.btn_discoverable);
        bluetoothDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 365);
                    startActivity(discoverableIntent);
                }
            }
        });

        bluetoothConnect = (Button) view.findViewById(R.id.btn_connect);
        bluetoothConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(intent, REQUEST_CONNECT_DEVICE_SECURE);
            }
        });
        return view;
    }
}
