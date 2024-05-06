package org.authentication.dto.ResponseDto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionDto {
    private Integer errorCode;
    private String errorMessage;
    private Integer errorStatus;
    private String uuid;
}
