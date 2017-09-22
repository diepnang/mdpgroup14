package mdpgroup14.com.mdpapplication.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import mdpgroup14.com.mdpapplication.R;
import mdpgroup14.com.mdpapplication.controller.MainController;

public class MDFStringFragment extends Fragment {
    public final static String TAG = "MDFStringFragment";
    private TextView mdf1;
    private TextView mdf2;
    private Button loadMDF;
    private Context context;
    public MDFStringFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mdf_fragment,container,false);
        mdf1 = (TextView) view.findViewById(R.id.mdf_1);
        mdf2 = (TextView) view.findViewById(R.id.mdf_2);
        loadMDF = (Button) view.findViewById(R.id.btn_load_mdf);
        loadMDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdf1.setText(MainController.mdf1);
                mdf2.setText(MainController.mdf2);
            }
        });
        return view;
    }
}
