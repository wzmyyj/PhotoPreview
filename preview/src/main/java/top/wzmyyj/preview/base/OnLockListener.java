package top.wzmyyj.preview.base;

/**
 * Created on 2019/07/03
 *
 * @author feling
 * @version 1.0
 * @since 1.0
 */
public interface OnLockListener {
    /**
     * @param isLock 是否锁定。
     */
    void lock(boolean isLock);
}
