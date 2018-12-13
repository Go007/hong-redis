package com.hong.mapper;

import com.hong.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
   User selectById(Long id);

   void updateCount(@Param("id")Long id, @Param("count")Long count);
}