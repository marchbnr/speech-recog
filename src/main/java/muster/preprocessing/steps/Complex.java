package muster.preprocessing.steps;

public final class Complex implements Cloneable {
	
	double re;
	double im;
	public static long objectCount = 0;
	
	public static Complex fromPolar(double radius, double angle)
	{
	  double real = radius * Math.cos(angle);
	  double imaginary = radius * Math.sin(angle);
	  return new Complex(real, imaginary);
	}
	
	public Complex(double re, double im) {
		this.re = re;
		this.im = im;
		objectCount++;
	}
	
	public void setPolar(double radius, double angle) {
		re = radius * Math.cos(angle);
		im = radius * Math.sin(angle);
	}
	
	public double getReal() {
		return re;
	}
	
	public double getImaginary() {
		return im;
	}
	
	public void add(Complex arg) {
		add(arg.re, arg.im);
	}
	
	public void add(double argre, double argim) {
		re = re+argre;
		im = im+argim;
	}
	
	public void subtract(Complex arg) {
		subtract(arg.re, arg.im);
	}
	
	public void subtract(double argre, double argim) {
		re = re-argre;
		im = im-argim;
	}
	
	public void multiply(Complex arg) {
		multiply(arg.re, arg.im);
	}
	
	public void multiply(double argre, double argim) {
		double newRe = (this.re * argre) - (this.im * argim);
		double newIm = (this.re * argim) + (this.im * argre);
		re = newRe;
		im = newIm;
	}
	
	public void divide(Complex arg) {
		divide(arg.re, arg.im);
	}
	
	public void divide(double argre, double argim) {
		double denominator = ((argre * argre) + (argim * argim));
		if (denominator == 0) throw new RuntimeException("Denominator is zero");
				
		double newRe = ((this.re * argre) + (this.im * argim)) / denominator;
		double newIm = ((this.re * argim * -1) + (argre * this.im)) / denominator;
		re = newRe;
		im = newIm;
	}
	
	public double abs() {
		return Math.sqrt(power());
	}
	
	public double power(){
		return (re * re) + (im * im);
	}
	
	public double angle() {
		return Math.atan2(im, re);
	}
	
	public void conjugate() {
		im = -im;
	}

	public String toString() {
		return "" + (Math.round(re*100)/100.0) + "+j" + (Math.round(im*100)/100.0);
	}
	
	public boolean equals(Complex arg) {
		return isSimilar(re, arg.re) && isSimilar(im, arg.im);
	}
	
	private boolean isSimilar(double expected, double value) {
		return Math.abs(expected-value) < Double.parseDouble("1.0E-9");
	}
	
	public Object clone() {
		return new Complex(re, im);
	}

}
