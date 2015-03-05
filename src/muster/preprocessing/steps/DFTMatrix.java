package muster.preprocessing.steps;

import java.util.ArrayList;
import java.util.List;


public class DFTMatrix {
	Complex[][] values;
	int dim;
	
	public DFTMatrix(int dimension) {
		dim = dimension;
		values = new Complex[dimension][dimension];
		for(int l=0; l<dimension; l++) {
			for(int k=0; k<dimension; k++) {
				values[l][k] = Complex.fromPolar(1, (2 * Math.PI * k * l /dimension));
			}
		}
	}
	
	public List<Complex> multiply(List<Complex> operand) {
		ArrayList<Complex> res = new ArrayList<Complex>();
		for (int l = 0; l<values.length; l++){
			Complex tmp = new Complex(0,0);
			for (int k = 0; k<values.length; k++){
				Complex clone = (Complex)values[l][k].clone();
				clone.multiply(operand.get(k));
				tmp.add(clone);
			}
			res.add(tmp);
		}
		return res;
	}
	
	public DFTMatrix invert() {
		DFTMatrix inv = new DFTMatrix(values.length);
		Complex divisor = new Complex(dim, 0);
		for (int l = 0; l<dim; l++){
			for (int k = 0; k<dim; k++){
				//inv.values[k][l] = values[l][k].conjugate().divide(divisor);
				inv.values[k][l] = (Complex)values[l][k].clone();
				inv.values[k][l].conjugate();
				inv.values[k][l].divide(divisor);
			}
		}
		return inv;
	}

	public int getDim() {
		return values.length;
	}
	
	public String toString() {
		String result = "";
		int dim = values.length;
		for(int l=0; l<dim; l++) {
			for(int k=0; k<dim; k++) {
				result += values[l][k] + " ";
			}
			result += "\n";
		}
		return result;
	}
	
	public boolean equals(DFTMatrix otherMatrix) {
		if(dim != otherMatrix.dim)
			return false;
		for(int l=0; l<dim; l++) {
			for(int k=0; k<dim; k++) {
				if(values[l][k] != otherMatrix.values[l][k])
					return false;
			}
		}
		return true;
	}
}
