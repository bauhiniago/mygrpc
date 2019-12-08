package com.example.mygrpc;

//import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.protobuffile.GreeterGrpc;
import com.example.protobuffile.HelloReply;
import com.example.protobuffile.HelloRequest;
import com.example.protobuffile.grpc.SimpleStreamObserver;
import com.example.protobuffile.grpc.gRPCChannelUtils;
import com.xuexiang.xutil.common.StringUtils;
import com.xuexiang.xutil.tip.ToastUtils;

import io.grpc.ManagedChannel;

public class MainActivity extends AppCompatActivity {
    GreeterGrpc.GreeterStub mStub;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_grpc_test);

        TextView callbackdata=(TextView)findViewById(R.id.tv_grpc_response);
        callbackdata.setText("这是显示的内容");

        Button btSelectPhoto= (Button) findViewById(R.id.btn_send);
        btSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = "192.168.1.6";
                int port = 50051;
                String message = "android test";

                if (StringUtils.isEmpty(host)) {
                    ToastUtils.toast("服务地址不能为空");
                    return;
                }

                if (StringUtils.isEmpty(message)) {
                    ToastUtils.toast("内容不能为空");
                    return;
                }

                //开始网络请求
                //构建通道
                final ManagedChannel channel = gRPCChannelUtils.newChannel(host, port);
                //构建服务api代理
                mStub = GreeterGrpc.newStub(channel);
                //构建请求实体
                HelloRequest request = HelloRequest.newBuilder().setName(message).build();
                //进行请求
                mStub.sayHello(request, new SimpleStreamObserver<HelloReply>() {
                    @Override
                    protected void onSuccess(HelloReply value) {
                        System.out.println(value.getMessage());
                        callbackdata.setText(value.getMessage());
                    }

                    @MainThread
                    @Override
                    public void onError(Throwable t) {
                        super.onError(t);
                        System.out.println(Log.getStackTraceString(t));
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        gRPCChannelUtils.shutdown(channel);
                    }
                });
            }
        });

        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);


//        tv.setText(stringFromJNI());
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
}


