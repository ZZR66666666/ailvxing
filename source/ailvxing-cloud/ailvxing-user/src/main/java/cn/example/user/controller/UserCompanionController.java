package cn.example.user.controller;

import cn.example.common.common.Result;
import cn.example.common.entity.UserCompanion;
import cn.example.user.service.UserCompanionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "常用出行人员")
@RestController
@RequestMapping("/api/companion")
@RequiredArgsConstructor
public class UserCompanionController {

    private final UserCompanionService userCompanionService;

    private Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute("userId");
    }

    @Operation(summary = "获取我的出行人员列表")
    @GetMapping
    public Result<List<UserCompanion>> listMyCompanions(HttpServletRequest request) {
        return userCompanionService.listByUserId(getUserId(request));
    }

    @Operation(summary = "添加出行人员")
    @PostMapping
    public Result<UserCompanion> addCompanion(HttpServletRequest request, @RequestBody UserCompanion companion) {
        return userCompanionService.addCompanion(getUserId(request), companion);
    }

    @Operation(summary = "更新出行人员")
    @PutMapping
    public Result<UserCompanion> updateCompanion(HttpServletRequest request, @RequestBody UserCompanion companion) {
        return userCompanionService.updateCompanion(getUserId(request), companion);
    }

    @Operation(summary = "删除出行人员")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCompanion(HttpServletRequest request, @PathVariable Long id) {
        return userCompanionService.deleteCompanion(getUserId(request), id);
    }
}