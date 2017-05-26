package com.vendomatica.vendroid.viewholder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vendomatica.vendroid.Common.Common;
import com.vendomatica.vendroid.Model.TaskInfo;
import com.vendomatica.vendroid.R;
import com.vendomatica.vendroid.connectivity.AuditManagerBase;
import com.vendomatica.vendroid.connectivity.AuditManagerDDCMP;
import com.vendomatica.vendroid.connectivity.AuditManagerDex;
import com.vendomatica.vendroid.connectivity.AuditManagerJofemar;
import com.vendomatica.vendroid.connectivity.AuditManagerJofemarRD;
import com.vendomatica.vendroid.connectivity.AuditManagerSpengler;
import com.vendomatica.vendroid.connectivity.IAuditManager;
import com.vendomatica.vendroid.db.DBManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

//import com.vendomatica.vendroid.Fragment.AbaTaskActivity;

public class CaptureViewHolder implements IAuditManager {
    public static final String BT_DEVICE = "BTDevice";

    public static final String SPENGLER = "Spengler";
    public static final String DEX = "DEX";
    public static final String JOFEMAR = "Jofemar";
    public static final String DDCMP = "DDCMP";
    public static final String JOFEMAR_RD = "Jofemar-RD";

    public static final String SPENGLER_CAPS = "SPENGLER";
    public static final String DEX_CAPS = "DEX";
    public static final String JOFEMAR_CAPS = "JOFEMAR";
    public static final String DDCMP_CAPS = "DDCMP";
    public static final String JOFFEMAR_RD_CAPS = "JOFEMAR-RD";

    public static final int BT_REQUEST_CODE = 11;
    public static final int MAX = 10000;

    private final Activity mContext;
    private final DBManager mDBManager;
    private final TaskInfo mTaskInfo;

    private final ListView mDeviceList;
    private final ListView mPairingList;
    private final ListView mTypeList;
    private final View mDeviceListLayout;
    private final View mPairingListLayout;
    private final View mTypeListLayout;
    private final TextView mDeviceTitle;
    private final TextView mPairingTitle;
    private final TextView mTypeTitle;
    private final ProgressBar mPairingLoading;
    private final TextView mPairingLoadingPercent;

    private AuditManagerBase mAuditManager;
    private BluetoothDevice mDevice;
    private String mType;
    private ArrayAdapter<BluetoothDeviceWrapper> mDeviceAdapter;

    private final Drawable mProgressDrawable;
    private BluetoothAdapter mBluetoothAdapter;

    ProgressDialog progressDialog = null;

    public CaptureViewHolder(Activity activity, View view, final TaskInfo taskInfo, DBManager dbManager) {
        mContext = activity;
        mDBManager = dbManager;
        mTaskInfo = taskInfo;

        mDeviceTitle = (TextView) view.findViewById(R.id.device);
        mPairingTitle = (TextView) view.findViewById(R.id.pairing);
        mTypeTitle = (TextView) view.findViewById(R.id.type);
        mDeviceList = (ListView) view.findViewById(R.id.device_list);
        mDeviceList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        mDeviceListLayout = view.findViewById(R.id.device_list_layout);
        mPairingListLayout = view.findViewById(R.id.pairing_list_layout);
        mPairingList = (ListView) view.findViewById(R.id.pairing_list);
        mTypeList = (ListView) view.findViewById(R.id.type_list);
        mTypeList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //((AbaTaskActivity)mContext).getScrollContent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mTypeListLayout = view.findViewById(R.id.type_list_layout);
        mPairingLoading = (ProgressBar) view.findViewById(R.id.pairing_loading);
        mPairingLoadingPercent = (TextView) view.findViewById(R.id.pairing_loading_percent);
        mPairingLoading.setMax(MAX);

        mType = taskInfo.getAux_valor1();

        mProgressDrawable = ContextCompat.getDrawable(mContext, android.R.drawable.ic_popup_sync);

        mDeviceTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                selectDevice();
            }
        });
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void start() {
        selectDevice();
    }

    public void start(final String type){
        mType = type;
        start();
    }

    private void selectDevice() {
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "You don't have bluetooth :(", Toast.LENGTH_LONG).show();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent intentBtEnabled = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mContext.startActivityForResult(intentBtEnabled, BT_REQUEST_CODE);
        } else {
            final List<BluetoothDevice> pairedDevices = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            restoreFromPrefs(pairedDevices);

            if (mDevice == null) {
                startDiscovery();
                mDeviceListLayout.setVisibility(View.VISIBLE);
                mDeviceAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, from(pairedDevices));
                mDeviceList.setAdapter(mDeviceAdapter);
                mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int position, long id) {
                        mBluetoothAdapter.cancelDiscovery();
                        mDevice = ((BluetoothDeviceWrapper) parent.getAdapter().getItem(position)).getDevice();
                        saveToPrefs();
                        mDeviceListLayout.setVisibility(View.GONE);
                        setDone(mDeviceTitle, true);
                        selectType();
                    }
                });
            } else {
                setDone(mDeviceTitle, true);
                selectType();
            }
        }
    }

    private void selectType() {
        if (mType.isEmpty()) {
            mTypeListLayout.setVisibility(View.VISIBLE);
            String[] types = {SPENGLER, DEX, DDCMP, JOFEMAR, JOFEMAR_RD};
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1, types);
            mTypeList.setAdapter(typeAdapter);
            mTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mType = (String) parent.getAdapter().getItem(position);
                    mTypeListLayout.setVisibility(View.GONE);
                    setDone(mTypeTitle, true);
                    startAudit();
                }
            });
        } else {
            setDone(mTypeTitle, true);
            startAudit();
        }
    }

    private void createAuditManager() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.show();
        progressDialog.setContentView(R.layout.loading);
        progressDialog.setCancelable(false);
        if (mAuditManager != null){
            mAuditManager.stop();
        }
        switch (mType.toUpperCase()) {
            case SPENGLER_CAPS:
                mAuditManager = new AuditManagerSpengler(CaptureViewHolder.this);
                break;
            case DEX_CAPS:
                mAuditManager = new AuditManagerDex(CaptureViewHolder.this);
                break;
            case DDCMP_CAPS:
                mAuditManager = new AuditManagerDDCMP(CaptureViewHolder.this);
                break;
            case JOFEMAR_CAPS:
                mAuditManager = new AuditManagerJofemar(CaptureViewHolder.this);
                break;
            case JOFFEMAR_RD_CAPS:
                mAuditManager = new AuditManagerJofemarRD(CaptureViewHolder.this);
            default:
                mType = "";
                selectType();
                break;
        }
    }

    private void startAudit() {
        createAuditManager();
        if (mAuditManager != null) {
            mAuditManager.setBTDevice(mDevice);
            mAuditManager.go(getCurrentBtType());
        }
    }

    private void restoreFromPrefs(List<BluetoothDevice> pairedDevices) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mmpairedDevices = mBluetoothAdapter.getBondedDevices();

        List<String> s = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices){
            String sss=bt.getName();
            String mdireccion = bt.getAddress();
            int mstate = bt.getBondState();
            int mtipo = bt.hashCode();
            String mstring = bt.toString();

            mBluetoothAdapter.setName(sss);
            s.add(bt.getName());
        }


        SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(mContext);
        String device = editor.getString(BT_DEVICE, "");

        if (!device.isEmpty()) {
            for (BluetoothDevice d : pairedDevices) {
                if (d.getAddress().equals(device)) {
                    mDevice = d;
                }
            }
        }
    }

    private void setDone(TextView view, boolean done) {
        if (view.getCompoundDrawables()[2] == mProgressDrawable) {
            if (mProgressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) mProgressDrawable).stop();
            }
        }
        view.setBackgroundColor(ContextCompat.getColor(mContext, done ? R.color.clr_green : R.color.clr_lightgraqy));
        view.setCompoundDrawablesWithIntrinsicBounds(0, 0, done ? R.drawable.check : 0, 0);
    }

    private void setProgress(TextView view, boolean progress) {
        if (progress) {
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, mProgressDrawable, null);
            if (mProgressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable)mProgressDrawable).start();
            }
        } else if (view.getCompoundDrawables()[2] == mProgressDrawable) {
            if (mProgressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) mProgressDrawable).stop();
            }
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
    }

    private void saveToPrefs() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.putString(BT_DEVICE, mDevice.getAddress());
        editor.apply();
    }

    private void reset() {
        setDone(mDeviceTitle, false);
        setDone(mTypeTitle, false);
        setDone(mPairingTitle, false);
        mTypeListLayout.setVisibility(View.GONE);
        mPairingLoading.setVisibility(View.GONE);
        mPairingLoadingPercent.setVisibility(View.GONE);
        mPairingTitle.setText(R.string.pairing);
        mDevice = null;
        mType = "";
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (mAuditManager != null) {
            mAuditManager.stop();
        }

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
        editor.remove(BT_DEVICE);
        editor.apply();
    }

    private String getCurrentBtType() {
        return "SENA";
    }

    @Override
    public void onAuditStart() {
        Log.d("AAA", "onAuditStart() called with: " + "");
        setProgress(0);
        setProgress(0);
        mPairingLoading.setVisibility(View.VISIBLE);
        mPairingLoadingPercent.setVisibility(View.VISIBLE);
    }

    @Override
    public void onError(String msg) {
        Log.d("AAA", "onError() called with: " + "msg = [" + msg + "]");
        progressDialog.dismiss();
        mPairingLoading.setProgress(0);
        Toast toast = Toast.makeText(mContext, R.string.capture_error, Toast.LENGTH_LONG);
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(24);
        toast.show();
        reset();
        selectDevice();
    }

    @Override
    public void onAuditDataTransferedSize(Integer data) {
        setProgress(data);
        mPairingTitle.setText(R.string.collecting);
    }

    @Override
    public void onSuccess(List<String> filesList) {
        setProgress(MAX);
        progressDialog.dismiss();
        setDone(mPairingTitle, true);
        Toast toast = Toast.makeText(mContext, R.string.capture_success, Toast.LENGTH_LONG);
        Common.getInstance().capture = true;
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(24);
        toast.show();
        for (int i = 0; i < filesList.size(); i = i + 3) {
            //mDBManager.insertLogFile(new LogFile(mTaskInfo.getTaskID(), filesList.get(i), filesList.get(i + 1), filesList.get(i + 2)));
            Log.d("AAA", "onSuccess() called with: " + "fileName = [" + filesList.get(i) + "]");
        }
        mPairingLoading.postDelayed(new Runnable() {
            @Override
            public void run() {
                mContext.onBackPressed();
            }
        }, 1000);
    }

    @Override
    public void onAuditLog(String msg) {
        Log.d("AAA", "onAuditLog() called with: " + "msg = [" + msg + "]");
    }

    private void startDiscovery() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

        mContext.registerReceiver(mReceiver, filter);
        mBluetoothAdapter.startDiscovery();
    }

    private List<BluetoothDeviceWrapper> from(List<BluetoothDevice> list) {
        List<BluetoothDeviceWrapper> res = new LinkedList<>();
        for (BluetoothDevice d : list) {
            res.add(new BluetoothDeviceWrapper(d));
        }
        return res;
    }

    private void setProgress(int progress) {
        mPairingLoading.setProgress(progress);
        mPairingLoadingPercent.setText(Math.round((progress * 100)/MAX) + "%");
    }

    private static class BluetoothDeviceWrapper {
        final private BluetoothDevice device;

        BluetoothDeviceWrapper(BluetoothDevice d) {
            device = d;
        }

        public BluetoothDevice getDevice() {
            return device;
        }

        @Override
        public String toString() {
            return device.getName();
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_OFF) {
                        Log.d("AAA", "Bluetooth turned off");
                    }
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d("AAA", "BT discovery started");
                    setProgress(mDeviceTitle, true);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d("AAA", "BT discovery finished");
                    setProgress(mDeviceTitle, false);
                    mContext.unregisterReceiver(this);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Log.d("AAA", "BT device found");
                    BluetoothDeviceWrapper bdevice = new BluetoothDeviceWrapper((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                    mDeviceAdapter.add(new BluetoothDeviceWrapper((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)));
                    break;
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                    int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                    switch (mode) {
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            Log.d("AAA", "SCAN_MODE_CONNECTABLE_DISCOVERABLE");
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                            Log.d("AAA", "SCAN_MODE_CONNECTABLE");
                            break;
                        case BluetoothAdapter.SCAN_MODE_NONE:
                            Log.d("AAA", "SCAN_MODE_NONE");
                            break;
                    }
            }
        }
    };
}
