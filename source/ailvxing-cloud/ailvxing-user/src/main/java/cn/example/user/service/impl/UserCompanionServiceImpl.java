package cn.example.user.service.impl;

import cn.example.common.common.Result;
import cn.example.common.entity.UserCompanion;
import cn.example.user.mapper.UserCompanionMapper;
import cn.example.user.service.UserCompanionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCompanionServiceImpl implements UserCompanionService {

    private final UserCompanionMapper userCompanionMapper;

    @Override
    public Result<List<UserCompanion>> listByUserId(Long userId) {
        return Result.success(userCompanionMapper.findByUserId(userId));
    }

    @Override
    public Result<UserCompanion> addCompanion(Long userId, UserCompanion companion) {
        companion.setUserId(userId);
        userCompanionMapper.insert(companion);
        return Result.success(companion);
    }

    @Override
    public Result<UserCompanion> updateCompanion(Long userId, UserCompanion companion) {
        UserCompanion existing = userCompanionMapper.findById(companion.getId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.error(403, "无权修改该出行人员");
        }
        userCompanionMapper.update(companion);
        return Result.success(companion);
    }

    @Override
    public Result<Void> deleteCompanion(Long userId, Long companionId) {
        UserCompanion existing = userCompanionMapper.findById(companionId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return Result.error(403, "无权删除该出行人员");
        }
        userCompanionMapper.deleteById(companionId);
        return Result.success();
    }
}