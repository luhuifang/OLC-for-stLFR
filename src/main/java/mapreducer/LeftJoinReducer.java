package mapreducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import edu.umd.cloud9.io.pair.PairOfStringInt;
import edu.umd.cloud9.io.pair.PairOfStrings;

public class LeftJoinReducer extends Reducer<PairOfStringInt, PairOfStrings, Text, Text>{

	private List<String> umis;
	
	@Override
	protected void reduce(PairOfStringInt key, Iterable<PairOfStrings> values,
			Context context) throws IOException, InterruptedException {
		
		System.out.println("key: " + key.toString());
		umis = new ArrayList<String>();
		String barcode = key.getLeftElement();
		
		for(PairOfStrings value : values) {
			System.out.println("value: " + value.toString());
			if(value.getLeftElement().equals("umi")) {
				umis.add(value.getRightElement());
				continue;
			}
			
			String reads = value.getRightElement();
			if(umis.isEmpty()) {
				context.write(new Text(barcode), new Text(reads));
			}else {
				for(String umi : umis) {
					context.write(new Text(umi), new Text(reads));
				}
			}
			
		}
	}
	
	
}
