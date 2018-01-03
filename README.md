#unicontract-app
## SpringMVC-Mybatis
```bash
 # If you want use the Logger.getLogger and the version must use 1.2.14
compile "apache-log4j:log4j:1.2.14"
```
### Log Encoding error for Chinese deal
#### WIN10:
1. add the server VM Arg: `-Dfile.encoding=UTF-8`
2. idea.exe.vmoptions and idea64.exe.vmoptions also add the arg `-Dfile.encoding=UTF-8`
3. if above is not ok, try set the VM Arg `-Dfile.encoding=GB2312`
