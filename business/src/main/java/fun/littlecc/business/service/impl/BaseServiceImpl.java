package fun.littlecc.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import fun.littlecc.business.service.BaseService;
import fun.littlecc.repository.entity.support.BaseSupportEntity;
import fun.littlecc.repository.mapper.support.BaseSupportMapper;

/**
 * @author
 * @date 2020-07-30 13:22
 */
public class BaseServiceImpl<M extends BaseSupportMapper<T>, T extends BaseSupportEntity> extends ServiceImpl<M, T> implements BaseService<T> {

    protected M baseMapper;

    /**
     * 通用更新方法
     *
     * @param t
     * @return
     */
    public int update(T t) {
        return baseMapper.update(t);
    }

}
