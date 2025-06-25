# Ubuntu AP站点通网配置指南

## 一、设备准备
- 硬件：Ubuntu系统 + NT-150M-RTL USB无线网卡（支持免驱动）
- 网络拓扑：主网卡(wlo1)连接主网络 / USB网卡(wlx60427f0f918f)连接AP站点

## 二、无线网卡配置

### 1. 启用无线网卡并连接热点
```bash
# 查看网卡名称（示例：enxaeb79227ad17）
ip a

# 启用无线网卡接口
sudo ip link set <网卡名称> up

# 扫描可用WiFi
sudo iwlist <网卡名称> scan

# 检查指定热点是否存在
sudo iwlist <网卡名称> scan | grep One

# 获取IP地址并连接网络
sudo dhclient <网卡名称>
```

## 三、NAT网络地址转换配置

### 1. 开启IP转发功能
```bash
# 编辑系统配置文件
sudo vim /etc/sysctl.conf

# 取消注释并修改
net.ipv4.ip_forward=1

# 应用配置
sudo sysctl -p
```

### 2. 配置iptables规则（一键脚本）
```bash
#!/bin/bash

# 替换为实际网卡名称
INTERFACE="enxaeb79227ad17"

# 清除原有规则（可选）
sudo iptables -F
sudo iptables -X

# 配置NAT规则
sudo iptables -t nat -A POSTROUTING -s 192.168.10.0/24 -o $INTERFACE -j
MASQUERADE
sudo iptables -A FORWARD -i wlo1 -o $INTERFACE -j ACCEPT
sudo iptables -A FORWARD -i $INTERFACE -o wlo1 -m state --state
RELATED,ESTABLISHED -j ACCEPT
```

> ⚠️ 注意：请将`enxaeb79227ad17`替换为实际的网卡名称，该名称可通过`ip a`命
令查看

## 四、小车网段配置

### 1. 修改网络接口配置
```bash
# 编辑配置文件
sudo nano /etc/network/interfaces

# 添加以下内容（根据实际情况修改）
auto eth0
iface eth0 inet static
address 192.168.10.200
netmask 255.255.255.0
gateway 192.168.10.238
```

> ⚠️ 请将`192.168.10.238`替换为实际的网关地址，该地址可通过以下方式获取：
> - 登录路由器管理界面查看
> - 使用`ip route`命令查看默认路由

## 五、验证与排查

### 1. 常用检查命令
```bash
# 查看路由表
ip route

# 查看网络连接状态
ip a

# 查看路由规则
ip rule

# 查看iptables规则
sudo iptables -L -n -v
```

### 2. 常见问题排查
- **无线连接失败**：检查网卡驱动状态（`lsmod | grep rtl`）
- **NAT失效**：确认`net.ipv4.ip_forward=1`已生效
- **路由异常**：检查`/etc/network/interfaces`配置文件
- **防火墙干扰**：临时禁用UFW（`sudo ufw disable`）

## 六、附录

### 1. 网卡名称获取方法
```bash
ip a
```
示例输出：
```
2: enxaeb79227ad17: <BROADCAST,MULTICAST,UP> mtu 1500 qdisc noqueue state
UP
    inet 192.168.10.200/24 brd 192.168.10.255 scope global dynamic
enxaeb79227ad17
```

### 2. 网关地址获取方法
```bash
ip route show default
```
示例输出：
```
default via 192.168.10.238 dev enxaeb79227ad17
```

### 3. 规则备份与恢复
```bash
# 保存当前规则
sudo iptables-save > /etc/iptables/rules.v4

# 恢复规则
sudo iptables-restore < /etc/iptables/rules.v4
```

> 📌 建议在修改配置前备份原始文件，避免配置错误导致网络中断

