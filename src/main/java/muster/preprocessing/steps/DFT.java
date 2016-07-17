package muster.preprocessing.steps;

import java.util.List;


public class DFT {

	private DFTMatrix mat;
	
	public DFT(int dim) {
		mat = new DFTMatrix(dim).invert();
	}
	
	public List<Complex> compute(List<Complex> valueList){
		if (valueList.size() != mat.getDim())
			throw new RuntimeException("Illegal Vectorsize");
		return mat.multiply(valueList);
	}
}
