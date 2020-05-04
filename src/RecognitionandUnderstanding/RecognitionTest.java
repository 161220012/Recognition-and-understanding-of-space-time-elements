package RecognitionandUnderstanding;
import java.io.*;
import java.sql.Time;
import java.util.regex.*;

import nlp.common.*;

import java.util.*;

public class RecognitionTest {
    private static List<String>geo_name=new ArrayList<>();
    private static List<String>holiday_name=new ArrayList<>();
    private static void test() throws IOException {
        Segment.init();
        NerRecognition.init();

        List<List<String>>result=new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("resource/time-space.csv"),"GBK"));//换成你的文件名
            reader.readLine();//第一行信息，为标题信息，不用,如果需要，注释掉
            String line = null;
            while((line=reader.readLine())!=null){
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                //System.out.println(line);
                //识别背景
                String background=item[2];
                List<String> locres = NerRecognition.getLocationFromStr(background);
                List<String> locres_ext=locRecognition(background);
                List<String> timeres = NerRecognition.getTime(background);
                List<String> timeres_ext= TimeRecognition(background);
                //识别问题
                String question = item[3];//这就是你要的数据了
                List<String> question_locres = NerRecognition.getLocationFromStr(question);
                List<String> question_locres_ext =locRecognition(question);
                List<String> question_timeres = NerRecognition.getTime(question);
                List<String> question_timeres_ext =TimeRecognition(question);
                locres.addAll(question_locres);
                locres_ext.addAll(question_locres_ext);
                timeres.addAll(question_timeres);
                timeres_ext.addAll(question_timeres_ext);
                //int value = Integer.parseInt(last);//如果是数值，可以转化为数值
                //识别选项
                for (int i=4;i<=7;i++){
                    String answer=item[i];
                    //System.out.println(answer);
                    List<String> answer_locres = NerRecognition.getLocationFromStr(answer);
                    List<String> answer_timeres = NerRecognition.getTime(answer);
                    List<String> answer_locres_ext = locRecognition(answer);
                    List<String> answer_timeres_ext =TimeRecognition(answer);
                    locres.addAll(answer_locres);
                    locres_ext.addAll(answer_locres_ext);
                    timeres.addAll(answer_timeres);
                    timeres_ext.addAll(answer_timeres_ext);
                }
                List<String> locres_ext_copy=new ArrayList<>();
                locres_ext_copy.addAll(locres_ext);
                for(String s:locres_ext_copy)
                {
                    if(locres.contains(s))
                    {
                        locres_ext.remove(s);
                    }
                }
                locres.addAll(locres_ext);
                List<String> timeres_ext_copy=new ArrayList<>();
                timeres_ext_copy.addAll(timeres_ext);
                for(String s:timeres_ext_copy)
                {
                    if(timeres.contains(s))
                    {
                        timeres_ext.remove(s);
                    }
                }
                timeres.addAll(timeres_ext);
                List<String> row=new ArrayList<>();
                row.add(background);
                row.add(question);
                row.add(item[4]);
                row.add(item[5]);
                row.add(item[6]);
                row.add(item[7]);
                row.add(timeres.toString().replace("[","").replace("]","").replace(",","，"));
                row.add(locres.toString().replace("[","").replace("]","").replace(",","，"));
                result.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream("resource/test.csv");
            osw = new OutputStreamWriter(out,"GBK");
            bw =new BufferedWriter(osw);
            bw.append("background,question,choice_A,choice_B,choice_C,choice_D,时间列表,地点列表\r");
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

    public static List<String>TimeRecognition(String args) throws IOException
    {
        //匹配处于……时、时期、季节
        String pattern1 ="处于(\\S*)(时期|时|季节|期)";
        // 创建 Pattern 对象
        Pattern r1= Pattern.compile(pattern1);
        // 现在创建 matcher 对象
        Matcher m = r1.matcher(args);
        List<String> result=new ArrayList<>();
        while(m.find())
        {
            System.out.println(m.group());
            result.add(m.group());
        }

        //匹配xx年，xx月，xx日，xx世纪、计时、农历
        String pattern2="(近|早|晚)?((正|零|一|二|三|四|五|六|七|八|九|十|百|千|万|亿|\\d)+(个)?" +
                "(世纪|年代|年|月份|月|天|日|小时|时|分钟|分|秒)" +
                "((初|十|廿|三)(一|二|三|四|五|六|七|八|九|十))?)+(后|前|之后|之前|中)?";
        // 创建 Pattern 对象
        Pattern r2= Pattern.compile(pattern2);
        // 现在创建 matcher 对象
        m = r2.matcher(args);
        while(m.find())
        {
            System.out.println(m.group());
            result.add(m.group());
        }

        //匹配daytime
        String pattern3="早晨|早上|上午|中午|正午|下午|黄昏|傍晚|晚上|夜晚|深夜|凌晨";
        // 创建 Pattern 对象
        Pattern r3= Pattern.compile(pattern3);
        // 现在创建 matcher 对象
        m = r3.matcher(args);
        while(m.find())
        {
            System.out.println(m.group());
            result.add(m.group());
        }

        for(String row:holiday_name) {
            if (kmp.kmpMatch(args, row) != -1) {
                System.out.println(row);
                result.add(row);
            }
        }
        return result;
    }

    public static List<String>locRecognition(String args) throws IOException
    {
        //匹配经纬度
        //编写模式
        String pattern1 ="[\\(（]?(\\s+)?((约)?(\\d+)\\°(\\d+[′'’])?(\\d+[″”])?[NSEW](\\s+)?[,，]?(\\s+)?)?(约)?(\\d+)\\°(\\d+[′'’])?(\\d+[″”])?[NSEW](\\s+)?[\\)）]?";
        // 创建 Pattern 对象
        Pattern r1= Pattern.compile(pattern1);
        // 现在创建 matcher 对象
        Matcher m = r1.matcher(args);
        List<String> result= new ArrayList<>();
        while(m.find())
        {
            //System.out.println(m.group());
            result.add(m.group());
        }

        //匹配指代
        String pattern2="[该①②③④甲乙丙丁A-Z当][地处国]";
        Pattern r2= Pattern.compile(pattern2);
        m=r2.matcher(args);
        while(m.find())
        {
            //System.out.println(m.group());
            result.add(m.group());
        }

        /*语料库，暂时弃用
        for(String row:geo_name) {
            if(row.length()<=1)
                continue;
            if (kmp.kmpMatch(args, row) != -1) {
                System.out.println(row);
                result.add(row);
            }
        }
        */

        return result;
    }
    public static List<String>Combination()throws IOException
    {
        List<String> timeres=new ArrayList<>();
        List<String>locres=new ArrayList<>();
        try{
            BufferedReader reader0 = new BufferedReader(new InputStreamReader(new FileInputStream("resource/supervision.csv"),"GBK"));//换成你的文件名
            reader0.readLine();
            String line=null;
            while((line=reader0.readLine())!=null) {
                String item[]=line.split(",");
                String background=item[0];
                String question=item[1];
                String answerA=item[2];
                String answerB=item[3];
                String answerC=item[4];
                String answerD=item[5];
                String time=item[6];
                String location=item[7];
                time=time.replace(" ","");
                location=location.replace(" ","");
                String[] time_array=time.split("，");
                String[] location_array=location.split("，");
                List<String>time_list=new ArrayList<>();
                List<String>location_list=new ArrayList<>();
                for(String s:time_array)
                {
                    time_list.add(s);
                }
                for(String s:location_array)
                {
                    location_list.add(s);
                }
                System.out.println(time_list);
                System.out.println(location_list);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        List<String> result=new ArrayList();
        return result;
    }
    public static void main(String[] args) throws IOException
    {
        //读取外部地名语料库
        /*
        try{
            BufferedReader reader0 = new BufferedReader(new InputStreamReader(new FileInputStream("resource/geo_zh.csv"),"GBK"));//换成你的文件名
            reader0.readLine();
            String line=null;
            while((line=reader0.readLine())!=null) {
                geo_name.add(line);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("语料库的数量:"+geo_name.size());
         */
        //读取外部节日名称
        /*
        try{
            BufferedReader reader0 = new BufferedReader(new InputStreamReader(new FileInputStream("resource/节日.csv"),"GBK"));//换成你的文件名
            reader0.readLine();
            String line=null;
            while((line=reader0.readLine())!=null) {
                holiday_name.add(line);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("节日数量:"+holiday_name.size());
         */
        //test();
        Combination();
        //System.out.println(TimeRecognition("早1小时看到日落"));
    }
}
