# jCasbin Spring Security Plugin

## Spring Security整合Casbin，在Spring Security中加入Casbin的权限角色验证

# [English](./README.md) | 中文

## 安装
目前尚未部署到maven，如要使用，请克隆工程自行在本地打包。

## 使用
1. 下载[model_request.conf](./src/main/resources/conf/model_request.conf)放在`classpath:casbin/model_request.conf`
   或者自己编写model_request.conf来定制模型。如果选择自行定制模型，请参考[配置](#配置)部分在配置文件中指定模型文件所在的路径。
2. 大功告成。 然后Spring Security的鉴权部分就会被jcasbin代替。

## 配置
下面的值为默认值

    spring-security-jcasbin:
      enabled: true //启用jcasbin
      model: classpath:casbin/model_request.conf //模型所在的路径
      synced: false //不使用读写锁(线程不安全)