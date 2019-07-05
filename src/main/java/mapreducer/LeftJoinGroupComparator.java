package mapreducer;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import edu.umd.cloud9.io.pair.PairOfStringInt;

public class LeftJoinGroupComparator extends WritableComparator{

	public LeftJoinGroupComparator() {
		super(PairOfStringInt.class, true);
	}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		PairOfStringInt one = (PairOfStringInt) a;
		PairOfStringInt two = (PairOfStringInt) b;
		return one.getLeftElement().compareTo(two.getLeftElement());
	}
	
	

}
