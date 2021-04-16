package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;

import java.io.IOException;
import java.util.regex.Pattern;

public class PatternTermTupleFilter extends AbstractTermTupleFilter {

    public PatternTermTupleFilter(AbstractTermTupleStream input){super(input);}

    /**
     * 过滤掉
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() throws IOException {
        return null;
    }
}
