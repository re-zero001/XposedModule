## Xposed module template with api 82

## Introduction
- app/src/main/assets/xposed_init 内为 xposed 入口点的 hook 类
- app/src/main/res/values/array.xml 内为 LSPosed 框架的推荐作用域
- app/src/main/res/values/strings.xml 为包名
- app/src/main/AndroidManifest.xml 内有软件名字以及模块描述
- 使用 [lsparanoid](https://github.com/LSPosed/LSParanoid) 混淆
- 编译 release 版本后生成的 apk 自动复制到项目根目录的 release 文件夹
