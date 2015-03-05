package muster.preprocessing.steps;

import java.util.List;


public class IDFT {

private DFTMatrix mat;
	
	public IDFT(int dim) {
		mat = new DFTMatrix(dim);
	}
	
	public List<Complex> compute(List<Complex> valueList){
		if (valueList.size() != mat.getDim())
			throw new RuntimeException("Illegal Vectorsize");
		return mat.multiply(valueList);
	}
}

