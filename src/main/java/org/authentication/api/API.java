package org.authentication.api;

import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.common.Const;
import org.authentication.common.JwtTokenUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.authentication.dto.RequestDto.LoginDto;
import org.authentication.dto.ResponseDto.LoginData;
import org.authentication.dto.ResponseDto.Person;
import org.authentication.model.Role;
import org.authentication.model.User;
import org.authentication.service.TransportServiceProxcy;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class API {
    @Autowired
    private UserService userService;
    @Autowired
    private TransportServiceProxcy transportServiceProxcy;

    @PostMapping(path = "/login")
    public ResponseEntity<LoginData> login(@RequestBody LoginDto loginDto, HttpServletRequest request) throws Exception {
        User user = userService.findOne(loginDto.getUsername(), loginDto.getPassword());
        String uuid = request.getHeader("X-UUID");
        if (user == null)
            return new ResponseEntity("1022", HttpStatus.BAD_REQUEST);
        String token = JwtTokenUtil.generateToken(user);
        Person person = transportServiceProxcy.getPerson(token, uuid, user.getPersonId());
        if (CommonUtils.isNull(person))
            return new ResponseEntity("1023", HttpStatus.BAD_REQUEST);
        List<Role> userRoles = userService.listAllRole(user.getId());
        if (!user.getIsAdmin()) {
            if (loginDto.getUserType() == Const.USER_TYPE_EMPLOYEE) {
                if (userRoles.stream().filter(item -> item.getId() == Const.USER_TYPE_EMPLOYEE.intValue()).count() == 0)
                    return new ResponseEntity("1010", HttpStatus.BAD_REQUEST);
            } else if (loginDto.getUserType() == Const.USER_TYPE_DRIVER) {
                if (userRoles.stream().filter(item -> item.getId() == Const.USER_TYPE_DRIVER.intValue()).count() == 0)
                    return new ResponseEntity("1010", HttpStatus.BAD_REQUEST);
            } else if (loginDto.getUserType() == Const.USER_TYPE_CUSTOMER) {
                if (userRoles.stream().filter(item -> item.getId() == Const.USER_TYPE_CUSTOMER.intValue()).count() == 0)
                    return new ResponseEntity("1010", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity(LoginData.builder().isAdmin(user.getIsAdmin()).isActive(user.getIsActive()).username(user.getUsername()).name(person.getName()).family(person.getFamily()).token(token).build(), HttpStatus.OK);
    }

    @GetMapping(path = "/authentication/getUserId")
    public ResponseEntity<String> getUserId(@RequestParam("token") String token) {
        Map map = JwtTokenUtil.getUsernameFromToken(token);
        return new ResponseEntity(map.get("id").toString(), HttpStatus.OK);
    }

    @GetMapping(path = "/authentication/getUser")
    public ResponseEntity<Map> getUserObject(@RequestParam("token") String token) {
        Map map = JwtTokenUtil.getUsernameFromToken(token);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @GetMapping(path = "/authentication/checkValidationToken")
    public ResponseEntity<String> checkValidationToken(@RequestParam(value = "token",required = false) String token, @RequestParam("url") String url) {
        try {
            CommonUtils.checkValidationToken(token, url);
            return new ResponseEntity(null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
