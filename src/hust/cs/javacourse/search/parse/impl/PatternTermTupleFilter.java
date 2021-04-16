package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StopWords;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class PatternTermTupleFilter extends AbstractTermTupleFilter {

    public PatternTermTupleFilter(AbstractTermTupleStream input){super(input);}

    /**
     * 过滤掉不符合正则表达式的term，设置见Config
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() throws IOException {
        AbstractTermTuple temTuple = input.next();
        if (temTuple == null){
            return null;
        }
        while(!temTuple.term.getContent().matches(Config.TERM_FILTER_PATTERN)){
            temTuple = input.next();
            if (temTuple == null) {
                return null;
            }
        }
        return temTuple;
    }
}
