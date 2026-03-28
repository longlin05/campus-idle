# 校园闲置物品交易平台

## 项目介绍

校园闲置物品交易平台是一个专为大学生设计的在线交易系统，旨在帮助学生方便地发布、浏览和购买闲置物品，促进校园内的资源循环利用。

## 技术栈

- **后端**：Spring Boot 4.0.4
- **数据库**：MySQL
- **缓存**：Redis
- **认证**：JWT
- **API文档**：SpringDoc
- **ORM**：MyBatis-Plus

## 核心功能

### 用户模块
- 用户注册（支持短信验证码）
- 用户登录（手机号密码/短信验证码）
- 个人信息管理
- 发布商品
- 收藏商品

### 商品模块
- 商品列表查询（支持分页）
- 商品详情查看
- 商品分类查询
- 商品搜索（关键词/分类）
- 商品发布、编辑、下架、删除

### 订单模块
- 订单创建
- 订单状态管理
- 退款申请与处理

### 通知模块
- 系统通知
- 未读通知计数
- 一键已读功能
- 通知管理

### 管理员模块
- 用户管理（查询、禁用/启用）
- 商品管理（更新）
- 分类管理（添加、修改、删除）
- 统计信息（用户总数、商品总数、分类总数）

## 项目结构

```
campus-idle/
├── src/
│   ├── main/
│   │   ├── java/org/lin/campusidle/
│   │   │   ├── common/         # 公共组件
│   │   │   ├── config/         # 配置类
│   │   │   ├── controller/     # 控制器
│   │   │   ├── entity/         # 实体类
│   │   │   ├── mapper/         # 数据访问层
│   │   │   ├── service/        # 业务逻辑层
│   │   │   ├── vo/             # 视图对象
│   │   │   └── CampusIdleApplication.java  # 应用入口
│   │   └── resources/
│   │       └── application.yaml  # 配置文件
│   └── test/                   # 单元测试
├── pom.xml                     # Maven配置
└── README.md                   # 项目说明
```

## 环境要求

- JDK 21+
- Maven 3.9+
- MySQL 8.0+
- Redis 7.0+

## 安装与运行

### 1. 克隆项目

```bash
git clone https://github.com/longlin05/campus-idle.git
cd campus-idle
```

### 2. 配置数据库

1. 创建数据库 `campus_idle`
2. 执行数据库初始化脚本（待提供）

### 3. 配置Redis

确保Redis服务已启动，默认使用 `localhost:6379`

### 4. 修改配置文件

编辑 `src/main/resources/application.yaml` 文件，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_idle?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: your-password
```

### 5. 构建项目

```bash
mvn clean package
```

### 6. 运行项目

```bash
java -jar target/campus-idle-0.0.1-SNAPSHOT.jar
```

项目将在 `http://localhost:8080` 启动

## API文档

项目集成了SpringDoc API文档，访问以下地址查看：

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 管理员账号

系统初始化时会自动创建管理员账号：
- 手机号：`13800138000`
- 密码：`admin123`

## 安全特性

- JWT令牌认证
- 密码MD5加密
- 短信验证码验证
- 管理员权限控制
- 用户状态检查
- Redis缓存防穿透、雪崩、击穿

## 开发规范

- 分层架构：Controller -> Service -> Mapper
- 统一响应格式：使用Result类封装返回结果
- 异常处理：全局异常处理器
- 日志记录：请求日志拦截器
- 代码风格：遵循Java编码规范

## 后续计划

- 前端页面开发
- 支付功能集成
- 消息队列优化
- 性能测试与优化
- 部署上线

## 贡献

欢迎提交Issue和Pull Request，一起完善这个项目！

## 许可证

MIT License
