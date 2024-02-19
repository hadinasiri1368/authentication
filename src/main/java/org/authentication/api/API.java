package org.authentication.api;

import org.authentication.common.CommonUtils;
import org.authentication.common.JwtTokenUtil;
import org.authentication.dto.LoginDto;
import org.authentication.model.Permission;
import org.authentication.model.User;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class API {
    @Autowired
    private UserService userService;

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) throws Exception {
        User user = userService.findOne(loginDto.getUsername(), loginDto.getPassword());
        if (user == null)
            return new ResponseEntity("login failed", HttpStatus.BAD_REQUEST);
        return new ResponseEntity(JwtTokenUtil.generateToken(user), HttpStatus.OK);
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
