package cn.example.user.service;

import cn.example.common.common.PageResult;
import cn.example.common.common.Result;
import cn.example.common.dto.UserLoginDTO;
import cn.example.common.dto.UserRegisterDTO;
import cn.example.common.entity.User;

public interface UserService {

    Result<Void> register(UserRegisterDTO dto);

    Result<String> login(UserLoginDTO dto);

    Result<User> getUserInfo(Long userId);

    Result<User> findByUsername(String username);

    Result<Void> updateUserInfo(User user);

    Result<PageResult<User>> listUsers(Integer pageNum, Integer pageSize);

    Result<Void> changePassword(Long userId, String oldPassword, String newPassword);
}