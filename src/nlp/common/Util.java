package nlp.common;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Util {
	public static List<String> read_file(String path){
		List<String> result = new ArrayList<>();
		try {
			BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
			String line = br1.readLine();
			if (line.startsWith("\uFEFF")) {
				line = line.substring(1);
			}
			while (line != null) {
				result.add(line.trim());
				line = br1.readLine();
			}
			br1.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
		return result;
	}
	
	public static void writeFile(String data,String file){
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
			String[] l=data.split(" ");
			for (String s : l) {
				String[] temp = s.split("_");
				bw.write(temp[0] + "\t" + temp[1] + "\n");
				bw.flush();
			}
			bw.write("\n");
			bw.flush();
			bw.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	//将空格间隔的taglist String转为句法树函数参数格式的list
	public static List<String> getTreeList(String data){
		List<String> result = new ArrayList<>();
		String[] l=data.split(" ");
		for (String s : l) {
			String[] temp = s.split("_");
			result.add(temp[0] + "\t" + temp[1]);
		}
		return result;
	}
}
