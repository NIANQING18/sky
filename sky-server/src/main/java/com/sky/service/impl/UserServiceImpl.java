package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String url = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO.getCode();

        //构建请求参数
        HashMap<String,String> map = new HashMap<>();
        map.put("js_code",code);
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("grant_type","authorization_code");

        //发送请求
        String response = HttpClientUtil.doGet(url, map);

        //解析返回结果
        JSONObject jsonObject = JSON.parseObject(response);

        //获得用户的openid
        String openid = jsonObject.getString("openid");
        if(openid == null) throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        //先查找判断是否是新用户
        User user = userMapper.getByOpenid(openid);

        if(user == null) { //是新用户
            //构建User对象
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.insert(user);
        }
        return user;


    }
}
