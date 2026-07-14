package cn.example.user.service;

import cn.example.common.common.Result;
import cn.example.common.entity.UserCompanion;

import java.util.List;

public interface UserCompanionService {

    Result<List<UserCompanion>> listByUserId(Long userId);

    Result<UserCompanion> addCompanion(Long userId, UserCompanion companion);

    Result<UserCompanion> updateCompanion(Long userId, UserCompanion companion);

    Result<Void> deleteCompanion(Long userId, Long companionId);
}