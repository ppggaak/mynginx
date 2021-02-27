# mynginx
android nginx
新版本修改了Android 10以后无法运行的问题。
html.zip自动解压到手机的sdcard目录，这是nginx的配置文件。
请尝试着修改配置文件。

android版本的nginx，debug目录的apk已经测试可以使用。


apk运行之后点击start就可以在局域网内访问http://ip:8080.
stop按键暂时没有用处。

共享目录是sdcard/nginx/html
这个目录下的文件可以在浏览器里面直接访问，但是网页还没有刷新文件。

目前只支持arm系列的芯片。
