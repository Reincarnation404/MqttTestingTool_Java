package com.example.mqtttesttool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mqtttesttool.databinding.ActivityMainBinding;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private mqttHelper mqttHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        String protocol = binding.txtProtocol.getText().toString().trim();
//        String ip = binding.txtIP.getText().toString().trim();
//        String port = binding.txtPort.getText().toString().trim();
//        String topic = binding.txtTopic.getText().toString().trim();
//        String qos = binding.txtQos.getText().toString().trim();
//        String msg = binding.txtMsg.getText().toString().trim();

        binding.btnConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String protocol = binding.txtProtocol.getText().toString().trim();
                String ip = binding.txtIP.getText().toString().trim();
                String port = binding.txtPort.getText().toString().trim();

                if (!protocol.isEmpty() && !ip.isEmpty() && !port.isEmpty()){
                    String uri = protocol+"://"+ip+":"+port;
                    System.out.println(uri);
                    String clientId = MqttClient.generateClientId();
                    startMqtt(uri,clientId);

                    Toast.makeText(MainActivity.this, "連線成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "uri有空格", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = binding.txtTopic.getText().toString().trim();
                String qos = binding.txtQos.getText().toString().trim();
                if (topic.isEmpty()){
                    binding.txtTopic.setError("topic不可為空");
                }else {
                    mqttHelper.subscribeToTopic(topic, qos);
                    Toast.makeText(MainActivity.this, topic+"訂閱成功", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnUnsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = binding.txtTopic.getText().toString().trim();
                if (topic.isEmpty()){
                    binding.txtTopic.setError("topic不可為空");
                }else {
                    mqttHelper.unsubscribeToTopic(topic);
                    Toast.makeText(MainActivity.this, topic+"取消訂閱成功", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnPub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = binding.txtTopic.getText().toString().trim();
                String qos = binding.txtQos.getText().toString().trim();
                String msg = binding.txtMsg.getText().toString().trim();

                boolean isRetained = binding.togRetain.isChecked();

                if (topic.isEmpty()){
                    binding.txtTopic.setError("topic不可為空");
                }else if(qos.isEmpty()){
                    binding.txtQos.setError("qos不可為空");
                }else if (msg.isEmpty()){
                    binding.txtMsg.setError("msg不可為空");
                }else {
                    if (mqttHelper.isConnected()){
                        mqttHelper.publishToTopic(topic, msg, qos,isRetained);
                    }else{
                        Toast.makeText(MainActivity.this, "沒有連線", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    //要用這個才會改textView
    private void startMqtt(String uri,String id){
        mqttHelper = new mqttHelper(getApplicationContext(),uri,id);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String payload = new String(message.getPayload());
                binding.txtResult.setText(payload);
                System.out.println("startMqtt的messageArrived "+payload);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }


    private boolean inputValid(){
        boolean valid = true;
        String protocol = binding.txtProtocol.getText().toString().trim();
        String ip = binding.txtIP.getText().toString().trim();
        String port = binding.txtPort.getText().toString().trim();
        String topic = binding.txtTopic.getText().toString().trim();
        String qos = binding.txtQos.getText().toString().trim();
        String msg = binding.txtMsg.getText().toString().trim();

        if (protocol.isEmpty()){
            binding.txtProtocol.setError("不可為空");
            valid = false;
        }
        if (ip.isEmpty()){
            binding.txtIP.setError("不可為空");
            valid = false;
        }
        if (port.isEmpty()){
            binding.txtPort.setError("不可為空");
            valid = false;
        }
        if (topic.isEmpty()){
            binding.txtTopic.setError("不可為空");
            valid = false;
        }
        if (qos.isEmpty()){
            binding.txtQos.setError("不可為空");
            valid = false;
        }
        if (msg.isEmpty()){
            binding.txtMsg.setError("不可為空");
            valid = false;
        }

        return valid;
    }


}

