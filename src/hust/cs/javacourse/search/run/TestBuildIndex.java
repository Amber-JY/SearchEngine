package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.index.impl.DocumentBuilder;
import hust.cs.javacourse.search.index.impl.Index;
import hust.cs.javacourse.search.index.impl.IndexBuilder;
import hust.cs.javacourse.search.util.Config;

import java.io.File;
import java.io.IOException;

/**
 * 测试索引构建
 */
public class TestBuildIndex {
    /**
     *  索引构建程序入口
     * @param args : 命令行参数
     */
    public static void main(String[] args) throws IOException {
        AbstractDocumentBuilder documentBuilder = new DocumentBuilder();
        AbstractIndexBuilder indexBuilder = new IndexBuilder(documentBuilder);
        //定义构建index的文档路径
        String testDocDir = Config.DOC_DIR + "/test/";
        //构建索引。
        System.out.println("Building index...");
        AbstractIndex index = indexBuilder.buildIndex(testDocDir);
        System.out.println("OK. The content is as following:");
        //输出index内容
        System.out.println(index.toString());
        //保存路径
        String indexFile = Config.INDEX_DIR + "index.bat";
        index.save(new File(indexFile));

        //从保存的index中加载，输出对比。
        System.out.println("---------------------------");
        AbstractIndex index_ = new Index();
        System.out.println("Rebuilding index...");
        index_.load(new File(indexFile));
        System.out.println("OK. The content is as following:");
        System.out.println(index_.toString());
    }
}
