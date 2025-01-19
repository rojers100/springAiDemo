package org.example.springaidemo.moudle;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserRestModel {
    private String userid;
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime endTime;    // 结束时间
    private String restType;          // 休假类型
    private String status;            // 状态：PENDING(待确认), CONFIRMED(已确认), REJECTED(已拒绝)
    private String rejectReason;      // 拒绝原因
}
