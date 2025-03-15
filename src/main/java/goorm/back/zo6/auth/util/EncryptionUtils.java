package goorm.back.zo6.auth.util;

import goorm.back.zo6.common.exception.CustomException;
import goorm.back.zo6.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptionUtils {

    private final String algorithm;

    private final SecretKey secretKey;

    public EncryptionUtils(@Value("${encrypt.algorithm}")String algorithm,
                           @Value("${encrypt.secret-key}")String secretKeyString){
        this.algorithm = algorithm;
        byte[] secretKeyBytes = secretKeyString.getBytes();
        secretKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, algorithm);
    }

    public String encrypt(String plainText){
        try{
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        }catch (Exception e){
            throw new CustomException(ErrorCode.ENCRYPT_CIPHER_EXCEPTION);
        }
    }

    public String decrypt(String encrypted){
        try{
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodeBytes = Base64.getDecoder().decode(encrypted);
            byte[] decryptedBytes = cipher.doFinal(decodeBytes);
            return new String(decryptedBytes);
        }catch (Exception e){
            throw new CustomException(ErrorCode.DECRYPT_CIPHER_EXCEPTION);
        }
    }

}


