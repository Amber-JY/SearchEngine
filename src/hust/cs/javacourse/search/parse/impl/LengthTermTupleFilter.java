package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;

import java.io.IOException;

public class LengthTermTupleFilter extends AbstractTermTupleFilter {
    /**
     *构造函数
     *
     * @param input，filter的输入
     */
    public LengthTermTupleFilter(AbstractTermTupleStream input){super(input);}

    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() throws IOException {
        AbstractTermTuple temTuple = input.next();
        if (temTuple == null){
            return null;
        }
        while(temTuple.term.getContent().length() > Config.TERM_FILTER_MAXLENGTH | temTuple.term.getContent().length()<Config.TERM_FILTER_MINLENGTH){
            temTuple = input.next();
            if (temTuple == null) {
                return null;
            }
        }
        return temTuple;
    }

}
