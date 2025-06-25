# AP站点通网问题

> ubuntu平台下

### ubuntu自带网卡+插入USB网卡

这里采用型号**NT-150M-RTL**USB 无线网卡 支持免驱动

### 启用网卡连接新网段

```bash
sudo ip link set wlx60427f0f918f up     #打开
sudo iwlist wlx60427f0f918f scan        #扫描wifi
sudo iwlist wlx60427f0f918f scan | grep One #指定热点看有没有
sudo dhclient wlx60427f0f918f           #分配ip就可以用了
```

### 启动NAT

启用IP转发

编辑 *****/etc/sysctl.conf** ，取消以下行的注释：
```bash
sudo nano /etc/sysctl.conf
```
找到并修改：
```bash
net.ipv4.ip_forward=1
```
应用配置：
```bash
sudo sysctl -p
```
写一个一键配置脚本，**enxaeb79227ad17**要换成你网卡在ubuntu里显示名称

```bash
sudo iptables -t nat -A POSTROUTING -s 192.168.10.0/24 -o enxaeb79227ad17 -j MASQUERADE
sudo iptables -A FORWARD -i wlo1 -o enxaeb79227ad17 -j ACCEPT
sudo iptables -A FORWARD -i enxaeb79227ad17 -o wlo1 -m state --state RELATED,ESTABLISHED -j ACCEPT
```

### 小车子网问题

修改/etc/network/interfaces/

添加 gateway 192.168.10.238

**192.168.10.238**需要自行修改成小车网段的网关地址
