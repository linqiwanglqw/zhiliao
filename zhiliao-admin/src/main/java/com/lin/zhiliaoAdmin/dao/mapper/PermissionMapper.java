package com.lin.zhiliaoAdmin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lin.zhiliaoAdmin.dao.pojo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {
    List<Permission> findPermissionsByAdminId(Long adminId);

}
