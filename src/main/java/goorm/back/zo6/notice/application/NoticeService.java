package goorm.back.zo6.notice.application;

import goorm.back.zo6.notice.domain.Notice;
import goorm.back.zo6.notice.domain.NoticeTarget;
import goorm.back.zo6.notice.infrastructure.NoticeRepository;
import goorm.back.zo6.notice.infrastructure.ReservationAttendeePhoneDao;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.exception.NurigoEmptyResponseException;
import net.nurigo.sdk.message.exception.NurigoMessageNotReceivedException;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final ReservationAttendeePhoneDao reservationAttendeePhoneDao;
    private final RedisTemplate<String, String> redisTemplate;
    private DefaultMessageService defaultMessageService;

    @PostConstruct
    public void init(){
        defaultMessageService = NurigoApp.INSTANCE.initialize(apiKey,apiSecretKey,provider);
    }

    @Value("${send.number}")
    private String number;


    @Value("${send.key}")
    private String apiKey;


    @Value("${send.secret}")
    private String apiSecretKey;

    @Value("${send.provider}")
    private String provider;

    @Transactional
    public void sendMessage(String message, Long conferenceId, Long sessionId, String noticeTarget) {
        NoticeTarget target = NoticeTarget.from(noticeTarget);
        noticeRepository.save(Notice.builder().message(message).conferenceId(conferenceId).sessionId(sessionId).noticeTarget(target).build());
        List<String> phones = getTarget(conferenceId,sessionId,target);

        //메시지 전송 로직
        ArrayList<Message> messageList = new ArrayList<>();

        for(String phone : phones){
            Message sms = new Message();
            sms.setFrom(number);
            sms.setTo(phone);
            sms.setText(message);

            messageList.add(sms);
        }

        try{
            log.info("메시지 전송 시작 전송 지도 메시지 갯수 : {}",messageList.size());
            MultipleDetailMessageSentResponse response = defaultMessageService.send(messageList,false,true);
            log.info("전송실패 메시지 갯수 : {}", response.getFailedMessageList().size());
        }
        catch (NurigoUnknownException | NurigoMessageNotReceivedException | NurigoEmptyResponseException e) {
            log.info(e.getMessage());
        }
    }

    private List<String> getTarget(Long conferenceId, Long sessionId, NoticeTarget noticeTarget){
        if (noticeTarget.equals(NoticeTarget.ALL)) {
            return getAllAttendee(conferenceId, sessionId);
        } else if (noticeTarget.equals(NoticeTarget.ATTENDEE)) {
            return  getAttendee(conferenceId, sessionId);
        } else {
            return getNonAttendee(conferenceId,sessionId);
        }
    }

    private List<String> getAllAttendee(Long conferenceId, Long sessionId){
        List<String> phones;
        if(sessionId==null){
            phones = reservationAttendeePhoneDao.getPhoneConferenceAttendee(conferenceId);
        }
        else{
            phones = reservationAttendeePhoneDao.getPhoneSessionAttendee(conferenceId,sessionId);
        }
        return phones;
    }

    private List<String> getAttendee(Long conferenceId, Long sessionId){
        Set<String> ids;
        if (sessionId == null) {
            ids = redisTemplate.opsForSet().members("conference:" + conferenceId);
        } else {
            ids = redisTemplate.opsForSet().members("conference:" + conferenceId + ":session:" + sessionId);
        }
        return reservationAttendeePhoneDao.getPhoneByUserId(ids);
    }

    private List<String> getNonAttendee(Long conferenceId, Long sessionId){
        List<String> allPhones = getAllAttendee(conferenceId,sessionId);
        List<String> attendeePhones = getAttendee(conferenceId,sessionId);
        allPhones.removeAll(attendeePhones);
        return allPhones;
    }
}
