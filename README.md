# 谷粒商城
视频问题集数:
```$xslt
161,162,176,178,182,180,184,188,186
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

elasticsearch mappings
PUT product
```json
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
        "type": "keyword"
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
        "type": "keyword"
      },
      "brandImg":{
        "type": "keyword"
      },
      "catalogName":{
        "type": "keyword"
      },
      "attrs":{
        "type": "nested",
        "properties": {
          "attrId":{
            "type": "long"
          },
          "attrName":{
            "type": "keyword"
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

```json
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "skuTitle": "Apple"
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
              12,
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
                    "terms": {
                      "attrs.attrValue": [
                        "以官网信息为准"
                      ]
                    }
                  }
                ]
              }
            }
          }
        },
        {
          "term": {
            "hasStock": {
              "value": true
            }
          }
        },
        {
          "range": {
            "skuPrice": {
              "gte": 0,
              "lte": 100000
            }
          }
        }
      ]
    }
  },
  "sort": [
    {
      "skuPrice": {
        "order": "desc"
      }
    }
  ],
  "from": 0,
  "size": 5,
  "highlight": {
    "fields": {
      "skuTitle": {}
    },
    "pre_tags": "<b style='color:red' >",
    "post_tags": "</b>"
  },
  "aggs": {
    "brand_agg": {
      "terms": {
        "field": "brandId",
        "size": 10
      },
      "aggs": {
        "brand_name_agg": {
          "terms": {
            "field": "brandName",
            "size": 10
          }
        },
        "brand_img_agg": {
          "terms": {
            "field": "brandImg",
            "size": 10
          }
        }
      }
    },
    "catalog_agg":{
      "terms": {
        "field": "catalogId",
        "size": 10
      },
      "aggs": {
        "catalog_name_agg": {
          "terms": {
            "field": "catalogName",
            "size": 10
          }
        }
      }
    },
    "attr_agg":{
      "nested": {
        "path": "attr"
      },
      "aggs": {
        "attr_id_agg": {
          "terms": {
            "field": "attrs.attrId",
            "size": 10
          },
          "aggs": {
            "attr_name_agg": {
              "terms": {
                "field": "attrs.attrName",
                "size": 10
              }
            },
            "attr_value_agg":{
              "terms": {
                "field": "attrs.attrValue",
                "size": 10
              }
            }
          }
        }
      }
    }
  }
}

```

### 错误编码
```shell script
10 通用
11 商品
12 订单
13 购物车
14 物流
15 用户
16 秒杀
21 库存
```
### rabbitMq
```shell script
 docker run  -d --name rabbitmq -p 5671:5671 -p 5672:5672 -p4369:4369 -p 25672:25672 -p 15671:15671 -p 15672:15672 rabbitmq:management
```

### zipkin
```shell script
docker run -d -p 9411:9411 openzipkin/zipkin
## 持久化容器
docker run --env STORAGE_TYPE=elasticsearch --env=ES_HOSTS=10.211.55.20:9200 openzipkin/zipkin-dependencies
```