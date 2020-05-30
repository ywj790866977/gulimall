package com.yanlaoge.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.yanlaoge.common.utils.PageUtils;
import com.yanlaoge.common.utils.Query;
import com.yanlaoge.gulimall.product.dao.CategoryDao;
import com.yanlaoge.gulimall.product.entity.CategoryEntity;
import com.yanlaoge.gulimall.product.service.CategoryBrandRelationService;
import com.yanlaoge.gulimall.product.service.CategoryService;
import com.yanlaoge.gulimall.product.vo.Catalog2Vo;
import com.yanlaoge.gulimall.product.vo.Catalog3Vo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listByTree() {
        //1.
        List<CategoryEntity> categoryEntities = list();
        //2
        return categoryEntities.stream()
                .peek(menu -> menu.setChildren(getChildren(menu, categoryEntities)))
                .filter(item -> item.getParentCid() == 0)
                .sorted(((o1, o2) -> (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 业务判断
        removeByIds(asList);
//        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public List<Long> findCatelogIds(Long catelogId) {
        ArrayList<Long> list = Lists.newArrayList();
        findParentPath(catelogId, list);
//        findParentPathByWhile(catelogId,list);
        return Lists.reverse(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCascade(CategoryEntity category) {
        updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateByCategoryId(category.getCatId(), category.getName());
            //TODO 其他冗余更新
        }
    }

    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        return this.list(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * 从缓存获取数据
     * 需要解决缓存穿透,雪崩,击穿问题
     *  1.缓存空数据,解决穿透
     *  2.设置随机过期时间,解决雪崩
     *  3.加锁,解决击穿问题
     *  3.1 在查询数据库之前加锁
     *   3.1.1 本地锁, 分布式时存在锁不住问题,一台机器一把锁
     *   3.1.2 redis 分布式锁
     *
     * @return 数据
     */
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {

        String catelogJson = stringRedisTemplate.opsForValue().get("catelogJson");
        if (StringUtils.isEmpty(catelogJson)) {
            // redis 锁
            return getCatalogJsonFromDbWithRedisLock();
            // 本地锁
            // return getCatalogJsonFromDbWithLock();
        }
        return JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
    }

    /**
     * redis分布式锁
     *
     * 问题1: 查询数据出现错误, 然后导致锁没有删掉,造成死锁
     * 解决1: 获取锁成功之后,设置超时时间
     * code: redisTemplate.expire("lock",300,TimeUnit.SECONDS);
     *
     * 问题2: 设置时间之前时程序出错
     * 解决2: 在设置锁的时候, 一并设置超时,让设置操作具有原子性
     * code: Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "11", 300, TimeUnit.SECONDS);
     *
     * 问题3: 设置超时之后,执行超过超时时间,锁释放之后,其他线程又进来了,但是还没执行完, 第一个任务执行完,删除锁,第二个线程的锁就被删除了
     *       1. 业务超时
     *       2. 删除锁时,其他任务在执行
     * 解决3: 设置锁的值设置一个uuid, 在删除的时候,进行判断
     * code1: String uuid = UUID.randomUUID().toString();
     *        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
     * code2: String lockValue = redisTemplate.opsForValue().get("lock");
     *       if(uuid.equals(lockValue)){redisTemplate.delete("lock");}
     *
     * 问题4: 在获取锁进行删除时, 某次获取时间可能比较长, 锁过去了,  然后设置了新值,当我们删除的时候把新值给删除了
     * 解决4: 要让删除操作也具有原子性
     *       1. 使用lua脚本, 脚本执行具有原子性, 获取到值相同, 直接删除,然后才返回给程序
     * code: String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
     *       redisTemplate.execute(new DefaultRedisScript<Integer>(luaScript, Integer.class), Lists.newArrayList("lock"), uuid);
     *       执行返回结果, 1为成功, 0 为失败
     *
     * 问题5: 过去时间的续期
     * 解决5: 设置长时间的超时时间
     *
     * 总结:
     *     设置锁和删除锁, 都需要具有原子性
     *
     * @return 数据
     */
    private Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        //方案1: 设置redis锁
        //Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "11");
        //方案3-1: 设置uuid
        String uuid = UUID.randomUUID().toString();
        //方案2: 设置时进行设置超时时间
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (!lock) {
            //自旋
            return getCatalogJsonFromDbWithRedisLock();
        }
        // 获取到锁
        try {
            Map<String, List<Catalog2Vo>> catelogs = getCatelogFromDb();
            // 进行解锁
            // redisTemplate.delete("lock");
            // 方案3-2: 进行判断是否为自己的锁
            // String lockValue = redisTemplate.opsForValue().get("lock");
            // if(uuid.equals(lockValue)){
            //    redisTemplate.delete("lock");
            // }
            // 方案4: 使用lua脚本删除
            return catelogs;
        } finally {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Lists.newArrayList("lock"),
                    uuid);
        }

    }

    /**
     * 使用 synchronized 加锁
     * 因为springBean是单例的, 当前类只会有一个, 如果是单体情况下,可以锁住
     * 只能有一个能够进入到查询数据库
     * 但是锁之后,不能让后续的人还继续查数据库, 所以需要再次查询redis
     * 问题:
     *   分布式情况下,每个服务,都会加一把锁,并没有真正的锁住
     * @return 数据
     */
    private Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithLock() {
        synchronized (this) {
            String catelogJson = stringRedisTemplate.opsForValue().get("catelogJson");
            if (!StringUtils.isEmpty(catelogJson)) {
                return JSON.parseObject(catelogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
                });
            }
            return getCatelogFromDb();
        }
    }

    private Map<String, List<Catalog2Vo>> getCatelogFromDb() {
        // 查询所有
        List<CategoryEntity> list = this.list();
        List<CategoryEntity> level1Categorys = getCategoryEntities(list, 0L);
        Map<String, List<Catalog2Vo>> catelogFromDb = level1Categorys.stream().collect(Collectors.toMap(v -> v.getCatId().toString(),
                item -> this.getCatelog2Vos(list, item)));
        String s = JSON.toJSONString(catelogFromDb);
        stringRedisTemplate.opsForValue().set("catelogJson", s, 1, TimeUnit.DAYS);
        return catelogFromDb;
    }


    private List<Catalog2Vo> getCatelog2Vos(List<CategoryEntity> list, CategoryEntity entity) {
        List<CategoryEntity> entities = getCategoryEntities(list, entity.getParentCid());
        List<Catalog2Vo> catalog2Vos = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(entities)) {
            catalog2Vos = entities.stream().map(item ->
                    new Catalog2Vo().setId(item.getCatId().toString()).setName(item.getName())
                            .setCatalog1Id(entity.getCatId().toString()).setCatalog3List(this.getCatelog3Vos(list, item))
            ).collect(Collectors.toList());
        }
        return catalog2Vos;
    }

    private List<CategoryEntity> getCategoryEntities(List<CategoryEntity> list, Long parenCid) {
        return list.stream().filter(item -> item.getParentCid().equals(parenCid)).collect(Collectors.toList());
    }

    private List<Catalog3Vo> getCatelog3Vos(List<CategoryEntity> list, CategoryEntity entity) {
        List<CategoryEntity> entities = getCategoryEntities(list, entity.getParentCid());
        List<Catalog3Vo> catalog3Vos = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(entities)) {
            catalog3Vos = entities.stream().map(catelog3 ->
                    new Catalog3Vo().setCatalog2Id(entity.getCatId().toString()).setId(catelog3.getCatId().toString())
                            .setName(catelog3.getName())
            ).collect(Collectors.toList());
        }
        return catalog3Vos;
    }


    private void findParentPathByWhile(Long catelogId, ArrayList<Long> list) {
        list.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        while (categoryEntity.getParentCid() != null && categoryEntity.getParentCid() != 0) {
            list.add(categoryEntity.getParentCid());
            categoryEntity = this.getById(categoryEntity.getParentCid());
        }
    }

    private void findParentPath(Long catelogId, ArrayList<Long> list) {
        list.add(catelogId);
        CategoryEntity categoryEntity = getById(catelogId);
        if (categoryEntity.getParentCid() != null && categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), list);
        }
    }

    public List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> list) {
        return list.stream()
                .filter(item -> item.getParentCid().equals(root.getCatId()))
                .peek(item -> item.setChildren(getChildren(item, list)))
                .sorted(((o1, o2) -> (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort())))
//                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());
    }

}