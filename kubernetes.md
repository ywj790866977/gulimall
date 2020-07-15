

# Kubernetes

## 一. 准备

### 1.初始化

3台服务器.

- 安装docker和kubeadm
- 关闭selinux
- 关闭防火墙
- 关闭swap ( swapoff -a) (/etc/fstab)

### 2.设置hosts

```shell
# /etc/hosts
10.211.55.21 k8s-node1
10.211.55.22 k8s-node2
10.211.55.23 k8s-node3
```

### 3.设置转发链

```shell
cat > /etc/sysctl.d/k8s.conf << EOF
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF

sysctl --system
```



## 二.安装docker

### 1.卸载

```

```



### 2.安装依赖

```shell
yum install -y yum-utils \
  device-mapper-persistent-data \
  lvm2
```

3.安装docker



```shell
yum-config-manager \
    --add-repo \
    https://download.docker.com/linux/centos/docker-ce.repo
```



```shell
yum install -y docker-ce docker-ce-cli containerd.io
```



4.配置镜像源

```
mkdir -p /etc/docker
tee /etc/docker/daemon.json << EOF
{
  "registry-mirrors":["https://reg-mirror.qiniu.com/"]
}
EOF

systemctl daemon-reload
systemctl restart docker
```



安装k8s

```shell
cat <<EOF > /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
enabled=1
gpgcheck=1
repo_gpgcheck=1
gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
EOF

yum install -y kubelet-1.17.3 kubeadm-1.17.3 kubectl-1.17.3
systemctl enable kubelet && systemctl start kubelet
```



## 三. master初始化

1. master镜像脚本

```shell
#!/bin/bash

images=(
  kube-apiserver:v1.17.3
  kube-proxy:v1.17.3
  kube-controller-manager:v1.17.3
  kube-scheduler:v1.17.3
  coredns:1.6.5
  etcd:3.4.3-0
  pause:3.1
)

for imageName in ${images[@]} ; do
    docker pull registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName
#   docker tag registry.cn-hangzhou.aliyuncs.com/google_containers/$imageName  k8s.gcr.io/$imageName
done

```

2. 初始化

```shell
kubeadm init  \
--kubernetes-version v1.17.3  \
--apiserver-advertise-address=10.211.55.21  \
--image-repository registry.cn-hangzhou.aliyuncs.com/google_containers  \
--service-cidr=10.96.0.0/16 \
--pod-network-cidr=10.244.0.0/16
```

3. 相关目录创建

```
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
  


```

4. 设置网络

```shell
kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml

kubectl  get pods --all-namespaces

```



5. 其他节点执行

```shell
kubeadm join 10.211.55.21:6443 --token 9gaytw.67edj8sjsqm82nux \
    --discovery-token-ca-cert-hash sha256:1d7d3c57dc3f2a4209082b598a1ed00c8b02cd0290b12ffc6e76bb0a37a3a562
```



```shell
watch kubectl get pod -n kube-system -o wide
查看创建状态

```



## 四.操作

### 1.初识

```shell
#创建一个服务
kubectl create deployment tomcat6 --image=tomcat:6.0.53-jre8
kubectl  get all

# 暴露一个服务
kubectl expose deployment tomcat6 --port=80 --target-port=8080 --type=NodePort
kubectl  get svc

#扩容
kubectl scale --replicas=3 deployment tomcat6
```



### 2.高级



```shell
# 创建过程导出为yaml文件
kubectl create deployment tomcat6 --image=tomcat:6.0.53-jre8 --dry-run -o yaml > tomcat.yaml

# 执行创建
kubectl apply -f tomcat.yaml

```





## 三.kubersphere

### 1.安装helm和tiller

1.准备

```shell
#1. 官方安装脚本, 注意下载版本
curl -L https://git.io/get_helm.sh | bash


#2. 自己下载的方式 
wget https://get.helm.sh/helm-v2.15.2-linux-amd64.tar.gz
tar -xf  helm-v2.15.2-linux-amd64.tar.gz
cd linux-amd64
cp helm /usr/local/bin/

```



2. 配置权限相关信息

vim  helm_rabc.yaml

```yaml
apiVersion: v1
kind: ServiceAccount
metadata:
  name: tiller
  namespace: kube-system
---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRoleBinding
metadata:
  name: tiller
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: cluster-admin
subjects:
  - kind: ServiceAccount
    name: tiller
    namespace: kube-system
```

3. 部署

```shell
kubectl apply -f helm_rabc.yaml
```

4. 初始化

```shell
# 和上面helm版本一样
helm init --service-account=tiller --tiller-image=sapcc/tiller:v2.16.9   --history-max 300
```



### 2.安装openeds



1. 查询是否有taints

```shell
[root@k8s-node1 ~]# kubectl describe node k8s-node1 | grep Taint
Taints:             node-role.kubernetes.io/master:NoSchedule
```

2. 清除

```shell
kubectl taint nodes k8s-node1  node-role.kubernetes.io/master:NoSchedule-
```

3. 创建名称空间

```shell
 kubectl create ns openebs
```

4. 安装openeds

```shell
helm install --namespace openebs --name openebs stable/openebs --version 1.5.0
```

5. 设置默认storegeClass

```shell
kubectl patch storageclass openebs-hostpath -p '{"metadata": {"annotations":{"storageclass.kubernetes.io/is-default-class":"true"}}}'


kubectl get sc

NAME                         PROVISIONER                                                RECLAIMPOLICY   VOLUMEBINDINGMODE      ALLOWVOLUMEEXPANSION   AGE
openebs-device               openebs.io/local                                           Delete          WaitForFirstConsumer   false                  4m19s
openebs-hostpath (default)   openebs.io/local                                           Delete          WaitForFirstConsumer   false                  4m19s
openebs-jiva-default         openebs.io/provisioner-iscsi                               Delete          Immediate              false                  4m19s
openebs-snapshot-promoter    volumesnapshot.external-storage.k8s.io/snapshot-promoter   Delete          Immediate              false                  4m19s
```

6. 添加taints

```shell
kubectl taint nodes k8s-node1 node-role.kubernetes.io/master=:NoSchedule
```



nfs

https://kubesphere.com.cn/forum/d/1272-kubeadm-k8s-kubesphere-2-1-1

### 3.安装kubersphere



最小安装

```
kubectl apply -f https://raw.githubusercontent.com/kubesphere/ks-installer/master/kubesphere-minimal.yaml
```



```yaml
---
apiVersion: v1
kind: Namespace
metadata:
  name: kubesphere-system

---
apiVersion: v1
data:
  ks-config.yaml: |
    ---

    persistence:
      storageClass: ""

    etcd:
      monitoring: False
      endpointIps: 192.168.0.7,192.168.0.8,192.168.0.9
      port: 2379
      tlsEnable: True

    common:
      mysqlVolumeSize: 20Gi
      minioVolumeSize: 20Gi
      etcdVolumeSize: 20Gi
      openldapVolumeSize: 2Gi
      redisVolumSize: 2Gi

    metrics_server:
      enabled: True

    console:
      enableMultiLogin: False  # enable/disable multi login
      port: 30880

    monitoring:
      prometheusReplicas: 1
      prometheusMemoryRequest: 400Mi
      prometheusVolumeSize: 20Gi
      grafana:
        enabled: False

    logging:
      enabled: False
      elasticsearchMasterReplicas: 1
      elasticsearchDataReplicas: 1
      logsidecarReplicas: 2
      elasticsearchMasterVolumeSize: 4Gi
      elasticsearchDataVolumeSize: 20Gi
      logMaxAge: 7
      elkPrefix: logstash
      containersLogMountedPath: ""
      kibana:
        enabled: False

    openpitrix:
      enabled: False

    devops:
      enabled: False
      jenkinsMemoryLim: 2Gi
      jenkinsMemoryReq: 1500Mi
      jenkinsVolumeSize: 8Gi
      jenkinsJavaOpts_Xms: 512m
      jenkinsJavaOpts_Xmx: 512m
      jenkinsJavaOpts_MaxRAM: 2g
      sonarqube:
        enabled: False
        postgresqlVolumeSize: 8Gi

    servicemesh:
      enabled: False

    notification:
      enabled: False

    alerting:
      enabled: False

kind: ConfigMap
metadata:
  name: ks-installer
  namespace: kubesphere-system

---
apiVersion: v1
kind: ServiceAccount
metadata:
  name: ks-installer
  namespace: kubesphere-system

---
apiVersion: rbac.authorization.k8s.io/v1
kind: ClusterRole
metadata:
  creationTimestamp: null
  name: ks-installer
rules:
- apiGroups:
  - ""
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - apps
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - extensions
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - batch
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - rbac.authorization.k8s.io
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - apiregistration.k8s.io
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - apiextensions.k8s.io
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - tenant.kubesphere.io
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - certificates.k8s.io
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - devops.kubesphere.io
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - monitoring.coreos.com
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - logging.kubesphere.io
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - jaegertracing.io
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - storage.k8s.io
  resources:
  - '*'
  verbs:
  - '*'
- apiGroups:
  - admissionregistration.k8s.io
  resources:
  - '*'
  verbs:
  - '*'

---
kind: ClusterRoleBinding
apiVersion: rbac.authorization.k8s.io/v1
metadata:
  name: ks-installer
subjects:
- kind: ServiceAccount
  name: ks-installer
  namespace: kubesphere-system
roleRef:
  kind: ClusterRole
  name: ks-installer
  apiGroup: rbac.authorization.k8s.io

---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ks-installer
  namespace: kubesphere-system
  labels:
    app: ks-install
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ks-install
  template:
    metadata:
      labels:
        app: ks-install
    spec:
      serviceAccountName: ks-installer
      containers:
      - name: installer
        image: kubesphere/ks-installer:v2.1.1
        imagePullPolicy: "Always"
```



查看安装进度

```shel
kubectl logs -n kubesphere-system $(kubectl get pod -n kubesphere-system -l app=ks-install -o jsonpath='{.items[0].metadata.name}') -f
```



```shell
Console: http://10.211.55.21:30880
Account: admin
Password: P@88w0rd
```

