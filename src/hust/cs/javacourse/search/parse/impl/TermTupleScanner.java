package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class TermTupleScanner extends AbstractTermTupleScanner {
    public TermTupleScanner(BufferedReader input) {
        super(input);
    }

    int pos;
    Queue<AbstractTermTuple> buffer = new LinkedList<>();

    /**
     * 获得下一个三元组
     *
     * @return: 下一个三元组；如果到了流的末尾，返回null
     */
    @Override
    public AbstractTermTuple next() throws IOException {
        if (buffer.isEmpty()) {
            String string = input.readLine();
            if (string == null) {
                return null;
            }
            while (string.trim().length() == 0) {
                string = input.readLine();
                if (string == null) {
                    return null;
                }
            }
            StringSplitter stringSplitter = new StringSplitter();
            stringSplitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
            for (String word : stringSplitter.splitByRegex(string)) {
                TermTuple tmpTuple = new TermTuple();
                tmpTuple.curPos = pos;
                if (Config.IGNORE_CASE) {
                    tmpTuple.term = new Term(word.toLowerCase(Locale.ROOT));
                } else {
                    tmpTuple.term = new Term(word);
                }
                buffer.add(tmpTuple);
                pos++;
            }
        }
        return buffer.poll();
    }
}