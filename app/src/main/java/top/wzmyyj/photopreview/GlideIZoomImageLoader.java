package top.wzmyyj.photopreview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import top.wzmyyj.preview.loader.ImageLoadSimpleTarget;
import top.wzmyyj.preview.loader.PreviewImageLoader;


/**
 * Created on 2019/01/29.
 *
 * @author feling
 */

public class GlideIZoomImageLoader implements PreviewImageLoader {
    private RequestOptions options;

    {
        options = new RequestOptions()
                .centerCrop()
                .priority(Priority.HIGH);
    }

    @Override
    public void displayImage(@NonNull ImageView imageView, @NonNull String path,
                             @NonNull final ImageLoadSimpleTarget<Bitmap> simpleTarget) {
        Glide.with(imageView)
                .asBitmap()
                .load(path)
                .apply(options)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        simpleTarget.onResourceReady(resource);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        simpleTarget.onLoadStarted();
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        simpleTarget.onLoadFailed(errorDrawable);
                    }
                });
    }

    @Override
    public void onStop(@NonNull ImageView imageView) {
        Glide.with(imageView).onStop();
    }

    @Override
    public void clearMemory(@NonNull Context context) {
        Glide.get(context).clearMemory();
    }
}
