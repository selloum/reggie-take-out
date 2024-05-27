package com.selloum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.selloum.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
