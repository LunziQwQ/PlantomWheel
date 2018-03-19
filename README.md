# PlantomWheel
A plantomGo AI with javafx  
基于策略的幻影围棋AI

> 中国大学生计算机博弈大赛参赛程序  
> 2016年版本获三等奖  
> 2017年版本获二等奖  

`创建于2017年2月`  

---

## 说明
使用了JavaFx实现的窗口应用程序，由于使用了JavaFx以及Lambda等新特性，您需要 **Java9或以上** 的Java环境来运行。  

**运行程序请直接下载解压Release.zip。 命令行使用：**  

```
java -jar PlantomWheel.jar
```

## 操作介绍
* **AI Mode**：运行AI，点击后可选择AI为黑方或白方。
	* **GetStep**：点击后AI生成下一步的坐标，等待是否合法的反馈。
	* **Legal**：点击后反馈上一步为合法。
	* **Illegal**：点击后反馈上一步为非法。  
	* **Capture**：提子，点击后进入选取模式，选取被提的子后点击Finish确定。  
	* **Review history**：勾选后消息栏变为本局历史操作，可点击查看任意一条操作（并不改变当前棋局），取消勾选后恢复消息栏。
	* **Apply**：点击后将当前选中的历史操作应用为当前棋局。
* **Replay Mode**：可读取本软件生成的棋谱，回放任何一步。Replay Mode中无法更改棋谱，AI引擎并不运行。   
