import java.util.*;
public class MenuPrincipalCajero {
	Scanner sc = new Scanner(System.in);
	ConexionBD bd = new ConexionBD();
	String rut,dv,id, opcion;
	int parseOpcion;
	float saldo = 0;
	
	public MenuPrincipalCajero(String rut, String dv, String id) {
		this.rut = rut;
		this.dv = dv;
		this.id = id;
		this.saldo = this.obtenerSaldo();
		
	}
	public void menuCajero() {
		int salida = 0;
		while(salida == 0) {
			System.out.println("\nBIENVENIDO AL CAJERO AUTOMATICO");
			System.out.println("(1): Consultar saldo");
			System.out.println("(2): Hacer retiro");
			System.out.println("(3): Hacer deposito");
			System.out.println("(4): Hacer transferencia");
			System.out.println("(5): Ver movimientos");
			System.out.println("(6): Salir");
			try {
				opcion = sc.nextLine().strip();
				parseOpcion = Integer.parseInt(opcion);
			}catch(Exception e) {
				System.out.println("Ingrese una opcion valida");
				continue;
			}
			switch(parseOpcion) {
			case 1:
				System.out.println("\nSu saldo es de: $" + this.saldo);
				break;
			case 2:
				this.menuRetiro();
				break;
			case 3:
				this.deposito();
				break;
			case 4:
				this.transferencia();
				break;
			case 5:
				this.movimientos();
				break;
			case 6:
				salida = 1;
				break;
			default:
				System.out.println("Ingrese una opcion valida");
			}
			
			
		}
	}
	private float obtenerSaldo() {
		Float saldo = 0F;
		try {
			ArrayList<String> cuentas = bd.cuentaExiste(this.rut, this.dv);
			String saldoString = cuentas.get(6);
			saldo = Float.parseFloat(saldoString);
		}catch(Exception e) {
			System.out.println("Error al recuperar el saldo de la cuenta");
		}
		
		return saldo;
	}
	private void menuRetiro() {
		int salida = 0;
		while(salida == 0) {
			System.out.println("\nELIJA EL MONTO A RETIRAR");
			System.out.println("(1): 1000");
			System.out.println("(2): 5000");
			System.out.println("(3): 10000");
			System.out.println("(4): 50000");
			System.out.println("(5): Otro monto");
			System.out.println("(6): Volver");
			try {
				opcion = sc.nextLine().strip();
				parseOpcion = Integer.parseInt(opcion);
			}catch(Exception e) {
				System.out.println("Ingrese una opcion valida");
				continue;
			}
			switch(parseOpcion) {
			case 1:
				this.retiro(1000);
				break;
			case 2:
				this.retiro(5000);
				break;
			case 3:
				this.retiro(10000);
				break;
			case 4:
				this.retiro(50000);
				break;
			case 5:
				float monto = this.ingresarMonto("retirar");
				if(monto != 0F) {this.retiro(monto);}
				break;
			case 6:
				salida = 1;
				break;
			default:
				System.out.println("Ingrese una opcion valida");
				break;
			}
		}
	}
	private void retiro(float monto) {
		if(this.saldo < monto) {
			System.out.println("\nNo tienes saldo suficiente para realizar esta operacion");
		}else {
			this.saldo -= monto;
			bd.retiro(this.rut, this.dv, this.id, this.saldo, monto);
		}
		
	}
	private void deposito() {
		Float monto = this.ingresarMonto("depositar");
		if(monto != 0F) {
			this.saldo += monto;
			bd.deposito(this.rut, this.dv, this.id, this.saldo, monto);
		}
	}
	private void transferencia() {
		String rutTransferencia, dvTransferencia, idTransferencia = "";
		ArrayList<String> cuenta = new ArrayList<String>();
		while(true) {
			System.out.println("Presione (q) para volver al menu anterior");
			System.out.print("Ingrese el rut de la persona a transferir (Sin puntos, guion ni dv): ");
			rutTransferencia = sc.nextLine().toLowerCase().strip();
			if(rutTransferencia.equals("q")) {break;}
			System.out.print("Ingrese el dv de la persona a transferir: ");
			dvTransferencia = sc.nextLine().toLowerCase().strip();
			if(dvTransferencia.equals("q")) {break;}
			if(rutTransferencia.equals(this.rut) && dvTransferencia.equals(this.dv)) {
				System.out.println("\nNo es posible hacerte una transferencia a ti mismo");
			}else {
				boolean datosValidos = MenuInicioCajero.validarDatosIngresados(rutTransferencia, dvTransferencia, "0000"); //Hago hardcode con la clave porque el metodo lo requiere
																											//No hago nada con este valor
				if(datosValidos) {
					cuenta = bd.cuentaExiste(rutTransferencia, dvTransferencia);
					if(cuenta.size() > 0) {
						idTransferencia = cuenta.get(0); //id se encuentra en el indice 0
						float saldoTransferencia = Float.parseFloat(cuenta.get(6)); //saldo se encuentra en el indice 6
						float monto = this.ingresarMonto("transferir");
						if(monto == 0F) {break;}
						if(monto > this.saldo) {
							System.out.println("\nNo tienes saldo suficiente para realizar esta operacion");
						}else {
							this.saldo -= monto;
							saldoTransferencia += monto;
							bd.transferencia(this.rut, this.dv, this.id, this.saldo, rutTransferencia, dvTransferencia, idTransferencia, saldoTransferencia, monto);
						}
						
					}else {
						System.out.println("\nEl usuario ingresado no esta registrado");
					}
				}else {
					System.out.println("\nLos datos ingresados no son validos");
				}
			}
			break;
			
		}
	}
	private float ingresarMonto(String tipoOperacion) {
		Float parseOpcionFloat = 0F;
		while(true) {
			System.out.println("\nIngrese el monto a " + tipoOperacion);
			System.out.println("Presione (q) para volver al menu anterior");
			opcion = sc.nextLine().toLowerCase().strip();
			if(opcion.equals("q")) {break;}
			else {
				try {
					parseOpcionFloat = Float.parseFloat(opcion);
					if(parseOpcionFloat <= 0) {
						System.out.println("No se pueden ingresar montos negativos o cero (0)");
						parseOpcionFloat = 0F;
					}else {
						break;
					}
				}catch(Exception e) {
					System.out.println("Debe ingresar un monto valido");
					parseOpcionFloat = 0F;
				}
			}
		}
		return parseOpcionFloat;
	}
	
	private void movimientos() {
		String signo = "$";
		ArrayList<String> movimientos = bd.movimineots(this.id);
		if(movimientos.size() > 0) {
			
			Collections.reverse(movimientos);
			//i Va a incrementar de 3 en 3
			for(int i =0; i < movimientos.size(); i+=3) {
				if(movimientos.get(i + 1).equals("SE REALIZO UN RETIRO") 
				|| movimientos.get(i + 1).contains("SE REALIZO UNA TRANSFERENCIA")) {
					
					signo = "-$";
				}else if(movimientos.get(i + 1).equals("SE REALIZO UN DEPOSITO")
				|| movimientos.get(i + 1).contains("RECIBIO UNA TRANSFERENCIA")) {
					signo = "+$";
				}
				System.out.println(signo + movimientos.get(i + 2) + ". " + movimientos.get(i+1) + " con fecha " + movimientos.get(i));

			}
			
		}else {
			System.out.println("No hay movimientos para mostrar");
		}
	}
	

}
