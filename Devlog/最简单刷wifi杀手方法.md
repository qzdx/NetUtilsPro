# 简单方法刷esp8266wifi杀手

### 首先安装刷固件工具

```bash
pip install esptool
```

### 然后查看是什么端口

```bash
 ls /dev/cu.*
```

拔出来看一次，对比一下少了哪个就是哪个

```bash
/dev/cu.BeatsSolo		/dev/cu.debug-console
/dev/cu.Bluetooth-Incoming-Port	/dev/cu.OnePlusBuds3
```

这几个都不是

```bash
/dev/cu.usbserial-214
```

这样的一般是

### 下载固件

```url
https://github.com/SpacehuhnTech/esp8266_deauther
```

这个网址,介绍里面选择download进去下载

### 最后刷入

```bash
esptool.py --port /dev/cu.usbserial-2140 \
           --baud 115200 \
           write_flash 0x0 esp8266_deauther_2.6.1_NODEMCU.bin
```

串口要改成自己的，固件也要改
