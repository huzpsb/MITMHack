MITM Hack
--

### 0x00 Abstract

MITM Hack is a PoC tool that allows reach and fly hack on Minecraft servers.  
If set up correctly, it can provide an Internet environment, where, any device connected to the network would hack on a
specific Minecraft server.  
The target client needs no modification at all, and thus this tool bypasses nearly all client-side anti-cheat systems,
like [CAC-Public](https://github.com/Luohuayu/CatAntiCheat-Public/), CiYuan-Free, and SkAc-Alpha.  
This tool wants to raise awareness that, screensharing, client-side anti-cheat, and other methods are NOT effective in
preventing cheating; the only effective way to prevent cheating is to have a server-side anti-cheat. It is unfair and
irresponsible to normal players if you host an event without a server-side anti-cheat, even if you have a client-side
one, or required participants to stream. (You don't have to fly, make it reach-only, or 1.05x speed etc.)

### 0x01 Setup

To run MITM Hack, you need the following tools:

- A router that supports dnsmasq
- A server with JDK21 or above installed
- Minecraft 1.21.1 client

Now, let's get started. Suppose:

- Your target server is (mc.example.com:25565, mc.example.com points to 1.2.3.4)
- Your server's IP is 5.6.7.8
- Your router has DHCP DNS set to itself

Login to your router, and find the dnsmasq configuration. Add the following line:

```
address=/mc.example.com/5.6.7.8
```

Then, restart your router.  
Login to your server, and open this repository. Modify the Main.java file, and change the upstream server to "1.2.3.4:
25565", and the listening port to "25565".  
Then run the Main.java file, with whichever method you prefer.  
All set! Now, any device connected to your network would hack on the target server, without any modification on the
device. ~~Just as B.W.Lu said, "从现在开始，这里叫卢本伟广场。"~~

### 0x02 Screenshots

![pic](./pic/poc.gif)  
It should be noted that this record is made on my own local server.

### 0x03 Other Information

This tool is deliberately flawed, that it only supports 1.21.1, and won't work on online-mode/authlib-injected
servers. (This does not necessarily mean it's safe if your server is not 1.21.1 or online-mode/authlib-injected without
a server-side anti-cheat!)  
This tool is for educational purposes only, do not set it up on networks you do not own or target servers you do not
own.  
Any responsible server-side anti-cheat like [Grim](https://github.com/GrimAnticheat/Grim)
and [UNCP](https://github.com/Updated-NoCheatPlus/NoCheatPlus) shall be able to detect this tool easily.

### 0x04 License

MIT

### 0x05 Credits

Authored by huzpsb  
Do not bother me if you have issues USING this tool. The goal of this tool is to raise awareness, and ultimately, to
prevent cheating from happening. Not to help you cheat.
