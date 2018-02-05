[![Build Status](https://travis-ci.org/netshoes/spring-cloud-sleuth-amqp-starter.svg?branch=master)](https://travis-ci.org/netshoes/spring-cloud-sleuth-amqp-starter)
[![Coverage Status](https://coveralls.io/repos/netshoes/spring-cloud-sleuth-amqp-starter/badge.svg?branch=master&service=github)](https://coveralls.io/github/netshoes/spring-cloud-sleuth-amqp-starter?branch=master)
[![License Apache2](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Why?
The project spring-cloud-sleuth does not provided a instrumentation for spring-rabbit. In project 
[spring-cloud-sleuth-amqp](https://github.com/netshoes/spring-cloud-sleuth-amqp) we implemented this 
instrumentation, this project is a starter for it.

# Compatibility
| spring-cloud-sleuth-amqp-starter  | spring-cloud-sleuth | spring-rabbit |
| --------------------------------- | ------------------- | ------------- |
| 0.9                               | 1.2.1.RELEASE       | 1.7.3.RELEASE |

# Usage
Add the following dependency to project:
```
<dependency>
  <groupId>com.netshoes</groupId>
  <artifactId>spring-cloud-sleuth-amqp-starter</artifactId>
  <version>0.9</version>
</dependency>
```

# Disabling auto configuration
If you want disable only this sleuth instrumentation set `spring.sleuth.amqp.enabled` to `false`.


# Contributing
Pull request are welcome. This project is not supported by Spring Framework Team and has its own rules:
* Use [google-java-format](https://github.com/google/google-java-format) to format your code.