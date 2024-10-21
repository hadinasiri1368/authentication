package org.authentication.dto.ResponseDto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaptchaData {
    private String captchaToken;
    private String uuid;
    private String captchaCode;
    private byte[] image;
}
