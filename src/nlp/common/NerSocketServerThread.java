package nlp.common;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by znt on 2019/8/27.
 */
public class NerSocketServerThread extends Thread {

    private Socket socket;

    public NerSocketServerThread(Socket socket)
    {
        this.socket = socket;
    }

    @Override
    public void  run()
    {
        InputStream inputStream = null;
        PrintWriter out = null;
        try {
            inputStream = socket.getInputStream(); //输入，from 客户端
            String str = Inputstr2Str_Reader(inputStream,"utf-8");
            System.out.println(str);  // 打印 客户 socket 发过来的字符
            List<String> seg_pos_res = Segment.getSegmentOringin(str, null);
            List<String> nerResLoc = NerRecognition.getLocationFromStr(str);
            List<String> nerResTime = NerRecognition.getTime(str);
            Map<String,List<String>> res = new HashMap<>();
            res.put("分词和词性标注",seg_pos_res);
            res.put("地点",nerResLoc);
            res.put("时间",nerResTime);
            Gson gson = new Gson();
            String resStr = gson.toJson(res);
            System.out.println(resStr);
            out = new PrintWriter(socket.getOutputStream()); //输出，to 客户端

            out.write(resStr);
            out.flush(); // to 客户端，输出

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(out != null)
                {
                    out.close();
                }
                if(inputStream != null)
                    inputStream.close();
                if(socket != null)
                    socket.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //把输入流转为String
    public static String Inputstr2Str_Reader(InputStream in, String encode)
    {

        String str = "";
        try
        {
            if (encode == null || encode.equals(""))
            {
                // 默认以utf-8形式
                encode = "utf-8";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, encode));
            StringBuffer sb = new StringBuffer();

            while ((str = reader.readLine()) != null)
            {
                sb.append(str).append("\n");
            }
            return sb.toString();
        }
        catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return str;
    }
}
