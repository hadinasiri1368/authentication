package org.authentication.api;

import org.authentication.common.CommonUtils;
import org.authentication.common.JwtTokenUtil;
import org.authentication.dto.RequestDto.LoginDto;
import org.authentication.dto.ResponseDto.LoginData;
import org.authentication.dto.ResponseDto.Person;
import org.authentication.model.User;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class API {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/login")
    public ResponseEntity<LoginData> login(@RequestBody LoginDto loginDto) throws Exception {
        User user = userService.findOne(loginDto.getUsername(), loginDto.getPassword());
        if (user == null)
            return new ResponseEntity("login failed", HttpStatus.BAD_REQUEST);
        String token = JwtTokenUtil.generateToken(user);
        Person person = CommonUtils.getPerson(user.getPersonId(), token);
        if (CommonUtils.isNull(person))
            return new ResponseEntity("person doesn't has info", HttpStatus.BAD_REQUEST);
        return new ResponseEntity(LoginData.builder().isAdmin(user.getIsAdmin()).isActive(user.getIsActive()).username(user.getUsername()).name(person.getName()).family(person.getFamily()).token(token).build(), HttpStatus.OK);
    }

    @GetMapping(path = "/getUserId")
    public ResponseEntity<String> getUserId(@ModelAttribute("token") String token) {
        Map map = JwtTokenUtil.getUsernameFromToken(token);
        return new ResponseEntity(map.get("id").toString(), HttpStatus.OK);
    }

    @GetMapping(path = "/getUser")
    public ResponseEntity<Map> getUserObject(@ModelAttribute("token") String token) {
        Map map = JwtTokenUtil.getUsernameFromToken(token);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @GetMapping(path = "/checkValidationToken")
    public ResponseEntity<String> checkValidationToken(@ModelAttribute("token") String token, @ModelAttribute("url") String url) {
        String message = CommonUtils.getTokenValidationMessage(token);
        if (!CommonUtils.isNull(message)) {
            return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
        }
        User user = JwtTokenUtil.getUserFromToken(token);
        if (!CommonUtils.hasPermission(user, url))
            return new ResponseEntity("you dont have permission", HttpStatus.BAD_REQUEST);
        return new ResponseEntity("token is ok", HttpStatus.OK);
    }
}
