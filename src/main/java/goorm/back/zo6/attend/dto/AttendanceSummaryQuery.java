package goorm.back.zo6.attend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceSummaryQuery {
    private String title;
    private Integer capacity;
    private List<UserAttendanceResponse> userAttendances;
}
