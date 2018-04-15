/*
-------------------------------------------------------------------------
fractalside's delta - Alpha 0.1 [20180415] 
(Don't use yet. There's work left)
-------------------------------------------------------------------------
http://fratalside.tecnosfera.info , https://github.com/fractalside

"The miracle is this: the more we share the more we have" 
                                           Leonard Nimoy 1931 - 2015
-------------------------------------------------------------------------
Copyright 2018 fractalside (Gonzalo Virgos Revilla)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package info.tecnosfera.fractalside.delta;

/**
 * CIF: Decreto 2423/1975 .. Orden 03/07/1998 		https://es.wikipedia.org/wiki/C%C3%B3digo_de_identificaci%C3%B3n_fiscal
 * NIF:						 Orden INT/2058/2008	https://es.wikipedia.org/wiki/N%C3%BAmero_de_identidad_de_extranjero
 * @author fractalside (Gonzalo Virgos Revilla)
 * @version 0.1 18415/1
 */
public class ValidaEs {

	public enum TipoNId {NO, NIF, NIE, CIF}
	
	private final String CONTROL_NIF 	= "TRWAGMYFPDXBNJZSQVHLCKE";
//	private final String PREFIJOS_NIE 	= "XYZ";
	private final String RELACION_CIB 	= "JABCDEFGHI";

	private final String PAUTA_NIF = "^[0-9]{8}[A-Z]{1}$";
	private final String PAUTA_NIE = "^(X([0-9]{8}))|([XYZ]([0-9]{7}))[A-Z]$"; 
	private final String PAUTA_CIF = "^([ABCDEFGHJNPQRSUVW]{1})([0-9]{7})[A-J0-9]$";
	
	/**
	 * 
	 * @param candidato
	 * @return
	 */
	public TipoNId validaDId(String candidato) {
		TipoNId tipo = TipoNId.NO;
		if (candidato == null) {
			//INTERRUPCION
			return tipo ;
		} 
		String preparado = candidato.trim().toUpperCase();
		tipo  = tipifica(preparado);
		if (TipoNId.NIF.equals(tipo)) {
			//La pauta deberían hacer que salgan siempre 2
			String[] trozos2 = trocea(TipoNId.NIF, preparado);
			tipo = (getControlNifNie(trozos2[0]).equals(trozos2[1])) ? TipoNId.NIF : TipoNId.NO;
		} else if (TipoNId.NIE.equals(tipo)) {
//			String[] trozos3 = trocea(TipoNId.NIE, preparado);
//			StringBuilder papel = new StringBuilder();
//			papel.append(String.valueOf(PREFIJOS_NIE.indexOf(trozos3[0])));
//			papel.append(papel.toString());
//			tipo = getControlNifNie(papel.toString()).equals(trozos3[2]) ? TipoNId.NIE : TipoNId.NO;
		} else if (TipoNId.CIF.equals(tipo)) {
//			String[] trozos3 = trocea(TipoNId.CIF, preparado);
//			String[] controles = getControlesCib(trozos3[0], trozos3[1]);
//			if (
//				(!controles[0].equals(trozos3[2]))
//				&& (controles.length < 2) || (!controles[0].equals(trozos3[2]))
//			) {
//				tipo = TipoNId.NO;
//			} 
		}

		return tipo;
	}
	
	/**
	 * 
	 * @param fuente
	 * @return
	 */
	private String getControlNifNie(String fuente) {
		//NumberFormatExcepcion? pero la pauta debería evitar esto.
		int num = Integer.parseInt(fuente); 
		String resultado = String.valueOf(CONTROL_NIF.charAt(num % 23));
		return resultado;
	}

	/**
	 * 
	 * @param trozo0
	 * @param trozo1
	 * @return
	 */
	protected String[] getControlesCib(String trozo0, String trozo1) {
		byte[] bytes = new String(trozo1).getBytes();
		for (int i = 0; i < bytes.length; i++) {
		}
//		Sumar los dígitos de las posiciones pares. Suma = A
		int a = bytes[1] + bytes[3] + bytes[5];
//		Para cada uno de los dígitos de las posiciones impares, multiplicarlo por 2 y sumar los dígitos del resultado.
//		Ej.: ( 8 * 2 = 16 --> 1 + 6 = 7 )
//		Acumular el resultado. Suma = B
		int b = sumaDigitos(bytes[0] * 2) + sumaDigitos(bytes[2] * 2) + sumaDigitos(bytes[4] * 2) + sumaDigitos(bytes[6] * 2);
//		Sumar A + B = C
		int c = a + b;
//		Tomar sólo el dígito de las unidades de C. Lo llamaremos dígito E.
		int e = c % 10;
//		Si el dígito E es distinto de 0 lo restaremos a 10. D = 10-E. Esta resta nos da D. 
//			Si no, si el dígito E es 0 entonces D = 0 y no hacemos resta.
		int d = (e > 0) ? 10 - e : 0; 
//		A partir de D ya se obtiene el dígito de control. 
//			Si ha de ser numérico es directamente D 
//			y si se trata de una letra se corresponde con la relación:
//				J = 0, A = 1, B = 2, C= 3, D = 4, E = 5, F = 6, G = 7, H = 8, I = 9

		String[] retorno;	

//		Será un NÚMERO si la entidad es A, B, E o H.		
		if ("ABEH".indexOf(trozo0) > -1) {
			retorno = new String[]{String.valueOf(d)};
//		Será una LETRA si la clave de entidad es P, Q, S o W. O también si los dos dígitos iniciales indican "No Residente"
		} else if (("PQSW".indexOf(trozo0) > -1) || ("00".equals(trozo1.substring(0,2))) ) {
			retorno = new String[]{String.valueOf(RELACION_CIB.charAt(d))};
//		Si no puede rer numero o letra
		} else {
			retorno = new String[]{String.valueOf(d), String.valueOf(RELACION_CIB.charAt(d))};
		}
		return retorno;
	}

	/**
	 * 
	 * @param numero
	 * @return
	 */
	private int sumaDigitos(int numero) {
        int entrada = numero, salida = 0;
        while (entrada > 0) {
            salida = salida + entrada % 10;
            entrada = entrada / 10;
        }
        return salida;
	}
	
	/**
	 * 
	 * @param candidato
	 */
	private TipoNId tipifica(String candidato) {
		TipoNId tipo = TipoNId.NO;
		if (candidato != null) {
			if (candidato.matches(PAUTA_NIF)) {
				tipo = TipoNId.NIF;
			} else if (candidato.matches(PAUTA_NIE)) {
				tipo = TipoNId.NIE;
			} else if (candidato.matches(PAUTA_CIF)) {
				tipo = TipoNId.CIF;
			}
		}
		return tipo;
	}
	
	/**
	 * 
	 * @param tipo
	 * @param nid
	 * @return
	 */
	private String[] trocea(TipoNId tipo, String nid) {
		String[] trozos = null; 
		if (TipoNId.NIF.equals(tipo)) {
			trozos = new String[]{
				nid.substring(0, nid.length() - 1),
				nid.substring(nid.length() - 1)
			};
		} else if ((TipoNId.NIE.equals(tipo))||(TipoNId.CIF.equals(tipo))) {
			trozos = new String[]{
				nid.substring(0, 1),
				nid.substring(1, nid.length() - 1),
				nid.substring(nid.length() - 1)
			};
		}
		return trozos;
	}
	
}
