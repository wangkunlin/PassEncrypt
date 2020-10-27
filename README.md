# 使用方法

基于 AGP 3.6.1 开发，已适配 4.0.1，下面的配置只是把所有的可配置项列出来了，在使用时，不要无脑复制

## google-services

如果需要使用 firebase 相关功能，此插件可以使 debug 打包不解析 google-services.json 文件，而 release 解析

root build.gradle

```groovy
buildscript {

    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.wcg.google.services:google-services:0.0.1'
    }
}
```
app build.gradle

```groovy
apply plugin: 'com.android.application' // required
apply plugin: 'com.wcg.google-services'
```

## keystore-generator

用于编译时自动生成签名文件，app/build.gradle 中不需要配置任何签名信息

root build.gradle

```groovy
buildscript {

    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.wcg.keystore.generator:keystore-generator:0.0.4'
    }
}
```
app build.gradle

```groovy
apply plugin: 'com.android.application' // required
apply plugin: 'com.wcg.keystore-generator'
```

## proguard-dictionary

在编译时动态生成混淆字典

root build.gradle

```groovy
buildscript {

    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.wcg.proguard.dictionary:proguard-dictionary:0.0.3'
    }
}
```
app build.gradle

```groovy
apply plugin: 'com.android.application' // required
apply plugin: 'com.wcg.proguard-dictionary'

dictionary { // 可以不定义这个闭包, count 默认 8000
    count 10000
    fromChars = "1ilILuc" // 指定字生成字典的字符有哪些，不配置随机生成
}
```

## rename-package

用于修改当 [buildType|Flavor] debuggable == false 的 applicationId

root build.gradle

```groovy
buildscript {

    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.wcg.field.encrypt:rename-package:0.0.3'
    }
}
```
app build.gradle

```groovy
apply plugin: 'com.android.application' // required
apply plugin: 'com.wcg.rename-package'

renamePackage {
    applicationId 'com.renamed.test.pkg'
}
```

## string-fog

用于将代码中的字符串混淆

root build.gradle

```groovy
buildscript {

    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.wcg.string.fog:string-fog:0.0.3'
    }
}
```
app build.gradle

```groovy
apply plugin: 'com.android.application' // required
apply plugin: 'com.wcg.string-fog'

stringFog {
    enable = true // 总开关，默认 false
//    password = 'testpsw' // 如果不设置，则使用随机密码，最好随机
    packages = ["com.spoon.pass.passencode"] // 指定哪些包下的类需要处理
    debugEnable = true // 默认 debug 不启用
}
```

## aab-resguard

用于 aab 打包的资源混淆，配置参考[aab-resguard](https://github.com/bytedance/AabResGuard/blob/develop/wiki/zh-cn/README.md)

root build.gradle

```groovy
buildscript {

    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.wcg.aab.resguard:aab-resguard:0.0.1'
    }
}
```
app build.gradle

```groovy
apply plugin: 'com.android.application' // required
apply plugin: 'com.wcg.aab-resguard'

aabResGuard {
    mappingFile = file("mapping.txt").toPath() // 用于增量混淆的 mapping 文件
    whiteList = [ // 白名单规则
        "*.R.raw.*",
        "*.R.drawable.icon"
    ]
    obfuscatedBundleFileName = "duplicated-app.aab" // 混淆后的文件名称，必须以 `.aab` 结尾
    mergeDuplicatedRes = true // 是否允许去除重复资源
    enableFilterFiles = true // 是否允许过滤文件
    filterList = [ // 文件过滤规则
        "*/arm64-v8a/*",
        "META-INF/*"
    ]
    enableFilterStrings = false // 过滤文案
    unusedStringPath = file("unused.txt").toPath() // 过滤文案列表路径 默认在mapping同目录查找
    languageWhiteList = ["en", "zh"] // 保留en,en-xx,zh,zh-xx等语言，其余均删除
}
```

