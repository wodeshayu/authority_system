package com.hopu.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hopu.domain.Menu;
import com.hopu.domain.Role;
import com.hopu.domain.User;
import com.hopu.domain.UserRole;
import com.hopu.service.IRoleService;
import com.hopu.service.IUserRoleService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import utils.PageEntity;
import utils.ResponseEntity;
import utils.UUIDUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static utils.ResponseEntity.error;
import static utils.ResponseEntity.success;

@Controller
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private IRoleService roleService;
    @Autowired
    private IUserRoleService userRoleService;

    @RequestMapping("/toListPage")
    @RequiresPermissions("role:list")
    public String toListPage(){
        return "admin/role/role_list";
    }

    @ResponseBody
    @RequestMapping("/list")
    public PageEntity roleList(int page, int limit, Role role, Model model){
        Page<Role> page1 = new Page<>(page, limit);
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>(new Role());
        if (role != null){
            if(!StringUtils.isEmpty(role.getRole())){
                roleQueryWrapper.like("role",role.getRole());
            }
        }
        IPage<Role> roleIPage = roleService.page(page1, roleQueryWrapper);
        return new PageEntity(roleIPage);
    }

    @RequestMapping("/toAddPage")
    @RequiresPermissions("role:add")
    public String toAddPage(){
        return "admin/role/role_add";
    }

    @ResponseBody
    @RequestMapping("/save")
    public ResponseEntity add(Role role){
        Role role1 = roleService.getOne(new QueryWrapper<Role>().eq("role", role.getRole()));
        if(role1!=null){
            return error("角色名已存在");
        }
        role.setId(UUIDUtils.getID());
        role.setCreateTime(new Date());
        roleService.save(role);
        return success();
    }

    @RequestMapping("/toUpdatePage")
    @RequiresPermissions("role:update")
    public String toUpdatePage(Integer id,Model model){
        Role role1 = roleService.getById(id);
        model.addAttribute("role",role1);
        return "admin/role/role_update";
    }

    @ResponseBody
    @RequestMapping("/update")
    public ResponseEntity update(Role role){
        role.setUpdateTime(new Date());
        roleService.updateById(role);
        return success();
    }

    @ResponseBody
    @RequestMapping("/delete")
    @RequiresPermissions("role:delete")
    public ResponseEntity delete(@RequestBody ArrayList<Role> roles){
        try{
            List<String> list = new ArrayList<String>();
            for (Role role : roles) {
                if ("root".equals(role.getRole())) {
                    throw new Exception("root角色不能被删除");
                }
                list.add(role.getId());
            }
            roleService.removeByIds(list);
        } catch (Exception e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
        return success();
    }

    /**
     * 跳转分配权限界面
     */
    @RequestMapping("/toSetMenuPage")
    @RequiresPermissions("role:setMenu")
    public String toSetMenuPage(String id, Model model){
        model.addAttribute("role_id",id);
        return "admin/role/role_setMenu";
    }
    /**
     * 设置权限
     */
    @ResponseBody
    @RequestMapping("/setMenu")
    public ResponseEntity setMenu(String id, @RequestBody ArrayList<Menu> menus){
        roleService.setMenu(id, menus);
        return success();
    }

    /**
     * 查询用户关联的角色列表
     */
    @ResponseBody
    @RequestMapping("/roleList")
    public PageEntity List(String userId, Role role){
        List<UserRole> userRoles = userRoleService.list(new QueryWrapper<UserRole>().eq("user_id", userId));

        QueryWrapper<Role> queryWrapper = new QueryWrapper<Role>();
        if (role!=null){
            if (!StringUtils.isEmpty(role.getRole())) queryWrapper.like("role", role.getRole());
        }
        List<Role> roles = roleService.list(queryWrapper);

        List<JSONObject> list = new ArrayList<JSONObject>();
        // 同样需要对用户已经关联的角色进行勾选，根据layui需要填充一个LAY_CHECKED字段
        for (Role role2 : roles) {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(role2));
            boolean rs = false;
            for (UserRole userRole : userRoles) {
                if (userRole.getRoleId().equals(role2.getId())) {
                    rs = true;
                }
            }
            jsonObject.put("LAY_CHECKED", rs);
            list.add(jsonObject);
        }
        return new PageEntity(list.size(), list);
    }
}
