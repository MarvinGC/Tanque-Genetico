// Clase TiroParab�lico
// Ultima Revisi�n  12/06/21

package genetico;

public class TiroParabolico {
	final float g = (float) 9.8;
	private double ang;
	private int xI;
	private int yI;
	private int vI;
	float t;
	
	/***
	 * @param vI  velocidad en m/s
	 * @param ang angulo en grados
	 * @param xI  posicion x inicial en metros
	 * @param yI  posicion y inicial en metros
	 */
	
	// Funci�n TiroParabolico. Calcula toda la matem�tica detras del tiro parab�lico
	TiroParabolico(int vI, int ang, int xI, int yI){ 
		this.ang = ang * 3.141592 / 180;
		this.xI = xI;
		this.yI = yI;
		this.vI = vI;
		t = 0;
	}
	
	// Estas funciones calculan la posici�n de la bala 
	double posicionX() {
		return xI + vI*Math.cos(ang)*t;
	}
	double posicionY() {
		return yI + vI*Math.sin(ang)*t - g/2*Math.pow(t,2);
	}
	
	// Esta funci�n le doy la velocidad conforme al tiempo
	public float desplazamiento() {
		t += 0.0001;
		while(posicionY() > 0) {
			
			//System.out.println("x --> " + posicionX() + " y --> " + posicionY());
			t += 0.0001;
		}
		return t;
	}
	
	// Traducci�n de todas las funciones anteriores dentro del lienzo en blanco de Processing
	public double pixelesX() {
		return posicionX() * 100;
	}
	public double pixelesY() {
		return posicionY() * 100;
	}
	
}
