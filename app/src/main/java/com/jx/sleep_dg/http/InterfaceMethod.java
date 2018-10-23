package com.jx.sleep_dg.http;


public class InterfaceMethod {
    /**
     * 注册
     */
    public static final String ADDUSER = "user/addUser.do";

    /**
     * 忘记密码
     */
    public static final String FORGETPASSWORD = "index/forgetPaw.do";

    /**
     * 登录
     */
    public static final String LOGIN = "index/login.do";

    /**
     * 获取个人信息
     */
    public static final String QUERYINFORMATION = "user/queryInformation.do";

    /**
     * 绑定设备
     */
    public static final String BINDDEVICE = "userBluetoothRelation/addRelation.do";

    /**
     * 删除设备
     */
    public static final String DELETEDEVICE = "bluetooth/delBluetooth.do";
    /**
     * 修改设备信息
     */
    public static final String CHANGEDEVICE = "bluetooth/alterBluetooth.do";

    /**
     * 获取该用户下所有的设备
     */
    public static final String GETALLDEVICE = "bluetooth/findAllBluetooth.do";

    /**
     * 当配对关闭时的接口
     */
    public static final String CLOSEDEVICE = "userBluetoothRelation/closeBluetooth.do";

    /**
     * 日/周/月 报表上部分接口
     */
    public static final String TOPCOUT = "userJinZhuiLog/count.do";
    /**
     * 日/周/月 报表下部分接口
     */
    public static final String BOTTOMCOUT = "userJinZhuiLog/graph.do";

    /**
     * 新增脊椎记录
     */
    public static final String ADDLOG = "userJinZhuiLog/addLog.do";

    /**
     * 验证码
     */
    public static final String YANZHENGMA = "user/yzm.do";

    /**
     * 修改个人头像
     */
    public static final String UPDATEHEAD = "user/updatehead_portrait.do";


    /**
     * 修改用户昵称
     */
    public static final String UPDATENICKNAME = "user/updateNickname.do";

    /**
     * 获取朋友圈
     */
    public static final String SELECTHEALTHY = "healthy/queryPageHealthy.do";

    /**
     * 发布健康圈
     */
    public static final String ADDHEALTHY = "healthy/addHealthy.do";

    /**
     * 查看指定文章
     */
    public static final String CHECKHEALTHY = "healthy/selectHealthyById.do";


}
