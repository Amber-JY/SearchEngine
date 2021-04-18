package hust.cs.javacourse.search.query.impl;

import hust.cs.javacourse.search.index.AbstractPosting;
import hust.cs.javacourse.search.index.AbstractPostingList;
import hust.cs.javacourse.search.index.AbstractTerm;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.util.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexSearcher extends AbstractIndexSearcher {
    /**
     * 从指定索引文件打开索引，加载到index对象里. 一定要先打开索引，才能执行search方法
     *
     * @param indexFile ：指定索引文件
     */
    @Override
    public void open(String indexFile) {
        this.index = new Index();
        index.load(new File(indexFile));
    }

    /**
     * 根据单个检索词进行搜索
     *
     * @param queryTerm ：检索词
     * @param sorter    ：排序器
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm, Sort sorter) {
        //如果忽略大小写
        if(Config.IGNORE_CASE){
            queryTerm.setContent(queryTerm.getContent().toLowerCase());
        }
        //查找结果为postingList
        AbstractPostingList queryResult = index.search(queryTerm);
        if(queryResult == null){
            return new AbstractHit[0];//为空即未找到
        }
        List<AbstractHit> result = new ArrayList<>();
        //如果找到则将每个posting分别计算分数，加入到Hit结果中
        for(int i=0;i<queryResult.size();i++){
            AbstractPosting posting = queryResult.get(i);
            AbstractHit hit = new Hit(posting.getDocId(), index.getDocName(posting.getDocId()));
            hit.getTermPostingMapping().put(queryTerm, posting);
            //hit.content 还要设置吗？
            //hit.setContent();
            hit.setScore(sorter.score(hit));
            result.add(hit);
        }
        sorter.sort(result);
        //转化为Array
        return result.toArray(new AbstractHit[0]);
    }

    /**
     * 根据二个检索词进行搜索
     *
     * @param queryTerm1 ：第1个检索词
     * @param queryTerm2 ：第2个检索词
     * @param sorter     ：    排序器
     * @param combine    ：   多个检索词的逻辑组合方式
     * @return ：命中结果数组
     */
    @Override
    public AbstractHit[] search(AbstractTerm queryTerm1, AbstractTerm queryTerm2, Sort sorter, LogicalCombination combine) {
        //是否忽略大小写
        if(Config.IGNORE_CASE){
            queryTerm1.setContent(queryTerm1.getContent().toLowerCase());
            queryTerm2.setContent(queryTerm2.getContent().toLowerCase());
        }
        AbstractPostingList queryResult1 = index.search(queryTerm1);
        AbstractPostingList queryResult2 = index.search(queryTerm2);
        //如果两个检索词都未找到，返回空
        if(queryResult1 == null && queryResult2 == null){
            return new AbstractHit[0];
        }
        List<AbstractHit> result = new ArrayList<>();
        if(combine == LogicalCombination.AND){
            //如果有一个为空
            if(queryResult1 == null || queryResult2 == null){
                return new AbstractHit[0];
            }
            //将交集中的每个posting分别计算分数，加入到Hit结果中
            for(int i=0;i<queryResult1.size();i++){
                AbstractPosting posting = queryResult1.get(i);
                if(queryResult2.indexOf(posting) == -1){
                    continue;
                }
                AbstractHit hit = new Hit(posting.getDocId(), index.getDocName(posting.getDocId()));
                hit.getTermPostingMapping().put(queryTerm1, posting);
                hit.setScore(sorter.score(hit));
                result.add(hit);
            }
        }else{//如果是或关系，只有一个不为空，相当于单个搜索；否则求并集
            if(queryResult1 == null){
                return search(queryTerm2, sorter);
            }
            if(queryResult2 == null){
                return search(queryTerm1, sorter);
            }
            //不是单纯求并集，相同的部分添加两个映射，这样计算分数会更高
            for(int i=0;i<queryResult1.size();i++){
                AbstractPosting posting = queryResult1.get(i);
                AbstractHit hit = new Hit(posting.getDocId(), index.getDocName(posting.getDocId()));
                if(queryResult2.indexOf(posting) != -1){
                    hit.getTermPostingMapping().put(queryTerm2, posting);
                }
                hit.getTermPostingMapping().put(queryTerm1, posting);
                hit.setScore(sorter.score(hit));
                result.add(hit);
            }
            for(int i=0;i<queryResult2.size();i++){
                AbstractPosting posting = queryResult2.get(i);
                if(queryResult1.indexOf(posting) == -1){//只需加入queryResult1中不存在的部分即可
                    AbstractHit hit = new Hit(posting.getDocId(), index.getDocName(posting.getDocId()));
                    hit.getTermPostingMapping().put(queryTerm2, posting);
                    hit.setScore(sorter.score(hit));
                    result.add(hit);
                }
            }
        }
        sorter.sort(result);
        //转化为Array
        return result.toArray(new AbstractHit[0]);
    }
}
