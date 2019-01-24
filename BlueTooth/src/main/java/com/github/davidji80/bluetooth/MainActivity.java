package com.github.davidji80.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //常数
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int SCAN_PERIOD = 10000;
    //控件
    private ListView lvBluetooth;
    private Switch swBluetooth;
    //线程
    private Handler delayedHandler = new Handler();
    //蓝牙
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    List<BluetoothDevice> deviceList = new ArrayList<>();
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        /**
         * 连接状态改变
         *
         * @param gatt 蓝牙设备的 Gatt 服务连接类
         * @param status 代表是否成功执行了连接操作，
         *               如果为 BluetoothGatt.GATT_SUCCESS 表示成功执行连接操作，
         *               第三个参数才有效，否则说明这次连接尝试不成功
         * @param newState 代表当前设备的连接状态，
         *                 如果 newState == BluetoothProfile.STATE_CONNECTED 说明设备已经连接，
         *                 可以进行下一步的操作了（发现蓝牙服务，也就是 Service）。
         *                 当蓝牙设备断开连接时，这一个方法也会被回调，
         *                 其中的 newState == BluetoothProfile.STATE_DISCONNECTED

         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.e(TAG, "连接状态:" + newState);
            if (BluetoothGatt.STATE_CONNECTED == newState) {
                Log.e(TAG, "连接成功:");
                //必须有，可以让onServicesDiscovered显示所有Services
                gatt.discoverServices();
                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_LONG).show();
            } else if (BluetoothGatt.STATE_DISCONNECTED == newState) {
                Log.e(TAG, "断开连接:");
                Toast.makeText(MainActivity.this, "断开连接", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * 发现服务，在蓝牙连接的时候会调用
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //遍历服务
                List<BluetoothGattService> list = mBluetoothGatt.getServices();
                for (BluetoothGattService bluetoothGattService : list) {
                    String str = bluetoothGattService.getUuid().toString();
                    Log.e(TAG, " BluetoothGattService：" + str);
                    //遍历该服务下的Characteristic
                    List<BluetoothGattCharacteristic> gattCharacteristics = bluetoothGattService
                            .getCharacteristics();
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        Log.e(TAG, " BluetoothGattCharacteristic：" + gattCharacteristic.getUuid());
                        //通知系统去读取特定的数据。
                        //UUID对应的characteristics，参考官网：https://www.bluetooth.com/specifications/gatt/characteristics
                        if ("00002a4a-0000-1000-8000-00805f9b34fb".equals(gattCharacteristic.getUuid().toString())) {
                            Log.e(TAG, gattCharacteristic.getUuid().toString());
                            gatt.readCharacteristic(gattCharacteristic);
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(final BluetoothGatt gatt,
                                         final BluetoothGattCharacteristic characteristic,
                                         final int status) {

            Log.e(TAG, " characteristic: " + characteristic.getUuid() + " status: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] bytes = characteristic.getValue();
                String encoded = null;
                try {
                    encoded = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ;
                Log.e(TAG, "read value: " + encoded);
            }

        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothPermissions();
        init();
        openBluetooth();

    }

    private void init() {
        lvBluetooth = findViewById(R.id.lv_bluetooth);
        lvBluetooth.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                connectBluetoothDevice(i);
            }
        });
        swBluetooth = findViewById(R.id.sw_bluetooth);
        swBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                scanLeDevice(b);
            }
        });

        // 蓝牙管理，这是系统服务可以通过getSystemService(BLUETOOTH_SERVICE)的方法获取实例
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        // 初始化 Bluetooth adapter.
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    /**
     * 扫描蓝牙设备
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable) {
        final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            /**
             * 一旦发现蓝牙设备，LeScanCallback 就会被回调，直到 stopLeScan 被调用。
             * 出现在回调中的设备会重复出现，所以如果我们需要通过 BluetoothDevice 获取外围设备的地址手动过滤掉已经发现的外围设备。
             *
             * @param device 蓝牙设备的类，可以通过这个类建立蓝牙连接获取关于这一个设备的一系列详细的参数，例如名字，MAC 地址等等；
             * @param rssi 蓝牙的信号强弱指标，通过蓝牙的信号指标，我们可以大概计算出蓝牙设备离手机的距离。计算公式为：d = 10^((abs(RSSI) - A) / (10 * n))
             * @param scanRecord 蓝牙广播出来的广告数据。
             */
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                //重复过滤方法，列表中包含不该设备才加入列表中，并刷新列表
                if (!deviceList.contains(device)) {
                    //将设备加入列表数据中
                    deviceList.add(device);
                }
            }
        };
        deviceList.clear();
        if (enable) {
            // 预先定义停止蓝牙扫描的时间（因为蓝牙扫描需要消耗较多的电量）
            delayedHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    showDevices();
                }
            }, SCAN_PERIOD);

            // 定义一个回调接口供扫描结束处理
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            showDevices();
        }
    }

    /**
     * 连接蓝牙
     *
     * @param index
     */
    private void connectBluetoothDevice(int index) {
        BluetoothDevice bluetoothDevice = deviceList.get(index);
        Toast.makeText(MainActivity.this, bluetoothDevice.getAddress(), Toast.LENGTH_LONG).show();
        /**
         * autoConnect 是否需要自动连接。true, 表示如果设备断开了，会不断的尝试自动连接。false, 表示只进行一次连接尝试。
         * mGattCallback 连接后进行的一系列操作的回调类。
         */
        mBluetoothGatt = bluetoothDevice.connectGatt(this, true, mGattCallback);
    }

    /**
     * 检测到蓝牙是否开启
     * 若没开启，弹出对话框让用户开启蓝牙
     */
    private void openBluetooth() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    /**
     * 在ListView中显示
     */
    private void showDevices() {
        List<String> datas = new ArrayList();
        for (int i = 0; i < deviceList.size(); i++) {
            String desc = "";
            desc += (deviceList.get(i).getAddress() == null) ? "" : deviceList.get(i).getAddress();
            desc += (deviceList.get(i).getName() == null) ? "" : "[" + deviceList.get(i).getName() + "]";
            desc += "-" + deviceList.get(i).getBondState();
            datas.add(desc);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, datas);
        lvBluetooth.setAdapter(adapter);
    }


    /**
     * 获取基于地理位置的动态权限
     */
    private void bluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }
}
