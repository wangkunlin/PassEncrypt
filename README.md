# 使用方法

最早是基于 AGP 3.6.1 开发，已经适配 4.0.1

## google-services

如果需要使用 firebase 相关功能，则需要添加此插件

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

用于编译时自动生成签名文件（如果不存在），现已支持不用在 build.gradle 中配置签名信息了，三个字段会随机生成

root build.gradle

```groovy
buildscript {

    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.wcg.keystore.generator:keystore-generator:0.0.1'
    }
}
```
app build.gradle

```groovy
apply plugin: 'com.android.application' // required
apply plugin: 'com.wcg.keystore-generator'
```

## manifest-editor

可以在编译时修改 manifest 文件

root build.gradle

```groovy
buildscript {

    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.wcg.manifest.editor:manifest-editor:0.0.3'
    }
}
```
app build.gradle

```groovy
apply plugin: 'com.android.application' // required
apply plugin: 'com.wcg.manifest-editor'

editManifest {
    application {
    	// 移除 com.spoon.pass.passencode.MainActivity
        remove 'activity', 'name', 'com.spoon.pass.passencode.MainActivity'
    }
}
```

## proguard-dictionary

可以在编译时动态生成混淆字典

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
}
```

## apk-reinforce

提供 apk 打包后的 [AndResGuard](https://github.com/shwenzhang/AndResGuard/blob/master/README.zh-cn.md) 和 腾讯乐固 加固功能

root build.gradle

```groovy
buildscript {

    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.wcg.apk.reinforce:reinforce:0.0.13'
    }
}
```
app build.gradle

```groovy
apply plugin: 'com.android.application' // required
apply plugin: 'com.wcg.apk-reinforce'

reinforce {
    enable true // 默认关闭
    sid 'tencent.sid'
    skey 'tencent.skey'

    resguard {
        enable true
        config 'config.xml' // resguard 配置文件
    }
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
        classpath 'com.wcg.field.encrypt:rename-package:0.0.2'
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

