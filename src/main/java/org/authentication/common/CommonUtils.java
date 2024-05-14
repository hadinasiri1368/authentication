package org.authentication.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.authentication.dto.ResponseDto.ExceptionDto;
import org.authentication.model.Permission;
import org.authentication.model.User;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CommonUtils {
    private static UserService userService;

    @Autowired
    public CommonUtils(UserService userService) {
        CommonUtils.userService = userService;
    }

    private static String getHashString(String inputStrong, String hashType) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashType);
            byte[] bytesOfMessage = inputStrong.getBytes();
            byte[] digest = md.digest(bytesOfMessage);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMD5Hash(String inputStrong) {
        return getHashString(inputStrong, "MD5");
    }

    public static String getSHA1Hash(String inputStrong) {
        return getHashString(inputStrong, "SHA1");
    }

    public static String getStringQuery(String query, Map<String, Object> param) {
        String returnValue = query + " where 1=1 ";
        for (String key : param.keySet()) {
            returnValue += String.format(" and %s = :%s", key, key);
            param.get(key);
        }
        return returnValue;
    }

    public static Long getUserId(String token) {
        Map map = JwtTokenUtil.getUsernameFromToken(token);
        return longValue(map.get("id"));
    }

    public static String getTokenValidationMessage(String token) {
        try {
            if (token == null || token.isBlank() || token.isEmpty())
                return "token is null";
            if (!TokenManager.getInstance().HasToken(token))
                return "token is not exists";
            if (!JwtTokenUtil.validateToken(token))
                return "token is not valid";
            Map map = JwtTokenUtil.getUsernameFromToken(token);
            if (map == null || map.size() == 0)
                return "token data is not valid";
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static boolean isNull(Object o) {
        if (o instanceof String) {
            if (o == null ||
                    ((String) o).isEmpty() ||
                    ((String) o).isBlank() ||
                    ((String) o).length() == 0 ||
                    ((String) o).toLowerCase().trim().equals("null"))
                return true;
            return false;
        }
        return o == null ? true : false;
    }

    public static Long longValue(Object number) {
        if (isNull(number))
            return null;
        else if (number instanceof Number)
            return ((Number) number).longValue();
        else
            try {
                return Long.valueOf(number.toString().trim());
            } catch (NumberFormatException e) {
                return null;
            }
    }

    public static String getToken(HttpServletRequest request) {
        if (CommonUtils.isNull(request.getHeader("Authorization")))
            return null;
        return request.getHeader("Authorization").replaceAll("Bearer ", "");
    }

    public static Boolean hasPermission(User user, String url) {
        if (!user.getIsActive())
            return false;
        if (user.getIsAdmin())
            return true;
        List<Permission> permissionList = userService.listAllPermission(user.getId());
        long count = 0;
        count = permissionList.stream().filter(a -> CommonUtils.isEqualUrl(a.getUrl().toLowerCase(), url.toLowerCase())).count();
        return count > 0 ? true : false;
    }

    public static void setNull(Object entity) throws Exception {
        Class cls = Class.forName(entity.getClass().getName());
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1, field.getName().length());
            Method m = entity.getClass().getMethod("get" + name);
            Object o = m.invoke(entity);
            if (CommonUtils.isNull(o)) {
                Method method = entity.getClass().getMethod("set" + name, field.getType());
                method.invoke(entity, field.getType().cast(null));
            }
        }
    }

    public static <T> T callService(String url, HttpMethod httpMethod, HttpHeaders headers, Object body, Class<T> aClass, Map<String, Object> params) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity httpEntity = new HttpEntity(body, headers);
        if (CommonUtils.isNull(params))
            params = new HashMap<>();
        HttpEntity<T> response = restTemplate.exchange(url, httpMethod, httpEntity, aClass, params);
        return response.getBody();
    }

    public static boolean isEqualUrl(String permissionUrl, String requestUrl) {
        String[] permissionUrlArray = permissionUrl.split("/");
        String[] requestUrlArray = permissionUrl.split("/");
        if (permissionUrlArray.length != requestUrlArray.length)
            return false;
        for (int i = 0; i < permissionUrlArray.length; i++) {
            if (permissionUrlArray[i].contains("{")) {
                if (isNull(requestUrlArray[i]))
                    return false;
            } else
                continue;
            if (permissionUrlArray[i].equals(requestUrlArray[i]))
                return false;
        }
        return true;
    }

    public static void checkValidationToken(String token, String url) throws RuntimeException {
        String message = getTokenValidationMessage(token);
        if (!isNull(message)) {
            throw new RuntimeException(message);
        }
        User user = JwtTokenUtil.getUserFromToken(token);
        if (!CommonUtils.hasPermission(user, url))
            throw new RuntimeException("you.dont.have.permission");

    }

    public static ExceptionDto getException(Exception exception) {
        try {
            String[] messageArray = exception.getMessage().split("]:");
            ObjectMapper objectMapper = new ObjectMapper();
            if (messageArray.length > 1) {
                return objectMapper.readValue(messageArray[1].replaceAll("\\[", ""), ExceptionDto.class);
            } else {
                return objectMapper.readValue(messageArray[0].replaceAll("\\[", ""), ExceptionDto.class);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static <E> E isNull(E expr1, E expr2) {
        return (!isNull(expr1)) ? expr1 : expr2;
    }

    public static <T>Page<T> listPaging(List<T> aClass) {
        PageRequest pageRequest = PageRequest.of(0, aClass.size());
        long countResult = (long) aClass.size();
        return new PageImpl<>(aClass, pageRequest, countResult);
    }

    public static <T>Page<T> listPaging(List<T> aClass, PageRequest pageRequest) {
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();
        long countResult = (long) aClass.size();
        aClass = aClass.subList((pageNumber*pageSize),pageSize);
        return new PageImpl<>(aClass, pageRequest, countResult);
    }
}
