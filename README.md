# MistShoreSMPCA

MistShore 生存服自己用的小插件，专门管重生锚和末影水晶能做的事：

- 改重生锚爆炸伤害，按倍率或者直接给固定值
- 关掉重生锚炸地形
- 禁止合成重生锚 / 末影水晶，自动合成器也一起拦

支持 Paper 和 Folia，1.21.11 ~ 26.2，jar 是按 Java 21 编译的，26.2 服务端跑在 Java 25 上也能直接用

## 安装

jar 丢进 `plugins`，开一次服会生成 `plugins/MistShoreSMPCA/config.yml`，改完执行 `/msca reload` 就行

## 命令

| 命令 | 说明 | 权限 |
| --- | --- | --- |
| `/mistshoresmpca status` | 看当前配置 | `mistshoresmpca.use` |
| `/mistshoresmpca reload` | 重载配置 | `mistshoresmpca.reload` |

别名 `/mssmpca`、`/msca`，权限默认只给 OP

## 配置

```yaml
damage:
  enabled: true
  mode: MULTIPLIER   # 或 FIXED
  value: 0.5

explosion:
  break-terrain: false

crafting:
  block-respawn-anchor: false
  block-end-crystal: false
```

`MULTIPLIER` 是在原版伤害上乘倍率，0.5 就是一半，离得远伤害低这个规律不变
`FIXED` 是把基础伤害定死，2.0 等于一颗心，护甲、保护和抗性照样会减伤

配置写错了 reload 会直接报错，插件继续用上一次能用的配置

## 编译

```
gradle build
```

jar 在 `build/libs` 下面

---

作者 Alizawa，关注Alizawa的B站，并严肃游玩MistShore.net！！！
