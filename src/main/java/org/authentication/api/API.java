package org.authentication.api;

import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.common.Const;
import org.authentication.common.JwtTokenUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.authentication.dto.RequestDto.LoginDto;
import org.authentication.dto.ResponseDto.LoginData;
import org.authentication.dto.ResponseDto.Person;
import org.authentication.exception.UnauthorizedException;
import org.authentication.model.Role;
import org.authentication.model.User;
import org.authentication.service.TransportServiceProxcy;
import org.authentication.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class API {
    private final UserService userService;
    private final TransportServiceProxcy transportServiceProxcy;

    public API(UserService userService, TransportServiceProxcy transportServiceProxcy) {
        this.userService = userService;
        this.transportServiceProxcy = transportServiceProxcy;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<LoginData> login(@RequestBody LoginDto loginDto, HttpServletRequest request) throws Exception {
        User user = userService.findOne(loginDto.getUsername(), loginDto.getPassword());
        String uuid = request.getHeader("X-UUID");
        if (user == null)
            throw new RuntimeException("1022") ;
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
            }
            else {
                throw new RuntimeException("1010");
            }
        }
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
}
