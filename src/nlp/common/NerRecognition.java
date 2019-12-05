package nlp.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cn.edu.nju.nlp.main.TimeAndLocMain;

/**
 * 时间地点识别,封装自Louc程序
 * @author Lizy
 * */
public class NerRecognition {

	public static TimeAndLocMain talm=null;
	
	public static void init() throws IOException {
		talm=new TimeAndLocMain();
		talm.load_NERModel("./data/templating/Data/");
	}
	
	/**输入一句话，返回所有时间*/
	public static List<String> getTime(String s){
		return talm.timeList(s);
	}
	
	/**输入一句话，判断是不是时间*/
	public static boolean isTime(String s){
		return talm.isTime(s);
	}
	
	/**输入一句分词结果，返回所有地点*/
	public static List<String> getLocation(String s){
		//地点识别中不能出现/
		s=s.replaceAll(" \\/ | \\/", " ");
		return talm.locList(s);
	}
	
	/**输入一句话，返回所有地点，需要传入地点list*/
	public static List<String> getLocationFromStr(String s, List<String> loclist){
		String seg=Segment.getSegment(s,loclist);
		seg=seg.replaceAll(" \\/ | \\/", " ");
		List<String> seglist=new ArrayList<>(Arrays.asList(seg.split(" ")));
		List<String> locCopy=new ArrayList<>(loclist);
		Iterator<String> locIt=locCopy.iterator();
		//删除loclist(locCopy)中和本句话无关的词语
		while(locIt.hasNext()){
			String str=locIt.next();
			if(!seglist.contains(str))
				locIt.remove();
		}
		List<String> locs=talm.locList(seg);
		Iterator<String> it=locs.iterator();
		//删除大别山区识别出来的大别山
		while(it.hasNext()){
			String str=it.next();
			if(locCopy.contains(str))
				it.remove();
			else{
				for(String ss : locCopy){
					if(ss.contains(str) || str.contains(ss)){
						it.remove();
						break;
					}
				}
			}
		}
		locs.addAll(locCopy);
		return locs;
	}
	
	/**输入一句话，返回所有地点*/
	public static List<String> getLocationFromStr(String s){
		String seg=Segment.getSegment(s,null);
		//System.out.println(seg);
		seg=seg.replaceAll(" \\/ | \\/", " ");
		return talm.locList(seg);
	}

	/**
	 * 输入分词和词性标注结果，得到词与词之间用空格间隔的字符串
	 */
	public static List<String> getLocationFromSegPosRes(List<String> seg_pos_res){
		StringBuffer sb=new StringBuffer();
		for(String str : seg_pos_res)
			sb.append(str.split("_")[0]+" ");
		String seg_res = sb.toString().trim();
		seg_res=seg_res.replaceAll(" \\/ | \\/", " "); //表达什么意思？
		return talm.locList(seg_res);
	}


//	/**
//	 * 在句子中回标
//	 */
	public static List<String> getSenBackmark(List<String> tagres, List<String> seg_pos_res, String mode)
	{
		List<String> res = new ArrayList<>();
		for(int i = 0;i < seg_pos_res.size();i++)
		{
			String[] splitstr = seg_pos_res.get(i).split("_");
			String word = splitstr[0];
			boolean flag = false;
			for(int j = 0;j < tagres.size();j++)
			{
				if(word.equals(tagres.get(j)))
				{
					flag = true;
					break;
				}
			}
			if(flag) res.add(word+"_"+mode);
			else res.add(word);
		}
		return res;
	}

	//edit
    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) throws IOException {
        Segment.init();
        NerRecognition.init();
//	    String s = "北京在2008年举办了奥运会";
		String s = "若福州楼盘每层2.8米共20层，南北楼距为28米。甲图②栋10层住户一年中不能直接获得太阳照射的时间最接近11个月";
	    //得到分词和词性标注的结果
	    List<String> seg_pos_res = Segment.getSegmentOringin(s, null);
	    System.out.println(seg_pos_res);
	    //得到地点列表
	    List<String> locres = NerRecognition.getLocationFromStr(s);
	    System.out.println("地点列表:");
		System.out.println(locres);
	    //根据地点列表进行回标
	    List<String> senLocRes = NerRecognition.getSenBackmark(locres, seg_pos_res, "LOC");
	    System.out.println(senLocRes);
        //得到时间列表
        List<String> timeres = NerRecognition.getTime(s);
        System.out.println("时间列表:");
        System.out.println(timeres);
		//根据时间列表进行回标
	    List<String> senTimeRes = NerRecognition.getSenBackmark(timeres, seg_pos_res, "TIME");
	    System.out.println(senTimeRes);
    }


}
