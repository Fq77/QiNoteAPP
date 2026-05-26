# QiNote

> AI 驱动的智能记账 Android 应用

---

## ✨ 特性

- 🤖 **AI 智能记账** - 通过对话式自然语言描述即可完成记账
- 🎨 **现代 UI 设计** - 基于 Jetpack Compose + Material 3 的精美界面
- 📊 **数据可视化** - 饼图、趋势图等多维度统计展示
- 🏝️ **超级岛支持** - 集成 HyperOS 超级岛实时通知
- 💾 **本地数据存储** - 使用 Room 数据库，数据安全可靠
- 🌙 **深色模式** - 完整的暗色主题支持

---

## 🛠️ 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **架构**: MVVM + Clean Architecture
- **依赖注入**: Hilt
- **数据库**: Room
- **导航**: Navigation Compose
- **网络**: OkHttp + Kotlinx Serialization
- **图片加载**: Coil
- **其他**: 
  - DataStore (偏好设置)
  - Shizuku (系统权限)
  - Hyper Notification (超级岛)

---

## 📱 截图

> 📸 待添加

---

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 11+
- Android SDK 24 (minSdk) - 35 (targetSdk)

### 构建步骤

1. 克隆仓库
```bash
git clone https://github.com/Fq77/QiNoteAPP.git
cd QiNoteAPP
```

2. 打开项目
   - 使用 Android Studio 打开项目
   - 等待 Gradle 同步完成

3. 运行应用
   - 连接 Android 设备或启动模拟器
   - 点击 Run 按钮或执行:
```bash
./gradlew installDebug
```

---

## 📁 项目结构

```
app/src/main/
├── java/com/qinoteapp/qinoteapp/
│   ├── components/       # 可复用 UI 组件
│   ├── data/
│   │   ├── dao/          # Room DAO
│   │   ├── database/     # 数据库配置
│   │   └── entity/       # 数据实体
│   ├── di/               # Hilt 依赖注入模块
│   ├── navigation/       # 导航配置
│   ├── network/          # 网络请求
│   ├── notification/     # 通知相关
│   ├── ui/
│   │   ├── entry/        # 记账录入页面
│   │   ├── home/         # 首页
│   │   ├── settings/     # 设置页
│   │   ├── stats/        # 统计页
│   │   └── theme/        # 主题配置
│   ├── usecase/          # 业务用例
│   ├── util/             # 工具类
│   └── MainActivity.kt
└── res/                  # 资源文件
```

---

## 📄 License

> 待添加

---

<p align="center">
Made with ❤️ using Android & Jetpack Compose
</p>
