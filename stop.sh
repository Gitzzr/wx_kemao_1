#! /bin/bash


cd weixin
mvn spring-boot:stop

cd ../subscribe
mvn spring-boot:stop

cd ../unsubscribe
mvn spring-boot:stop

cd ../self_menu
mvn spring-boot:stop