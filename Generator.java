import java.util.*;

public class Generator {
	public Queue<Character> q;
	
	//creates new generator object
	public Generator(){
		this.q = new LinkedList<Character>();
	}
	
	//inserts c characters into the queue
	public void generate(int c){
		// "(x)28" means concatenate x with itself between 2 and 8 times.
		// 0 means pick a random pattern from l0, 1 means pick a random pattern from l1...
		String[] l0 = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
		String[] l1 = {"kw","aaab","cd","eqe","rr","uv0z","l0p","(m)28(y)28", "day","banana"};
		String[] l2 = {l1[1]+l1[2]+l1[3], "("+l1[4]+")46",l1[5]+"1"+l1[6], l1[9]+l1[8],l1[0]+l1[7]+l1[8],l1[2]+l1[4]+l1[2]+l1[4]+l1[3],"good"+l1[8]};
		String[] l3 = {l2[5]+l2[0]+l2[2]+l2[4]+"palm","so"+l2[1]+l2[0]+l2[3],l1[5]+l1[5]+l1[2]+l2[0]+l2[2]+l2[4]+l1[2]+l1[5]+l1[5],l2[5]+l2[1]+"(0)09"+l2[6]+l2[5],l2[5]+l2[0]+l2[3],l1[1]+l2[3]+l2[0]+l2[3]+"2","("+l2[1]+")46"};
		String[] l4 = {"("+l3[0]+")35",l3[1]+l3[1]+l2[6],l3[4]+l3[5]+l3[1],l3[2]+l3[5],l3[3]+l3[6]+l3[3],l3[6]+"3"};
		String [][] ph = {l0,l1,l2,l3,l4}; //pattern hierarchy
		String toSend = "";
		while(toSend.length() < c){
			int x = (int) (Math.random()*256);
			
			if(x<1){
				String pattern  = l4[(int) (Math.random()*l4.length)];
				toSend += createString(pattern, ph);
				continue;
			}
			if(x<4){
				String pattern  = l3[(int) (Math.random()*l3.length)];
				toSend += createString(pattern, ph);
				continue;
			}
			if(x<16){
				String pattern  = l2[(int) (Math.random()*l2.length)];
				toSend += createString(pattern, ph);
				continue;
			}
			if(x<64){
				String pattern  = l1[(int) (Math.random()*l1.length)];
				toSend += createString(pattern, ph);
				continue;
			}
			if(x<256){
				String pattern  = l0[(int) (Math.random()*l0.length)];
				toSend += createString(pattern, ph);
				continue;
			}
		}
		for(int i = 0; i < toSend.length(); i++){
			q.add(toSend.charAt(i));
		}
		
		
		
		return;
	}
	private String createString(String p, String[][] ph){
		String toSend = "";
		for(int i = 0; i < p.length(); i++){
			if(Character.isLetter(p.charAt(i))){
				toSend+=p.charAt(i);
				continue;
			}
			if(p.charAt(i) == '0'){
				toSend+=createString(ph[0][(int) (Math.random()*ph[0].length)], ph);
				continue;
			}
			if(p.charAt(i) == '1'){
				toSend+=createString(ph[1][(int) (Math.random()*ph[1].length)], ph);
				continue;
			}
			if(p.charAt(i) == '2'){
				toSend+=createString(ph[2][(int) (Math.random()*ph[2].length)], ph);
				continue;
			}
			if(p.charAt(i) == '3'){
				toSend+=createString(ph[3][(int) (Math.random()*ph[3].length)], ph);
				continue;
			}
			if(p.charAt(i) == '('){
				int start = i;
				int a = 0;
				i++;
				while(!((p.charAt(i) == ')') && a == 0)){
					if(p.charAt(i) == '(')
						a++;
					if(p.charAt(i) == ')')
						a--;
					i++;
				}
				int end = i;
				int lb = p.charAt(i+1)-'0';
				int ub = p.charAt(i+2)-'0';
				int n = (int) (Math.random()*(ub-lb+1));
				i=i+2;
				for(int j = 0; j<lb+n; j++){
					toSend+=createString(p.substring(start+1,end), ph);
				}
				continue;
			}
		}
		return toSend;
	}
}
// idea: have it only generate l4 patterns
// idea: add a lot more patterns to each level.

