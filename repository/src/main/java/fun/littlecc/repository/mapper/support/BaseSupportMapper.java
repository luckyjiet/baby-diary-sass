package fun.littlecc.repository.mapper.support;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.littlecc.domain.enums.StatusEnum;
import fun.littlecc.repository.entity.support.BaseSupportEntity;


import java.util.Date;
import java.util.List;

/**
 * @author heyi
 * @date 2019-11-07 18:12
 */

public interface BaseSupportMapper<T extends BaseSupportEntity> extends BaseMapper<T> {

    /**
     * 查询 status 为正常状态的数据
     *
     * @param id
     * @return
     */
    default T getNormalById(Long id) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", StatusEnum.NORMAL.getStatus())
                .eq("id", id);
        return this.selectOne(queryWrapper);
    }

    /**
     * 更新数据
     *
     * @param t
     * @return
     */
    default int update(T t) {
        t.setUpdateTime(new Date());
        int i = this.updateById(t);
        if (i == 0) {

        }
        return i;
    }
}
