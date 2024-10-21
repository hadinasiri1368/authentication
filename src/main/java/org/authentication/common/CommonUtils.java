package org.authentication.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.authentication.dto.RequestDto.CaptchaDto;
import org.authentication.dto.ResponseDto.ExceptionDto;
import org.authentication.model.Permission;
import org.authentication.model.User;
import org.authentication.service.CaptchaTokenManager;
import org.authentication.service.GenericService;
import org.authentication.service.TokenManager;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

@Component
@Slf4j
public class CommonUtils {

    private static MessageSource messageSource;
    private static List<Permission> permissionList;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static int captchaHeight;
    private static int captchaWidth;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        CommonUtils.messageSource = messageSource;
    }

    private static UserService userService;
    private static GenericService<Permission> permissionService;

    @Autowired
    public CommonUtils(UserService userService, GenericService<Permission> permissionService) {
        CommonUtils.userService = userService;
        CommonUtils.permissionService = permissionService;
    }

    @Value("${authentication.captcha-height}")
    public void setCaptchaHeight(int captchaHeight) {
        this.captchaHeight = captchaHeight;
    }

    @Value("${authentication.captcha-width}")
    public void setCaptchaWidth(int captchaWidth) {
        this.captchaWidth = captchaWidth;
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
                return "1003";
            if (!TokenManager.getInstance().exists(token))
                return "1002";
            if (!JwtTokenUtil.validateToken(token))
                return "1004";
            Map map = JwtTokenUtil.getUsernameFromToken(token);
            if (map == null || map.size() == 0)
                return "1005";
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

    public static Boolean isUrlSensitive(String url) {
        if (isNull(permissionList))
            permissionList = permissionService.findAll(Permission.class);
        Optional<Permission> permission = permissionList.stream().filter(a -> CommonUtils.isEqualUrl(a.getUrl().toLowerCase(), url.toLowerCase())).findFirst();
        if(!permission.isPresent())
            return true;
        return permission.get().getIsSensitive();
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
        String[] requestUrlArray = requestUrl.split("/");
        if (permissionUrlArray.length != requestUrlArray.length)
            return false;
        for (int i = 0; i < permissionUrlArray.length; i++) {
            if (permissionUrlArray[i].contains("{")) {
                if (isNull(requestUrlArray[i]) || !isNumeric(requestUrlArray[i]))
                    return false;
            } else if (!permissionUrlArray[i].equals(requestUrlArray[i]))
                return false;
        }
        return true;
    }

    public static void checkValidationToken(String token, String url) throws RuntimeException {
        if (!isUrlSensitive(url))
            return;
        String message = getTokenValidationMessage(token);
        if (!isNull(message)) {
            throw new RuntimeException(message);
        }
        User user = JwtTokenUtil.getUserFromToken(token);
        if (!hasPermission(user, url))
            throw new RuntimeException("1010");

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


    public static String getMessage(String key) {
        return messageSource.getMessage(key, null, null);
    }

    public static ExceptionDto getException(SQLException exception) {
        if (exception.getMessage().toLowerCase().contains("duplicate key")) {
            return ExceptionDto.builder()
                    .errorCode(409)
                    .errorMessage(getMessage("1007"))
                    .build();
        } else {
            return ExceptionDto.builder()
                    .errorCode(409)
                    .errorMessage("1008")
                    .build();
        }
    }


    public static <E> E isNull(E expr1, E expr2) {
        return (!isNull(expr1)) ? expr1 : expr2;
    }

    public static <T> Page<T> listPaging(List<T> aClass) {
        PageRequest pageRequest = PageRequest.ofSize(aClass.size());
        return listPaging(aClass, pageRequest);
    }

    public static <T> Page<T> listPaging(List<T> aClass, PageRequest pageRequest) {
        long countResult = aClass.size();
        int pageNumber = pageRequest.getPageNumber();
        int pageSize = pageRequest.getPageSize();

        int fromIndex = pageNumber * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, aClass.size());

        if (fromIndex > toIndex) {
            fromIndex = toIndex;
        }

        List<T> subList = aClass.subList(fromIndex, toIndex);
        return new PageImpl<>(subList, pageRequest, countResult);
    }

    public static String generateRandomString(int length) {
        StringBuilder result = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            result.append(CHARACTERS.charAt(randomIndex));
        }

        return result.toString();
    }

    public static UUID generateUUID() {
        long most64SigBits = get64MostSignificantBitsForVersion1();
        long least64SigBits = get64LeastSignificantBitsForVersion1();
        return new UUID(most64SigBits, least64SigBits);
    }

    private static long get64LeastSignificantBitsForVersion1() {
        Random random = new Random();
        long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
        long variant3BitFlag = 0x8000000000000000L;
        return random63BitLong | variant3BitFlag;
    }

    private static long get64MostSignificantBitsForVersion1() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long time_low = (currentTimeMillis & 0x0000_0000_FFFF_FFFFL) << 32;
        final long time_mid = ((currentTimeMillis >> 32) & 0xFFFF) << 16;
        final long version = 1 << 12;
        final long time_hi = ((currentTimeMillis >> 48) & 0x0FFF);
        return time_low | time_mid | version | time_hi;
    }

    public static byte[] generateCaptchaImage(String captchaText) throws IOException {
        int width = captchaWidth;
        int height = captchaHeight;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, width, height);

        Font font = new Font("Arial", Font.BOLD, 40);
        g2d.setFont(font);

        Random random = new Random();
        for (int i = 0; i < captchaText.length(); i++) {
            g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g2d.drawString(String.valueOf(captchaText.charAt(i)), (i * 30) + 10, 40);
        }

        for (int i = 0; i < 5; i++) {
            g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g2d.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
        }

        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        return baos.toByteArray();
    }

    public static CaptchaDto getCaptcha(HttpServletRequest request) {
        String token = getToken(request);
        if (token == null)
            throw new RuntimeException("1025");
        if (!JwtTokenUtil.validateCaptchaToken(token))
            throw new RuntimeException("1025");
        CaptchaDto captchaDto = JwtTokenUtil.getCaptchaFromToken(token);
        if (captchaDto == null || captchaDto.getCaptchaCode() == null || captchaDto.getUuid() == null)
            throw new RuntimeException("1025");
        return captchaDto;
    }
}
