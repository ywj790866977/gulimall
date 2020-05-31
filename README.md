# 谷粒商城
视频问题集数:
```$xslt
161,162,176,
```

## 一.初始化

### 1.docker安装

```shell script
yum install -y yum-utils
yum-config-manager     --add-repo     https://download.docker.com/linux/centos/docker-ce.repo
yum install docker-ce docker-ce-cli containerd.io
systemctl enable docker
systemctl restart docker
```
#### 1.1 docker 安装mysql
```shell script
docker images
docker pull mysql:5.7
docker images
docker run -p 3306:3306 --name mysql -v /mydata/mysql/log:/var/log/mysql -v /mydata/mysql/data:/var/lib/mysql -v /mydata/mysql/conf:/etc/mysql -e MYSQL_ROOT_PASSWORD=root -d mysql:5.7

```
修改配置文件
```shell script
cd /mydata/mysql/conf
vim my.cnf
####################################
[client]
default-character-set=utf8
[mysql]
default-character-set=utf8

[mysqld]
default-storage-engine=INNODB
character-set-server=utf8
collation-server=utf8_general_ci
~                                  
```

#### 1.2 docker 安装redis
```shell script
docker pull redis
mkdir -p /mydata/redis/conf
touch /mydata/redis/conf/redis.conf
docker run -p 6379:6379 --name redis -v /mydata/redis/data:/data -v /mydata/redis/conf/redis.conf:/etc/redis/redis.conf -d redis redis-server /etc/redis/redis.conf

vim /mydata/redis/conf/redis.con
appendonly yes

docker restart redis
```
#### 1.3  安装elasticsearch
```shell script
docker pull elasticsearch:7.4.2
docker pull kibana:7.4.2
mkdir /mydata/elasticsearch
mkdir /mydata/elasticsearch/config
mkdir /mydata/elasticsearch/data
mkdir /mydata/elasticsearch/plugins
echo "http.host: 0.0.0.0" >  /mydata/elasticsearch/config/elasticsearch.yml
chmod -R 777 /mydata/elasticsearch

docker run --name elasticsearch -p 9200:9200 -p 9300:9300 \
                      -e "discovery.type=single-node" \
                      -e ES_JAVA_OPTS="-Xms64m -Xmx512m" \
                      -v /mydata/elasticsearch/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml \
                      -v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
                      -v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \
                      -d elasticsearch:7.4.2
```

```shell script
docker run  --name kibana -e ELASTICSEARCH_HOSTS=http://10.211.55.20:9200 -p 5601:5601 \
-d kibana:7.4.2

docker run  --name kibana -e ELASTICSEARCH_HOSTS=http://192.168.52.128:9200 -p 5601:5601 \
-d kibana:7.4.2
```
```

```shell script
## es测试数据
https://raw.githubusercontent.com/elastic/elasticsearch/master/docs/src/test/resources/accounts.json?raw=ture
```

#### 1.4 安装nginx
```shell script
docker run -p 80:80 --name nginx -d  nginx:1.10
mkdir /mydata/nginx
cd /mydata/nginx
docker container cp nginx:/etc/nginx .
mv nginx conf

###
docker run -p 80:80 --name nginx \
-v /mydata/nginx/html:/usr/share/nginx/html \
-v /mydata/nginx/logs:/var/log/nginx \
-v /mydata/nginx/conf:/etc/nginx \
-d  nginx:1.10

```

```shell script
## elasticsearch mappings
PUT product
{
  "mappings": {
    "properties": {
      "skuId":{
        "type": "long"
      },
      "spuId":{
        "type": "keyword"
      },
      "skuTitle":{
        "type": "text",
        "analyzer": "ik_smart"
      },
      "skuPrice":{
        "type": "keyword"
      },
      "skuImg":{
        "type": "keyword",
        "index": false,
        "doc_values": false
      },
      "saleCount":{
        "type": "long"
      },
      "hasStock":{
        "type": "boolean"
      },
      "hotScore":{
        "type": "long"
      },
      "brandId":{
        "type": "long"
      },
      "catalogId":{
        "type": "long"
      },
      "brandName":{
        "type": "keyword",
        "index": false,
        "doc_values": false
      },
      "brandImg":{
        "type": "keyword",
        "index": false,
        "doc_values": false
      },
      "catalogName":{
        "type": "keyword",
        "index": false,
        "doc_values": false
      },
      "attrs":{
        "type": "nested",
        "properties": {
          "attrId":{
            "type": "long"
          },
          "attrName":{
            "type": "keyword",
            "index": false,
            "doc_values": false
          },
          "attrValue" :{
            "type": "keyword"
          }
        }
      }
    }
  }
}
```

```shell script
###
GET game_record_2019_01/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "skuTitle": "华为"
          }
        }
      ],
      "filter": [
        {
          "term": {
            "catalogId": 225
          }
        },
        {
          "terms": {
            "brandId": [
              1,
              3,
              9
            ]
          }
        },
        {
          "nested": {
            "path": "attrs",
            "query": {
              "bool": {
                "must": [
                  {
                    "term": {
                      "attrs.attrId": {
                        "value": "15"
                      }
                    }
                  },
                  {
                    "term": {
                      "attrs.attrValue": [
                        "超哥",
                        "帅气"
                      ]
                    }
                  }
                ]
              }
            }
          }
        }
      ]
    }
  }
}

```