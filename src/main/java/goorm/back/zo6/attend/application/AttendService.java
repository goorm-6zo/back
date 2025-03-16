package goorm.back.zo6.attend.application;

import goorm.back.zo6.attend.domain.Attend;
import goorm.back.zo6.attend.domain.AttendRepository;
import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import goorm.back.zo6.reservation.domain.Reservation;
import goorm.back.zo6.reservation.domain.ReservationRepository;
import goorm.back.zo6.user.domain.User;
import goorm.back.zo6.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class AttendService {
    private final ReservationRepository reservationRepository;
    private final AttendRepository attendRepository;
    private final UserRepository userRepository;

    @Transactional
    public Attend registerAttend(Long userId, Long conferenceId, Long sessionId){
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Reservation reservation = reservationRepository.findByPhoneAndConferenceId(user.getPhone(), conferenceId)
                .orElseThrow(()-> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(reservation.getReservationSessions().isEmpty()){

        }
        //
        Attend attend = Attend.of(user.getId(), reservation.getId());
        attendRepository.save(attend);


    }


}
