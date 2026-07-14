package cn.example.user.controller;

import cn.example.common.common.Result;
import cn.example.common.entity.UserPreference;
import cn.example.user.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户偏好设置")
@RestController
@RequestMapping("/api/preference")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @Operation(summary = "获取我的偏好设置")
    @GetMapping
    public Result<UserPreference> getMyPreference(HttpServletRequest request) {
        return userPreferenceService.getByUserId(getUserId(request));
    }

    @Operation(summary = "保存偏好设置")
    @PostMapping
    public Result<Void> savePreference(HttpServletRequest request, @RequestBody UserPreference preference) {
        return userPreferenceService.saveOrUpdate(getUserId(request), preference);
    }
}