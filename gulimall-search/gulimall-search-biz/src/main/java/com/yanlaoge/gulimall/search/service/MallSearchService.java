package com.yanlaoge.gulimall.search.service;

import com.yanlaoge.gulimall.search.vo.SearchParamVo;
import com.yanlaoge.gulimall.search.vo.SearchResponseVo;

import java.util.List;

/**
 * seachService
 *
 * @author rubyle
 */
public interface MallSearchService {

    /**
     * 检索
     *
     * @param vo 参数
     * @return 集合
     */
    List<SearchResponseVo> search(SearchParamVo vo);
}
