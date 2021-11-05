package Assembler;
public class Assembler {
	   public static void main(String[] args) {
	      instructionCycle cpu = new instructionCycle();
	      cpu.printCycle();
	   
	   }
	}


	class instructionCycle{
	   private short DR, AR, AC, IR, INPR, TR, SC, indirection, head, I, E, S, R, D7, FGI, OUTR, FGO, IEN, HLT;   //각각의 메모리 또는 레지스터.
	   int h,END; //h = HTL의 LC. END = 마지막 LC
	   short PC = 0;         //프로그램 카운터
	   private short[] M = new short[5000];
	   String var[]= {"A","B","C"};
	   private String symbol,operation;

	   instructionCycle(){                
	      setMemory2(); // 테스트중 임의로 2로 변경
	      showMemory();
	   }
	   
	   /*
	   private void setMemory(){            
	      M[0] = 0x2004;
	      M[1] = 0x1005;
	      M[2] = 0x3006;
	      M[3] = 0x7001;
	      M[4] = 0x0053;
	      M[5] = (short)0xffe9;
	      M[6] = 0x0000;
	      h=3; // HLT위치
	      END=6;	      
	   }
	   */
	   
	   /*
	    * ============================================================================
	    * << 조정중 >>
		* 1. (short)0xF080 같은 알파벳 시작 16진수의 첫 자리를 인식하지 못하는 에러
		* -> 해당 종류의 명령어를 전부 ADD로 인식.
		* -> ION을 통해 IEN=1으로 설정 불가
		* -> 인터럽트 발생x
		* 
		* 2. printCycle의 count 조절(setMemory2()의 h값)
	    *============================================================================
	    */
	   
	   
	   private void setMemory2(){   // 인터럽트 테스트용         
		      M[0] = 0x2004; // LDA X
		      M[1] = (short)0xf080; // ION , 현재 F인식 불가.
		      M[2] = 0x3005; // STA Y , 3005
		      M[3] = 0x7001; // HLT
		      M[4] = 0x0100; // X
		      M[5] = 0x0000; // Y
		      M[6] = 0x0000; // END
		      h=3; // 임의값 23.
		      END=6;    
		      
			  // 인터럽트 분기시 AR=0x0FF로 설정. M[0x0FF]에는 기존의 PC값 저장.
			  M[0x0FF] = 0x0000; // 복귀주소 저장공간
			  M[0x100] = 0x3200; // STA SAC
			  M[0x101] = 0x7080; // CIR
			  M[0x102] = 0x3201; // STA SE
			  M[0x103] = (short)0xF200; // SKI
			  M[0x104] = 0x4109; // BUN NXT
			  M[0x105] = (short)0xF800; // INP
			  M[0x106] = (short)0xF400; // OUT 
			  M[0x107] = (short)0xB202; // STA PT1 I
			  M[0x108] = 0X6202; // ISZ PT1
			  M[0x109] = (short)0xF100; // NXT, SKO
			  M[0x10A] = 0x410E; // BUN EXT
			  M[0x10B] = (short)0xA205; // LDA PT2 I
			  M[0x10C] = (short)0xF400; // OUT
			  M[0x10D] = 0x6205; // ISZ PT2
			  M[0x10E] = 0x2201; // EXT, LAD SE
			  M[0x10F] = 0x7040; // CIL
			  M[0x110] = 0x2200; // LDA SAC
			  M[0x111] = (short)0xF080; // ION
			  M[0x112] = (short)0xC0FF; // BUN ZRO I, 복귀
							   
			  M[0x200] = 0x0000; // SAC
			  M[0x201] = 0x0000; // SE
			  M[0x202] = 0x0208; // PT1 - Pointer to input Buffer 
			  M[0x205] = 0x0209; // PT2 - Pointer to output Buffer
			   
			  INPR = M[0x208] = 0x0011; // input Buffer 가정 
			  M[0x209] = OUTR; // output Buffer 가정
		      
		   }
	   
	   private void showMemory() {
		   for(int i=0;i<7;i++) {
			   System.out.println("M["+i+"] ="+Integer.toHexString(M[i]+0x100000).substring(2).toUpperCase());
		   }
	   }

	   private String symbolCheck(int a) {   //instruction 값인지 체크해 심볼을 문자열로 반환하는 메소드
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
	         address = "   "; 
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
	         address = "   "; //주소를 없엔다
	         D7 = 1;
	         operation = "'I/O' reference operation";
	         switch( a & 0x0FFF){ // 뒷자리를 비교한다.
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
	         if (indirection == 1) // indirect bit가 1이면 간접 주소임을 표시한다..
	            symbol = "I " + symbol;
	      }

	      return symbol + "  " + address; // symbol + 주소값 반환

	   }

	 
	   private void T0(){               // T0 
	      AR = (short) PC;
	   }

	   private void T1(){              // T1
	      IR = M[AR]; PC = (short) (PC + 1);
	   }

	   private void T2(){             //T2 
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
	      else if(head == 0xf){  ///D7 = 1 이고, I = 1 인경우

	         switch(symbol){
	         case "INP":

	            AC = INPR; FGI = 0; // FGI = 1일때의 상황.
	            break;
	         case "OUT":

	            OUTR = (short) (0x00ff & AC); FGO = 0; // FGO = 1일 때의 상황. AC 끝의 두자리만 OUTR로 이동.
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

	            if(AC < 0 && AC + DR < 0 && DR > 0 || AC > 0 && AC + DR > 0 && DR < 0){ ///오버플로우가 일어났을때
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
	   
	   void interruptCheck() { // R = 1일 때.
		   //RT0
		   AR = 0x0FF; 
		   TR = PC;
		   
		   //RT1
		   M[AR] = TR; // M[0x0ff]에 복귀장소인 M[2] 저장.
		   PC = 0x0FF; 
		   
		   //RT2
		   PC++;       // M[100]
		   IEN = 0;	   // 이하 인터럽트 발생 차단
		   R=0;
		   SC = 0;
	
	   }
	   


	   void printCycle(){//명령어 사이클을 눈에 보이게 프린트 해준다.
		   int count=h; 
		   while(count>=0){
			    System.out.println();
	            System.out.println("-- Location : " 
	            + Integer.toHexString(PC) );
	            System.out.println("01.입력 = "+Integer.toHexString(M[PC]+0x100000).substring(2).toUpperCase());
	            if(R==1) interruptCheck(); // R을 통한 인터럽트 분기
	            T0();
	            T1();
	            T2();
	            R = (short) ((IEN == 0) ? 0 : (FGI == 1) ? 1 : (FGO == 1) ? 1 : 0); // 조건 확인 후 R 설정.
	            instructionCheck();
	            System.out.println("02.명령어 형식 = "+operation);
	            System.out.println("03.Symbol ="+symbol.toUpperCase());
	            System.out.println("AR["+Integer.toHexString(AR + 0x10000).substring(2).toUpperCase() +"], "
	            +"PC["+Integer.toHexString(PC + 0x10000).substring(2).toUpperCase() +"], "
	            +"DR["+Integer.toHexString(DR + 0x10000).substring(1).toUpperCase() +"], "
	            +"AC["+Integer.toHexString(AC + 0x10000).substring(1).toUpperCase() +"],"
	            +"IR["+Integer.toHexString(IR + 0x10000).substring(1).toUpperCase()+"], "
	            +"TR["+Integer.toHexString(TR + 0x10000).substring(1).toUpperCase()+"], "
	            +"INPR["+Integer.toHexString(INPR + 0x10000).substring(3).toUpperCase()+"], "
	            +"OUTR["+Integer.toHexString(OUTR + 0x10000).substring(3).toUpperCase()+"]");

	            System.out.println();
	           
	            count= count-1;
	       }
	   		System.out.println();
	   		for(int i=0;i<END-h;i++) { //h는 HLT의 LC. END는 메모리 마지막.
	   		System.out.print(var[i]+" : "+M[h+1+i]+"\t");
	   		}
	   		System.out.println();
	   		showMemory();
	   }
	} 