package assembler;

public class instructionCycle {

	private short DR, AR, AC, IR, INPR, OUTPR, TR, SC, indirection, head, I, E, S, D7, INpR, FGI, OUTR, FGO, IEN;
	short PC = 0;
	private short[] M = new short[5000];
	private String symbol;

	// ǥ 6-2 �޸� ���� �κ�.
	instructionCycle() {
		setMemory1();
	}

	private void setMemory1() {		// Instruction
		M[0] = 0x2004;				// LDA 004
		M[1] = 0x1005;				// ADD 005
		M[2] = 0x3006;				// STA 006
		M[3] = 0x7001;				// HLT
		M[4] = 0x0053;				// 0053
		M[5] = (short) 0xffe9;		// FFE9
		M[6] = 0x0000;				// 0000
	}

	// ǥ 6-9 �޸� ���� �κ�.
	instructionCycle(int r) {
		setMemory2();
	}
									// Instruction 
	private void setMemory2() {		// 		ORG 100
		M[0x100] = 0x2107;			// 		LDA SUB
		M[0x101] = 0x7200;			// 		CMA
		M[0x102] = 0x7020;			// 		INC
		M[0x103] = 0x1106;			// 		ADD MIN
		M[0x104] = 0x3108;			// 		STA DIF
		M[0x105] = 0x7001;			// 		HLT
		M[0x106] = 0x0053;			// MIN,	DEC 83
		M[0x107] = (short) 0xFFE9;	// SUB,	DEC -23
		M[0x108] = 0x0000;			// DIF,	HEX 0
									// 		END
	}

	// ����Ŭ�� �����ϴ� �κ�.
	void printCycle() {
		try {
			while (true) {
				System.out.println("--------------- Location : " + Integer.toHexString(PC + 0x10000).substring(2)
						+ "------------------");
				start();
				T0();
				T1();
				T2();
				/*
					instructionCheck(); 
					T2 ���ĸ� �����Ѵ�. �̱����̹Ƿ� �ּ�ó��.
				*/
				System.out.println("------------------------------------------------");
				System.out.println();
			}
		} catch (Exception e) { 
			/*
			 	������ catch ( HaltException e).
			 	�̱����̹Ƿ� Exception���� ����
			 */
			System.out.println("���� �˴ϴ�.");
		}
	}

	private void start() {
		SC = 0;
	}

	private void T0() {
		SC++;
		AR = (short) PC;
		System.out.println("T0 : ");
		System.out.println("AR <- PC");
		System.out.println("AR = " + AR);
		System.out.println();
	}

	private void T1() {
		SC++;
		IR = M[AR];
		PC = (short) (PC + 1);
		System.out.println("T1 : ");
		System.out.println("IR <- M[AR], PC <- PC + 1");
		System.out.println("IR = " + Integer.toHexString(M[AR] + 0x10000).substring(1) + ", PC = " + PC);
		System.out.println();
	}

	private void T2() {
		SC++; // sc ������Ű��.
		symbol = symbolCheck(IR); // ��ɾ� Ȯ��(decode)

		AR = (short) (IR & 0x0fff); // IR(0-11) AR�� ����
		I = indirection; // symbolic check�� indirection ����.

		System.out.println("T2 : ");
		System.out.println("Decode operation code in IR(12-14)");
		System.out.println("AR <- IR(0-11), I <- IR(15)");
		System.out.println("AR = " + Integer.toHexString(AR + 0x10000).substring(2) + ", I = " + I);
		System.out.println("D7 = " + D7);
		System.out.println();
	}

	private String symbolCheck(int a) {
		// [0~6] : �޸�_����, [7] : ��������, [8~E] : �޸�_����, [F] : �����
		head = (short) ((short) a / 0x1000); // IR(15)Ȯ��.
		D7 = 0;
		indirection = (short) (head / 8); // I=0 [0~6], I=1 [8~E]
		symbol = "nop";
		String address = Integer.toHexString(a + 0x10000).substring(2);

		// 7XXX -> �������� ��ɾ�(0111 XXXX XXXX XXXX)
		if (head == 7) {
			address = "   ";
			D7 = 1;
			switch (a & 0x0FFF) { // IR(11-0)Ȯ��
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

		// FXXX -> ����� ��ɾ�(1111 XXXX XXXX XXXX)
		else if (head == 0xf) { // HEX(0xf) = DEC(15) = BIN(1111)
			address = "   ";
			switch (a & 0x0FFF) { // IR(11-0)Ȯ��
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
		
		// �޸� ��ɾ�
		else {
			switch (head % 8) { 
			case 0:
				symbol = "AND"; // 0 or 8
				break;
			case 1:
				symbol = "ADD"; // 1 or 9
				break;
			case 2:
				symbol = "LDA"; // 2 or A
				break;
			case 3:
				symbol = "STA"; // 3 or B
				break;
			case 4:
				symbol = "BUN"; // 4 or C
				break;
			case 5:
				symbol = "BSA"; // 5 or D
				break;
			case 6:
				symbol = "ISZ"; // 6 or E
				break;
			}
			if (indirection == 1)
				symbol = "I " + symbol; 
		}
		return symbol + "  " + address; // ex) I STA 751
	}
}