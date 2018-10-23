package com.jx.sleep_dg.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jx.sleep_dg.R;
import com.jx.sleep_dg.utils.Constance;
import com.jx.sleep_dg.utils.PreferenceUtils;

import java.util.ArrayList;

public class LeDeviceListAdapter extends BaseAdapter {

    // Adapter for holding devices found through scanning.

    private ArrayList<BluetoothDevice> mLeDevices;
    private LayoutInflater mInflator;
    private Activity mContext;

    public ArrayList<BluetoothDevice> getmLeDevices() {
        return mLeDevices;
    }

    public void setmLeDevices(ArrayList<BluetoothDevice> mLeDevices) {
        this.mLeDevices = mLeDevices;
    }

    public LeDeviceListAdapter(Activity c) {
        super();
        mContext = c;
        mLeDevices = new ArrayList<BluetoothDevice>();
        mInflator = mContext.getLayoutInflater();
    }

    public void addDevice(BluetoothDevice device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceConned = (ImageView) view.findViewById(R.id.device_conned);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();

        viewHolder.deviceConned.setVisibility(View.INVISIBLE);
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText("UNKNOWN");

        return view;
    }

    class ViewHolder {
        TextView deviceName;
        ImageView deviceConned;
    }
}
