# 手动启动Nginx说明文档

## 概述

本文档详细说明如何手动启动nginx反向代理服务器，为商城系统提供统一的API入口和静态文件服务。

## 环境准备

### 1. 确认nginx安装
- 安装路径：`D:\softinstall\nginx\nginx-1.22.0-tlias`
- 确认文件：`nginx.exe` 存在于安装目录中
- 确认文件：`conf\mime.types` 存在于nginx安装目录中

### 2. 确认配置文件
- 配置文件：`D:\Code\Mall\nginx\conf\nginx-mall.conf`
- 确认配置文件存在且语法正确

### 3. 确认微服务状态
确保以下微服务正在运行：
- 认证服务：http://127.0.0.1:8081
- 商品服务：http://127.0.0.1:8082
- 用户服务：http://127.0.0.1:8085

## 手动启动步骤

### 步骤1：打开命令提示符
1. 按 `Win + R` 打开运行对话框
2. 输入 `cmd` 并按回车
3. 或者搜索"命令提示符"并打开

### 步骤2：切换到nginx安装目录
```cmd
cd /d D:\softinstall\nginx\nginx-1.22.0-tlias
```

### 步骤3：检查nginx配置语法
```cmd
nginx.exe -t -c "D:\Code\Mall\nginx\conf\nginx-mall.conf"
```
如果显示 "nginx: configuration file ... test is successful"，说明配置正确。

**注意**：如果出现 "mime.types" 文件找不到的错误，说明配置文件中的路径不正确，请检查配置文件中的 `include` 语句。

### 步骤4：检查端口占用
```cmd
netstat -ano | findstr :80
```
如果端口80被占用，需要先停止占用进程。

### 步骤5：启动nginx
```cmd
nginx.exe -c "D:\Code\Mall\nginx\conf\nginx-mall.conf"
```

### 步骤6：验证nginx启动
```cmd
tasklist /fi "imagename eq nginx.exe"
```
如果看到nginx.exe进程，说明启动成功。

### 步骤7：测试nginx功能
在浏览器中访问以下地址进行测试：
- 健康检查：http://localhost/health
- 前端商城：http://localhost
- 管理员后台：http://localhost/admin

## 停止nginx

### 方法1：优雅停止
```cmd
cd /d D:\softinstall\nginx\nginx-1.22.0-tlias
nginx.exe -s stop
```

### 方法2：强制停止
```cmd
taskkill /f /im nginx.exe
```

## 重启nginx

### 方法1：重新加载配置
```cmd
cd /d D:\softinstall\nginx\nginx-1.22.0-tlias
nginx.exe -s reload
```

### 方法2：完全重启
1. 先停止nginx
2. 再重新启动nginx

## 查看nginx状态

### 检查进程
```cmd
tasklist /fi "imagename eq nginx.exe"
```

### 检查端口
```cmd
netstat -ano | findstr :80
```

### 查看日志
nginx日志文件位置：
- 访问日志：`D:\softinstall\nginx\nginx-1.22.0-tlias\logs\mall-access.log`
- 错误日志：`D:\softinstall\nginx\nginx-1.22.0-tlias\logs\mall-error.log`

查看实时日志：
```cmd
cd /d D:\softinstall\nginx\nginx-1.22.0-tlias
type logs\mall-error.log
```

## 解决Nginx进程过多问题

### 问题现象
当执行 `tasklist /fi "imagename eq nginx.exe"` 时，发现多个nginx.exe进程在运行，这通常会导致：
- 端口冲突
- 配置文件不生效
- 服务异常

### 解决步骤

#### 步骤1：检查当前nginx进程
```cmd
tasklist /fi "imagename eq nginx.exe"
```
如果看到多个nginx.exe进程，说明需要清理。

#### 步骤2：强制停止所有nginx进程
```cmd
taskkill /f /im nginx.exe
```
这个命令会强制终止所有nginx.exe进程。

#### 步骤3：确认进程已停止
```cmd
tasklist /fi "imagename eq nginx.exe"
```
应该显示"没有运行的任务匹配指定标准"。

#### 步骤4：检查端口80是否释放
```cmd
netstat -ano | findstr :80
```
如果端口80仍被占用，找到占用进程的PID并终止：
```cmd
taskkill /f /pid [进程ID]
```

#### 步骤5：等待系统清理
```cmd
timeout /t 3
```
等待3秒让系统完全清理进程。

#### 步骤6：使用正确配置启动nginx
```cmd
cd /d D:\softinstall\nginx\nginx-1.22.0-tlias
nginx.exe -c "D:\Code\Mall\nginx\conf\nginx-mall.conf"
```

#### 步骤7：验证启动结果
```cmd
tasklist /fi "imagename eq nginx.exe"
```
应该只看到一个nginx.exe进程。

### 预防措施

#### 1. 使用正确的启动方式
- 始终使用 `-c` 参数指定配置文件
- 避免使用默认配置文件启动
- 启动前检查是否已有nginx进程

#### 2. 创建启动脚本
创建批处理文件 `start-nginx.bat`：
```batch
@echo off
echo 停止所有nginx进程...
taskkill /f /im nginx.exe >nul 2>&1
timeout /t 2 >nul

echo 启动nginx...
cd /d D:\softinstall\nginx\nginx-1.22.0-tlias
nginx.exe -c "D:\Code\Mall\nginx\conf\nginx-mall.conf"

echo 检查nginx状态...
tasklist /fi "imagename eq nginx.exe"
pause
```

#### 3. 定期检查进程状态
```cmd
tasklist /fi "imagename eq nginx.exe"
```
如果发现多个进程，及时清理。

### 常见问题排查

#### 问题1：无法停止nginx进程
**解决方案**：
1. 以管理员身份运行命令提示符
2. 使用 `taskkill /f /im nginx.exe`
3. 如果仍然无法停止，重启计算机

#### 问题2：端口80被其他程序占用
**解决方案**：
1. 查找占用进程：`netstat -ano | findstr :80`
2. 终止占用进程：`taskkill /f /pid [进程ID]`
3. 或者修改nginx配置文件使用其他端口

#### 问题3：配置文件不生效
**解决方案**：
1. 确认使用了正确的配置文件路径
2. 检查配置文件语法：`nginx.exe -t -c "配置文件路径"`
3. 重启nginx而不是重新加载

## 故障排除

### 问题1：nginx启动失败
**现象**：执行启动命令后没有反应或报错

**解决方案**：
1. 检查配置文件语法：
   ```cmd
   nginx.exe -t -c "D:\Code\Mall\nginx\conf\nginx-mall.conf"
   ```

2. 检查端口80是否被占用：
   ```cmd
   netstat -ano | findstr :80
   ```

3. 检查nginx安装路径是否正确

4. 查看错误日志：
   ```cmd
   type logs\mall-error.log
   ```

### 问题2：mime.types文件找不到
**现象**：`nginx: [emerg] CreateFile() "mime.types" failed`

**解决方案**：
1. 确认nginx安装目录中存在 `conf\mime.types` 文件
2. 检查配置文件中的 `include` 语句路径是否正确
3. 使用绝对路径指定mime.types文件位置

### 问题3：前端访问失败
**现象**：http://localhost 无法访问

**解决方案**：
1. 确认nginx进程正在运行
2. 检查前端文件路径是否正确
3. 查看nginx错误日志

### 问题4：API代理失败
**现象**：API请求返回错误

**解决方案**：
1. 确认所有微服务正在运行
2. 检查nginx配置文件中的代理设置
3. 测试直接访问微服务地址

### 问题5：跨域问题
**现象**：浏览器控制台显示CORS错误

**解决方案**：
1. 确认前端使用正确的API地址（http://localhost/api/...）
2. 检查nginx CORS配置
3. 清除浏览器缓存

## 常用命令总结

| 操作 | 命令 |
|------|------|
| 检查配置 | `nginx.exe -t -c "配置文件路径"` |
| 启动nginx | `nginx.exe -c "配置文件路径"` |
| 停止nginx | `nginx.exe -s stop` |
| 重新加载 | `nginx.exe -s reload` |
| 检查进程 | `tasklist /fi "imagename eq nginx.exe"` |
| 检查端口 | `netstat -ano | findstr :80` |
| 强制停止 | `taskkill /f /im nginx.exe` |
| 停止所有nginx | `taskkill /f /im nginx.exe` |

## 验证清单

启动nginx后，请按以下清单验证功能：

- [ ] nginx进程正在运行（只有一个进程）
- [ ] 端口80被nginx占用
- [ ] http://localhost/health 返回 "healthy"
- [ ] http://localhost 显示前端页面
- [ ] http://localhost/admin 显示管理员后台
- [ ] API代理正常工作（测试任意API接口）
- [ ] 没有CORS错误
- [ ] 日志文件正常生成

## 注意事项

1. **权限问题**：如果遇到权限问题，请以管理员身份运行命令提示符

2. **防火墙**：确保Windows防火墙允许nginx访问网络

3. **杀毒软件**：某些杀毒软件可能会阻止nginx，需要添加例外

4. **端口冲突**：如果端口80被其他程序占用，可以修改nginx配置文件使用其他端口

5. **配置文件路径**：确保配置文件路径使用双引号包围，特别是路径中包含空格时

6. **mime.types文件**：确保nginx安装目录中存在mime.types文件，配置文件中的include路径正确

7. **日志文件**：定期检查日志文件，及时发现问题

8. **进程管理**：定期检查nginx进程数量，避免多个进程冲突

## 联系支持

如果在手动启动过程中遇到问题，请提供：
1. 具体的错误信息
2. nginx错误日志内容
3. 系统环境信息
4. 执行的具体步骤 