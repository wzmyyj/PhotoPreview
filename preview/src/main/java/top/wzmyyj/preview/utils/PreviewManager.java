package top.wzmyyj.preview.utils;


import top.wzmyyj.preview.loader.PreviewImageLoader;

/**
 * Created on 2019/06/25.
 * <p>
 * 图片加载管理类，单例。
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public class PreviewManager {


    /**
     * private constructor.
     */
    private PreviewManager() {

    }

    private static class Holder {
        private static PreviewManager holder = new PreviewManager();
    }

    private volatile PreviewImageLoader loader;

    /**
     * @return PreviewManager Instance.
     */
    public static PreviewManager getInstance() {
        return Holder.holder;
    }


    /**
     * 初始化图片加载器。
     *
     * @param loader 图片加载器。
     */
    public void init(PreviewImageLoader loader) {
        this.loader = loader;
    }

    /**
     * 获取图片加载器。
     *
     * @return ImageLoader.
     */
    public PreviewImageLoader getImageLoader() {
        if (loader == null) {
            throw new NullPointerException("image loader no init!");
        }
        return loader;
    }
}
