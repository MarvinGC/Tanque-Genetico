/*Proyeto Tanque
 * Proyecto para Inteligencia Artificial
 * Versi�n 1.1
 * Ultima modificaci�n: 12/06/21
 * Esperando a ser m�s optimizado*/


package genetico;

// Importan Librerias
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// Se importan Lbrer�as de Processing
import processing.core.PApplet;
import processing.core.PImage;

/// Variables Cambiantes 

//	Lienzo Principal
public class Escenario extends PApplet{
	// Inicia el main
	public static void main(String[] args) {
		// T�tulo de la ventana
		PApplet.main("genetico.Escenario");
	}
	
	//Processing
	// Delaraci�n de variables para que Processing lo entienda
	float i = 0;
	// Objeto de la clase tiroParab�lico que calcula el tiro parab�lico
	TiroParabolico tp ; 
	// Estas variables indican la posici�n del obajetivo en el espacio bidimencional
	int objetivox;
	int objetivoy;
	int[][] poblacion;
	// Hace un arreglo de Pares Ordenados 
	ArrayList<Individuo> vivos;
	int in = 0;
	
	
	// Evolucion del Algoritmo Gen�tico.
	
	// Se define el fitness (modelo) cada individuo de la poblaci�n.
	int modelo = 36; 
	// L�mite de material gen�tico
	int material_g = 10;
	// Cantidad m�xima de individuos que se aceptan en la poblaci�n
	int num_pob = 10;
	// Esta variable (presion) evita que futuras mutaciones se apliquen a toda la poblaci�n de la generaci�n
	int presion = (int) (num_pob * 0.2);
	// Esta variable evita que mute demasiado un miembro de la poblaci�n
	float mutacion_v = (float) 0.3;
	// Este variable para saber si un individuo alcanzo el objetivo
	boolean mejor = false;

	
	public void settings() {
		size(1500, 800);
		objetivox = (int) (width*0.8);
		objetivoy = 490;
	}
	public void setup() {
		PImage titlebaricon = loadImage("icon.png");
		surface.setIcon(titlebaricon);
		surface.setResizable(true);
		//surface.setTitle("SimpleAI - Intelligent Points");
		background(255);
		
		// Aqui se incicializa la poblaci�n de vivos
		poblacion = crearPoblacion(); 
		vivos = new ArrayList<Individuo>();
		
		System.out.println("Poblacion Inicial:");
		
		//Se muestra la poblacion inicial
		mostrarArrCuad(poblacion);
		int x = sumX(poblacion[in]);
		int ang = sumAng(poblacion[in]);
		in++;
		
		tp = new TiroParabolico(x,ang,0,0);
	}
	public void draw() {
		
		background(255);
		disparo(20,480);
		
		escenografia();
		
	}
	public void escenografia() {
		//Piso
		line(0,500,width,500);
		
		//Tanque
		fill(66, 134, 244);
		ellipse(10,490, 20, 20);
		
		//Objetivo
		fill(66, 134, 244);
		ellipse(objetivox,objetivoy, 20, 20);
	}
	
	// Esta funci�n realiza los calculos para que se pueda realizar el Tiro Parab�lico
	public void disparo(int posx, int posy) {
		
		// Mientras el tanque est� encima de la posci�n -500,
		// El tanque har� su disparo
		if(tp.pixelesY() - posy > -500) {
			
			// Rellena de color la bala
			fill(247, 4, 4);
			//System.out.println("x --> " + tp.posicionX() + " y --> " + tp.posicionY());
			
			// Dibuja la trayectoria de la bala conforme al tiempo
			ellipse((float) tp.pixelesX() + posx, 
					(float) (tp.pixelesY() - posy) * -1,
					10,10);
			tp.t += 0.01;
		}
		else {
			// Este print avisar� por consola si la bala atin� al objetivo
			System.out.println("\n"+(int) (tp.pixelesX() + posx));
			// Y esta funci�n evalua qu� tan cerca estuvo del blanco y le d� una calificaci�n
			evaluar((int) (tp.pixelesX() + posx));
			
			// Ya con el puntaje que le fue otrogado al tiro. El If
			// Realiza el proceso de la Selecci�n Natural y seleccionar los nuevos individuos
			if(in == num_pob) {
				poblacion = seleccionNatural();
				mostrarArrCuad(poblacion);
				vivos = new ArrayList<Individuo>();
				in = 0;
			}
			int x = sumX(poblacion[in]);
			int ang = sumAng(poblacion[in]);
			
			// En caso de que tp no sea visible en los l�mites de la pantalla,
			// Crear� una nueva poblaci�n y un nuevo tiro.
			tp = new TiroParabolico(x,ang,0,0);
		}		
	}
	
	// Esta funci�n evaluar� qu� tan cerca estuvo el tiro de darle al blanco 
	public void evaluar(int puntaje) {
		int a = (int) (aptitud(poblacion[in],puntaje) * 1000000)+1;
		vivos.add(new Individuo(a,poblacion[in]));
		in++;
	}

	
	public int[]  crearAdn(int min, int max){
        int[] ADN = new int[material_g];
        for (int i = 0; i < material_g; i++) {
            ADN[i] = new Random().nextInt(max) + min;
        }
        return ADN;
    }

    public int[][] crearPoblacion(){
    	int[][] poblacion = new int[num_pob][material_g];
    	
    	for (int i = 0; i < num_pob; i++) {
			poblacion[i] =  crearAdn(1,9);
		}
        return poblacion;
    }
    
    public float aptitud(int[] individuo, int puntaje) {
    	int diferencia = Math.abs(objetivox - puntaje);
    	if(diferencia == 0)
    		mejor = true;

    	return (float) (1.0 / (1.0 + diferencia)); 
    }
	public int sumX(int[] individuo) {
		int x = 0;
		for (int i = 0; i < individuo.length/2; i++) {
			x += individuo[i];
		}
		return x;
	}
	public int sumAng(int[] individuo) {
		int x = 0;
		for (int i = individuo.length/2; i < individuo.length; i++) {
			x+=individuo[i];
		}
		return x;
	}
	
	// Funci�n selecci�nNatural. Aqu� se desarolla el Algoritmo Gen�tico
	public int[][] seleccionNatural(){
		
	    //Ordena los pares ordenados y se queda solo con el array de valores
		Collections.sort(vivos,new Individuo().new OrdxNum());
		
		/*El mejor*/
		Individuo mejor = new Individuo(
										vivos.get(vivos.size()-1).num,
										vivos.get(vivos.size()-1).ADN);
		System.out.println("\nMejor: "+mejor.num);
		mostrarArr(mejor.ADN);
		System.out.println();
		

		int[][] poblacion = new int[num_pob][material_g];
		
		// AraayList para seleccionar a los mejores resutados de la generaci�n anterior
		ArrayList<Integer> rifa = new ArrayList<Integer>();
		
		//Sorteo
		// Por cada esp�cimen en la poblaci�n (arreglo)
		for (Individuo especimen : vivos) {
			
			// Si saca un puntaje alto, va a poner m�s individuos en la rifa
			int puntaje = especimen.getNum();
			
			for(int i=0; i<puntaje; i++) {
				rifa.add(vivos.indexOf(especimen));
			}
		}
		
		//Se mezcla el material genetico para crear nuevos individuos
	    for (int i = 0; i < num_pob; i++){
	    	
	        //Se elige un punto para hacer el intercambio
	    	// El Random hace su funci�n a a partir del numero gen�tico para aplicarselo al nuevo individuo.
	        int punto = new Random().nextInt(material_g-1)+1; 
	        //Se eligen dos padres
	        int a = Math.round((float) (Math.random() * (rifa.size() - 1)));
	        int b = a;
	        while(b==a)
	        	b = Math.round((float) (Math.random() * (rifa.size() - 1)));
			
	        //Se mezcla el material genetico de los padres en cada nuevo individuo
			poblacion[i] = mitosis(vivos.get(rifa.get(a)).ADN,
									vivos.get(rifa.get(b)).ADN, 	punto);
	    }
	    
		return mutacion(poblacion);
	}
	
	//Mutacion agregar� una mutaci�n para diferenciar la siguiente generaci�n de la anterior
	
	public int[][] mutacion(int[][]population) {
	    int rango = population.length - presion;
	    for (int i = 0; i < rango ; i++) {
	    	Random a = new Random();
	    	// Este if regula la mutaci�n de la nueva generaci�n
	        if(a.nextFloat() <= mutacion_v) {
	            int punto = a.nextInt(material_g-1);
	            int nuevo_valor = a.nextInt(8) + 1;
	            
	            // Si la mutaci�n no afecto a la nueva generaci�n, este while
	            // calcula una nueva mutaci�n hasta que haya una diferencia entre padres e hijos
	            while( nuevo_valor == population[i][punto])
	                nuevo_valor = a.nextInt(8) + 1;
	  
	            //Se aplica la mutacion a la nueva generaci�n
	            population[i][punto] = nuevo_valor;
	        }
	    }
	  
	    return population;
    }
	
	// Combina ambos materiales gen�ticos a partir de un punto.
	// Elegir� un punto para cortar el material gen�tico y as� combinarlo
	public int[] mitosis(int[] a, int[] b, int punto) {
        int[] arr = new int[material_g];
        for (int i = 0; i < material_g; i++) {
			if(i<punto)
				arr[i] = a[i];
			else
				arr[i] = b[i];
		}
		return arr;
	}
	
	// Convierte una lista de parOrdenado en un arreglo bidimensional.
	// Recorre todos los pares ordenados de la lista
	// Y a�ade el ADN al nuevo arreglo
	
	
	public int[][] listaAArr(ArrayList<Individuo> lista) {
		int[][] arr = new int[lista.size()][0];
		for (int i = 0; i < lista.size(); i++) {
			arr[i] = lista.get(i).ADN;
		}
		return arr;
	}
	
	// mostrarArr muestra el arreglo en la consola
	// esto ayuda para las pruebas con el debug.
	
	public void mostrarArr(int[] arr) {
		System.out.print("{");
		for (int j = 0; j < arr.length; j++) {
			System.out.print(arr[j]);
			if(j<arr.length-1)
				System.out.print(",");
		}
		System.out.print("}");
	}
	
	// muestra el arreglo cuadrado
	// Esta funci�n muestra toda la poblaci�n
	public void mostrarArrCuad(int[][] arr) {
		System.out.print("{");
		for (int i = 0; i < arr.length; i++) {
			System.out.print("{");
			for (int j = 0; j < arr[i].length; j++) {
				System.out.print(arr[i][j]);
				if(j<arr[i].length-1)
					System.out.print(",");
			}
			System.out.print("}");
			if(i<arr.length-1)
				System.out.print(", ");
		}
		System.out.print("}");
	}
}
