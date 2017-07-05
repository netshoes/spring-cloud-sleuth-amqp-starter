
![Build status](https://travis-ci.org/netshoes/spring-cloud-sleuth-amqp-starter.svg?branch=master "Logo Title Text 1")

# Motivation
The project spring-cloud-sleuth does not provided a instrumentation for spring-rabbit. In this project we implement this instrumentation.

# Compatibility
| spring-cloud-sleuth-amqp-starter  | spring-cloud-sleuth | spring-rabbit |
| --------------------------------- | ------------------- | ------------- |
| 1.0.RELEASE                       | 1.2.1.RELEASE       | 1.7.3.RELEASE |

# Usage
Add the following dependency to project:
```
<dependency>
  <groupId>com.netshoes</groupId>
  <artifactId>spring-cloud-sleuth-amqp-starter</artifactId>
  <version>1.0.RELEASE</version>
</dependency>
```

# Disabling
If you want disable only this sleuth instrumentation set `spring.sleuth.amqp.enabled` to `false`.


# Contributing
Pull request are welcome. This project is not supported by Spring Framework Team and has its own rules:
* Use [google-java-format](https://github.com/google/google-java-format) to format your code.