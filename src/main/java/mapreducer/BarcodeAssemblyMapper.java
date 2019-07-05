package mapreducer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class BarcodeAssemblyMapper extends Mapper<Text, Text, Text, Text>{
	
	
	@Override
	protected void map(Text key, Text value, Context context)
			throws IOException, InterruptedException {
		Matcher matcher = Pattern.compile("(\\d+_\\d+_\\d+)").matcher(key.toString());
		String barcode = "";
		if(matcher.find()) {
			barcode = matcher.group(1);
		}
		
		if(! "0_0_0".equals(barcode)) {
			context.write(new Text(barcode), value);
		}
	}

	
}
