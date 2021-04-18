package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;

import java.io.*;

/**
 * 测试搜索
 */
public class TestSearchIndex {
    /**
     *  搜索程序入口
     * @param args ：命令行参数
     */
    public static void main(String[] args) throws IOException {
        String indexFile = Config.INDEX_DIR + "index.bat"; //已经建立index流文件
        String searchResultFile = Config.INDEX_DIR + "searchResult.txt"; //搜索结果目标文件
        String searchWordFile = Config.DOC_DIR + "searchWord.txt"; //检索词文件
        Sort simpleSorter = new SimpleSorter();
        //搜索引擎实例，打开index文件
        AbstractIndexSearcher searcher = new IndexSearcher();
        searcher.open(indexFile);
        FileWriter resultWriter = new FileWriter(new File(searchResultFile));
        BufferedReader searchWordReader = new BufferedReader(new InputStreamReader(new FileInputStream(searchWordFile)));
        String line = "";
        while(true){
            //读取一行
            line = searchWordReader.readLine();
            if(line==null){
                break;
            }
            //
            AbstractHit[] hits = searcher.search(new Term(line), simpleSorter);
            System.out.println("查询单词："+line);
            resultWriter.write("查询单词："+line);
            for(AbstractHit hit : hits){
                System.out.println(hit.toString());
                resultWriter.write(hit.toString());
            }
            System.out.println("---------------");
        }
        searchWordReader.close();
    }
}
