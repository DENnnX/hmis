package cn.edu.scnu.hmis.controller;

import cn.edu.scnu.hmis.common.ApiResponse;
import cn.edu.scnu.hmis.entity.User;
import cn.edu.scnu.hmis.mapper.UserMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UserMapper userMapper;

    public LoginController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestParam String username,
                                                   @RequestParam String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        if (!user.getPassword().equals(password)) {
            return ApiResponse.error("密码错误");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("referenceId", user.getReferenceId());
        return ApiResponse.ok(result);
    }
}
