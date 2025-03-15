package goorm.back.zo6.face.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "faces")
public class Face {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "face_id")
    Long id;

    @Column(name = "rekognition_face_id")
    String rekognitionFaceId;

    @Column(name = "user_id")
    Long userId;

    public static Face of(String rekognitionFaceId, Long userId){
        return Face.builder()
                .rekognitionFaceId(rekognitionFaceId)
                .userId(userId)
                .build();
    }
}
