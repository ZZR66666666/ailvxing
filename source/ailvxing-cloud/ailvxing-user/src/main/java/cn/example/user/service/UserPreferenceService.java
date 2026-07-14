package cn.example.user.service;

import cn.example.common.common.Result;
import cn.example.common.entity.UserPreference;

public interface UserPreferenceService {

    Result<UserPreference> getByUserId(Long userId);

    Result<Void> saveOrUpdate(Long userId, UserPreference preference);
}