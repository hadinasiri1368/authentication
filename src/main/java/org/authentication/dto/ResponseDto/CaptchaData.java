package org.authentication.dto.ResponseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.*;
import org.authentication.common.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaptchaData {
    private String captchaToken;
    private String uuid;
    @JsonSerialize(using = CaptchaCodeSerializer.class)
    private String captchaCode;
    private byte[] image;
}

@Component
class CaptchaCodeSerializer extends StdSerializer<String> {

    @Autowired
    private Environment environment;

    public CaptchaCodeSerializer() {
        this(null);
    }

    public CaptchaCodeSerializer(Class<String> t) {
        super(t);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(Const.PROFILE_DEVELOPER_NAME::equals)) {
            gen.writeString(value);
        } else {
            gen.writeNull();
        }
    }
}
