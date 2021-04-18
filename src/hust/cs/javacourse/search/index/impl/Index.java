package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.*;

import java.io.*;
import java.util.*;

/**
 * AbstractIndex的具体实现类
 */
public class Index extends AbstractIndex {
    /**
     * 构造函数
     */
    public Index() {}

    /**
     * 返回索引的字符串表示
     *
     * @return 索引的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("docId\t----\tdocPath mapping\n");
        //加入文档id和其对应的文档绝对路径
        for (Map.Entry<Integer, String> entry : docIdToDocPathMapping.entrySet()) {
            builder.append(entry.getKey());//docId
            builder.append("\t--->\t");
            builder.append(entry.getValue());//docPath
            builder.append("\n");
        }
        //加入term和对应的postinglist
        for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
            builder.append(entry.getKey().toString());//term
            builder.append("\t--->\t");
            builder.append(entry.getValue().toString());//postingList
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * 添加文档到索引，更新索引内部的HashMap
     *
     * @param document ：文档的AbstractDocument子类型表示
     */
    @Override
    public void addDocument(AbstractDocument document) {
        //先加入docID到docPath的映射，然后根据其中每一个term进行处理
        docIdToDocPathMapping.put(document.getDocId(), document.getDocPath());
        for(AbstractTermTuple termTuple : document.getTuples()){
            //如果倒排索引中不包含当前term
            if(!termToPostingListMapping.containsKey(termTuple.term)){
                //新加入一个term到postingList的k-v
                AbstractPostingList postingList = new PostingList();
                LinkedList<Integer> linkedList = new LinkedList<>();
                linkedList.add(termTuple.curPos);
                AbstractPosting posting = new Posting(document.getDocId(), termTuple.freq, linkedList);
                postingList.add(posting);
                termToPostingListMapping.put(termTuple.term, postingList);
            }else{
                //如果包含该term，分posting中是否有当前docID来处理
                AbstractPostingList postingList = termToPostingListMapping.get(termTuple.term);
                boolean flag = true;
                for(int i=0;i<postingList.size();i++){
                    //如果有posting的docId 为该document 的docId
                    if(postingList.get(i).getDocId() == document.getDocId()){
                        postingList.get(i).getPositions().add(termTuple.curPos);
                        postingList.get(i).setFreq(postingList.get(i).getFreq()+1);
                        flag = false;
                    }
                }
                if(flag){
                    List<Integer> positions = new ArrayList<>();
                    positions.add(termTuple.curPos);
                    AbstractPosting posting = new Posting(document.getDocId(), termTuple.freq, positions);
                    termToPostingListMapping.get(termTuple.term).add(posting);
                }
            }
            optimize();
        }
    }

    /**
     * <pre>
     * 从索引文件里加载已经构建好的索引.内部调用FileSerializable接口方法readObject即可
     * @param file ：索引文件
     * </pre>
     */
    @Override
    public void load(File file) {
        try {
            readObject(new ObjectInputStream(new FileInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <pre>
     * 将在内存里构建好的索引写入到文件. 内部调用FileSerializable接口方法writeObject即可
     * @param file ：写入的目标索引文件
     * </pre>
     */
    @Override
    public void save(File file) {
        try{
            writeObject(new ObjectOutputStream(new FileOutputStream(file)));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 保存为txt文件，用于提交检查
     *
     * @param file
     */
    public void saveAsText(File file){
        try{
            FileWriter fw = new FileWriter(file);
            fw.write(toString());
            fw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 返回指定单词的PostingList
     *
     * @param term : 指定的单词
     * @return ：指定单词的PostingList;如果索引字典没有该单词，则返回null
     */
    @Override
    public AbstractPostingList search(AbstractTerm term) {
        return termToPostingListMapping.get(term);
    }

    /**
     * 返回索引的字典.字典为索引里所有单词的并集
     *
     * @return ：索引中Term列表
     */
    @Override
    public Set<AbstractTerm> getDictionary() {
        return termToPostingListMapping.keySet();
    }

    /**
     * <pre>
     * 对索引进行优化，包括：
     *      对索引里每个单词的PostingList按docId从小到大排序
     *      同时对每个Posting里的positions从小到大排序
     * 在内存中把索引构建完后执行该方法
     * </pre>
     */
    @Override
    public void optimize() {
        for (Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()) {
            for (int i = 0; i < entry.getValue().size(); i++) {
                entry.getValue().get(i).sort();//内部position排序
            }
            entry.getValue().sort();//根据文档id对posting排序
        }
    }

    /**
     * 根据docId获得对应文档的完全路径名
     *
     * @param docId ：文档id
     * @return : 对应文档的完全路径名
     */
    @Override
    public String getDocName(int docId) {
        return docIdToDocPathMapping.get(docId);
    }

    /**
     * 写到二进制文件
     *
     * @param out :输出流对象
     */
    @Override
    public void writeObject(ObjectOutputStream out) {
        try{
            out.writeObject(docIdToDocPathMapping.size());
            for(Map.Entry<Integer, String> entry : docIdToDocPathMapping.entrySet()){
                out.writeObject(entry.getKey());
                out.writeObject(entry.getValue());
            }
            out.writeObject(termToPostingListMapping.size());
            for(Map.Entry<AbstractTerm, AbstractPostingList> entry : termToPostingListMapping.entrySet()){
                entry.getKey().writeObject(out);
                entry.getValue().writeObject(out);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 从二进制文件读
     *
     * @param in ：输入流对象
     */
    @Override
    public void readObject(ObjectInputStream in) {
        int size;
        try{
            size = (Integer) in.readObject();
            for(int i=0;i<size;i++){
                int docId = (Integer) in.readObject();
                String docPath = (String) in.readObject();
                docIdToDocPathMapping.put(docId, docPath);
            }
            size = (Integer) in.readObject();
            for(int i=0;i<size;i++){
                AbstractTerm term = new Term();
                AbstractPostingList postingList = new PostingList();
                term.readObject(in);
                postingList.readObject(in);
                termToPostingListMapping.put(term, postingList);
            }

        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}
