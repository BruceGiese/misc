package com.brucegiese.testingloaders;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class LoaderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<MixedData> {

    private static final String TAG = "testload.fragment";
    private static final int LOADER_NUMBER = 12;
    private static final String ARG_PARAM_A = "paramA";
    private static final String ARG_PARAM_B = "paramB";

    private String mParamA;
    private int mParamB;
    private MixedData mData;

    private View mView;
    private TextView mFragmentText;

    private OnFragmentInteractionListener mListener;

    interface OnFragmentInteractionListener {
        void whatsGoingOn(String whatsup );
    }



    /**
     *
     * @param paramA
     * @param paramB
     * @return A new instance of fragment LoaderFragment.
     *
     */
    public static LoaderFragment newInstance(String paramA, int paramB) {
        Log.d(TAG, "newInstance() called with: " + paramA + ", " + paramB );

        LoaderFragment fragment = new LoaderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_A, paramA);
        args.putInt(ARG_PARAM_B, paramB);
        fragment.setArguments(args);

        return fragment;
    }


    public LoaderFragment() {
        // Required empty public constructor
        Log.d(TAG, "LoaderFragment() constructor called");
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnFragmentInteractionListener) activity;
        mListener.whatsGoingOn("attached to activity");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamA = getArguments().getString(ARG_PARAM_A);
            mParamB = getArguments().getInt(ARG_PARAM_B);
        }
        mListener.whatsGoingOn("onCreate in the fragment");

        getLoaderManager().initLoader( LOADER_NUMBER, null, this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_loader, container, false);
        mFragmentText = (TextView) mView.findViewById(R.id.fragment_text);

        mListener.whatsGoingOn("onCreateView() in the fragment");
        return mView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "onDestroyView()");
        mListener.whatsGoingOn("destroying the view in the fragment");
        mView = null;

    }


    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach() called");
        mListener = null;
    }


    /**
     *      Loader Callbacks
     *
     */
    @Override
    public Loader<MixedData> onCreateLoader(int id, Bundle args) {
        // Configure a loader and return it.
        Log.d(TAG, "onCreateLoader() called");
        Random rn = new Random();
        int maxFoo = rn.nextInt(12);
        int maxBar = rn.nextInt(12);
        Log.d(TAG, "... setting up a loader with maxFoo=" + maxFoo + ", maxBar=" + maxBar);

        TheLoader loader = new TheLoader( getActivity(), maxFoo, maxBar );

        return loader;
    }


    @Override
    public void onLoadFinished( Loader<MixedData> loader, MixedData data ) {
        // NOTE: Can't commit fragment transactions, etc. because this can happen after Activity
        // state has been saved.

        // Remove all use of old data (which is about to be released by the loader).
        // For cursors, just swap the cursor, don't close it.

        // This gets called when there is new data.

        // Don't tell the cursor to FLAG_AUTO_REQUERY or FLAG_REGISTER_CONTENT_OBSERVER since
        // the loader takes care of updating the data, not the cursor.  (so flags = 0 in cursor
        // adapter constructor).
        Log.d(TAG, "onLoadFinished() called");
        if( loader == null ) {
            Log.d(TAG, "...loader is null");
        }

        mData = data;
        if( data == null ) {
            Log.d(TAG, "...data is null");
        } else {
            for( Foo foo : data.getFooList() ) {
                Log.d(TAG, "... foo: " + foo.getA() );
            }
            for( Bar bar : data.getBarList() ) {
                Log.d(TAG, "... bar: " + bar.getX() );
            }

            Log.d(TAG, "other string is: " + data.getOther());
        }

    }


    @Override
    public void onLoaderReset( Loader<MixedData> loader ) {
        // Remove all references to the data.  Not sure what we do with the cursor here.
        Log.d(TAG, "onLoaderReset() called");

        mData = null;
    }


}
