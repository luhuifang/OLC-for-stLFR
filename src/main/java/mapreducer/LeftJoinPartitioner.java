package mapreducer;

import org.apache.hadoop.mapreduce.Partitioner;

import edu.umd.cloud9.io.pair.PairOfStringInt;
import edu.umd.cloud9.io.pair.PairOfStrings;

public class LeftJoinPartitioner extends Partitioner<PairOfStringInt, PairOfStrings>{

	@Override
	public int getPartition(PairOfStringInt key, PairOfStrings value, int num_reducer) {
		return Math.abs(key.getLeftElement().hashCode()) % num_reducer;
	}

}
