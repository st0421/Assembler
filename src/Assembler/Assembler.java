
/* 
public class Assembler {
	   public static void main(String[] args) {
	      instructionCycle cpu = new instructionCycle();
	      cpu.printCycle();
	   
	   }
	}
*/

package Assembler;


import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;


public class Assembler {
	   public static void main(String[] args) {
	      First_Pass pass1 = new First_Pass();
	      
	      pass1.input();
	      pass1.firstpass();
	      
	      for (int i=0; i<50; i++) {			//명령어 들어간 배열 출력 테스트 
	    	  System.out.println("code : "+pass1.code[i]);
	      }
	      for ( String key : First_Pass.ht.keySet()) { 
	    	  System.out.println("Key : " + key + ", Value : " + First_Pass.ht.get(key)); 
	      }
	      System.out.println(pass1.ht.get("B"));
	
	   }
}

/*	class Loader {
		/* "사용자가 텔레타이프 상에서 기호 프로그램을 타이핑하면 로더(Loader) 프로그램은 기호 프로그램을 메모리 안으로 입력시킨다." (p.148) 
		Scanner type = new Scanner(System.in);
		static String s;
		String[] code = new String[100]; 	//사용자가 입력한 코드 넣을 배열 생성 
		int size = code.length;				//size = 코드 라인 수 
		
		
		public void input() {			//사용자의 입력을 받는 input 메소드
			int i = 0;
			do {
				s = type.nextLine();	
				code[i] = s;
				++i;
			} while(!(s.contains("END"))); 	//END 입력 시 종료 input 메소드 종료
		}
	}
	
클래스 따로 만드는 게 여러모로 불편한 것 같아서 그냥 Loader 클래스 없애고 First_Pass 클래스에 input 메소드 넣었습니다*/


	class First_Pass {
		static Hashtable<String, Integer> ht = new Hashtable<>();
		Scanner type = new Scanner(System.in);
		private String s;
		int size = 100,count=0;						//size = 코드 최대 몇 줄?
		String[] input = new String[size];
		String[] code = new String[size]; 			//code[] = 사용자가 입력한 코드 넣을 배열
		
		static int LC,seq;						//LC = 명령어의 위치를 추적하기 위한 location counter
		private boolean bool;					//명령어에 Label이 존재하면 bool = true
		private static String Value,Label;
		
		public int getLC() {
			return LC;
		}
		public boolean getBool() {
			return bool;
		}
		public void setBool() {
			this.bool = true;
		}
		
		
		public void input() {			//사용자의 입력을 받는 input 메소드
			int i = 0;
			do {
				s = type.nextLine();	
				input[i++] = s;
			} while(!(s.contains("END"))); 	//END 입력 시 종료 input 메소드 종료
			

		}
		

		void firstpass() {		//퍼스트패스 루틴 시작
			
			LC = 0;					//set LC as 0
			seq=0;
			for(int i=0; i<100; i++){		//배열 원소 개수만큼 반복
				if(this.input[i]==null)
					continue;
				
				if(this.input[seq].contains(",")) {			//라벨 유무 판별 조건문 
					setBool();					//명령어에 콤마가 있으면 bool을 true로 설정 
					int idx = this.input[seq].indexOf(",");
					Label = this.input[seq].substring(0,idx);	//코드에 있는 라벨 명령어 기호를 Label에 저장 
					Value = this.input[seq].substring(idx+2);
			
				}
				
				if(bool == true) {					//콤마가 있으면,
					hashtable(Label, LC);					//hashtable 메소드 호출
					this.code[LC++]=Value;
					seq++;
					bool=false;
				}
				
				else {							//콤마가 없으면(라벨이 없으면), 명령어 필드의 기호를 체크
					String code = this.input[seq];
					String[] codecut = code.split("\\s");		//코드를 공백으로 구분하여 split
				
					switch (codecut[0]) {				//명령어 필드가 
					case "ORG":					//ORG이면 
					{
						LC = Integer.parseInt(codecut[1]);	//LC를 ORG 다음에 오는 숫자로 설정해주고 
						seq++;
						continue;
					}
					default: 					//둘 다 아니면 
					{
						this.code[LC++]=input[seq++];
						 					//LC increment 해주고 다시 다음 코드 읽어서 라벨 판별 
						continue;		//다시 다음 코드 읽어서 라벨 판별
					}
					case "END":					//END면 
						break;					//end first pass and go to second pass
						
					}
					break;
				}
			}
		}
		
		
		static void hashtable(String key,int value) {		//주소-기호 테이블 제작 메소드(약식) (해시테이블에 라벨 기호와 LC값만 저장)
			ht.put(key, value);			//메소드가 호출되면 해시테이블에 라벨 기호와 LC값 넣기 
			
		}
	}
	
	
	


/////////////////////////////////////////////////////////////


	class instructionCycle{
	   private int DR, AR, AC, IR, INPR, OUTPR, TR, SC, indirection, head, I, E, S, D7, INpR, FGI, OUTR, FGO, IEN,HLT;   //각각의 메모리 또는 레지스터
	   int h,END; //h는 HLT의 LC. END는 마지막 LC
	   int PC = 0;         //프로그램 카운터
	   private int[] M = new int[5000];
	   String var[]= {"A","B","C"};
	   private String symbol,operation;


	   instructionCycle(){                
	      setMemory();
	      showMemory();
	   }
	   


	   private void setMemory(){        
		   
	      M[0] = 0x2004;
	      M[1] = 0x1005;
	      M[2] = 0x3006;
	      M[3] = 0x7001;
	      M[4] = 0x0053;
	      M[5] = 0xffe9;
	      M[6] = 0x0000;
	      h=3;
	      END=6;
	      /*
	      String[] var = new String[END-h];
	      String example[]= {"A","B","C"};
	      for(int i=0;i<var.length;i++) {
	    	  var[i]=example[i];
	      }*/
	      
	   }
	   
	   private void showMemory() {
		   System.out.println("Location\tInstruction");
		   for(int i=0;i<7;i++) {
			   System.out.println("  "+i+"\t\t "+Integer.toHexString(M[i]+0x100000).substring(2).toUpperCase());
		   }
	   }


	   private String symbolCheck(int a) {                          //instruction 값인지 체크해 심볼을 문자열로 반환하는 메소드
	      //instruction 값인지 체크해 심볼을 문자열로 반환하는 메소드


		 
	      head = (short) ((short)a / 0x1000) ; 
	      //16진수의 맨 앞을 얻음 ex) 0x3006 이면 head = 6
	      D7 = 0;


	      indirection = (short) (head / 8); 
	      //indirect bit 를 얻음 ex) 0~7로 시작하면 0, 8~f로 시작하면 1


	      symbol = "";


	      String address = Integer.toHexString(a + 0x10000).substring(2);   
	      //주소값 넣는다.


	      if(head == 7){ // 7xxx 
	         address = "   "; //주소를 없앤다
	         D7 = 1;
	         operation = "'Register' reference operation";
	         switch( a & 0x0FFF){ //뒷자리를 비교한다.
	         case 0x800:
	            symbol = "CLA";
	            break;
	         case 0x400:
	            symbol = "CLE";
	            break;
	         case 0x200:
	            symbol = "CMA";
	            break;
	         case 0x100:
	            symbol = "CME";
	            break;
	         case 0x80:
	            symbol = "CIR";
	            break;
	         case 0x40:
	            symbol = "CIL";
	            break;
	         case 0x20:
	            symbol = "INC";
	            break;
	         case 0x10:
	            symbol = "SPA";
	            break;
	         case 0x8:
	            symbol = "SNA";
	            break;
	         case 0x4:
	            symbol = "SZA";
	            break;
	         case 0x2:
	            symbol = "SZE";
	            break;
	         case 0x1:
	            symbol = "HLT";
	            break;
	         }
	      }
	      else if(head == 0xf){ // fxxx
	         address = "   "; //주소를 없앤다
	         D7 = 1;
	         operation = "'I/O' reference operation";
	         switch( a & 0x0FFF){ //뒷자리를 비교한다.
	         case 0x800:
	            symbol = "INP";
	            break;
	         case 0x400:
	            symbol = "OUT";
	            break;
	         case 0x200:
	            symbol = "SKI";
	            break;
	         case 0x100:
	            symbol = "SKO";
	            break;
	         case 0x80:
	            symbol = "ION";
	            break;
	         case 0x40:
	            symbol = "IOF";
	            break;
	         }
	      }
	      else{
	    	  operation = "'Memory' reference operation";
	         switch( head % 8){ // 
	         case 0: //  (a=0xxx, a=8xxx)
	            symbol = "AND";
	            break;
	         case 1: // (1xxx, 9xxx)
	            symbol = "ADD";
	            break;
	         case 2: // (2xxx, Axxx)
	            symbol = "LDA";
	            break;
	         case 3: // (3xxx, Bxxx)
	            symbol = "STA";
	            break;
	         case 4:
	            symbol = "BUN";
	            break;
	         case 5:
	            symbol = "BSA";
	            break;
	         case 6:
	            symbol = "ISZ";
	            break;
	         }
	         if (indirection == 1) // indirect bit 가 1 이면 간접 주소임을 표시한다.
	            symbol = "I " + symbol;
	      }


	      return symbol + "  " + address; // symbol + 주소값 반환


	   }


	 
	   private void T0(){               // T0 일 때
	      AR = (short) PC;
	   }


	   private void T1(){              // T1 일 때
	      IR = M[AR]; PC = (short) (PC + 1);
	   }


	   private void T2(){             //T2 일 때
	      symbol = symbolCheck(M[AR]);
	      AR = (short) (IR & 0x0fff); I = indirection;
	   }


	   private void instructionCheck() {  //인스트럭션 체크하고 명령어에 따라 T3, T4, T5 ... 할일 결정
	       symbol = symbol.substring(0,3);
		   if(head == 7){      //D7 = 1 이고, I = 0 인경우
	         switch(symbol) {
	         case "CLA":
	            AC = 0;
	           
	            break;
	         case "CLE":
	            E = 0;
	            
	            break;
	         case "CMA":
	            AC = (short) ~(short)AC;
	          
	         case "CME":
	            if(E == 0){
	               E = 1;
	            }
	            else{
	               E = 0;
	            }
	       
	            break;
	         case "CIR":
	            E = (short) (AC & 0x0001); AC = (short) ((short)AC >> 1);
	           
	            break;
	         case "CIL":
	            E = I; AC = (short) ((short)AC << 1);
	           
	            break;
	         case "INC":
	            AC = (short) ((short)AC + (short)1);
	         
	            break;
	         case "SPA":
	            if(I == 0)
	               PC = (short) ((short)PC + (short)1);
	       
	            break;
	         case "SNA":
	            if(I == 1)
	               PC = (short) (PC + 1);
	           
	            break;
	         case "SZA":
	            if(AC == 0)
	               PC = (short) (PC + 1);


	            break;
	         case "SZE":
	            if(E == 0)
	               PC = (short) (PC + 1);


	            break;
	         case "HLT":
	            S = 0;
	            HLT=1;
	            
	         }
	         SC = 0;


	      }
	      else if(head == 0xf){  //D7 = 1 이고, I = 1 인경우


	         switch(symbol){
	         case "INP":


	            AC = INPR; FGI = 0;
	            break;
	         case "OUT":


	            OUTR = (short) (0x00ff & AC); FGO = 0;
	            break;
	         case "SKI":


	            if(FGI == 1){
	               PC += 1;
	            }
	            break;
	         case "SKO":


	            if(FGO==1){
	               PC+=1;
	            }
	            break;
	         case "ION":


	            IEN = 1;
	            break;
	         case "IOF":


	            IEN = 0;
	            break;
	         }


	      }
	      else{
	         if(I == 1)
	            AR = M[AR];


	         switch(symbol){
	         case "AND":


	            DR = M[AR];


	            AC = (short) (AC & DR);
	            SC = 0;


	            break;
	         case "ADD":
	            int Cout = 0;


	            DR = M[AR];


	            if(AC < 0 && AC + DR < 0 && DR > 0 || AC > 0 && AC + DR > 0 && DR < 0){ //오버플로우가 일어났을때
	               Cout = 1;
	            }
	            AC = (short) (AC + DR);
	            E = (short) Cout;
	            SC = 0;


	            break;
	         case "LDA":
	            DR = M[AR];


	            AC = DR;
	            SC = 0;


	            break;
	         case "STA":


	            M[AR] = AC;
	            SC = 0;


	            break;
	         case "BUN":


	            PC = AR;
	            SC = 0;


	            break;
	         case "BSA":


	            M[AR] = PC;
	            AR = (short) (AR + 1);


	            PC = AR;
	            SC = 0;


	            break;
	         case "ISZ":


	            DR = M[AR];


	            DR = (short) (DR + 1);


	            M[AR] = DR;
	            if(DR == 0){
	               PC = (short) (PC + 1);
	            }
	            SC = 0;


	         }
	      }
	   }


	   void printCycle(){//명령어 사이클을 눈에 보이게 프린트 해준다.
		   int count=h; 
		   while(count>=0){
			    System.out.println();
	            System.out.println("-- Location : " 
	            + Integer.toHexString(PC) );
	            System.out.println("01.입력 = "+Integer.toHexString(M[PC]+0x100000).substring(2).toUpperCase());
	            T0();
	            T1();
	            T2();
	            instructionCheck();
	            System.out.println("02.명령어 형식 = "+operation);
	            System.out.println("03.Symbol ="+symbol.toUpperCase());
	            System.out.println("AR["+Integer.toHexString(AR + 0x10000).substring(2).toUpperCase() +"], "
	            +"PC["+Integer.toHexString(PC + 0x10000).substring(2).toUpperCase() +"], "
	            		+"DR["+Integer.toHexString(DR + 0x10000).substring(2).toUpperCase() +"], "
	            +"AC["+Integer.toHexString(AC + 0x10000).substring(2).toUpperCase() +"],"
	            +"IR["+Integer.toHexString(IR + 0x10000).substring(2).toUpperCase()+"], "
	            +"TR["+Integer.toHexString(TR + 0x10000).substring(2).toUpperCase()+"]");


	            System.out.println();
	           
	            count= count-1;
	       }
	   		System.out.println();
	   		for(int i=0;i<END-h;i++) { //h는 HLT의 LC. END는 메모리 마지막.
	   		System.out.print(var[i]+" : "+(short)M[h+1+i]+",\t");
	   		}
	   		System.out.println();
	   		for(int i=0;i<=END;i++) {
	   			System.out.print("M["+i+"] : "+Integer.toHexString(M[i]+ 0x10000).substring(1).toUpperCase()+"\t");
	   		}
	   }
	} 
