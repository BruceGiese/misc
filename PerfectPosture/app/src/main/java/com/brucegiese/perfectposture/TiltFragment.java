package com.brucegiese.perfectposture;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * This fragment allows the user to start and stop the posture measurement service.
 */
public class TiltFragment extends Fragment {
    private static final String TAG = "com.brucegiese.t";
    private View mView;
    private boolean mButtonState = false;
    private Messenger mService;         // service for communicating with OrientationService
    private boolean mServiceConnected = false;
    private static OrientationService sOrientationService;

    static {
        // We need the service to be essentially a singleton
        sOrientationService = new OrientationService();
    }


    public TiltFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( ! sOrientationService.checkIsRunning()) {
            // We need to start the service before we bind to it so it will be persistent.
            mServiceConnected = false;      // can't be connected if it's not running
            Intent intent = new Intent(getActivity(), OrientationService.class);
            // make it persistent by calling this before attempting to bind
            getActivity().startService(intent);
        }
        getActivity().bindService(new Intent(getActivity(), OrientationService.class),
                    mConnection, getActivity().BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_tilt, container, false);
        checkAndSetButtonState();

        Button button = (Button) mView.findViewById(R.id.start_stop_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We're already connected to the service, so send the message now
                if (mButtonState) {
                    Log.d(TAG, "onClick(): shutting down");
                    stopOrientation();
                    ((Button) v).setText(R.string.start_tilt_detection);
                    mButtonState = false;

                } else {
                    Log.d(TAG, "onClick(): starting up");
                    startOrientation();
                    ((Button) v).setText(R.string.stop_tilt_detection);
                    mButtonState = true;
                }
            }
        });
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // In case of multiple instances running.
        checkAndSetButtonState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mView = null;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Check the global state of the service to see if it's running.  This is the most
     * reliable way of keeping track of the service without going into AIDL.
     */
    private void checkAndSetButtonState() {
        mButtonState = sOrientationService.checkIsRunning();
        if( mView != null ) {
            Button button = (Button) mView.findViewById(R.id.start_stop_button);
            if( mButtonState ) {
                button.setText(R.string.stop_tilt_detection);
            } else {
                button.setText(R.string.start_tilt_detection);
            }
        }
    }


    /*
    *       Orientation Related Stuff
    *
     */
    private void startOrientation() {
        if( ! mServiceConnected ) {
            Log.e(TAG, "We never got the service running or never got connected to it: ");
        } else {
            Log.d(TAG, "Sending start message");
            Message msg = Message.obtain(null, OrientationService.MSG_START_MONITORING, 0, 0);
            try {
                mService.send(msg);
            } catch (Exception e) {
                Log.e(TAG, "Exception trying to send message to start orienting: ", e);
            }
        }
    }

    private void stopOrientation() {
        if( ! mServiceConnected ) {
            Log.e(TAG, "Service was not already running when we went to stop it");
        } else {
            Log.d(TAG, "Sending stop message");
            Message msg = Message.obtain(null, OrientationService.MSG_STOP_MONITORING, 0, 0);
            try {
                mService.send(msg);
            } catch( Exception e) {
                Log.e(TAG, "Exception trying to send message to stop orienting: ", e);
            }
        }
    }


    /**
     * This provides a means for getting a connection to the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        /** this is called when the connection with the service has been established,
         * giving us the object we can use for the service.  We need a client-side
         * representation of the Messenger from the raw IBinder object
         */
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "Connection established connection with the service");
            mServiceConnected = true;
            mService = new Messenger(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            // Called when the connection with the service was unexpectedly disconnected
            Log.e(TAG, "ServiceConnection: The OrientationService probably crashed, onServiceDisconnected() called");
            mService = null;
            mServiceConnected = false;
        }
    };
}
