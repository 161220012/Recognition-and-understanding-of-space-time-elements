import nlp.common.*;

import java.io.IOException;
import java.util.List;

public class RecognitionTest {
    private static void test() throws IOException {
        Segment.init();
        NerRecognition.init();
        String s="11月11日，我国广西北海市(109°E，21°N)的风向主要为";
        List<String> locres = NerRecognition.getLocationFromStr(s);
        List<String> timeres = NerRecognition.getTime(s);
        System.out.print("时间列表：");
        System.out.println(timeres);
        System.out.print("地点列表：");
        System.out.println(locres);
    }

    public static void main(String[] args) throws IOException
    {
        test();
    }
}
