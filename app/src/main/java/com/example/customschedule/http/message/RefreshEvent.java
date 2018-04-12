package com.example.customschedule.http.message;

import lombok.Data;

/**
 * @author wangyu
 * @date 18-3-30
 * @describe TODO
 */
@Data
public class RefreshEvent {
    boolean refresh;

    public RefreshEvent() {
        this.refresh = false;
    }

    public RefreshEvent(boolean refresh) {
        this.refresh = refresh;
    }
}
