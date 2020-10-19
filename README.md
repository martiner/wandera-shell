# Wandera Shell [![Build Status](https://travis-ci.org/martiner/wandera-shell.png?branch=master)](https://travis-ci.org/martiner/wandera-shell)

## Use

1. download from [releases](https://github.com/martiner/wandera-shell/releases)
1. run `java --add-opens java.base/java.lang=ALL-UNNAMED wandera-shell.jar`
1. type `key add --name master --api xxx --secret yyy` with correct values
1. type `get https://foo/bar` to perform request with Wandera Auth
1. type `help` to get help

## Build

```
mvn package
```

## Develop

```
mvn spring-boot:run
```