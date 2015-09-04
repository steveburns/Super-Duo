package it.jaschke.alexandria;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {
    private static final String TAG = ScannerFragment.class.getSimpleName();

    private ZXingScannerView mScannerView;
    private ScannerFragmentCallbacks mCallbacks;

    public ScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mScannerView = new ZXingScannerView(getActivity());   // Programmatically initialize the scanner view
        return mScannerView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (ScannerFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ScannerFragmentCallbacks.");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // register this class as handler for scan result
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result result) {
        Log.v(TAG, result.getText());
        Log.v(TAG, result.getBarcodeFormat().toString());

        Toast toast = Toast.makeText(getActivity(), String.format("Text: %s, Barcode: %s", result.getText(), result.getBarcodeFormat()), Toast.LENGTH_SHORT);
        toast.show();

        mCallbacks.onScanResultHandler(result.getText());
    }

    @Override
    public void onPause() {

        super.onPause();
        mScannerView.stopCamera();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface ScannerFragmentCallbacks {
        /**
         * Called when scan button is clicked.
         */
        void onScanResultHandler(String code);
    }
}
