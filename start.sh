#! /bin/bash

mvn install

cd weixin
mvn spring-boot:start

cd ../subscribe
mvn spring-boot:start

cd ../unsubscribe
mvn spring-boot:start

cd ../self_menu
mvn spring-boot:start