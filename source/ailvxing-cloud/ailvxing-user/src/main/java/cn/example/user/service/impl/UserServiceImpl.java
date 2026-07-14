package cn.example.user.service.impl;

import cn.example.common.common.PageResult;
import cn.example.common.common.Result;
import cn.example.common.dto.UserLoginDTO;
import cn.example.common.dto.UserRegisterDTO;
import cn.example.common.entity.User;
import cn.example.user.mapper.UserMapper;
import cn.example.user.service.UserService;
import cn.example.common.util.JwtUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    private static final String USER_INFO_KEY = "user:info:";
    private static final long USER_CACHE_TTL_HOURS = 1;

    @Override
    public Result<Void> register(UserRegisterDTO dto) {
        User existing = userMapper.findByUsername(dto.getUsername());
        if (existing != null) {
            return Result.error(400, "用户名已存在");
        }
        if (dto.getPhone() != null) {
            existing = userMapper.findByPhone(dto.getPhone());
            if (existing != null) {
                return Result.error(400, "手机号已注册");
            }
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setRole(0);
        user.setStatus(1);
        userMapper.insert(user);
        return Result.success();
    }

    @Override
    public Result<String> login(UserLoginDTO dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null) {
            return Result.error(400, "用户不存在");
        }
        if (!user.getPassword().equals(dto.getPassword())) {
            return Result.error(400, "密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error(400, "账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        redisTemplate.opsForValue().set("user:token:" + token, user.getId(), 24, TimeUnit.HOURS);
        cacheUserInfo(user);
        return Result.success("登录成功", token);
    }

    @Override
    public Result<User> getUserInfo(Long userId) {
        String key = USER_INFO_KEY + userId;
        User cached = (User) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            cached.setPassword(null);
            return Result.success(cached);
        }
        User user = userMapper.findById(userId);
        if (user == null) return Result.error(404, "用户不存在");
        cacheUserInfo(user);
        user.setPassword(null);
        return Result.success(user);
    }

    private void cacheUserInfo(User user) {
        redisTemplate.opsForValue().set(USER_INFO_KEY + user.getId(), user, USER_CACHE_TTL_HOURS, TimeUnit.HOURS);
    }

    @Override
    public Result<User> findByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result<Void> updateUserInfo(User user) {
        userMapper.updateProfile(user);
        redisTemplate.delete(USER_INFO_KEY + user.getId());
        return Result.success();
    }

    @Override
    public Result<PageResult<User>> listUsers(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.findAll();
        Page<User> page = (Page<User>) users;
        users.forEach(u -> u.setPassword(null));
        return Result.success(new PageResult<>(page.getTotal(), page.getPageNum(), page.getPageSize(), page.getResult()));
    }

    @Override
    public Result<Void> changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            return Result.error(400, "原密码错误");
        }
        if (newPassword == null || newPassword.length() < 6) {
            return Result.error(400, "新密码长度不能少于6位");
        }
        userMapper.updatePassword(userId, newPassword);
        redisTemplate.delete(USER_INFO_KEY + userId);
        return Result.success();
    }
}