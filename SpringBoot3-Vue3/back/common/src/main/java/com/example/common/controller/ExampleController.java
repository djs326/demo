package com.example.common.controller;

import com.example.common.model.Result;
import com.example.common.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/example")
@Tag(name = "示例接口", description = "提供示例相关的 API 接口")
public class ExampleController {

    @GetMapping("/hello")
    @Operation(summary = "Hello World 接口", description = "返回 Hello World 字符串")
    public Result<String> hello() {
        return Result.success("Hello World!");
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详情")
    public Result<User> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        User user = new User();
        user.setId(id);
        user.setUsername("testUser" + id);
        user.setEmail("test" + id + "@example.com");
        return Result.success(user);
    }

    @GetMapping("/users")
    @Operation(summary = "获取用户列表", description = "获取所有用户列表")
    public Result<List<User>> getUserList() {
        List<User> users = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            User user = new User();
            user.setId(i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            users.add(user);
        }
        return Result.success(users);
    }

    @PostMapping("/user")
    @Operation(summary = "创建用户", description = "创建一个新用户")
    public Result<User> createUser(
            @Parameter(description = "用户信息", required = true)
            @RequestBody User user) {
        user.setId(System.currentTimeMillis());
        return Result.success(user);
    }

    @PutMapping("/user/{id}")
    @Operation(summary = "更新用户", description = "根据用户ID更新用户信息")
    public Result<User> updateUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "用户信息", required = true)
            @RequestBody User user) {
        user.setId(id);
        return Result.success(user);
    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        return Result.success();
    }
}
