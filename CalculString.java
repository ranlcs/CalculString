// package mesClasses.util;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import java.util.Vector;
import java.util.Set;
import java.util.Iterator;

public class CalculString{
	private static String[] listeChiffre={"0","1","2","3","4","5","6","7","8","9"};
	private static String[] fonction={"cos","sin","tan","ln","exp"};
	private static String[] autre={" ","(",")","E",".","^"};
	private static Vector<String> debutFonction = new Vector<String>();
	private static Vector<String> autreFonction = new Vector<String>();
	private static String aAfficher = "";
	private static String[] erreur = {	"Virgule en trop",
										"Puissance en trop",
										"Fonction inconnue",
										"Ceci n'est pas un nombre",
										"C'est quoi ca",
										"C'est ca la fin ?Tu es sur?",
										"Il manque un espace"
									};
	private static boolean generer = false;
	private static int numero=-1;
	private static int typeErreur=-1;


	private CalculString(){
		
	}

	private static int misyParenthese(String expr,int debut){
		boolean misy = false;
		int i=debut;
		while(i<expr.length() && !misy){
			if(expr.charAt(i)=='('){
				i--;
				misy=true;
			}
			i++;
		}
		return i;
	}

	

	private static double fonctionParticuliere(String expr){
		int esp = expr.indexOf(" ");
		String fonc = expr.substring(0,esp);
		double nb = Double.parseDouble(expr.substring(esp+1));
		double resultat = 0;
		boolean neg = false;
		if(expr.charAt(0)=='-' || expr.charAt(0)=='+'){
			if(expr.charAt(0)=='-')
				neg = true;
			fonc = expr.substring(1,esp);
		}
		switch(fonc){
			case "cos":resultat = Math.cos(nb);
			break;
			case "sin":resultat = Math.sin(nb);
			break;
			case "tan":resultat = Math.tan(nb);
			break;
			case "ln":resultat = Math.log(nb);
			break;
			case "exp":resultat = Math.exp(nb);
			break;
		}
		if(neg) 
			resultat = -resultat;
		return resultat;
	}

	private static String fonction(String nb){

		// PUISSANCE -45^6=???? ET (-45)^6= ????? sur prog meme

		int puiss = nb.indexOf("^");
		double resultat = 0;
		if(puiss!=-1){
			double nb1=Double.parseDouble(nb.substring(0,puiss));
			double nb2=Double.parseDouble(nb.substring(puiss+1));
			resultat = Math.pow(nb1,nb2);
		}
		else{
			if(nb.charAt(0)=='-' || nb.charAt(0)=='+'){
				if(estChiffre(nb.charAt(1)))
					resultat = Double.parseDouble(nb);
				else
					resultat = fonctionParticuliere(nb);
			}
			else{
				if(estChiffre(nb.charAt(0)))
					resultat = Double.parseDouble(nb);
				else
					resultat = fonctionParticuliere(nb);
			}
		}
		return String.valueOf(resultat);
	}

	private static Vector<String> fonction(Vector<String> nbs){
		Vector<String> temp = new Vector<String>();
		for(int i=0;i<nbs.size();i++){
			temp.add(fonction(nbs.elementAt(i)));

		}
		return temp;
	}


	private static double simpleCalcule(String expr){
		int posLastOp = -1;
		Vector<String> nombre = new Vector<String>();
		Vector<String> operateur = new Vector<String>();
		for(int i=0;i<expr.length();i++){
			if(estOperateur(expr,i)){
				nombre.add(expr.substring(posLastOp+1,i));
				operateur.add(expr.substring(i,i+1));
				posLastOp = i;
			}
		}
		nombre.add(expr.substring(posLastOp+1));
		nombre = fonction(nombre);
		for(int i=0;i<operateur.size();i++){
			String temp = operateur.get(i);
			switch(temp){
				case "*":case "/":{
					double nb1 = Double.parseDouble(nombre.get(i));
					double nb2 = Double.parseDouble(nombre.get(i+1));
					double valiny=0;
					if(temp.equals("*"))
						valiny = nb1*nb2;
					else 
						valiny = nb1/nb2;
					nombre.set(i,String.valueOf(valiny));
					nombre.remove(i+1);
					operateur.remove(i);
					i--;
				}
				break;
				case "+":case "-":{
					int j=1;
					while((j+i)<operateur.size() && (!operateur.get(j+i).equals("+") && !operateur.get(j+i).equals("-")))
					 	j++;
					String t = "";
					for(int k=1;k<j;k++){
						t = t+nombre.get(i+1)+operateur.get(i+1);
						nombre.remove(i+1);
						operateur.remove(i+1);
					}
					t = t+nombre.get(i+1);
					if(t.length()>1){
						double nbTemp = simpleCalcule(t);
						nombre.set(i+1,String.valueOf(nbTemp));
					}
					double nb1 = Double.parseDouble(nombre.get(i));
					double nb2 = Double.parseDouble(nombre.get(i+1));
					double valiny=0;
					if(temp.equals("+"))
						valiny = nb1+nb2;
					else 
						valiny = nb1-nb2;
					nombre.set(i,String.valueOf(valiny));
					nombre.remove(i+1);
					operateur.remove(i);
					i--;
				}
			}
		}
		return Double.parseDouble(nombre.get(0));
	}

	private static double calculeAvecParenthese(String expr){
		if(!generer){
			generer();
			generer=true;
		}
		String expression = expr;
		int numParenthese = misyParenthese(expression,0);
		while(numParenthese!=expression.length()){
			int i=numParenthese+1,parenth=1;
			while(i<expression.length() && parenth!=0){
				if(expression.charAt(i)=='(')
					parenth++;
				else if(expression.charAt(i)==')')
					parenth--;
				i++;
			}
			String temp = expression.substring(numParenthese+1,i-1);
			expression = expression.substring(0,numParenthese)+calculeAvecParenthese(temp)+expression.substring(i);
			numParenthese = misyParenthese(expression,numParenthese);
		}
		return simpleCalcule(expression);
	}

	
	public static String calcule(String expr){
		String ret = "";
		verifier(expr);
		if(typeErreur!=-1)
			ret = erreur[typeErreur];
		else ret = String.valueOf(calculeAvecParenthese(expr));
		return ret;
	}



	//MAIN POUR ESSAYER 
	public static void main(String[] args){
		JFrame fr = new JFrame();
		fr.setSize(300,400);
		fr.setLayout(new FlowLayout());
		JTextField tf = new JTextField(25);
		JLabel res = new JLabel();
		fr.add(tf);
		fr.add(res);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tf.addKeyListener(new KeyListener(){
			@Override
			public void keyPressed(KeyEvent k){
				if(k.getKeyCode()==KeyEvent.VK_ENTER){
					String cal = tf.getText();
					verifier(cal);
					if(typeErreur!=-1)
						res.setText(erreur[typeErreur]);
					else res.setText(String.valueOf(calculeAvecParenthese(cal)));
					// tf.setText(tf.getText()+aAfficher);
					res.setText(calcule(tf.getText()));
				}
				else if(k.getKeyCode()==KeyEvent.VK_ESCAPE){
					System.exit(0);
				}
			}
			@Override
			public void keyReleased(KeyEvent k){}
			@Override
			public void keyTyped(KeyEvent k){}
		});
		fr.setVisible(true);
	}









	// POUR LA VERIFICATION

	private static void generer(){
		
		for(int i=0;i<fonction.length;i++){
			if(!existant(debutFonction,String.valueOf(fonction[i].charAt(0))))
				debutFonction.add(String.valueOf(fonction[i].charAt(0)));
			for(int j=1;j<fonction[i].length();j++)
				if(!existant(autreFonction,String.valueOf(fonction[i].charAt(j))))
					autreFonction.add(String.valueOf(fonction[i].charAt(j)));
			// OU String[] = string.split("");
		}

		for(int i=0;i<autreFonction.size();i++)
			aAfficher = aAfficher+autreFonction.get(i)+",";
	}

	private static boolean existant(Vector<String> ve,String v){
		boolean existant = false;
		int i=0;
		while(i<ve.size() && !existant){
			if(ve.get(i).equals(v))
				existant = true;
			i++;
		}
		return existant;
	}
	private static boolean existant(String[] ta,char c){
		int i=0;
		boolean existant = false;
		while(i<ta.length && !existant){
			if(ta[i].equals(String.valueOf(c)))
				existant=true;
			i++;
		}
		return existant;
	}
	private static boolean estFonction(String fct){
		boolean estFonction=false;
		int i=0;
		while(i<fonction.length && !estFonction){
			if(fct.equals(fonction[i]))
				estFonction=true;
			i++;
		}
		return estFonction;
	}

	private static boolean estAutreFonction(String f){
		return existant(autreFonction,f);
	}
	private static boolean estAutreFonction(char c){
		return estAutreFonction(String.valueOf(c));
	}
	private static boolean estDebutFonction(String f){
		return existant(debutFonction,f);
	}
	private static boolean estDebutFonction(char c){
		return estDebutFonction(String.valueOf(c));
	}
	private static boolean estChiffre(char c){
		return existant(listeChiffre,c);
	}
	private static boolean estAutre(char f){
		return existant(autre,f);
	}
	private static boolean estOperateur(char c){
		boolean estOperateur = false;
		if(c=='+' || c=='-' || c=='*' || c=='/')
			estOperateur = true;
		return estOperateur;
	}

	private static boolean estOperateur(String expr,int sur){
		boolean resultat=false;
		switch(expr.charAt(sur)){
			case '*':case '/':
				if(sur+1<expr.length() && expr.charAt(sur+1)!='*' && expr.charAt(sur+1)!='/')
					resultat = true;
				if(sur-1>=0 && (expr.charAt(sur-1)=='+' || expr.charAt(sur-1)=='-' || expr.charAt(sur-1)=='*' || expr.charAt(sur-1)=='/'))
					resultat = false;
			break;
			case '+':case '-':{
				if(sur+1<expr.length() && expr.charAt(sur+1)!='*' && expr.charAt(sur+1)!='/')
					resultat = true;
				if(sur-1>0 && expr.charAt(sur-1)==' ')
					resultat = false;
				if(sur-1>=0 && (estOperateur(expr.charAt(sur-1))))
					resultat = false;
				if(sur==0)
					resultat = false;
				if(sur-1>=0 && (expr.charAt(sur-1)=='e' || expr.charAt(sur-1)=='E'))
					resultat=false;
			}
			break;
		}
		return resultat;
	}
	private static boolean estSigne(String exp,int i){
		boolean estSigne=false;
		if(exp.charAt(i)=='-' || exp.charAt(i)=='+'){
			if(i==0)
				estSigne=true;
			else if(exp.charAt(i-1)=='e' || exp.charAt(i-1)=='E' || exp.charAt(i-1)==' ' || estDebutFonction(exp.charAt(i-1)) || estOperateur(exp,i-1))
				estSigne=true;
		}
		return estSigne;
	}


	// MANQUE ENCORE BEAUOUP DE CHOSE EX:FIN PARENTH,AVANT?????



	private static void verifier(String expr){
		if(!generer){
			generer();
			generer=true;
		}

		boolean virgule = false,puissance=false;
		typeErreur = -1;
		numero=0;
		int debut=-1;
		if(!estChiffre(expr.charAt(expr.length()-1)) && expr.charAt(expr.length()-1)!=')'){
				typeErreur=5;
				numero=expr.length()-1;
		}
		while(numero<expr.length() && typeErreur==-1){
			boolean estChiffre = estChiffre(expr.charAt(numero));
			boolean estOperateur = estOperateur(expr,numero);
			boolean estSigne = estSigne(expr,numero);
			boolean estDebutFonction = estDebutFonction(expr.charAt(numero));
			boolean estAutre = estAutre(expr.charAt(numero));
			boolean estAutreFonction = estAutreFonction(expr.charAt(numero));
			char temp =expr.charAt(numero);
			if(!estChiffre && !estOperateur && !estSigne && !estDebutFonction && !estAutre && !estAutreFonction){
				typeErreur = 4;
				numero--;
			}
			else if(debut!=-1 && (!estAutreFonction || temp==' ')){
				if((temp==' ')){
					if(!estFonction(expr.substring(debut,numero))){
						typeErreur=2;
						numero=debut;
					}
					else
						debut=-1;
				}
				else{
					numero--;
					typeErreur=6;
				}
			}
			else if(estOperateur){
				virgule=false;
				puissance=false;
			}
			else if(temp=='.'){
				if(virgule){
					typeErreur=0;
					numero--;
				}
				virgule=true;
			}
			else if((temp=='e' || temp=='E') && numero+1<expr.length() && expr.charAt(numero+1)!='x'){
				if(puissance){
					typeErreur=1;
					numero--;
				}
				puissance=true;
			}
			else if(estDebutFonction && debut==-1){
				debut=numero;
			}
			numero++;
		}
	}
}

