package com.jx.sleep_dg.Bean;

import java.util.List;

/**
 * Created by dingt on 2018/7/30.
 */

public class EveXinlvBean {
    String type;
    List<HeartRateBean> heartRateBeans;

    public EveXinlvBean(String type, List<HeartRateBean> heartRateBeans) {
        this.type = type;
        this.heartRateBeans = heartRateBeans;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<HeartRateBean> getHeartRateBeans() {
        return heartRateBeans;
    }

    public void setHeartRateBeans(List<HeartRateBean> heartRateBeans) {
        this.heartRateBeans = heartRateBeans;
    }
}
