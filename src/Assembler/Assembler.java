package Assembler;

public class Assembler {
   public static void main(String[] args) {
      instructionCycle cpu = new instructionCycle();
      cpu.printCycle();

      
   }
}


class instructionCycle{
   private short DR, AR, AC, IR, INPR, OUTPR, TR, SC, indirection, head, I, E, S, D7, INpR, FGI, OUTR, FGO, IEN,HLT;   //각각의 메모리 또는 레지스터
   int count=10;
   short PC = 0;         //프로그램 카운터
   private short[] M = new short[5000];
   private String symbol;

   instructionCycle(){                
      setMemory();
   }
   

   private void setMemory(){            
      M[0] = 0x2004;
      M[1] = 0x1005;
      M[2] = 0x3006;
      M[3] = 0x7001;
      M[4] = 0x0053;
      M[5] = (short)0xffe9;
      M[6] = 0x0000;
   }
   


   private String symbolCheck(int a) {                          //instruction 값인지 체크해 심볼을 문자열로 반환하는 메소드
      //instruction 값인지 체크해 심볼을 문자열로 반환하는 메소드

      head = (short) ((short)a / 0x1000) ; 
      //16진수의 맨 앞을 얻음 ex) 0x1234 이면 head = 1
      D7 = 0;

      indirection = (short) (head / 8); 
      //indirect bit 를 얻음 ex) 0~7로 시작하면 0, 8~f로 시작하면 1

      symbol = "nop";
      //symbol을 nop 으로 설정한 후 조건에 따라 바꾼다.

      String address = Integer.toHexString(a + 0x10000).substring(2);   
      //주소값을 미리 넣는다.

      if(head == 7){ // 7xxx 
         address = "   "; //주소를 없앤다
         D7 = 1;

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

   private void start(){            //시작 메소드
      SC = 0;
   }

   private void T0(){               // T0 일 때
      SC++;
      AR = (short) PC;
  
   }

   private void T1(){              // T1 일 때
      SC++;
      IR = M[AR]; PC = (short) (PC + 1);
     
   }

   private void T2(){             //T2 일 때
      SC++;
      symbol = symbolCheck(M[AR]);
      AR = (short) (IR & 0x0fff); I = indirection;

     
   }

   private void instructionCheck() {                  //인스트럭션 체크하고 명령어에 따라 T3, T4, T5 ... 할일 결정
  
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

   void printCycle(){    //명령어 사이클을 눈에 보이게 프린트 해준다.
	   while(count>0){
            System.out.println("--------------- Location : " 
            + Integer.toHexString(PC + 0x10000).substring(2) + "------------------");
            System.out.println("01.입력 = "+Integer.toHexString(M[PC])9);
            start();
            T0();
            T1();
            T2();
            instructionCheck();
            System.out.println("02.명령어 형식 = ");
            System.out.println("03.Symbol ="+symbol);
            System.out.println("AR["+Integer.toHexString(AR + 0x10000).substring(2) +"], "
            +"PC["+Integer.toHexString(PC + 0x10000).substring(2) +"], "
            		+"DR["+Integer.toHexString(DR + 0x10000).substring(2) +"], "
            +"AC["+Integer.toHexString(AC + 0x10000).substring(2) +"],"
            +"IR["+Integer.toHexString(IR + 0x10000).substring(2)+"], "
            +"TR["+Integer.toHexString(TR + 0x10000).substring(2)+"]");
            System.out.println("-------------------------------------------------------");
            System.out.println();
            count= count-1;
       }
   }

} 
