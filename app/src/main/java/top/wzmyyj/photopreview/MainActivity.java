package top.wzmyyj.photopreview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import top.wzmyyj.preview.base.PreviewConfig;
import top.wzmyyj.preview.enitity.ThumbImageInfo;
import top.wzmyyj.preview.utils.PreviewManager;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "FEL";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreviewManager.getInstance().init(new GlideIZoomImageLoader());
        setContentView(R.layout.activity_main);


        urlList.add("https://upload-images.jianshu.io/upload_images/3262738-0a5b030907019fd8.jpg");
        urlList.add("https://upload-images.jianshu.io/upload_images/3262738-3136460bca8a06e8.jpg");
        urlList.add("https://upload-images.jianshu.io/upload_images/3262738-1f1bcd714aa0813c.jpg");
        urlList.add("https://upload-images.jianshu.io/upload_images/3262738-470a155a7e646aa9.png");
        urlList.add("https://upload-images.jianshu.io/upload_images/3262738-3136460bca8a06e8.jpg");
        urlList.add("https://upload-images.jianshu.io/upload_images/3262738-9fb08e3a3c7f56fb.jpg");
        urlList.add("https://upload-images.jianshu.io/upload_images/3262738-ca280ccc73362b20.jpg");


        imageView = findViewById(R.id.img_go);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                go();
            }
        });

        Glide.with(this)
                .load(urlList.get(0))
                .into(imageView);

    }

    private List<String> urlList = new ArrayList<>();

    private void go() {


        ArrayList<ThumbImageInfo> mThumbViewInfoList = new ArrayList<>(); // 这个最好定义成成员变量

        for (int i = 0; i < urlList.size(); i++) {
            ThumbImageInfo info;
            if (i == 0) {
                info = new ThumbImageInfo(urlList.get(i), imageView);
            } else {
                info = new ThumbImageInfo(urlList.get(i));
            }
            mThumbViewInfoList.add(info);
        }


        //打开预览界面
        PreviewConfig.Builder.from(this)
                .to(ImageLookActivity.class)
//                .toDialog(new FPreviewDialogFragment())
                .setData(mThumbViewInfoList)
                .setCurrentIndex(0)
                .setSingleFling(true)
                .setType(PreviewConfig.IndicatorType.Number)// 数字
//                .setType(PreviewConfig.IndicatorType.Dot) // 小圆点
                .start();//启动
    }
}
