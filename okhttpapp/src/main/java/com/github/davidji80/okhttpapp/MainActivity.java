package com.github.davidji80.okhttpapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class MainActivity extends AppCompatActivity {
    private TextView tvShow;
    private ImageView iv_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvShow = findViewById(R.id.tv_show);
        iv_img=findViewById(R.id.iv_img);
    }

    /**
     * 异步GET请求
     *
     * @param view
     */
    public void asynGet(View view) {
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //构造Request对象,采用建造者模式，链式调用指明进行Get请求,传入Get的请求地址
        //Request request = new Request.Builder().get().url("http://www.baidu.com/").build();
        //构造Request对象,设置一个url地址,设置请求方式。
        Request request = new Request.Builder().url("http://www.baidu.com").method("GET", null).build();
        //创建一个call对象,参数就是Request请求对象
        Call call = client.newCall(request);
        //异步调用并设置回调函数
        call.enqueue(new Callback() {
            //请求失败执行的方法
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Get 失败", Toast.LENGTH_SHORT);
            }

            /**
             * 请求成功执行的方法
             * @param call
             * @param response
             * @throws IOException
             */
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                /**
                 * response.body().string()返回的是字符串
                 * response.body().bytes()返回的二进制字节数组
                 * response.body().byteStream()返回inputStream，有inputStream我们就可以通过IO的方式写文件
                 */
                final String responseStr = response.body().string();
                //异步调用的回调函数是在子线程,我们不能在子线程更新UI,需要借助于 runOnUiThread() 方法或者 Handler 来处理。
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShow.setText(responseStr);
                    }
                });
            }
        });
    }

    /**
     * 同步GET请求
     *
     * @param view
     */
    public void synGet(View view) {
        //创建OkHttpClient对象
        OkHttpClient client = new OkHttpClient();
        //构造Request对象,设置一个url地址,设置请求方式。
        Request request = new Request.Builder().url("http://www.baidu.com").method("GET", null).build();
        //创建一个call对象,参数就是Request请求对象
        final Call call = client.newCall(request);
        //同步调用会阻塞主线程,这边在子线程进行
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //同步调用,返回Response,会抛出IO异常
                    Response response = call.execute();
                    final String responseStr = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvShow.setText(responseStr);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 下载图片
     *
     * @param view
     */
    public void downloadImage(View view) {
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request
                .Builder()
                .get()
                .url("https://www.baidu.com/img/bd_logo1.png")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //拿到字节流
                InputStream inputStream = response.body().byteStream();
                //将图片显示到ImageView中,用Bitmap decodeStream(InputStream is)方法将输入流转为Bitmap然后显示出来
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_img.setImageBitmap(bitmap);
                    }
                });
                //将图片保存到本地存储卡中
                File file = new File(Environment.getExternalStorageDirectory(), "image.png");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] temp = new byte[128];
                int length;
                while ((length = inputStream.read(temp)) != -1) {
                    fileOutputStream.write(temp, 0, length);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
            }
        });
    }

    /**
     * POST 键值对
     *
     * @param view
     */
    public void postKeyValue(View view) {
        OkHttpClient client = new OkHttpClient();
        //构建FormBody，通过new FormBody()调用build方法,创建一个RequestBody,可以用add添加键值对
        FormBody formBody = new FormBody
                .Builder()
                .add("username", "initObject")
                .add("password", "initObject")
                .build();
        //创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        final Request request = new Request.Builder()
                .url("http://www.baidu.com/")
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "POST 失败", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                int responseCode = response.code();
                Toast.makeText(MainActivity.this, "Response Code：" + responseCode, Toast.LENGTH_LONG);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShow.setText(responseStr);
                    }
                });
            }
        });
    }

    /**
     * Post 字符串
     *
     * @param view
     */
    public void postString(View view) {
        OkHttpClient client = new OkHttpClient();
        //RequestBody中的MediaType指定为纯文本，编码方式是utf-8
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"),
                "{username:admin;password:admin}");
        final Request request = new Request.Builder()
                .url("http://www.baidu.com/")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "POST 失败", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                int responseCode = response.code();
                Toast.makeText(MainActivity.this, "Response Code：" + responseCode, Toast.LENGTH_LONG);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShow.setText(responseStr);
                    }
                });
            }
        });
    }

    /**
     * Post 表单
     *
     * @param view
     */
    public void postForm(View view) {
        OkHttpClient client = new OkHttpClient();
        //这里使用到 MuiltipartBody 来构建一个RequestBody，这是 RequestBody 的一个子类，提交表单数据就是利用这个类来实现的
        RequestBody requestBody = new MultipartBody.Builder()
                //设置类型是表单
                .setType(MultipartBody.FORM)
                /*
                添加数据
                一个参数就是类似于键值对的键,是供服务端使用的
                第二个参数是文件的本地的名字
                第三个参数是 RequestBody，里面包含了我们要上传的文件的路径以及 MidiaType
                .addFormDataPart("image","img.png",RequestBody.create(MediaType.parse("image/png"),file))
                 */
                .addFormDataPart("username", "user1")
                .addFormDataPart("password", "pasw1")
                .build();
        final Request request = new Request.Builder()
                .url("http://www.baidu.com/")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "POST 失败", Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShow.setText(responseStr);
                    }
                });
            }
        });
    }

    /**
     * Post 流
     *
     * @param view
     */
    public void postStreaming(View view) {
        final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_MARKDOWN;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.writeUtf8("Numbers\n");
                sink.writeUtf8("-------\n");
                for (int i = 2; i <= 997; i++) {
                    sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
                }
            }

            private String factor(int n) {
                for (int i = 2; i < n; i++) {
                    int x = n / i;
                    if (x * i == n) return factor(x) + " × " + i;
                }
                return Integer.toString(n);
            }
        };
        Request request = new Request.Builder()
                .url("https://www.baidu.com/")
                .post(requestBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShow.setText(responseStr);
                    }
                });
            }
        });
    }


    /**
     * Post 文件
     *
     * @param view
     */
    public void postFile(View view) {
        OkHttpClient client = new OkHttpClient();
        final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        File file = new File("README.md");
        Request request = new Request.Builder()
                .url("https://www.baidu.com")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvShow.setText(responseStr);
                    }
                });
            }
        });
    }


}
