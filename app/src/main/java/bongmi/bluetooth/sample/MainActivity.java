package bongmi.bluetooth.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.basic.util.Callback;
import com.bm.android.thermometer.ble.exceptions.NoDeviceExistException;
import com.bm.android.thermometer.ble.exceptions.NoPermissionException;
import com.bm.android.thermometer.ble.exceptions.NotEnableBleException;
import com.bm.android.thermometer.ble.exceptions.NotSupportBleException;
import com.bm.android.thermometer.ble.model.Growp;
import com.bm.android.thermometer.ble.model.Temperature;
import com.bm.android.thermometer.device.storage.DeviceInfo;
import com.bm.android.thermometer.network.basic.Response;
import com.bm.android.thermometer.sdk.LollypopSDK;

import cn.lollypop.be.exception.LollypopException;
import cn.lollypop.be.model.DeviceType;
import cn.lollypop.be.unit.HeightUnit;
import cn.lollypop.be.unit.WeightUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private TextView log;
  private Button disconnect;
  private Button connect;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    log = findViewById(R.id.log);
    disconnect = findViewById(R.id.disconnect);
    disconnect.setOnClickListener(this);
    disconnect.setEnabled(false);

    connect = findViewById(R.id.connect);
    connect.setOnClickListener(this);

    findViewById(R.id.getDeviceInfo).setOnClickListener(this);
    findViewById(R.id.signOut).setOnClickListener(this);
    findViewById(R.id.setC).setOnClickListener(this);
    findViewById(R.id.setF).setOnClickListener(this);
    findViewById(R.id.setKG).setOnClickListener(this);
    findViewById(R.id.setLB).setOnClickListener(this);
    findViewById(R.id.setJIN).setOnClickListener(this);
    findViewById(R.id.setCM).setOnClickListener(this);
    findViewById(R.id.setINCH).setOnClickListener(this);

    LollypopSDK.getInstance().registerCallback(
        new LollypopSDK.LollypopCallback() {
          @Override
          public void createUser(Response response) {}

          @Override
          public void login(Response response) {}

          @Override
          public void connect(boolean suc) {
            if (suc) {
              log.append("connected\n");
              disconnect.setEnabled(true);
            } else {
              try {
                LollypopSDK.getInstance().disconnect();
              } catch (Exception e) {
                log.append(e.getMessage() + "\n");
              }
              connect.setEnabled(true);
              disconnect.setEnabled(false);
            }
          }

          @Override
          public void disconnect() {
            log.append("disconnect\n");
          }

          @Override
          public void receiveTemperature(Temperature temperature) {
            log.append(temperature.toString() + "\n");
          }

          @Override
          public void receiveGrowp(Growp growp) {
            log.append(growp.toString() + "\n");
          }
        });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    try {
      LollypopSDK.getInstance().disconnect();
    } catch (LollypopException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.disconnect:
        try {
          LollypopSDK.getInstance().disconnect();
        } catch (Exception e) {
          log.append(e.getMessage() + "\n");
        }
        connect.setEnabled(true);
        disconnect.setEnabled(false);
        break;

      case R.id.connect:
        doConnect();
        break;

      case R.id.getDeviceInfo:
        try {
          DeviceInfo deviceInfo = LollypopSDK.getInstance().getDeviceInfo(this);
          log.append(deviceInfo.toString() + "\n");
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        break;

      case R.id.signOut:
        LollypopSDK.getInstance().signOut(this);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        break;
      case R.id.setC:
        try {
          LollypopSDK.getInstance().setTemperatureUnit(true);
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        break;
      case R.id.setF:
        try {
          LollypopSDK.getInstance().setTemperatureUnit(false);
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        break;
      case R.id.setKG:
        try {
          LollypopSDK.getInstance().setGrowpWeightUnit(WeightUnit.KILOGRAM);
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        break;
      case R.id.setLB:
        try {
          LollypopSDK.getInstance().setGrowpWeightUnit(WeightUnit.POUND);
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        break;
      case R.id.setJIN:
        try {
          LollypopSDK.getInstance().setGrowpWeightUnit(WeightUnit.JIN);
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        break;
      case R.id.setCM:
        try {
          LollypopSDK.getInstance().setGrowpHeightUnit(HeightUnit.CM);
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        break;
      case R.id.setINCH:
        try {
          LollypopSDK.getInstance().setGrowpHeightUnit(HeightUnit.INCH);
        } catch (LollypopException e) {
          log.append(e.getMessage() + "\n");
        }
        break;
      default:
        break;
    }
  }

  private void doConnect() {
    try {
      // 连接棒米体温计
      // LollypopSDK.getInstance().connect();
      // 连接棒米耳温枪
      // LollypopSDK.getInstance().connect(DeviceType.SMARTTHERMO);
      // 连接棒米生长发育仪
      LollypopSDK.getInstance().connect(DeviceType.GROWP);
    } catch (LollypopException e) {
      log.append(e.getMessage() + "\n");
    } catch (NoPermissionException e) {
      log.append("请开启GPS并授权\n");
      LollypopSDK.getInstance().requestLocationPermissions(this,
          new Callback() {
            @Override
            public void doCallback(Boolean aBoolean, Object o) {
              if (aBoolean) {
                log.append("授权成功\n");
                doConnect();
              } else {
                log.append("授权失败\n");
              }
            }
          });
    } catch (NoDeviceExistException e) {
      log.append("设备类型错误\n");
    } catch (NotEnableBleException e) {
      log.append("蓝牙未打开\n");
    } catch (NotSupportBleException e) {
      log.append("该手机不支持低功耗蓝牙\n");
    }
    connect.setEnabled(false);
  }
}
