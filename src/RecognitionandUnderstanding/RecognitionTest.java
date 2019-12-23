package RecognitionandUnderstanding;
import java.io.*;

import nlp.common.*;

import java.util.*;

public class RecognitionTest {
    private static void test() throws IOException {
        Segment.init();
        NerRecognition.init();
        List<List<String>>result=new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("resource/time.csv"),"GBK"));//换成你的文件名
            reader.readLine();//第一行信息，为标题信息，不用,如果需要，注释掉
            String line = null;
            while((line=reader.readLine())!=null){
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                //System.out.println(line);
                String question = item[2];//这就是你要的数据了

                //int value = Integer.parseInt(last);//如果是数值，可以转化为数值
                for (int i=3;i<=6;i++){
                    String answer=item[i];
                    //System.out.println(answer);
                    String QA=question+answer;
                    List<String> locres = NerRecognition.getLocationFromStr(QA);
                    List<String> timeres = NerRecognition.getTime(QA);
                    List<String> temp=new ArrayList<>();
                    temp.add(QA);

                    List<String> row=new ArrayList<>();
                    row.add(QA);
                    row.add(timeres.toString().replace("[","").replace("]","").replace(",","，"));
                    row.add(locres.toString().replace("[","").replace("]","").replace(",","，"));
                    result.add(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream("resource/test.csv");
            osw = new OutputStreamWriter(out,"UTF-8");
            bw =new BufferedWriter(osw);
            bw.append("问答对,时间列表,地点列表\r");
            for(List<String> row : result)
            {
                for(int index=0;index<row.size()-1;index++)
                {
                    bw.append(row.get(index)).append(",");
                }
                bw.append(row.get(row.size()-1));
                bw.append("\r");
            }
        } catch (Exception e) {

        }
        finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        test();
    }
}
