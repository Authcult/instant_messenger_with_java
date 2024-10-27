# instant_messenger_with_java
- **项目名称**：基于Java的即时通讯软件
一个类似于QQ的即时通讯软件，涉及到服务端和客户端的设计、网络协议的实现、消息存储与转发、用户管理等多个方面。

### 开发计划与时间表

| 阶段             | 目标描述                                           | 开始日期     | 结束日期     | 负责人        |
|------------------|----------------------------------------------------|--------------|--------------|---------------|
| **系统设计** | 架构设计、数据库设计、模块划分、接口定义             |   |   |    |
| **服务端开发** | 实现用户管理、消息转发、文件传输等功能               |    |   | |
| **客户端开发** | 开发桌面端和移动端的UI界面及核心逻辑                 |   |    | |
| **测试**     | 功能测试、性能测试、安全测试、Bug修复               |    |    |    |


### 项目目标

1. **用户管理**：实现用户注册、登录、好友关系管理、黑名单等功能。
2. **消息管理**：支持文本、图片、文件的即时发送和接收，保障高并发情况下的系统稳定性。
3. **聊天管理**：支持一对一聊天，具备推送未读消息功能。
4. **数据加密与隐私保护**：采用数据加密技术，确保用户数据安全和隐私保护。
5. **跨平台兼容**：提供桌面版客户端（JavaFX）。
   
### 一、项目架构设计

1. **服务端**
   - **框架**：Spring Boot、Netty（WebSocket支持）、Spring Cloud（微服务架构）
   - **数据库**：MySQL（关系型数据）、Redis（缓存）
   - **消息队列**：Kafka（高效的消息转发）
   - **安全**：JWT（用户认证）、Spring Security（用户权限管理）
   - 服务端主要负责用户的注册、登录、认证、消息转发、好友管理等功能。
   - 使用数据库来存储用户信息、聊天记录等。
   

3. **客户端**
   - **桌面端**：JavaFX（UI框架）、OkHttp（HTTP请求）、Java-WebSocket库（WebSocket）
   - 客户端负责用户界面的展示，消息的发送和接收。
   - 实现消息加密、消息队列、图片/文件发送等功能。
   - 提供好友管理、聊天管理、通知推送等功能。

5. **通信协议**
   - **数据传输**：Protocol Buffers或JSON
   - **日志管理**：SLF4J + Logback
   - **加密**：AES/RSA
   - 采用WebSocket协议实现实时通信，确保消息能即时发送和接收。
   - 通过RESTful API处理用户注册、登录、好友请求等操作。

### 二、技术栈与第三方库

1. **服务端**
   - **Java开发框架**：Spring Boot/Spring Cloud（用于微服务架构）、Netty（用于处理WebSocket通信）、MyBatis或JPA（用于数据库访问）
   - **数据库**：MySQL（存储用户信息、消息记录等）
   - **消息队列**：RabbitMQ或Kafka（用于处理大量的消息转发）
   - **分布式和负载均衡**：Nginx（反向代理和负载均衡）、Docker（容器化服务）、Kubernetes（服务编排和扩展）
   - **其他工具**：JWT（用于用户认证和授权）、Spring Security（用于用户登录认证）

2. **客户端**
   - **开发框架**：JavaFX（桌面客户端）
   - **网络通信**：OkHttp（用于HTTP请求）、Java-WebSocket库（用于WebSocket通信）
   - **UI框架**：JavaFX或Swing（桌面应用的UI），或Jetpack Compose（Android UI）
   - **其他库**：Gson或Jackson（用于JSON解析）、Glide或Picasso（用于图片加载）

3. **公共**
   - **消息加密**：使用AES或RSA对消息内容进行加密，保护用户隐私。
   - **日志管理**：Logback或SLF4J（用于记录服务日志）
   - **编解码**：Protocol Buffers或JSON（用于传输的数据编码格式）

### 三、设计方案与技术思路

1. **用户管理**
   - 用户注册、登录和退出，采用JWT来保证用户会话安全。
   - 支持用户添加好友、删除好友以及黑名单等操作。

2. **消息管理**
   - 使用WebSocket协议实现实时消息通信。客户端与服务端建立WebSocket连接，进行消息的推送与接收。
   - 消息类型可以包括文本、图片、视频等，根据需要设计消息格式。
   - 服务端接收到消息后，将消息转发至目标用户的WebSocket连接或暂存（若用户不在线）。

3. **好友管理**
   - 提供搜索、添加、删除好友的功能。
   - 支持好友分组，使用数据库存储好友关系信息。

4. **聊天管理**
   - 实现一对一聊天、群组聊天和聊天室功能。
   - 群组和聊天室的消息推送需要做分发管理，服务端要记录群组成员的信息并推送消息。

5. **文件传输**
   - 图片和文件消息可以采用HTTP上传的方式，服务端存储文件并返回URL。
   - 客户端可通过URL进行文件或图片加载。

6. **离线消息**
   - 使用Redis等缓存存储离线消息，当用户重新上线时推送未读消息。

7. **推送服务**
   - 待定

### 四、代码实现思路

1. **服务端（Spring Boot + Netty）**
   - 使用Spring Boot搭建基本的服务端框架，Netty处理WebSocket通信。
   - 定义消息处理器，对不同类型的消息进行处理（如聊天、好友请求等）。
   - 使用数据库存储用户、好友关系、消息记录等信息。

2. **客户端（JavaFX/Android）**
   - 创建登录、好友管理、聊天界面等模块。
   - 实现与服务端的通信模块，处理WebSocket连接和消息接收。
   - 为界面设计消息队列来管理消息顺序，保证用户体验。





