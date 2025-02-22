package org.example.springaidemo.service;

import org.example.springaidemo.dao.mapper.Mapper1;
import org.example.springaidemo.moudle.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private Mapper1 mapper1;

    @Cacheable(value = "userCache", key = "#token")
    public UserModel getUserByToken(String token) {
        // 这里假设token就是userId，实际项目中应该根据token查询用户信息
        return mapper1.getUser(token);
    }
} 