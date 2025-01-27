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
	PImage tanque;
	PImage diana;
	PImage fondo;
	
	//Processing
	float i = 0;
	TiroParabolico tp ; // Objeto de la clase tiroParab�lico que calcula el tiro parab�lico 
	
	// Estas variables indican la posici�n del objetivo en el espacio bidimencional
	int objetivox;
	int objetivoy;
	int[][] poblacion;
								
	ArrayList<Individuo> vivos;// Hace un arreglo de Individuos
	int in = 0;
	
	
	// Parametros del Algoritmo Gen�tico.
	
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

	
	// Define las dimensiones de la ventana del ejecutable.
	public void settings() {
		size(1500, 800);
		// Posiciones del tanque y la diana
		objetivox = (int) (width*0.8);
		objetivoy = 490;
		fondo = loadImage("Fondo.jpeg");
		fondo.resize(width,height);
	}
	
	
	public void setup() {
		PImage titleBarIcon = loadImage("icon.png");
		surface.setIcon(titleBarIcon);
		surface.setResizable(true);
		//surface.setTitle("Tanque-Genetico");
		background(255);
		
		// Aqui se incicializa la poblaci�n de vivos
		poblacion = crearPoblacion(); 
		vivos = new ArrayList<Individuo>();
		
		System.out.println("Poblacion Inicial:");
		
		//Se muestra la poblacion inicial
		mostrarArregloB(poblacion);
		int x = sumPMitad(poblacion[in]);
		int ang = sumSMitad(poblacion[in]);
		in++;
		
		tp = new TiroParabolico(x,ang,0,0);
	}
	
	// 
	public void draw() {
		background(fondo);
		disparo(20,480);
		escenografia();
	}
	public void escenografia() {
		//Piso
		line(0,500,width,500);
		
		//Tanque
		tanque = loadImage("Tanque.jpeg");
		tanque.resize(50, 50);
		image(tanque, 20, 500-tanque.width);
		
		//Objetivo
		diana = loadImage("Diana.jpeg");
		diana.resize(50, 50);
		image(diana, objetivox, objetivoy-diana.width);
	}
	
	// Esta funci�n realiza los calculos para que se pueda realizar el Tiro Parab�lico
	public void disparo(int posx, int posy) {
		
		// Mientras la pocision incial de la bala est� encima de la posci�n -500,
		// El tanque har� su disparo
		if(tp.pixelesY() - posy > -500) {
			//System.out.println("x --> " + tp.posicionX() + " y --> " + tp.posicionY());
			
			// Rellena de color la bala
			fill(247, 4, 4);
			// Dibuja la trayectoria de la bala conforme al tiempo
			ellipse((float) tp.pixelesX() + posx, 
					(float) (tp.pixelesY() - posy) * -1,
					10,10);
			tp.t += 0.01;
		}
		else {
			// System.out.println("\n"+(int) (tp.pixelesX() + posx));
			// Y esta funci�n evalua qu� tan cerca estuvo del blanco y le d� una calificaci�n
			evaluar((int) (tp.pixelesX() + posx));
			
			// Ya con el puntaje que le fue otrogado al tiro. El If
			// Realiza el proceso de la Selecci�n Natural y seleccionar los nuevos individuos
			if(in == num_pob) {
				poblacion = seleccionNatural();
				mostrarArregloB(poblacion);
				vivos = new ArrayList<Individuo>();
				in = 0;
			}
			int x = sumPMitad(poblacion[in]);
			int ang = sumSMitad(poblacion[in]);
			
			// En caso de que tp no sea visible en los l�mites de la pantalla,
			// Crear� una nueva poblaci�n y un nuevo tiro.
			tp = new TiroParabolico(x,ang,0,0);
		}		
	}
	
    // Funci�n que da el puntaje del tiro.
    public float aptitud(int[] individuo, int puntaje) {
    	int diferencia = Math.abs(objetivox - puntaje);
    	if(diferencia == 0)
    		mejor = true;
    	return (float) (1.0 / (1.0 + diferencia)); 
    }
	
	// Esta funci�n evaluar� qu� tan cerca estuvo el tiro de darle al blanco 
	public void evaluar(int puntaje) {
		int a = (int) (aptitud(poblacion[in],puntaje) * 1000000)+1;
		vivos.add(new Individuo(a,poblacion[in]));
		in++;
	}

	// Crea el adn del individuo, para la poblaci�n inicial
	public int[]  crearAdn(int min, int max){
		/*
		 	Este se compone de 10 numeros de los cuales,
		 	la suma de los primeros 5 representan la velocidad,
		 	mientras que la suma de los ultimos 5 representan el angulo de inclinaci�n
		 */
        int[] ADN = new int[material_g];
        for (int i = 0; i < material_g; i++) {
        	// Se generea un n�mero aleatorio del 1 al 9
            ADN[i] = new Random().nextInt(max) + min;
        }
        return ADN;
    }
	
	// Funci�n que crea la Poblaci�n Inicial
    public int[][] crearPoblacion(){
    	int[][] poblacion = new int[num_pob][material_g];
    	
    	for (int i = 0; i < num_pob; i++) {
			poblacion[i] =  crearAdn(1,9);
		}
        return poblacion;
    }
    
    //La suma de la primera mitad del material genetico de un individuo
    //Esta reprecenta la velocidad del disparo
	public int sumPMitad(int[] mat_geneticoInd) {
		int x = 0;
		for (int i = 0; i < mat_geneticoInd.length/2; i++) {
			x += mat_geneticoInd[i];
		}
		return x;
	}
	//La suma de la segundo mitad del material genetico de un individuo
	//Esta representa el angulo de inclinaci�n del disparo
	public int sumSMitad(int[] mat_geneticoInd) {
		int x = 0;
		for (int i = mat_geneticoInd.length/2; i < mat_geneticoInd.length; i++) {
			x+=mat_geneticoInd[i];
		}
		return x;
	}
	
	// Funci�n selecci�nNatural. Aqu� se desarolla el Algoritmo Gen�tico
	public int[][] seleccionNatural(){
		
	    //Ordena los indiviuos mediante la aptitud de menor a mayor
		Collections.sort(vivos,new Individuo().new CompMayorMejor());
		
		//Al realizar el ordenamiento el mejor seria el ultimo elemento
		Individuo mejor = 
				new Individuo(vivos.get(vivos.size()-1).aptitud,
										vivos.get(vivos.size()-1).ADN);
		System.out.println("\nMejor: "+mejor.aptitud);
		mostrarArreglo(mejor.ADN);
		System.out.println();
		

		int[][] poblacion = new int[num_pob][material_g];
		
		// AraayList para seleccionar a los mejores resutados de la generaci�n anterior
		ArrayList<Integer> rifa = new ArrayList<Integer>();
		
		//Sorteo
		// Por cada esp�cimen en la poblaci�n (arreglo)
		for (Individuo especimen : vivos) {
			
			// Si saca un puntaje alto, va a poner m�s individuos en la rifa
			int puntaje = especimen.getAptitud();
			
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
	
	// Convierte una lista de Individuos en un arreglo bidimensional.
	// Recorre todos los individuos y solo a�ade el ADN al nuevo arreglo
	public int[][] listaAArregloB(ArrayList<Individuo> lista) {
		int[][] arr = new int[lista.size()][0];
		for (int i = 0; i < lista.size(); i++) {
			arr[i] = lista.get(i).ADN;
		}
		return arr;
	}
	
	// Muestra el arreglo en la consola
	// esto ayuda para las pruebas con el debug.
	
	public void mostrarArreglo(int[] arr) {
		System.out.print("{");
		for (int j = 0; j < arr.length; j++) {
			System.out.print(arr[j]);
			if(j<arr.length-1)
				System.out.print(",");
		}
		System.out.print("}");
	}
	
	// Muestra el arreglo bidimencional el cual
	// representa toda la poblaci�n
	public void mostrarArregloB(int[][] arr) {
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
