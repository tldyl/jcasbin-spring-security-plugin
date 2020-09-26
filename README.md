# jCasbin Spring Security Plugin

## Spring Security integrates Casbin, added casbin's permission role verification in spring security

# English | [中文](./README_CN.md)

## Reference
Currently not published to maven, if you want to use it anyway, please clone this repository and run `mvn package` locally.

## Getting Started
1. Download [model_request.conf](./src/main/resources/conf/model_request.conf) put into `classpath:casbin/model_request.conf`
or write your own model_request.conf to customize the model. If you choose to customize the model, please refer to the [configuration](#Configuration) section to specify the path of the model file in the configuration file.
2. That's all. Then the authentication part of spring security will be replaced by jcasbin.

## Configuration
The following values ​​are default values

    spring-security-jcasbin:
      enabled: true //Enabled jcasbin
      model: classpath:casbin/model_request.conf //madel path
      synced: false //disable read-write locks(thread unsafe)