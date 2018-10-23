package com.jx.sleep_dg.permission;

/**
 * 作者：  王静波
 * 日期：  2018/2/28
 * 注明：
 */

public interface PermissionListener {
    /**
     * 通过授权
     *
     * @param permission
     */
    void permissionGranted(String[] permission);

    /**
     * 拒绝授权
     *
     * @param permission
     */
    void permissionDenied(String[] permission);
}
