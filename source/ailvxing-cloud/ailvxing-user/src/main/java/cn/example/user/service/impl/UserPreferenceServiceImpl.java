package cn.example.user.service.impl;

import cn.example.common.common.Result;
import cn.example.common.entity.UserPreference;
import cn.example.user.mapper.UserPreferenceMapper;
import cn.example.user.service.UserPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPreferenceServiceImpl implements UserPreferenceService {

    private final UserPreferenceMapper userPreferenceMapper;

    @Override
    public Result<UserPreference> getByUserId(Long userId) {
        UserPreference pref = userPreferenceMapper.findByUserId(userId);
        if (pref == null) {
            pref = new UserPreference();
            pref.setUserId(userId);
            pref.setBudgetLevel("comfort");
        }
        return Result.success(pref);
    }

    @Override
    public Result<Void> saveOrUpdate(Long userId, UserPreference preference) {
        UserPreference existing = userPreferenceMapper.findByUserId(userId);
        preference.setUserId(userId);
        if (existing == null) {
            userPreferenceMapper.insert(preference);
        } else {
            userPreferenceMapper.updateByUserId(preference);
        }
        return Result.success();
    }
}