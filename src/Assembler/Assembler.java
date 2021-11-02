package Assembler;
public class Assembler {
<<<<<<< HEAD
	   public static void main(String[] args) {
	      instructionCycle cpu = new instructionCycle();
	      cpu.printCycle();
	   
	   }
	}


	class instructionCycle{
	   private short DR, AR, AC, IR, INPR, OUTPR, TR, SC, indirection, head, I, E, S, D7, INpR, FGI, OUTR, FGO, IEN,HLT;   //°¢°¢ÀÇ ¸Þ¸ð¸® ¶Ç´Â ·¹Áö½ºÅÍ
	   int count=10;
	   short PC = 0;         //ÇÁ·Î±×·¥ Ä«¿îÅÍ
	   private short[] M = new short[5000];
	   private String symbol,operation;

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
	   


	   private String symbolCheck(int a) {                          //instruction °ªÀÎÁö Ã¼Å©ÇØ ½Éº¼À» ¹®ÀÚ¿­·Î ¹ÝÈ¯ÇÏ´Â ¸Þ¼Òµå
	      //instruction °ªÀÎÁö Ã¼Å©ÇØ ½Éº¼À» ¹®ÀÚ¿­·Î ¹ÝÈ¯ÇÏ´Â ¸Þ¼Òµå

	      head = (short) ((short)a / 0x1000) ; 
	      //16Áø¼öÀÇ ¸Ç ¾ÕÀ» ¾òÀ½ ex) 0x1234 ÀÌ¸é head = 1
	      D7 = 0;

	      indirection = (short) (head / 8); 
	      //indirect bit ¸¦ ¾òÀ½ ex) 0~7·Î ½ÃÀÛÇÏ¸é 0, 8~f·Î ½ÃÀÛÇÏ¸é 1

	      symbol = "";

	      String address = Integer.toHexString(a + 0x10000).substring(2);   
	      //ÁÖ¼Ò°ªÀ» ¹Ì¸® ³Ö´Â´Ù.

	      if(head == 7){ // 7xxx 
	         address = "   "; //ÁÖ¼Ò¸¦ ¾ø¾Ø´Ù
	         D7 = 1;
	         operation = "'Register' reference operation";
	         switch( a & 0x0FFF){ //µÞÀÚ¸®¸¦ ºñ±³ÇÑ´Ù.
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
	         address = "   "; //ÁÖ¼Ò¸¦ ¾ø¾Ø´Ù
	         D7 = 1;
	         operation = "'I/O' reference operation";
	         switch( a & 0x0FFF){ //µÞÀÚ¸®¸¦ ºñ±³ÇÑ´Ù.
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
	         if (indirection == 1) // indirect bit °¡ 1 ÀÌ¸é °£Á¢ ÁÖ¼ÒÀÓÀ» Ç¥½ÃÇÑ´Ù.
	            symbol = "I " + symbol;
	      }

	      return symbol + "  " + address; // symbol + ÁÖ¼Ò°ª ¹ÝÈ¯

	   }

	   private void start(){            //½ÃÀÛ ¸Þ¼Òµå
	      SC = 0;
	   }

	   private void T0(){               // T0 ÀÏ ¶§
	      SC++;
	      AR = (short) PC;
	  
	   }

	   private void T1(){              // T1 ÀÏ ¶§
	      SC++;
	      IR = M[AR]; PC = (short) (PC + 1);
	     
	   }

	   private void T2(){             //T2 ÀÏ ¶§
	      SC++;
	      symbol = symbolCheck(M[AR]);
	      AR = (short) (IR & 0x0fff); I = indirection;

	     
	   }

	   private void instructionCheck() {  //ÀÎ½ºÆ®·°¼Ç Ã¼Å©ÇÏ°í ¸í·É¾î¿¡ µû¶ó T3, T4, T5 ... ÇÒÀÏ °áÁ¤
	       symbol = symbol.substring(0,3);
		   if(head == 7){      //D7 = 1 ÀÌ°í, I = 0 ÀÎ°æ¿ì
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
	      else if(head == 0xf){  //D7 = 1 ÀÌ°í, I = 1 ÀÎ°æ¿ì

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

	            if(AC < 0 && AC + DR < 0 && DR > 0 || AC > 0 && AC + DR > 0 && DR < 0){ //¿À¹öÇÃ·Î¿ì°¡ ÀÏ¾î³µÀ»¶§
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

	   void printCycle(){    //¸í·É¾î »çÀÌÅ¬À» ´«¿¡ º¸ÀÌ°Ô ÇÁ¸°Æ® ÇØÁØ´Ù.
		   while(count>0){
	            System.out.println("-- Location : " 
	            + Integer.toHexString(PC) );
	            System.out.println("01.ÀÔ·Â = "+Integer.toHexString(M[PC]+0x100000).substring(2).toUpperCase());
	            start();
	            T0();
	            T1();
	            T2();
	            instructionCheck();
	            System.out.println("02.¸í·É¾î Çü½Ä = "+operation);
	            System.out.println("03.Symbol ="+symbol.toUpperCase());
	            System.out.println("AR["+Integer.toHexString(AR + 0x10000).substring(2).toUpperCase() +"], "
	            +"PC["+Integer.toHexString(PC + 0x10000).substring(2).toUpperCase() +"], "
	            		+"DR["+Integer.toHexString(DR + 0x10000).substring(2).toUpperCase() +"], "
	            +"AC["+Integer.toHexString(AC + 0x10000).substring(2).toUpperCase() +"],"
	            +"IR["+Integer.toHexString(IR + 0x10000).substring(2).toUpperCase()+"], "
	            +"TR["+Integer.toHexString(TR + 0x10000).substring(2).toUpperCase()+"]");
	            System.out.println();
	            System.out.println();
	            count= count-1;
	       }
	   }

	} 
=======
   public static void main(String[] args) {
      instructionCycle cpu = new instructionCycle();
      cpu.printCycle();

      
   }
}


class instructionCycle{
   private short DR, AR, AC, IR, INPR, OUTPR, TR, SC, indirection, head, I, E, S, D7, INpR, FGI, OUTR, FGO, IEN,HLT;   //ê°ê°ì˜ ë©”ëª¨ë¦¬ ë˜ëŠ” ë ˆì§€ìŠ¤í„°
   int count=10;
   short PC = 0;         //í”„ë¡œê·¸ëž¨ ì¹´ìš´í„°
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
   


   private String symbolCheck(int a) {                          //instruction ê°’ì¸ì§€ ì²´í¬í•´ ì‹¬ë³¼ì„ ë¬¸ìžì—´ë¡œ ë°˜í™˜í•˜ëŠ” ë©”ì†Œë“œ
      //instruction ê°’ì¸ì§€ ì²´í¬í•´ ì‹¬ë³¼ì„ ë¬¸ìžì—´ë¡œ ë°˜í™˜í•˜ëŠ” ë©”ì†Œë“œ

      head = (short) ((short)a / 0x1000) ; 
      //16ì§„ìˆ˜ì˜ ë§¨ ì•žì„ ì–»ìŒ ex) 0x1234 ì´ë©´ head = 1
      D7 = 0;

      indirection = (short) (head / 8); 
      //indirect bit ë¥¼ ì–»ìŒ ex) 0~7ë¡œ ì‹œìž‘í•˜ë©´ 0, 8~fë¡œ ì‹œìž‘í•˜ë©´ 1

      symbol = "nop";
      //symbolì„ nop ìœ¼ë¡œ ì„¤ì •í•œ í›„ ì¡°ê±´ì— ë”°ë¼ ë°”ê¾¼ë‹¤.

      String address = Integer.toHexString(a + 0x10000).substring(2);   
      //ì£¼ì†Œê°’ì„ ë¯¸ë¦¬ ë„£ëŠ”ë‹¤.

      if(head == 7){ // 7xxx 
         address = "   "; //ì£¼ì†Œë¥¼ ì—†ì•¤ë‹¤
         D7 = 1;

         switch( a & 0x0FFF){ //ë’·ìžë¦¬ë¥¼ ë¹„êµí•œë‹¤.
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
         address = "   "; //ì£¼ì†Œë¥¼ ì—†ì•¤ë‹¤
         D7 = 1;

         switch( a & 0x0FFF){ //ë’·ìžë¦¬ë¥¼ ë¹„êµí•œë‹¤.
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
         if (indirection == 1) // indirect bit ê°€ 1 ì´ë©´ ê°„ì ‘ ì£¼ì†Œìž„ì„ í‘œì‹œí•œë‹¤.
            symbol = "I " + symbol;
      }

      return symbol + "  " + address; // symbol + ì£¼ì†Œê°’ ë°˜í™˜

   }

   private void start(){            //ì‹œìž‘ ë©”ì†Œë“œ
      SC = 0;
   }

   private void T0(){               // T0 ì¼ ë•Œ
      SC++;
      AR = (short) PC;
  
   }

   private void T1(){              // T1 ì¼ ë•Œ
      SC++;
      IR = M[AR]; PC = (short) (PC + 1);
     
   }

   private void T2(){             //T2 ì¼ ë•Œ
      SC++;
      symbol = symbolCheck(M[AR]);
      AR = (short) (IR & 0x0fff); I = indirection;

     
   }

   private void instructionCheck() {                  //ì¸ìŠ¤íŠ¸ëŸ­ì…˜ ì²´í¬í•˜ê³  ëª…ë ¹ì–´ì— ë”°ë¼ T3, T4, T5 ... í• ì¼ ê²°ì •
  
      if(head == 7){      //D7 = 1 ì´ê³ , I = 0 ì¸ê²½ìš°

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
      else if(head == 0xf){  //D7 = 1 ì´ê³ , I = 1 ì¸ê²½ìš°

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

            if(AC < 0 && AC + DR < 0 && DR > 0 || AC > 0 && AC + DR > 0 && DR < 0){ //ì˜¤ë²„í”Œë¡œìš°ê°€ ì¼ì–´ë‚¬ì„ë•Œ
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

   void printCycle(){    //ëª…ë ¹ì–´ ì‚¬ì´í´ì„ ëˆˆì— ë³´ì´ê²Œ í”„ë¦°íŠ¸ í•´ì¤€ë‹¤.
	   while(count>0){
            System.out.println("--------------- Location : " 
            + Integer.toHexString(PC + 0x10000).substring(2) + "------------------");
            System.out.println("01.ìž…ë ¥ = "+Integer.toHexString(M[PC])9);
            start();
            T0();
            T1();
            T2();
            instructionCheck();
            System.out.println("02.ëª…ë ¹ì–´ í˜•ì‹ = ");
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
>>>>>>> 6d8148c417a1f66d223e2fc0d98eeab241fa6e06
