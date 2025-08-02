# hsperfdata-parser

Application for parsing and analysing a hsperfdata file from a Java application in real time using its PID or directly from a hsperfdata file.

> hsperfdata is a performance monitoring file that is created when a Java application opens, once the process is closed, the file is deleted. 
> The file is located in %temp% in the hsperfdata_%username% folder and the name is the PID of the java process that is running the application. 

> This application allows you to perform a real-time inspection by obtaining a snapshot of the hsperfdata file of a Java process using its PID, alternatively, you can also import a hsperfdata manually

## Basic usage

Java 8

```
java -jar hsperfparser.jar
```

<img width="1920" height="1040" alt="image" src="https://github.com/user-attachments/assets/0becc353-4217-454f-99b4-728c5cd9610d" />
<img width="1920" height="1040" alt="image" src="https://github.com/user-attachments/assets/efb77b8d-5b4d-496a-ab08-097a10d6aa8b" />
<img width="1920" height="1040" alt="image" src="https://github.com/user-attachments/assets/0f617585-92f3-4a31-9870-88348a073b2b" />
