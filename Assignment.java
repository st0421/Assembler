package assembler;
/*
 *  ����� PPT ���Ͽ� ������ �ڵ���� ��ƺý��ϴ�.
 *  �޸� �Է�, T0, T1, T2�� �����Ǿ��ֽ��ϴ�.
 *  T2 ���Ĵ� �������� �ʾұ⿡ PPT�� ������� ���̰� �ֽ��ϴ�.
 *  HLT�� �������� �ʾҽ��ϴ�!
 *  
 *  ������ �ʿ��� ����� ������ �����ϴ�.
 *  1. asm ������ �о setMemory�� �����ϴ� ���(�ʿ��).
 *  2. instructionCheck(), �� T3������ ������ �����ϴ� ���.
 *  3. ��� ���� �ٵ��(�ʿ��)
 * 
 * */
public class Assignment {
	public static void main(String[] args) {

		instructionCycle cpu = new instructionCycle();
		System.out.println("ǥ 6-2 ���α׷� ����!!!!!");
		cpu.printCycle();

		cpu = new instructionCycle(1);
		System.out.println("ǥ 6-9 ���α׷� ����!!!!!");
		cpu.printCycle();

	}
}
