package cn.example.user.controller;

import cn.example.common.common.PageResult;
import cn.example.common.common.Result;
import cn.example.common.dto.UserLoginDTO;
import cn.example.common.dto.UserRegisterDTO;
import cn.example.common.entity.User;
import cn.example.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        return userService.register(dto);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserLoginDTO dto) {
        return userService.login(dto);
    }

    @Operation(summary = "根据用户名查询")
    @GetMapping("/findByUsername")
    public Result<User> findByUsername(@RequestParam String username) {
        return userService.findByUsername(username);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public Result<User> getUserInfo(@PathVariable Long id) {
        return userService.getUserInfo(id);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping
    public Result<Void> updateUserInfo(@RequestBody User user) {
        return userService.updateUserInfo(user);
    }

    @Operation(summary = "用户列表（管理员）")
    @GetMapping("/list")
    public Result<PageResult<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return userService.listUsers(pageNum, pageSize);
    }

    @Operation(summary = "修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(HttpServletRequest request,
                                       @RequestParam String oldPassword,
                                       @RequestParam String newPassword) {
        return userService.changePassword(getUserId(request), oldPassword, newPassword);
    }
}