package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.Sort;

import java.util.List;
import java.util.Map;

//实现sort接口所有方法的基本排序器
public class SimpleSorter implements Sort {
    /**
     * 对命中结果集合根据文档得分排序
     *
     * @param hits ：命中结果集合
     */
    @Override
    public void sort(List<AbstractHit> hits) {
        hits.sort(AbstractHit::compareTo);
    }

    /**
     *
     * 使用频率作为得分依据，计算命中文档的得分, 作为命中结果排序的依据.
     * 排序结果应当时从大到小，将分数取负值，然后使用Collections的排序
     *
     * @param hit ：命中文档
     * @return ：命中文档的得分
     *
     */
    @Override
    public double score(AbstractHit hit) {
        double res = 0;
        for(Map.Entry<AbstractTerm, AbstractPosting> entry : hit.getTermPostingMapping().entrySet()){
            if(entry.getValue() != null){
                res -= entry.getValue().getFreq(); //采用负分数以便排序
            }
        }
        return res;
    }
}
