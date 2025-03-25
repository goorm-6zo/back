package goorm.back.zo6.common.log;

import org.aspectj.lang.annotation.Pointcut;

public class TimeLogPointCut {

    @Pointcut("( attendPackagePointCut() || authPackagePointCut() || conferencePackagePointCut()" +
            "|| facePackagePointCut() || noticePackagePointCut() || qrPackagePointCut()" +
            "|| reservationPackagePointCut() || ssePackagePointCut() || userPackagePointCut())" +
            "&& ( componentPointCut() || servicePointCut() || repositoryPointCut() )")
    public void timeLogPointCut() {}

    @Pointcut("execution(* goorm.back.zo6.attend..*.*(..))")
    private void attendPackagePointCut() {}

    @Pointcut("execution(* goorm.back.zo6.auth..*.*(..))"
            + " && !execution(* goorm.back.zo6.auth.config..*.*(..))"
            + " && !execution(* goorm.back.zo6.auth.filter..*.*(..))")
    private void authPackagePointCut() {}

    @Pointcut("execution(* goorm.back.zo6.conference..*.*(..))")
    private void conferencePackagePointCut() {}

    @Pointcut("execution(* goorm.back.zo6.face..*.*(..))")
    private void facePackagePointCut() {}

    @Pointcut("execution(* goorm.back.zo6.notice..*.*(..))")
    private void noticePackagePointCut() {}

    @Pointcut("execution(* goorm.back.zo6.qr..*.*(..))")
    private void qrPackagePointCut() {}

    @Pointcut("execution(* goorm.back.zo6.reservation..*.*(..))")
    private void reservationPackagePointCut() {}

    @Pointcut("execution(* goorm.back.zo6.sse..*.*(..))")
    private void ssePackagePointCut() {}

    @Pointcut("execution(* goorm.back.zo6.user..*.*(..))")
    private void userPackagePointCut() {}

    @Pointcut("@target(org.springframework.stereotype.Service)")
    private void servicePointCut() {}

    @Pointcut("@target(org.springframework.stereotype.Repository)")
    private void repositoryPointCut() {}

    @Pointcut("@target(org.springframework.stereotype.Component)")
    private void componentPointCut() {}
}
