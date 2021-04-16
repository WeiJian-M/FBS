# FBS

线程一{
while(true){
        synchronized(obj){
           obj.wait(); // 线程一进入等待状态
            发送指令
        }
　  }
}
　
// 某项操作（通、断电或开锁）
其他线程{
    synchronized(obj){
      obj.notify(); // 线程一被唤醒
    }
}


author: MaWeiJian
