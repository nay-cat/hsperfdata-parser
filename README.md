# hsperfdata-parser

`hsperfdata-parser` is an application for parsing and analyzing `hsperfdata` files from a Java application. It can obtain a snapshot of the `hsperfdata` file of a Java process in real-time using its PID or you can import the file manually instead.

> `hsperfdata` is a performance monitoring file that is created when a Java application opens and is deleted once the process terminates.
> In Windows, these files are located inside %temp% in the `hsperfdata_%username%` folder. The name of each file inside it represents the PID of a Java process.

## Pre-requisites
- Java 8

## Usage
```shell
$ java -jar hsperfparser.jar
```

## Screenshots
<img width="1920" height="1040" alt="image" src="https://github.com/user-attachments/assets/0becc353-4217-454f-99b4-728c5cd9610d" />
<img width="1920" height="1040" alt="image" src="https://github.com/user-attachments/assets/efb77b8d-5b4d-496a-ab08-097a10d6aa8b" />
<img width="1920" height="1040" alt="image" src="https://github.com/user-attachments/assets/0f617585-92f3-4a31-9870-88348a073b2b" />
