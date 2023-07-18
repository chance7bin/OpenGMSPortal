# OpenGMSPortal



## 项目介绍

> OpenGMS为地理分析模型研究共享建模与模拟资源。此外，平台还为不同领域的研究人员提供了一个虚拟的协作社区。通过在网络环境下进行开放的分布式资源共享和协作，可以促进开放式地理建模和模拟，实现更广泛的参与和探索。





## 系统架构

![image-20230619160957767](https://cdn.jsdelivr.net/gh/chance7bin/img-repo@main/blog/202306/202306191630294.png)



## 中台管理系统

> 提供包括模型服务可用性检查、版本审核、基于RBAC的用户权限控制、服务调用记录、服务器监控等一系列功能

![](https://blog-images-1301988137.cos.ap-nanjing.myqcloud.com/blog/2207/202307181652022.png)


## 基础设施

> 为满足分布式网络环境下地理模拟有关资源接入、数据传输、集群运算等需求。设计了不同计算层面，以接入不同地理模拟资源。
>
> **相关服务器：**
>
> [模型服务容器](https://gitee.com/geomodeling/GeoModelServiceContainer)
>
> [管理服务器](https://gitee.com/chen7bin/manager-server)
>
> [任务服务器](https://gitee.com/geomodeling/GeoModelTaskServer)

![image-20230619161418173](https://cdn.jsdelivr.net/gh/chance7bin/img-repo@main/blog/202306/202306191630513.png)



