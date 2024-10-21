package org.authentication.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.authentication.common.CommonUtils;
import org.authentication.common.Const;
import org.authentication.common.JwtTokenUtil;
import org.authentication.dto.RequestDto.CaptchaDto;
import org.authentication.dto.RequestDto.LoginDto;
import org.authentication.dto.ResponseDto.CaptchaData;
import org.authentication.dto.ResponseDto.LoginData;
import org.authentication.dto.ResponseDto.Person;
import org.authentication.exception.UnauthorizedException;
import org.authentication.model.Role;
import org.authentication.model.User;
import org.authentication.service.CaptchaTokenManager;
import org.authentication.service.TransportServiceProxcy;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
public class API {
    private final UserService userService;
    private final TransportServiceProxcy transportServiceProxcy;
    @Value("${authentication.captcha-length}")
    private int captchaLength;

    public API(UserService userService, TransportServiceProxcy transportServiceProxcy) {
        this.userService = userService;
        this.transportServiceProxcy = transportServiceProxcy;
    }

    @PostMapping(path = "/login")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<LoginData> login(@RequestBody LoginDto loginDto ,@RequestHeader("X-CAPTCHA-UUID") String captchaUuid, HttpServletRequest request) throws Exception {
        String uuid = request.getHeader("X-UUID");
        User user = userService.findOne(loginDto.getUsername(), loginDto.getPassword());
        if (user == null)
            throw new RuntimeException("1022");
        if (captchaUuid == null)
            throw new RuntimeException("1025");
        CaptchaDto captchaDto = CommonUtils.getCaptcha(request);
        if (!captchaUuid.equals(captchaDto.getUuid()) ||
            !loginDto.getCaptchaCode().toLowerCase().equals(captchaDto.getCaptchaCode().toLowerCase()))
            throw new RuntimeException("1026");
        String token = JwtTokenUtil.generateToken(user);
        Person person = transportServiceProxcy.getPerson(token, uuid, user.getPersonId());
        if (CommonUtils.isNull(person))
            throw new RuntimeException("1023");
        List<Role> userRoles = userService.listAllRole(user.getId());
        if (!user.getIsAdmin()) {
            if (Objects.equals(loginDto.getUserType(), Const.USER_TYPE_EMPLOYEE)) {
                if (userRoles.stream().noneMatch(item -> item.getId() == Const.USER_TYPE_EMPLOYEE.intValue()))
                    throw new RuntimeException("1010");
            } else if (Objects.equals(loginDto.getUserType(), Const.USER_TYPE_DRIVER)) {
                if (userRoles.stream().noneMatch(item -> item.getId() == Const.USER_TYPE_DRIVER.intValue()))
                    throw new RuntimeException("1010");
            } else if (Objects.equals(loginDto.getUserType(), Const.USER_TYPE_CUSTOMER)) {
                if (userRoles.stream().noneMatch(item -> item.getId() == Const.USER_TYPE_CUSTOMER.intValue()))
                    throw new RuntimeException("1010");
            } else {
                throw new RuntimeException("1010");
            }
        }
        CaptchaTokenManager.getInstance().removeTokenById(captchaDto.getUuid());
        return new ResponseEntity<>(LoginData.builder().isAdmin(user.getIsAdmin()).isActive(user.getIsActive()).username(user.getUsername()).name(person.getName()).family(person.getFamily()).token(token).build(), HttpStatus.OK);
    }

    @GetMapping(path = "/authentication/getUserId")
    public ResponseEntity<String> getUserId(@RequestParam("token") String token) {
        Map map = JwtTokenUtil.getUsernameFromToken(token);
        return new ResponseEntity<>(map.get("id").toString(), HttpStatus.OK);
    }

    @GetMapping(path = "/authentication/getUser")
    public ResponseEntity<Map> getUserObject(@RequestParam("token") String token) {
        Map map = JwtTokenUtil.getUsernameFromToken(token);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping(path = "/authentication/checkValidationToken")
    public ResponseEntity<String> checkValidationToken(@RequestParam(value = "token",required = false) String token, @RequestParam("url") String url) {
        try {
            CommonUtils.checkValidationToken(token, url);
            return new ResponseEntity(null, HttpStatus.OK);
        } catch (Exception e) {
            throw  new UnauthorizedException(e.getMessage());
        }
    }

    @GetMapping("/authentication/captcha/generate")
    public ResponseEntity<CaptchaData> generateCaptcha() {
        CaptchaData captchaData = new CaptchaData();
        try {
            String uuid = CommonUtils.generateUUID().toString();
            String captchaCode = CommonUtils.generateRandomString(captchaLength);
            String token = JwtTokenUtil.generateCaptchaToken(new CaptchaDto(captchaCode, uuid));
            byte[] image = CommonUtils.generateCaptchaImage(captchaCode);
            captchaData = CaptchaData.builder().captchaToken(token).uuid(uuid).image(image).captchaCode(captchaCode).build();

            return new ResponseEntity<>(captchaData, HttpStatus.OK);
        } catch (Exception e) {
            log.info("captcha generate failed " + e.getMessage());
            throw new RuntimeException("1001");
        }
    }

}
