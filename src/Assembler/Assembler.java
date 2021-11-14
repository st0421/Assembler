package Assembler;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;


public class Assembler {
	
	   public static void main(String[] args) {
	      First_Pass pass1 = new First_Pass();  //FirstPass
	      
	      pass1.input();
	      pass1.firstpass();
	      
	      transfer trans = new transfer(pass1.code,pass1.startpoint,pass1.LC);
	     
	      instructionCycle cpu = new instructionCycle(trans.M_temp);
	      cpu.printCycle();
	   }
}

	class First_Pass {
		static Hashtable<String, Integer> ht = new Hashtable<>(); //라벨명과 라벨 유효주소를 저장하기위한 해시테이블
		Scanner type = new Scanner(System.in);
		private String s;		
		String[] input = new String[size];			//input[] = 사용자가 입력한 코드 넣을 배열 
		String[] code = new String[size]; 			//code[] = ORG로 이동하여 인덱스(LC)에 맞춰서 값을 넣은 배열( LC에 맞춰서 인덱스 이동 후 input에 넣은 값 중 라벨을 제외하고 저장)
		
		static int startpoint,LC,seq, size = 5000; 
		//startpoint = transfer 클래스에 ORG값(저장시작하는 인덱스(LC))을 전달해주기위함
		//LC = 명령어의 위치를 추적하기 위한 location counter
		//seq = code[LC]에 무사히 값 전달하도록하는 counter   (input[seq])
		//size = 배열크기(메모리)

		boolean bool;					//명령어에 Label이 존재하면 bool = true
		static String Value,Label;		//Label = opcode||Label||HEX||DEC. Value = operand 

		int length=0;
		public void input() {			//사용자의 입력을 받는 input 메소드
			do {
				s = type.nextLine();	
				input[length++] = s;
			} while(!(s.contains("END"))); 	//END 입력 시 종료 input 메소드 종료
		}
		

		void firstpass() {		//퍼스트패스 루틴 시작
			int sp_check=0;
			startpoint=LC = 0;				//set LC as 0,  startpoint = transfer 클래스에 ORG값(저장시작하는 인덱스(LC))을 전달해주기위함
			seq=0; 							//seq = code[LC]에 무사히 값 전달하도록하는 counter   (input[seq])
			for(int i=0; i<length; i++){		//배열 원소 개수만큼 반복

				if(this.input[seq].contains(",")) {			//라벨 유무 판별 조건문 
					bool = true;				//명령어에 콤마가 있으면 bool을 true로 설정 
					int idx = this.input[seq].indexOf(","); 	//,기준으로 나누기위해 ,의 인덱스 따로 저장
					Label = this.input[seq].substring(0,idx);	//코드에 있는 라벨 명령어 기호를 Label에 저장 
					Value = this.input[seq].substring(idx+2);	//라벨의 operand저장 
			
				}
				
				if(bool == true) {					//콤마가 있으면,
					hashtable(Label, LC);			//hashtable 메소드 호출 key=라벨, Value=LC(해당라벨 유효주소)
					this.code[LC++]=Value;			//위에서 저장한 유효주소에 라벨의 값 저장
					seq++;							//다음으로 이동
					bool=false;						//bool초기화
				}
				
				else {								 	 //콤마가 없으면(라벨이 없으면), 명령어 필드의 기호를 체크
					String code = this.input[seq]; 		 //일단 저장한 문자열 가져옴 (ex- ORG 100, LDA Y .._
					String[] codecut = code.split("\\s");//코드를 공백으로 구분하여 split
				
					switch (codecut[0]) {				//명령어 필드 (opcode) 
					case "ORG":					//ORG이면 
					{
						LC = Integer.parseInt(codecut[1],16);	//LC를 ORG 다음에 오는 숫자로 설정해주고 
						if(sp_check==0) {
							startpoint= LC;
							sp_check=1;
						}//다른 클래스에 전달하기위해 starpoint 설정(ORG로 지정한 LC 시작값)
						seq++;
						continue;
					}
					default: 								//ORG가 아니면 
					{
						this.code[LC++]=input[seq++];   //instruction 저장

						continue;
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

	
	
	class transfer{ 										//입력한 배열의 문자열값 16진수로 바꾸기
		static int dec_hex=0,I=0,ORG=0,HLT=0,END=0,No_op=0;
		//dec_hex 10진수인지 16진수인지 판별
		//I==0(direct),1(indirect) 
		//ORG = 배열 시작 인덱스 HLT = HLT값의 인덱스, END = 유효한 값이 있는 인덱스의 끝. No_op = 레지스터명령, I/O명령과같이 operand가 없는 명령 확인
		int[] M_temp = new int[5000];

		transfer(String[] code,int sp,int ep){   					//입력한 문자열배열, 시작 인덱스, 끝 인덱스 받아옴
			int idx,lc,idx2,result_op,result_adr,trans=0;	
			//idx,idx2 = 문자열을 나누는 기준
			//lc = 반복문 돌리기위한 counter (code[] -> M_temp[])
			//result_op,adr = idx로 opcode, operand 나눈 값들
			//trans = result_op,adr 합쳐서 4자리 16진수로 만듬. -> M_temp[]에 넣기위함
			String symbol=" ",operand=" ";
			lc =ORG= sp; // (ex - ORG 100입력시 sp==100 )ORG는 instructionCycle클래스에 보내기 위함 
			END=ep;		 //  END도 동일 
			while(lc<ep) {		//배열 중 값이 들어있는 부분만 반복문 진행
				I=0;			//direct로 일단 초기화
				if(code[lc]==null) {
					lc++;
					continue;
				}
				idx= code[lc].indexOf(" ");			 //공백의 인덱스를 가져와서 그 인덱스르 기준으로 나눔. 
				if(idx==-1) { 						 //해당 문자열 없으면 -1 리턴 명령어 3자리만 따옴
					symbol=code[lc].substring(0,3);
					No_op=1;						 //초기화
				}
				else {
					symbol = code[lc].substring(0,idx);	 //코드에 있는 라벨 명령어 기호 저장 
				}
				if(symbol.equals("HLT")) {
					HLT=lc;					//HLT의 인덱스 따로 저장(instructionCycle에서 변수관리를 위함)
				}

				
				//피연산자부분 가져오기
				if(No_op==0) {
					operand = code[lc].substring(idx+1);
					idx2= operand.indexOf(" ");
					if(idx2!=-1) {
						if(operand.substring(idx2+1).equals("I")) {
							I=1;
							operand= operand.substring(0,idx2);
						}
					}
				}
				else {
					operand = " ";
					No_op=0;
				}

				
				//symbol check
				result_op = trans_op(symbol); // 명령어 symbol 16진수 변환
				
				//operand check
				result_adr = trans_adr(operand,code);  //operand 16진수변환
				trans = result_op+result_adr;  //7020
				M_temp[lc++]=trans; //16진수로 변환한 배열
			}
		}
		static int trans_op(String a) {    //1.슈도 명령어 16진수 반환메소드
	        int op = 0;
	        switch(a) {
	        case "DEC":
	           dec_hex=0;     // 상수가 16진수인지 10진수 인지 체크
	           break;
	        case "HEX":
	           dec_hex=1;       
	           break;
	           					 	
	        case "AND":				//메모리참조연산은 4번째자리 숫자먼저 구함
	           op = 0*16*16*16;
	           if(I==1)
	        	   op=8;
	           break;
	        case "ADD":
	           op = 1*16*16*16;
	           if(I==1)
	        	   op=9*16*16*16;
	           break;
	        case "LDA":
	           op = 2*16*16*16;
	           if(I==1)
	        	   op=10*16*16*16;
	           break;
	        case "STA":
	           op = 3*16*16*16;
	           if(I==1)
	        	   op=11*16*16*16;
	           break;
	        case "BUN":
	           op = 4*16*16*16;
	           if(I==1)
	        	   op=12*16*16*16;
	           break;
	        case "BSA":
	           op = 5*16*16*16; 
	           if(I==1)
	        	   op=13*16*16*16;
	           break;
	        case "ISZ":
	           op = 6*16*16*16;
	           if(I==1)
	        	   op=14*16*16*16;
	           break;
	        
	       
	          //3. non - MRI 명령어 16진수 반환메소드
	        case "CLA":
	           op = 0x7800;
	           break;
	        case "CLE":
	           op = 0x7400;
	           break;
	        case "CMA":
	           op = 0x7200;
	           break;
	        case "CME":
	           op = 0x7100;
	           break;
	        case "CIR":
	           op = 0x7080;
	           break;
	        case "CIL":
	           op = 0x7040;
	           break;
	        case "INC":
	           op = 0x7020;
	           break;
	        case "CPA":
	           op = 0x7010;
	           break;
	        case "SNA":
	           op = 0x7008;
	           break;
	        case "SZA":
	           op = 0x7004;
	           break;
	        case "SZE":
	           op = 0x7002;
	           break;
	        case "HLT":
	           op = 0x7001;
	           break;
	        case "INP":
	           op = 0xF800;
	           break;
	        case "OUT":
	           op = 0xF400;
	           break;
	        case "SKI":
	           op = 0xF200;
	           break;
	        case "SKO":
	           op = 0xF100;
	           break;
	        case "ION":
	           op = 0xF080;
	           break;
	        case "IOF":
	           op = 0xF040;
	           break;
	        }
	        return op;
	     }
		
		static int trans_adr(String b,String[] code) {	//operand에 라벨이 들어있는 경우
			int adr=0;
			if(First_Pass.ht.containsKey(b)) {  //hashtable에 해당 라벨이 있으면 
				adr = First_Pass.ht.get(b);		//그 라벨의 유효주소 가져옴
			}
				
			else if (b==" ") {				//Non-MRI
				adr=0;
				//피연산자없는거
			}
			
			else { 							//상수			
		        if (dec_hex==0) 
		          adr = Integer.parseInt(b);
		        else{
		          adr = Integer.parseInt(b,16);    
		        }
			}
		
			return adr;		//operand출력
			
		}
		
	}
	
	
	class instructionCycle{
	   private int DR, AR, AC, IR, INPR, OUTPR, TR, SC, indirection, head, I, E, S, D7, INpR, FGI, OUTR, FGO, IEN,HLT;   //각각의 메모리 또는 레지스터
	   int h,END; //h는 HLT의 LC. END는 마지막 LC
	   int PC = transfer.ORG,var_num=transfer.END-transfer.HLT-1,ti=0;         //프로그램 카운터
	   int[] M = new int[5000];
	   String var[]=  new String[var_num];

	   private String symbol,operation;


	   instructionCycle(){                
	   }
	   instructionCycle(int m[]){                
		    this.M=m;
		    showMemory();
		   }

	   
	   private void showMemory() {
		   System.out.println("Location\tInstruction");
		   for(int i=transfer.ORG;i<transfer.END;i++) {
			   System.out.println("  "+Integer.toHexString(i).toUpperCase()+"\t\t "+Integer.toHexString(M[i]+0x110000).substring(2).toUpperCase());
		   }
	   }


	   private String symbolCheck(int a) {                          //instruction 값인지 체크해 심볼을 문자열로 반환하는 메소드
	      //instruction 값인지 체크해 심볼을 문자열로 반환하는 메소드


	      head = (short) (a / 0x1000) ; 
	      //16진수의 맨 앞을 얻음 ex) 0x3006 이면 head = 3
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
	            symbol = symbol + " I";
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
		   int count=transfer.HLT; 
		   //연산과정 출력
		   while(true){
			    System.out.println();
	            System.out.println("-- Location : " 
	            +Integer.toHexString(PC).toUpperCase() );
	            System.out.println("01.입력 = "+Integer.toHexString(M[PC]+0x100000).substring(2).toUpperCase());
	            T0();
	            T1();
	            T2();
	            instructionCheck();
	            System.out.println("02.명령어 형식 = "+operation);
	            System.out.println("03.Symbol ="+symbol.toUpperCase());
	            System.out.println("AR["+Integer.toHexString(AR + 0x11000).substring(2).toUpperCase() +"], "
	            +"PC["+Integer.toHexString(PC + 0x11000).substring(2).toUpperCase() +"], "
	            		+"DR["+Integer.toHexString(DR + 0x10000).substring(1).toUpperCase() +"], "
	            +"AC["+Integer.toHexString(AC + 0x10000).substring(1).toUpperCase() +"],"
	            +"IR["+Integer.toHexString(IR + 0x10000).substring(1).toUpperCase()+"], "
	            +"TR["+Integer.toHexString(TR + 0x10000).substring(1).toUpperCase()+"]");
	            System.out.println();
	           if(symbol.toUpperCase().equals("HLT"))
	        	   break;
	       }
		   //hashtable key-value 출력부
	   		for(int i=0;i<var_num;i++) { 

		   		Set<Entry<String, Integer>> entrySet = First_Pass.ht.entrySet();
				for (Entry<String, Integer> entry : entrySet) {
					if(Integer.toHexString(transfer.HLT+1+i).equals(Integer.toHexString(entry.getValue()))) {
						var[i]=entry.getKey();
					}
				}
	   			if(var[i]==null)
	   				continue;
				System.out.print(var[i]+" : "+(short)M[transfer.HLT+1+i]+"\t");
	   		}
	   		System.out.println();
	   		
	   		//현재 메모리 상태 출력

	   		for(int i=transfer.ORG;i<=transfer.END;i++) {
	   			if(M[i]==0)
	   				continue;
	   			System.out.print("M["+Integer.toHexString(i)+"] : "+Integer.toHexString(M[i]+ 0x110000).substring(2).toUpperCase()+"\t");
	   		}
	   }
	}
