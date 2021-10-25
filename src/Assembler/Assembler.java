package Assembler;

public class Assembler {
	static short PC=0,DR, AR, AC, IR, TR, SC, indirection, head,opcode,E;
	static short[] M = new short[65536];
	
	public static void main(String arg[]) {
		setMemory1();
		for(int i=0;i<7;i++) {
			System.out.println(i);
			memory_ref(M[i]);
		}
	}
	
	
	private static void setMemory1(){            //표 6-2번 메모리 세팅 메소드
	      M[0] = 0x2004;
	      M[1] = 0x1005;
	      M[2] = 0x3006;
	      M[3] = 0x7001;
	      M[4] = 0x0053;
	      M[5] = (short)0xffe9;
	      M[6] = 0x0000;
	   }
	
	
	
	public static  void memory_ref(int instr) {
		head = (short) ((short)instr/0x1000); //16진수 맨 앞자리 뽑아냄
		indirection = (short) (head/8); // 8로 나누어 간접비트 확인
		opcode = (short)((short)instr%0x1000);
		System.out.print("T3.  ");
		if(indirection ==1) {
			System.out.println("AR <- M[AR]");
            AR = M[AR];
            System.out.println("AR = " + Integer.toHexString(AR + 0x10000).substring(1));
         }
         else{
            System.out.println("Nothing");
         }
		
		switch(head%8) {
		case 0: //AND
			System.out.print("T4.  ");
            System.out.println("DR <- M[AR]");
            DR = M[AR];
            System.out.println("\tDR = " + 
            Integer.toHexString(DR + 0x10000).substring(1));
            System.out.print("T5.  ");
            System.out.print("AC <- AC ^ DR, ");
            System.out.println("SC <- 0");
            AC = (short) (AC & DR);
            SC = 0;
            System.out.println("\tAC = " + Integer.toHexString(DR + 0x10000).substring(1));
            System.out.println("\tSC = " + SC);
            System.out.println();
			break;
		case 1: //ADD
			int Cout = 0;
            System.out.print("T4.  ");
            System.out.println("DR <- M[AR]");
            DR = M[AR];
            System.out.println("\tDR = " + 
            Integer.toHexString(DR + 0x10000).substring(1));

            System.out.print("T5.  ");
            System.out.print("AC <- AC + DR, ");
            System.out.print("E <- Cout, ");
            System.out.println("SC <- 0");
            if(AC < 0 && AC + DR < 0 && DR > 0 || AC > 0 && AC + DR > 0 && DR < 0){ //오버플로우가 일어났을때
               Cout = 1;
            }
            AC = (short) (AC + DR);
            E = (short) Cout;
            SC = 0;
            System.out.println("\tAC = " + 
            Integer.toHexString(AC + 0x10000).substring(1));
            System.out.println("\tE = " + E);
            System.out.println("\tSC = " + SC);
			break;
		case 2: //LDA
			System.out.print("T4.  ");
            System.out.println("DR <- M[AR]");
            DR = M[AR];
            System.out.println("\tDR = " + Integer.toHexString(DR + 0x10000).substring(1));

            System.out.print("T5.  ");
            System.out.print("AC <- DR, ");
            System.out.println("SC <- 0");
            AC = DR;
            SC = 0;
            System.out.println("\tAC = " + 
            Integer.toHexString(AC + 0x10000).substring(1));
            System.out.println("\tSC = " + SC);
			break;
		case 3: //STA
			System.out.print("T4.  ");
            System.out.print("M[AR] <- AC, ");
            System.out.println("SC <- 0");
            M[AR] = AC;
            SC = 0;
            System.out.println("\tM[AR] = " + 
            Integer.toHexString(M[AR] + 0x10000).substring(1));
            System.out.println("\tSC = " + SC);
			break;
		case 4: //BUN
			System.out.print("T4.  ");
            System.out.print("PC <- AR, ");
            System.out.println("SC <- 0");
            PC = AR;
            SC = 0;
            System.out.println("\tPC = " + 
            Integer.toHexString(PC + 0x10000).substring(1));
            System.out.println("\tSC = " + 0);
			break;
		case 5: //BSA
			System.out.print("T4.  ");
            System.out.print("M[AR] <- PC, ");
            System.out.println("AR <- AR + 1");
            M[AR] = PC;
            AR = (short) (AR + 1);
            System.out.println("\tM[AR] = " + 
            Integer.toHexString(M[AR] + 0x10000).substring(1));
            System.out.println("\tAR = " + Integer.toHexString(AR + 0x10000).substring(2));

            System.out.print("T5.  ");
            System.out.print("PC <- AR, ");
            System.out.println("SC <- 0");
            PC = AR;
            SC = 0;
            System.out.println("\tPC = " + 
            Integer.toHexString(PC + 0x10000).substring(1));
            System.out.println("\tSC = " + SC);
            System.out.println();
			break;
		case 6: //ISZ
			System.out.print("T4.  ");
            System.out.println("DR <- M[AR]");
            DR = M[AR];
            System.out.println("\tDR = " + 
            Integer.toHexString(DR + 0x10000).substring(1));

            System.out.print("T5.  ");
            System.out.println("DR = DR + 1");
            DR = (short) (DR + 1);
            System.out.println("\tDR = " + 
            Integer.toHexString(DR + 0x10000).substring(1));

            System.out.print("T6.  ");
            System.out.print("M[AR] <- DR, ");
            System.out.print("If(DR = 0) then (PC <- PC + 1), ");
            System.out.println("SC <- 0");
            M[AR] = DR;
            if(DR == 0){
               PC = (short) (PC + 1);
            }
            SC = 0;
            System.out.println("\tM[AR] = " + 
            Integer.toHexString(M[AR] + 0x10000).substring(1));
            System.out.println("\tPC = " + 
            Integer.toHexString(PC + 0x10000).substring(1));
            System.out.println("\tSC = " + SC);
			break;
		}
		System.out.println();
		
		
	}
}


